package com.example.booking.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booking.Model.User;
import com.example.booking.R;

import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;
    private boolean isStaffList;

    public UserAdapter(Context context, List<User> userList, boolean isStaffList) {
        this.context = context;
        this.userList = userList;
        this.isStaffList = isStaffList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.txtUserName.setText(user.getFullName());
        holder.txtUserEmail.setText(user.getEmail());

        if (isStaffList) {
            holder.txtUserBalance.setVisibility(View.VISIBLE);
            holder.txtUserBalance.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", user.getBalance()));
        } else {
            holder.txtUserBalance.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserName, txtUserEmail, txtUserBalance;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserNameItem);
            txtUserEmail = itemView.findViewById(R.id.txtUserEmailItem);
            txtUserBalance = itemView.findViewById(R.id.txtUserBalanceItem);
        }
    }
}
