package com.example.booking;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booking.Activity.BookingHistoryActivity;
import com.example.booking.Activity.LoginActivity;
import com.example.booking.Adapter.RoomAdapter;
import com.example.booking.Model.Room;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView txtUserName;
    private EditText edtSearch;
    private RecyclerView rvRooms;
    private FloatingActionButton btnLogout;
    private ImageView btnHistory;

    private RoomAdapter adapter;
    private List<Room> roomList;
    private List<Room> filteredList;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();
        initFirebase();
        loadRooms();

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, BookingHistoryActivity.class));
        });

        setupSearch();
    }

    private void initUi() {
        txtUserName = findViewById(R.id.txtUserName);
        edtSearch = findViewById(R.id.edtSearch);
        rvRooms = findViewById(R.id.rvRooms);
        btnLogout = findViewById(R.id.btnLogout);
        btnHistory = findViewById(R.id.btnHistory);

        roomList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new RoomAdapter(this, filteredList);
        rvRooms.setLayoutManager(new LinearLayoutManager(this));
        rvRooms.setAdapter(adapter);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            mDatabase.child("Users").child(userId).child("fullName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        txtUserName.setText(snapshot.getValue(String.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    private void loadRooms() {
        mDatabase.child("Rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Room room = data.getValue(Room.class);
                    if (room != null) {
                        roomList.add(room);
                    }
                }
                
                if (roomList.isEmpty()) {
                    addSampleData();
                } else {
                    filter("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addSampleData() {
        DatabaseReference roomRef = mDatabase.child("Rooms");
        
        Room r1 = new Room("1", "Phòng Deluxe Người Lớn", "35m2", 1, "Phòng sang trọng dành cho 2 người lớn với đầy đủ tiện nghi.", 1200000, 4.8f, "", "Người lớn");
        Room r2 = new Room("2", "Phòng Kid Suite", "40m2", 2, "Thiết kế vui nhộn dành riêng cho trẻ em, an toàn và sáng tạo.", 900000, 4.5f, "", "Trẻ em");
        Room r3 = new Room("3", "Phòng Family Happy", "60m2", 3, "Không gian rộng rãi cho cả gia đình, có khu vực riêng cho bé.", 2500000, 5.0f, "", "Cả hai");

        roomRef.child("1").setValue(r1);
        roomRef.child("2").setValue(r2);
        roomRef.child("3").setValue(r3);
    }

    private void setupSearch() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(roomList);
        } else {
            for (Room room : roomList) {
                if (room.getName().toLowerCase().contains(text.toLowerCase()) || 
                    room.getCategory().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(room);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
