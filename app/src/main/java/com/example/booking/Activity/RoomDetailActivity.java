package com.example.booking.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.booking.Model.Room;
import com.example.booking.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RoomDetailActivity extends AppCompatActivity {

    private ImageView imgRoomDetail;
    private TextView txtName, txtDescription, txtCheckIn, txtCheckOut, txtPriceCalc, txtTotalPrice;
    private EditText edtAdults, edtChildren;
    private Button btnCheckAvailability, btnConfirmBooking;
    private LinearLayout layoutPriceInfo;
    private MaterialCardView cardBookingAction;
    
    private Room room;
    private Calendar calendarIn, calendarOut;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private long diffDays = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        room = (Room) getIntent().getSerializableExtra("room_data");
        
        initUi();
        displayRoomData();
        
        // Nhận dữ liệu từ Intent (BƯỚC 3: Kế thừa tiêu chí từ trang chủ)
        handleIntentData();
        
        setupDatePickers();

        btnCheckAvailability.setOnClickListener(v -> checkAvailability());
        
        // Chuyển sang BƯỚC 4: Nhập thông tin người ở
        btnConfirmBooking.setOnClickListener(v -> {
            Intent intent = new Intent(RoomDetailActivity.this, ConfirmBookingActivity.class);
            intent.putExtra("room_data", room);
            intent.putExtra("checkIn", calendarIn.getTimeInMillis());
            intent.putExtra("checkOut", calendarOut.getTimeInMillis());
            intent.putExtra("adults", Integer.parseInt(edtAdults.getText().toString()));
            intent.putExtra("children", Integer.parseInt(edtChildren.getText().toString()));
            intent.putExtra("totalPrice", diffDays * room.getPrice());
            startActivity(intent);
        });
    }

    private void handleIntentData() {
        long checkIn = getIntent().getLongExtra("checkIn", 0);
        long checkOut = getIntent().getLongExtra("checkOut", 0);
        int guests = getIntent().getIntExtra("guests", 0);

        if (checkIn != 0 && checkOut != 0) {
            calendarIn.setTimeInMillis(checkIn);
            calendarOut.setTimeInMillis(checkOut);
            txtCheckIn.setText(sdf.format(calendarIn.getTime()));
            txtCheckOut.setText(sdf.format(calendarOut.getTime()));
            edtAdults.setText(String.valueOf(guests));
            
            // Tự động kiểm tra luôn vì khách đã chọn ở trang chủ
            checkAvailability();
        }
    }

    private void initUi() {
        imgRoomDetail = findViewById(R.id.imgRoomDetail);
        txtName = findViewById(R.id.txtRoomNameDetail);
        txtDescription = findViewById(R.id.txtDescriptionDetail);
        txtCheckIn = findViewById(R.id.txtCheckIn);
        txtCheckOut = findViewById(R.id.txtCheckOut);
        edtAdults = findViewById(R.id.edtAdults);
        edtChildren = findViewById(R.id.edtChildren);
        btnCheckAvailability = findViewById(R.id.btnCheckAvailability);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        layoutPriceInfo = findViewById(R.id.layoutPriceInfo);
        cardBookingAction = findViewById(R.id.cardBookingAction);
        txtPriceCalc = findViewById(R.id.txtPriceCalc);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        
        calendarIn = Calendar.getInstance();
        calendarOut = Calendar.getInstance();
        calendarOut.add(Calendar.DAY_OF_MONTH, 1);
    }

    private void displayRoomData() {
        if (room != null) {
            txtName.setText(room.getName());
            txtDescription.setText(room.getDescription());
            Glide.with(this).load(room.getImageUrl()).placeholder(R.drawable.dlmix).into(imgRoomDetail);
        }
    }

    private void setupDatePickers() {
        txtCheckIn.setOnClickListener(v -> showDatePicker(true));
        txtCheckOut.setOnClickListener(v -> showDatePicker(false));
    }

    private void showDatePicker(boolean isCheckIn) {
        Calendar c = isCheckIn ? calendarIn : calendarOut;
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            if (isCheckIn) {
                calendarIn = selected;
                txtCheckIn.setText(sdf.format(selected.getTime()));
            } else {
                calendarOut = selected;
                txtCheckOut.setText(sdf.format(selected.getTime()));
            }
            layoutPriceInfo.setVisibility(View.GONE);
            cardBookingAction.setVisibility(View.GONE);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void checkAvailability() {
        if (calendarOut.before(calendarIn) || calendarOut.equals(calendarIn)) {
            Toast.makeText(this, "Ngày không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings");
        bookingsRef.orderByChild("roomId").equalTo(room.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int bookedCount = 0;
                long start = calendarIn.getTimeInMillis();
                long end = calendarOut.getTimeInMillis();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Long bIn = data.child("checkInDate").getValue(Long.class);
                    Long bOut = data.child("checkOutDate").getValue(Long.class);
                    String status = data.child("status").getValue(String.class);

                    if (bIn != null && bOut != null && !"Cancelled".equals(status) && !"Expired".equals(status)) {
                        if (start < bOut && end > bIn) bookedCount++;
                    }
                }

                if (bookedCount < room.getTotalRooms()) {
                    showPriceCalculation();
                } else {
                    Toast.makeText(RoomDetailActivity.this, "Hết phòng trong thời gian này", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showPriceCalculation() {
        long diff = calendarOut.getTimeInMillis() - calendarIn.getTimeInMillis();
        diffDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        double total = diffDays * room.getPrice();
        txtPriceCalc.setText(String.format(Locale.getDefault(), "%,.0f VNĐ x %d đêm", room.getPrice(), diffDays));
        txtTotalPrice.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", total));
        layoutPriceInfo.setVisibility(View.VISIBLE);
        cardBookingAction.setVisibility(View.VISIBLE);
    }
}
