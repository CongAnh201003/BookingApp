package com.example.booking;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView txtUserName, tvMainCheckIn, tvMainCheckOut;
    private EditText edtMainGuests;
    private Button btnMainSearch;
    private RecyclerView rvRooms;
    private FloatingActionButton btnLogout;
    private ImageView btnHistory;

    private RoomAdapter adapter;
    private List<Room> roomList;
    private List<Room> filteredList;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    
    private Calendar calendarIn, calendarOut;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();
        initFirebase();
        setupSearchLogic();
        loadAllRooms();

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, BookingHistoryActivity.class));
        });
    }

    private void initUi() {
        txtUserName = findViewById(R.id.txtUserName);
        tvMainCheckIn = findViewById(R.id.tvMainCheckIn);
        tvMainCheckOut = findViewById(R.id.tvMainCheckOut);
        edtMainGuests = findViewById(R.id.edtMainGuests);
        btnMainSearch = findViewById(R.id.btnMainSearch);
        rvRooms = findViewById(R.id.rvRooms);
        btnLogout = findViewById(R.id.btnLogout);
        btnHistory = findViewById(R.id.btnHistory);

        roomList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new RoomAdapter(this, filteredList);
        rvRooms.setLayoutManager(new LinearLayoutManager(this));
        rvRooms.setAdapter(adapter);
        
        calendarIn = Calendar.getInstance();
        calendarOut = Calendar.getInstance();
        calendarOut.add(Calendar.DAY_OF_MONTH, 1);
        
        tvMainCheckIn.setText(sdf.format(calendarIn.getTime()));
        tvMainCheckOut.setText(sdf.format(calendarOut.getTime()));
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

    private void setupSearchLogic() {
        tvMainCheckIn.setOnClickListener(v -> showDatePicker(true));
        tvMainCheckOut.setOnClickListener(v -> showDatePicker(false));
        
        btnMainSearch.setOnClickListener(v -> {
            String guestsStr = edtMainGuests.getText().toString();
            if (guestsStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số khách", Toast.LENGTH_SHORT).show();
                return;
            }
            performSearch(Integer.parseInt(guestsStr));
        });
    }

    private void showDatePicker(boolean isCheckIn) {
        Calendar c = isCheckIn ? calendarIn : calendarOut;
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            if (isCheckIn) {
                calendarIn = selected;
                tvMainCheckIn.setText(sdf.format(selected.getTime()));
            } else {
                calendarOut = selected;
                tvMainCheckOut.setText(sdf.format(selected.getTime()));
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadAllRooms() {
        mDatabase.child("Rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Room room = data.getValue(Room.class);
                    if (room != null) roomList.add(room);
                }
                if (roomList.isEmpty()) {
                    addSampleData();
                } else {
                    // Mặc định hiện tất cả nếu chưa bấm Tìm kiếm
                    filteredList.clear();
                    filteredList.addAll(roomList);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void performSearch(int guests) {
        // Logic BƯỚC 2: Kiểm tra phòng trống
        // Ở đây ta lọc theo Sức chứa trước, sau đó RoomDetail sẽ check thực tế ngày bận
        filteredList.clear();
        for (Room room : roomList) {
            if ((room.getCapacityAdults() + room.getCapacityChildren()) >= guests) {
                filteredList.add(room);
            }
        }
        
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Không có phòng phù hợp với số khách này", Toast.LENGTH_SHORT).show();
        }
        
        // Truyền thông tin ngày vào Adapter để RoomDetailActivity nhận được
        adapter.setSearchCriteria(calendarIn.getTimeInMillis(), calendarOut.getTimeInMillis(), guests);
        adapter.notifyDataSetChanged();
    }

    private void addSampleData() {
        DatabaseReference roomRef = mDatabase.child("Rooms");
        Room r1 = new Room("1", "Phòng Deluxe City View", "35m2", 1, "View thành phố tuyệt đẹp...", 1200000, 4.8f, "", "Người lớn", 5, 2, 1);
        Room r2 = new Room("2", "Phòng Family Suite", "55m2", 2, "Dành cho gia đình 4 người...", 2500000, 4.9f, "", "Cả hai", 3, 2, 2);
        Room r3 = new Room("3", "Phòng Kids Zone", "40m2", 2, "Trang trí hoạt hình cho bé...", 1500000, 4.7f, "", "Trẻ em", 2, 1, 2);
        roomRef.child("1").setValue(r1);
        roomRef.child("2").setValue(r2);
        roomRef.child("3").setValue(r3);
    }
}
