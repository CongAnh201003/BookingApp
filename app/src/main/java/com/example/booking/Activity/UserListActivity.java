package com.example.booking.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booking.Adapter.UserAdapter;
import com.example.booking.Model.User;
import com.example.booking.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private RecyclerView rvUserList;
    private UserAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private DatabaseReference mDatabase;
    private String roleToFilter;
    private TextView txtTitle;
    private ImageButton btnBack;
    private Button btnAddUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        roleToFilter = getIntent().getStringExtra("role");
        mDatabase = FirebaseDatabase.getInstance("https://bookingapp-933ac-default-rtdb.firebaseio.com/").getReference();

        initUi();
        loadUsers();

        btnBack.setOnClickListener(v -> finish());
        
        btnAddUser.setOnClickListener(v -> {
            startActivity(new Intent(UserListActivity.this, AddStaffActivity.class));
        });
    }

    private void initUi() {
        rvUserList = findViewById(R.id.rvUserList);
        txtTitle = findViewById(R.id.txtUserListTitle);
        btnBack = findViewById(R.id.btnBackUserList);
        btnAddUser = findViewById(R.id.btnAddUserInList);

        if ("Staff".equals(roleToFilter)) {
            txtTitle.setText("Danh sách nhân viên");
            btnAddUser.setVisibility(View.VISIBLE);
            
            adapter = new UserAdapter(this, userList, true, new UserAdapter.OnUserActionListener() {
                @Override
                public void onEdit(User user) {
                    // Khi nhấn sửa nhân viên
                    Intent intent = new Intent(UserListActivity.this, AddStaffActivity.class);
                    intent.putExtra("user_data", user); // Sẽ hết lỗi sau khi thực hiện Bước 1
                    startActivity(intent);
                }

                @Override
                public void onDelete(User user) {
                    confirmDeleteUser(user);
                }
            });
        } else {
            txtTitle.setText("Danh sách khách hàng");
            btnAddUser.setVisibility(View.GONE);
            adapter = new UserAdapter(this, userList, false, new UserAdapter.OnUserActionListener() {
                @Override
                public void onEdit(User user) {
                    // Chỉnh sửa khách hàng nếu cần
                    Intent intent = new Intent(UserListActivity.this, AddStaffActivity.class);
                    intent.putExtra("user_data", user);
                    startActivity(intent);
                }
                @Override
                public void onDelete(User user) {
                    confirmDeleteUser(user);
                }
            });
        }

        rvUserList.setLayoutManager(new LinearLayoutManager(this));
        rvUserList.setAdapter(adapter);
    }

    private void loadUsers() {
        mDatabase.child("Users").orderByChild("role").equalTo(roleToFilter).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    if (user != null) {
                        user.setUsername(data.getKey()); // Lưu ID (UID) của Firebase vào username để xóa/sửa
                        userList.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void confirmDeleteUser(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa người dùng")
                .setMessage("Bạn có chắc chắn muốn xóa " + user.getFullName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    mDatabase.child("Users").child(user.getUsername()).removeValue()
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã xóa thành công", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
