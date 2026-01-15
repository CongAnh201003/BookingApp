package com.example.booking.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booking.Model.Hotel;
import com.example.booking.R;

import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {

    private Context context;
    private List<Hotel> hotelList;

    public HotelAdapter(Context context, List<Hotel> hotelList) {
        this.context = context;
        this.hotelList = hotelList;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hotel, parent, false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = hotelList.get(position);
        holder.txtName.setText(hotel.getName());
        holder.txtAddress.setText(hotel.getAddress());
        holder.txtPrice.setText(String.format("%,.0f VNĐ / đêm", hotel.getPrice()));
        holder.txtRating.setText(String.valueOf(hotel.getRating()));
        
        // For now, we use a placeholder image if needed, or the one set in XML.
        // If using Glide: Glide.with(context).load(hotel.getImageUrl()).into(holder.imgHotel);
    }

    @Override
    public int getItemCount() {
        return hotelList.size();
    }

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        ImageView imgHotel;
        TextView txtName, txtAddress, txtPrice, txtRating;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            imgHotel = itemView.findViewById(R.id.imgHotel);
            txtName = itemView.findViewById(R.id.txtHotelName);
            txtAddress = itemView.findViewById(R.id.txtHotelAddress);
            txtPrice = itemView.findViewById(R.id.txtHotelPrice);
            txtRating = itemView.findViewById(R.id.txtHotelRating);
        }
    }
}
