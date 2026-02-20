package com.example.booking.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class StaffNewRequestsFragment extends Fragment {

    private RecyclerView rvNewRequests;
    private TextView txtNoRequests;
    private BookingAdapter adapter;
    private List<Booking> bookingList = new ArrayList<>();
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff_new_requests, container, false);

        rvNewRequests = view.findViewById(R.id.rvStaffNewRequests);
        txtNoRequests = view.findViewById(R.id.txtNoNewRequests);

        adapter = new BookingAdapter(getActivity(), bookingList, true);
        rvNewRequests.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvNewRequests.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance("https://bookingapp-933ac-default-rtdb.firebaseio.com/").getReference("Bookings");
        loadNewRequests();

        return view;
    }

    private void loadNewRequests() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Booking booking = data.getValue(Booking.class);
                    // Hiển thị các đơn đã trả tiền nhưng chưa được duyệt
                    if (booking != null && "Paid".equals(booking.getStatus())) {
                        bookingList.add(booking);
                    }
                }

                if (bookingList.isEmpty()) {
                    txtNoRequests.setVisibility(View.VISIBLE);
                    rvNewRequests.setVisibility(View.GONE);
                } else {
                    txtNoRequests.setVisibility(View.GONE);
                    rvNewRequests.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
