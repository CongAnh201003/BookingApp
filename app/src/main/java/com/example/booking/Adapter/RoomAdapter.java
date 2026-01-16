package com.example.booking.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.booking.Activity.RoomDetailActivity;
import com.example.booking.Model.Room;
import com.example.booking.R;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private Context context;
    private List<Room> roomList;
    
    private long checkInTime, checkOutTime;
    private int guests;

    public RoomAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
    }
    
    public void setSearchCriteria(long checkIn, long checkOut, int guests) {
        this.checkInTime = checkIn;
        this.checkOutTime = checkOut;
        this.guests = guests;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.txtName.setText(room.getName());
        holder.txtDetails.setText("Diện tích: " + room.getArea() + " | Sức chứa: " + (room.getCapacityAdults() + room.getCapacityChildren()) + " người");
        holder.txtPrice.setText(String.format("%,.0f VNĐ", room.getPrice()));
        holder.txtCategory.setText(room.getCategory());
        
        Glide.with(context)
                .load(room.getImageUrl())
                .placeholder(R.drawable.dlmix)
                .into(holder.imgRoom);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RoomDetailActivity.class);
            intent.putExtra("room_data", room);
            // Truyền tiếp tiêu chí tìm kiếm sang trang chi tiết
            intent.putExtra("checkIn", checkInTime);
            intent.putExtra("checkOut", checkOutTime);
            intent.putExtra("guests", guests);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRoom;
        TextView txtName, txtDetails, txtPrice, txtCategory;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRoom = itemView.findViewById(R.id.imgRoom);
            txtName = itemView.findViewById(R.id.txtRoomName);
            txtDetails = itemView.findViewById(R.id.txtDetails);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtCategory = itemView.findViewById(R.id.txtCategory);
        }
    }
}
