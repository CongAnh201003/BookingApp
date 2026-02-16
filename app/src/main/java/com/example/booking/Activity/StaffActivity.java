package com.example.booking.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booking.Adapter.BookingAdapter;
import com.example.booking.Model.Booking;
import com.example.booking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StaffActivity extends AppCompatActivity {

    private RecyclerView rvStaffBookings;
    private BookingAdapter adapter;
    private List<Booking> bookingList = new ArrayList<>();
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);

        mDatabase = FirebaseDatabase.getInstance("https://bookingapp-933ac-default-rtdb.firebaseio.com/").getReference();

        rvStaffBookings = findViewById(R.id.rvStaffBookings);
        Button btnLogout = findViewById(R.id.btnStaffLogout);

        adapter = new BookingAdapter(this, bookingList, true); // true = staff mode
        rvStaffBookings.setLayoutManager(new LinearLayoutManager(this));
        rvStaffBookings.setAdapter(adapter);

        loadBookingsForStaff();

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(StaffActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void loadBookingsForStaff() {
        mDatabase.child("Bookings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Booking booking = data.getValue(Booking.class);
                    if (booking != null && "Pending".equals(booking.getStatus())) {
                        bookingList.add(booking);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
