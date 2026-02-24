package com.example.booking.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    public interface OnRoomActionListener {
        void onEdit(Room room);
        void onDelete(Room room);
    }

    private Context context;
    private List<Room> roomList;
    private boolean isAdmin;
    private OnRoomActionListener listener;

    public RoomAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
        this.isAdmin = false;
    }

    public RoomAdapter(Context context, List<Room> roomList, boolean isAdmin, OnRoomActionListener listener) {
        this.context = context;
        this.roomList = roomList;
        this.isAdmin = isAdmin;
        this.listener = listener;
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

        if (room.getImageUrl() == null || room.getImageUrl().isEmpty()) {
            holder.imgRoom.setImageResource(R.drawable.dlmix);
        } else {
            Glide.with(context)
                    .load(room.getImageUrl())
                    .placeholder(R.drawable.dlmix)
                    .error(R.drawable.dlmix)
                    .into(holder.imgRoom);
        }

        if (isAdmin) {
            holder.layoutAdminActions.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(room);
            });
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(room);
            });
        } else {
            holder.layoutAdminActions.setVisibility(View.GONE);
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
        LinearLayout layoutAdminActions;
        ImageButton btnEdit, btnDelete;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRoom = itemView.findViewById(R.id.imgRoom);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtRating = itemView.findViewById(R.id.txtRating);
            layoutAdminActions = itemView.findViewById(R.id.layoutAdminRoomActions);
            btnEdit = itemView.findViewById(R.id.btnEditRoom);
            btnDelete = itemView.findViewById(R.id.btnDeleteRoom);
        }
    }
}
