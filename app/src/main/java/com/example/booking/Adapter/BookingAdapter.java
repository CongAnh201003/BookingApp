package com.example.booking.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booking.Model.Booking;
import com.example.booking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private Context context;
    private List<Booking> bookingList;
    private boolean isStaff;
    private DatabaseReference mDatabase;
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public BookingAdapter(Context context, List<Booking> bookingList, boolean isStaff) {
        this.context = context;
        this.bookingList = bookingList;
        this.isStaff = isStaff;
        this.mDatabase = FirebaseDatabase.getInstance("https://bookingapp-933ac-default-rtdb.firebaseio.com/").getReference();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.txtRoomName.setText(booking.getRoomName());
        holder.txtPrice.setText(String.format(Locale.getDefault(), "Tổng tiền: %,.0f VNĐ", booking.getTotalPrice()));
        
        String status = booking.getStatus();
        holder.txtStatus.setText(status);

        holder.txtDate.setText("Ngày đặt: " + dateTimeFormat.format(new Date(booking.getTimestamp())));
        
        String stayTime = "Thời gian ở: " + dateFormat.format(new Date(booking.getCheckInDate())) + 
                          " - " + dateFormat.format(new Date(booking.getCheckOutDate()));
        holder.txtStayTime.setText(stayTime);

        // Reset visibility
        holder.layoutActions.setVisibility(View.GONE);

        if ("Pending".equals(status)) {
            holder.txtStatus.setTextColor(Color.parseColor("#EF6C00")); 
            holder.txtStatus.setText("Chờ thanh toán");
        } else if ("Paid".equals(status)) {
            holder.txtStatus.setTextColor(Color.BLUE);
            holder.txtStatus.setText("Đã thanh toán - Chờ duyệt");
            if (isStaff) {
                holder.layoutActions.setVisibility(View.VISIBLE);
            }
        } else if ("Confirmed".equals(status)) {
            holder.txtStatus.setTextColor(Color.parseColor("#2E7D32")); 
            holder.txtStatus.setText("Đã xác nhận");
        } else {
            holder.txtStatus.setTextColor(Color.RED);
            holder.txtStatus.setText("Đã hủy");
        }

        holder.btnApprove.setOnClickListener(v -> approveBooking(booking));
        holder.btnCancel.setOnClickListener(v -> updateBookingStatus(booking.getBookingId(), "Cancelled"));
    }

    private void approveBooking(Booking booking) {
        String staffId = FirebaseAuth.getInstance().getUid();
        if (staffId == null) return;

        // Cập nhật trạng thái và gán staffId
        mDatabase.child("Bookings").child(booking.getBookingId()).child("status").setValue("Confirmed");
        mDatabase.child("Bookings").child(booking.getBookingId()).child("staffId").setValue(staffId)
                .addOnSuccessListener(aVoid -> {
                    // Cộng tiền vào balance của Staff
                    mDatabase.child("Users").child(staffId).child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            double currentBalance = 0;
                            if (snapshot.exists() && snapshot.getValue() != null) {
                                currentBalance = snapshot.getValue(Double.class);
                            }
                            mDatabase.child("Users").child(staffId).child("balance").setValue(currentBalance + booking.getTotalPrice());
                            Toast.makeText(context, "Duyệt thành công!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                });
    }

    private void updateBookingStatus(String bookingId, String status) {
        mDatabase.child("Bookings").child(bookingId).child("status").setValue(status)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Đã cập nhật trạng thái đơn", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView txtRoomName, txtStatus, txtDate, txtPrice, txtStayTime;
        LinearLayout layoutActions;
        Button btnApprove, btnCancel;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRoomName = itemView.findViewById(R.id.txtBookingRoomName);
            txtStatus = itemView.findViewById(R.id.txtBookingStatus);
            txtDate = itemView.findViewById(R.id.txtBookingDate);
            txtPrice = itemView.findViewById(R.id.txtBookingPrice);
            txtStayTime = itemView.findViewById(R.id.txtBookingStayTime);
            layoutActions = itemView.findViewById(R.id.layoutStaffActions);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnCancel = itemView.findViewById(R.id.btnCancelBooking);
        }
    }
}
