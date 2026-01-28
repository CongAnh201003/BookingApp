package com.example.booking;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    private List<Room> roomList = new ArrayList<>();
    private List<Room> filteredList = new ArrayList<>();

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Calendar calendarIn, calendarOut;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final String DATABASE_URL = "https://bookingapp-933ac-default-rtdb.firebaseio.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();
        initFirebase();
        setupSearchLogic();
        loadAllRoomsFromFirebase();

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

        // Khởi tạo Adapter với filteredList
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
        mDatabase = FirebaseDatabase.getInstance(DATABASE_URL).getReference();

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
            String guestsStr = edtMainGuests.getText().toString().trim();
            if (guestsStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số người", Toast.LENGTH_SHORT).show();
                return;
            }

            hideKeyboard();

            try {
                int guests = Integer.parseInt(guestsStr);
                performSearch(guests);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số người không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
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

    private void loadAllRoomsFromFirebase() {
        mDatabase.child("Rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Room room = data.getValue(Room.class);
                    if (room != null) roomList.add(room);
                }
                filteredList.clear();
                filteredList.addAll(roomList);
                adapter.notifyDataSetChanged();
                Log.d("DEBUG_DATA", "Đã tải " + roomList.size() + " phòng");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void performSearch(int totalGuests) {
        filteredList.clear();
        for (Room room : roomList) {
            // Kiểm tra sức chứa (nếu data firebase không có capacity thì nó sẽ là 0)
            int maxCapacity = room.getCapacityAdults() + room.getCapacityChildren();
            
            // Nếu bạn chưa nhập capacity vào firebase, hãy thử bỏ điều kiện if này để hiện tất cả
            if (maxCapacity >= totalGuests || totalGuests == 0) {
                filteredList.add(room);
            }
        }

        adapter.notifyDataSetChanged();
        rvRooms.scrollToPosition(0);

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy phòng phù hợp cho " + totalGuests + " người", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Tìm thấy " + filteredList.size() + " phòng", Toast.LENGTH_SHORT).show();
        }
        Log.d("DEBUG_SEARCH", "Đã hiển thị " + filteredList.size() + " phòng");
    }
}
