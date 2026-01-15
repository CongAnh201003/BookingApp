package com.example.booking.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booking.Model.Booking;
import com.example.booking.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private Context context;
    private List<Booking> bookingList;

    public BookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
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
        holder.txtPrice.setText(String.format("%,.0f VNĐ", booking.getPrice()));
        holder.txtStatus.setText(booking.getStatus());

        // Format date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.txtDate.setText("Ngày đặt: " + sdf.format(new Date(booking.getTimestamp())));

        // Color status
        if ("Pending".equals(booking.getStatus())) {
            holder.txtStatus.setTextColor(Color.parseColor("#EF6C00"));
        } else if ("Confirmed".equals(booking.getStatus())) {
            holder.txtStatus.setTextColor(Color.parseColor("#2E7D32"));
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView txtRoomName, txtStatus, txtDate, txtPrice;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRoomName = itemView.findViewById(R.id.txtBookingRoomName);
            txtStatus = itemView.findViewById(R.id.txtBookingStatus);
            txtDate = itemView.findViewById(R.id.txtBookingDate);
            txtPrice = itemView.findViewById(R.id.txtBookingPrice);
        }
    }
}
