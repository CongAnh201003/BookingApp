package com.example.booking.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

public class BookingHistoryActivity extends AppCompatActivity {

    private RecyclerView rvBookingHistory;
    private TextView txtNoHistory;
    private ImageView btnBack;
    private BookingAdapter adapter;
    private List<Booking> bookingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        initUi();
        loadBookingHistory();

        btnBack.setOnClickListener(v -> finish());
    }

    private void initUi() {
        rvBookingHistory = findViewById(R.id.rvBookingHistory);
        txtNoHistory = findViewById(R.id.txtNoHistory);
        btnBack = findViewById(R.id.btnBackHistory);

        bookingList = new ArrayList<>();
        adapter = new BookingAdapter(this, bookingList);
        rvBookingHistory.setLayoutManager(new LinearLayoutManager(this));
        rvBookingHistory.setAdapter(adapter);
    }

    private void loadBookingHistory() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("Bookings");
        // Lọc các booking có userId khớp với người dùng hiện tại
        bookingRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Booking booking = data.getValue(Booking.class);
                    if (booking != null) {
                        bookingList.add(booking);
                    }
                }

                if (bookingList.isEmpty()) {
                    txtNoHistory.setVisibility(View.VISIBLE);
                    rvBookingHistory.setVisibility(View.GONE);
                } else {
                    txtNoHistory.setVisibility(View.GONE);
                    rvBookingHistory.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
