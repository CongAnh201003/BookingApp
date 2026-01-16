package com.example.booking.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.booking.Model.Room;
import com.example.booking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ConfirmBookingActivity extends AppCompatActivity {

    private TextView txtRoomName, txtStayTime, txtGuestCount, txtFinalPrice;
    private EditText edtGuestName, edtGuestPhone, edtSpecialRequest;
    private CheckBox cbConfirmTerms;
    private Button btnNextToPayment;
    private ImageView btnBack;

    private Room room;
    private long checkIn, checkOut;
    private int adults, children;
    private double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);

        getDataFromIntent();
        initUi();
        displaySummary();

        btnNextToPayment.setOnClickListener(v -> handlePendingBooking());
        btnBack.setOnClickListener(v -> finish());
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        room = (Room) intent.getSerializableExtra("room_data");
        checkIn = intent.getLongExtra("checkIn", 0);
        checkOut = intent.getLongExtra("checkOut", 0);
        adults = intent.getIntExtra("adults", 0);
        children = intent.getIntExtra("children", 0);
        totalPrice = intent.getDoubleExtra("totalPrice", 0);
    }

    private void initUi() {
        txtRoomName = findViewById(R.id.txtRoomNameConfirm);
        txtStayTime = findViewById(R.id.txtStayTime);
        txtGuestCount = findViewById(R.id.txtGuestCount);
        txtFinalPrice = findViewById(R.id.txtFinalPrice);
        edtGuestName = findViewById(R.id.edtGuestName);
        edtGuestPhone = findViewById(R.id.edtGuestPhone);
        edtSpecialRequest = findViewById(R.id.edtSpecialRequest);
        cbConfirmTerms = findViewById(R.id.cbConfirmTerms);
        btnNextToPayment = findViewById(R.id.btnNextToPayment);
        btnBack = findViewById(R.id.btnBackConfirm);
    }

    private void displaySummary() {
        if (room != null) {
            txtRoomName.setText(room.getName());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String stayStr = sdf.format(new Date(checkIn)) + " - " + sdf.format(new Date(checkOut));
            txtStayTime.setText(stayStr);
            txtGuestCount.setText(adults + " Người lớn, " + children + " Trẻ em");
            txtFinalPrice.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", totalPrice));
        }
    }

    private void handlePendingBooking() {
        String name = edtGuestName.getText().toString().trim();
        String phone = edtGuestPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Vui lòng nhập thông tin người ở", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbConfirmTerms.isChecked()) {
            Toast.makeText(this, "Vui lòng đồng ý với điều khoản", Toast.LENGTH_SHORT).show();
            return;
        }

        // BƯỚC 5: Tạo Booking tạm (PENDING)
        String userId = FirebaseAuth.getInstance().getUid();
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("Bookings");
        String bookingId = bookingRef.push().getKey();

        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("bookingId", bookingId);
        bookingData.put("userId", userId);
        bookingData.put("roomId", room.getId());
        bookingData.put("roomName", room.getName());
        bookingData.put("guestName", name);
        bookingData.put("guestPhone", phone);
        bookingData.put("specialRequest", edtSpecialRequest.getText().toString());
        bookingData.put("checkInDate", checkIn);
        bookingData.put("checkOutDate", checkOut);
        bookingData.put("adults", adults);
        bookingData.put("children", children);
        bookingData.put("totalPrice", totalPrice);
        bookingData.put("status", "Pending"); // BƯỚC 5
        bookingData.put("timestamp", System.currentTimeMillis());
        bookingData.put("expiryTime", System.currentTimeMillis() + (15 * 60 * 1000)); // Hết hạn sau 15 phút

        if (bookingId != null) {
            bookingRef.child(bookingId).setValue(bookingData).addOnSuccessListener(aVoid -> {
                // Chuyển sang BƯỚC 6: Thanh toán
                Intent intent = new Intent(ConfirmBookingActivity.this, PaymentActivity.class);
                intent.putExtra("bookingId", bookingId);
                intent.putExtra("totalPrice", totalPrice);
                startActivity(intent);
                finish();
            }).addOnFailureListener(e -> Toast.makeText(this, "Lỗi tạo đơn: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
