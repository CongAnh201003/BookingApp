package com.example.booking.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.booking.Model.Booking;
import com.example.booking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class StaffReportFragment extends Fragment {

    private TextView txtTotalEarnings, txtBookingCount;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff_report, container, false);

        txtTotalEarnings = view.findViewById(R.id.txtStaffTotalEarnings);
        txtBookingCount = view.findViewById(R.id.txtStaffBookingCount);

        mDatabase = FirebaseDatabase.getInstance("https://bookingapp-933ac-default-rtdb.firebaseio.com/").getReference();
        loadReportData();

        return view;
    }

    private void loadReportData() {
        String staffId = FirebaseAuth.getInstance().getUid();
        if (staffId == null) return;

        // Thống kê dựa trên những booking mà Staff này đã duyệt (Confirmed và có staffId khớp)
        mDatabase.child("Bookings").orderByChild("staffId").equalTo(staffId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double total = 0;
                int count = 0;
                for (DataSnapshot data : snapshot.getChildren()) {
                    Booking booking = data.getValue(Booking.class);
                    if (booking != null && "Confirmed".equals(booking.getStatus())) {
                        total += booking.getTotalPrice();
                        count++;
                    }
                }
                txtTotalEarnings.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", total));
                txtBookingCount.setText(count + " đơn");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
