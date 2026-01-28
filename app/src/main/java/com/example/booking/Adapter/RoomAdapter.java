package com.example.booking.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import java.util.Locale;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private Context context;
    private List<Room> roomList;

    public RoomAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
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

        Log.d("ADAPTER_DEBUG", "Binding room: " + room.getName());

        holder.txtName.setText(room.getName());
        holder.txtDescription.setText(room.getDescription());
        holder.txtRating.setText(String.valueOf(room.getStarRating()));
        holder.txtPrice.setText(String.format(Locale.getDefault(), "%,.0f VNĐ/đêm", room.getPrice()));

        // Xử lý ảnh: Nếu link trống, hiện ảnh mặc định dlmix
        if (room.getImageUrl() == null || room.getImageUrl().isEmpty()) {
            holder.imgRoom.setImageResource(R.drawable.dlmix);
        } else {
            Glide.with(context)
                    .load(room.getImageUrl())
                    .placeholder(R.drawable.dlmix)
                    .error(R.drawable.dlmix)
                    .into(holder.imgRoom);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RoomDetailActivity.class);
            intent.putExtra("room_data", room);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return roomList != null ? roomList.size() : 0;
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRoom;
        TextView txtName, txtPrice, txtDescription, txtRating;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRoom = itemView.findViewById(R.id.imgRoom);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtRating = itemView.findViewById(R.id.txtRating);
        }
    }
}
