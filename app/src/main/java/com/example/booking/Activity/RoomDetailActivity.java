package com.example.booking.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.booking.Model.Room;
import com.example.booking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RoomDetailActivity extends AppCompatActivity {

    private ImageView imgRoomDetail;
    private TextView txtName, txtCategory, txtInfo, txtDescription, txtPrice;
    private Button btnBooking;
    private Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        room = (Room) getIntent().getSerializableExtra("room_data");

        initUi();
        displayRoomData();

        btnBooking.setOnClickListener(v -> handleBooking());
    }

    private void initUi() {
        imgRoomDetail = findViewById(R.id.imgRoomDetail);
        txtName = findViewById(R.id.txtRoomNameDetail);
        txtCategory = findViewById(R.id.txtCategoryDetail);
        txtInfo = findViewById(R.id.txtRoomInfo);
        txtDescription = findViewById(R.id.txtDescriptionDetail);
        txtPrice = findViewById(R.id.txtPriceDetail);
        btnBooking = findViewById(R.id.btnBooking);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void displayRoomData() {
        if (room != null) {
            txtName.setText(room.getName());
            txtCategory.setText("Loại: " + room.getCategory());
            txtInfo.setText("• Diện tích: " + room.getArea() + "\n• Số giường: " + room.getBedCount() + " Giường\n• Hạng sao: " + room.getStarRating() + " sao");
            txtDescription.setText(room.getDescription());
            txtPrice.setText(String.format("%,.0f VNĐ", room.getPrice()));

            Glide.with(this)
                    .load(room.getImageUrl())
                    .placeholder(R.drawable.dlmix)
                    .into(imgRoomDetail);
        }
    }

    private void handleBooking() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để đặt phòng", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("Bookings");
        String bookingId = bookingRef.push().getKey();

        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("bookingId", bookingId);
        bookingData.put("userId", userId);
        bookingData.put("roomId", room.getId());
        bookingData.put("roomName", room.getName());
        bookingData.put("price", room.getPrice());
        bookingData.put("timestamp", System.currentTimeMillis());
        bookingData.put("status", "Pending");

        if (bookingId != null) {
            bookingRef.child(bookingId).setValue(bookingData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(RoomDetailActivity.this, "Đặt phòng thành công! Chúng tôi sẽ liên hệ bạn sớm.", Toast.LENGTH_LONG).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(RoomDetailActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
