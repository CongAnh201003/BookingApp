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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StaffMyHistoryFragment extends Fragment {

    private RecyclerView rvHistory;
    private TextView txtNoHistory;
    private BookingAdapter adapter;
    private List<Booking> bookingList = new ArrayList<>();
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff_my_history, container, false);

        rvHistory = view.findViewById(R.id.rvStaffMyHistory);
        txtNoHistory = view.findViewById(R.id.txtNoHistoryStaff);

        adapter = new BookingAdapter(getActivity(), bookingList, true);
        rvHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvHistory.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance("https://bookingapp-933ac-default-rtdb.firebaseio.com/").getReference("Bookings");
        loadMyHistory();

        return view;
    }

    private void loadMyHistory() {
        String staffId = FirebaseAuth.getInstance().getUid();
        if (staffId == null) return;

        mDatabase.orderByChild("staffId").equalTo(staffId).addValueEventListener(new ValueEventListener() {
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
                    rvHistory.setVisibility(View.GONE);
                } else {
                    txtNoHistory.setVisibility(View.GONE);
                    rvHistory.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
