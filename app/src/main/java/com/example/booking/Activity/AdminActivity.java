package com.example.booking.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.booking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class AdminActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private TextView txtTotalRevenue;
    private Button btnManageRooms, btnManageBookings, btnViewStaffs, btnViewCustomers, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mDatabase = FirebaseDatabase.getInstance("https://bookingapp-933ac-default-rtdb.firebaseio.com/").getReference();

        initUi();
        calculateTotalRevenue();

        // 1. Quản lý khách sạn
        btnManageRooms.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, AdminRoomListActivity.class));
        });

        btnManageBookings.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, AdminBookingListActivity.class));
        });

        // 2. Quản lý nhân sự
        btnViewStaffs.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, UserListActivity.class);
            intent.putExtra("role", "Staff");
            startActivity(intent);
        });

        btnViewCustomers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, UserListActivity.class);
            intent.putExtra("role", "Customer");
            startActivity(intent);
        });

        // 3. Đăng xuất
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void initUi() {
        txtTotalRevenue = findViewById(R.id.txtTotalRevenue);
        btnManageRooms = findViewById(R.id.btnManageRooms);
        btnManageBookings = findViewById(R.id.btnManageBookings);
        btnViewStaffs = findViewById(R.id.btnViewStaffs);
        btnViewCustomers = findViewById(R.id.btnViewCustomers);
        btnLogout = findViewById(R.id.btnAdminLogout);
    }

    private void calculateTotalRevenue() {
        mDatabase.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double total = 0;
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    Double balance = userSnap.child("balance").getValue(Double.class);
                    if (balance != null) {
                        total += balance;
                    }
                }
                txtTotalRevenue.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", total));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
