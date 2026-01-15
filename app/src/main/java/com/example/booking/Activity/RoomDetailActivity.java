package com.example.booking.Activity;

import android.app.DatePickerDialog;
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
import com.example.booking.Model.Booking;
import com.example.booking.Model.Room;
import com.example.booking.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
        setupDatePickers();

        btnCheckAvailability.setOnClickListener(v -> checkAvailability());
        btnConfirmBooking.setOnClickListener(v -> createPendingBooking());
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
            // Reset check state if dates change
            layoutPriceInfo.setVisibility(View.GONE);
            cardBookingAction.setVisibility(View.GONE);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void checkAvailability() {
        // 1. Validate input
        if (calendarOut.before(calendarIn) || calendarOut.equals(calendarIn)) {
            Toast.makeText(this, "Ngày trả phòng phải sau ngày nhận phòng", Toast.LENGTH_SHORT).show();
            return;
        }

        int adults = edtAdults.getText().toString().isEmpty() ? 0 : Integer.parseInt(edtAdults.getText().toString());
        int children = edtChildren.getText().toString().isEmpty() ? 0 : Integer.parseInt(edtChildren.getText().toString());

        if (adults > room.getCapacityAdults() || children > room.getCapacityChildren()) {
            Toast.makeText(this, "Số lượng khách vượt quá sức chứa của phòng", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Logic Check phòng trống thực tế trên Firebase
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings");
        bookingsRef.orderByChild("roomId").equalTo(room.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int bookedCount = 0;
                long checkInTime = calendarIn.getTimeInMillis();
                long checkOutTime = calendarOut.getTimeInMillis();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Long bIn = data.child("checkInDate").getValue(Long.class);
                    Long bOut = data.child("checkOutDate").getValue(Long.class);
                    String status = data.child("status").getValue(String.class);

                    if (bIn != null && bOut != null && !"Cancelled".equals(status)) {
                        // Kiểm tra trùng lặp thời gian
                        if (checkInTime < bOut && checkOutTime > bIn) {
                            bookedCount++;
                        }
                    }
                }

                if (bookedCount < room.getTotalRooms()) {
                    showPriceCalculation();
                } else {
                    Toast.makeText(RoomDetailActivity.this, "Rất tiếc, loại phòng này đã hết trong khoảng thời gian này", Toast.LENGTH_LONG).show();
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

    private void createPendingBooking() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("Bookings");
        String bookingId = bookingRef.push().getKey();

        // 4. Tạo Booking tạm (Pending/Hold)
        java.util.Map<String, Object> bookingData = new java.util.HashMap<>();
        bookingData.put("bookingId", bookingId);
        bookingData.put("userId", userId);
        bookingData.put("roomId", room.getId());
        bookingData.put("roomName", room.getName());
        bookingData.put("checkInDate", calendarIn.getTimeInMillis());
        bookingData.put("checkOutDate", calendarOut.getTimeInMillis());
        bookingData.put("adults", Integer.parseInt(edtAdults.getText().toString()));
        bookingData.put("children", Integer.parseInt(edtChildren.getText().toString()));
        bookingData.put("totalPrice", diffDays * room.getPrice());
        bookingData.put("status", "Pending"); // Trạng thái Tạm giữ
        bookingData.put("timestamp", System.currentTimeMillis());

        if (bookingId != null) {
            bookingRef.child(bookingId).setValue(bookingData).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Đã giữ chỗ tạm thời! Vui lòng thanh toán trong 15 phút.", Toast.LENGTH_LONG).show();
                // Luồng tiếp theo sẽ là chuyển đến trang Thanh toán (Payment)
                finish();
            });
        }
    }
}
