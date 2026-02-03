package com.example.booking.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.booking.Model.Room;
import com.example.booking.R;
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
    private TextView txtName, txtDescription, txtPrice, txtCheckIn, txtCheckOut, txtPriceCalc, txtTotalPrice;
    private EditText edtAdults, edtChildren;
    private Button btnCheckAvailability, btnConfirmBooking;
    private LinearLayout layoutPriceInfo;
    private ImageView btnBack;

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
        handleIntentData();
        setupDatePickers();

        btnCheckAvailability.setOnClickListener(v -> checkAvailability());
        btnBack.setOnClickListener(v -> finish());

        btnConfirmBooking.setOnClickListener(v -> {
            if (diffDays <= 0) {
                Toast.makeText(this, "Vui lòng kiểm tra tính trạng phòng trước", Toast.LENGTH_SHORT).show();
                return;
            }
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

    private void initUi() {
        imgRoomDetail = findViewById(R.id.imgRoomDetail);
        txtName = findViewById(R.id.txtRoomNameDetail);
        txtPrice = findViewById(R.id.txtPriceDetail);
        txtDescription = findViewById(R.id.txtDescriptionDetail);
        txtCheckIn = findViewById(R.id.txtCheckIn);
        txtCheckOut = findViewById(R.id.txtCheckOut);
        edtAdults = findViewById(R.id.edtAdults);
        edtChildren = findViewById(R.id.edtChildren);
        btnCheckAvailability = findViewById(R.id.btnCheckAvailability);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        layoutPriceInfo = findViewById(R.id.layoutPriceInfo);
        txtPriceCalc = findViewById(R.id.txtPriceCalc);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        btnBack = findViewById(R.id.btnBack);

        calendarIn = Calendar.getInstance();
        calendarOut = Calendar.getInstance();
        calendarOut.add(Calendar.DAY_OF_MONTH, 1);

        txtCheckIn.setText(sdf.format(calendarIn.getTime()));
        txtCheckOut.setText(sdf.format(calendarOut.getTime()));
    }

    private void displayRoomData() {
        if (room != null) {
            txtName.setText(room.getName());
            txtPrice.setText(String.format(Locale.getDefault(), "%,.0f VNĐ/Đêm", room.getPrice()));
            txtDescription.setText(room.getDescription());

            if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(room.getImageUrl())
                        .placeholder(R.drawable.dlmix)
                        .error(R.drawable.dlmix)
                        .into(imgRoomDetail);
            }
        }
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
            checkAvailability();
        }
    }

    private void setupDatePickers() {
        txtCheckIn.setOnClickListener(v -> showDatePicker(true));
        txtCheckOut.setOnClickListener(v -> showDatePicker(false));
    }

    private void showDatePicker(boolean isCheckIn) {
        Calendar c = isCheckIn ? calendarIn : calendarOut;
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
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
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void checkAvailability() {
        if (!calendarOut.after(calendarIn)) {
            Toast.makeText(this, "Ngày trả phòng phải sau ngày nhận phòng", Toast.LENGTH_SHORT).show();
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

                    if (bIn != null && bOut != null && !"Cancelled".equals(status)) {
                        if (start < bOut && end > bIn) bookedCount++;
                    }
                }

                if (bookedCount < room.getTotalRooms()) {
                    showPriceCalculation();
                } else {
                    layoutPriceInfo.setVisibility(View.GONE);
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
        if (diffDays == 0) diffDays = 1;

        double total = diffDays * room.getPrice();
        txtPriceCalc.setText(String.format(Locale.getDefault(), "%,.0f VNĐ x %d đêm", room.getPrice(), diffDays));
        txtTotalPrice.setText(String.format(Locale.getDefault(), "Tổng tiền: %,.0f VNĐ", total));
        layoutPriceInfo.setVisibility(View.VISIBLE);
    }
}