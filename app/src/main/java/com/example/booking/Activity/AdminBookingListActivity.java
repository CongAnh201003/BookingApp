package com.example.booking.Activity;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booking.Adapter.BookingAdapter;
import com.example.booking.Model.Booking;
import com.example.booking.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminBookingListActivity extends AppCompatActivity {

    private RecyclerView rvAdminBookingList;
    private BookingAdapter adapter;
    private List<Booking> bookingList = new ArrayList<>();
    private DatabaseReference mDatabase;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_booking_list);

        mDatabase = FirebaseDatabase.getInstance("https://bookingapp-933ac-default-rtdb.firebaseio.com/").getReference();

        rvAdminBookingList = findViewById(R.id.rvAdminBookingList);
        btnBack = findViewById(R.id.btnBackAdminBooking);

        adapter = new BookingAdapter(this, bookingList, false); // Admin chỉ xem, không duyệt như Staff (hoặc bạn có thể để true nếu muốn Admin cũng duyệt được)
        rvAdminBookingList.setLayoutManager(new LinearLayoutManager(this));
        rvAdminBookingList.setAdapter(adapter);

        loadAllBookings();

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadAllBookings() {
        mDatabase.child("Bookings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Booking booking = data.getValue(Booking.class);
                    if (booking != null) bookingList.add(booking);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
