package com.example.booking.Activity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        roleToFilter = getIntent().getStringExtra("role");
        mDatabase = FirebaseDatabase.getInstance("https://bookingapp-933ac-default-rtdb.firebaseio.com/").getReference();

        initUi();
        loadUsers();

        btnBack.setOnClickListener(v -> finish());
    }

    private void initUi() {
        rvUserList = findViewById(R.id.rvUserList);
        txtTitle = findViewById(R.id.txtUserListTitle);
        btnBack = findViewById(R.id.btnBackUserList);

        if ("Staff".equals(roleToFilter)) {
            txtTitle.setText("Danh sách nhân viên");
            adapter = new UserAdapter(this, userList, true);
        } else {
            txtTitle.setText("Danh sách khách hàng");
            adapter = new UserAdapter(this, userList, false);
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
                        userList.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
