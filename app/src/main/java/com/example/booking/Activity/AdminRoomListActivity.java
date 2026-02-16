package com.example.booking.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booking.Adapter.RoomAdapter;
import com.example.booking.Model.Room;
import com.example.booking.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminRoomListActivity extends AppCompatActivity {

    private RecyclerView rvRoomList;
    private RoomAdapter adapter;
    private List<Room> roomList = new ArrayList<>();
    private DatabaseReference mDatabase;
    private Button btnAddRoom;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        mDatabase = FirebaseDatabase.getInstance("https://bookingapp-933ac-default-rtdb.firebaseio.com/").getReference();

        rvRoomList = findViewById(R.id.rvAdminRoomList);
        btnAddRoom = findViewById(R.id.btnOpenAddRoomDialog);
        btnBack = findViewById(R.id.btnBackRoomList);

        adapter = new RoomAdapter(this, roomList);
        rvRoomList.setLayoutManager(new LinearLayoutManager(this));
        rvRoomList.setAdapter(adapter);

        loadRooms();

        btnBack.setOnClickListener(v -> finish());
        btnAddRoom.setOnClickListener(v -> showAddRoomDialog());
    }

    private void loadRooms() {
        mDatabase.child("Rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Room room = data.getValue(Room.class);
                    if (room != null) roomList.add(room);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showAddRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_room, null);
        builder.setView(view);

        EditText edtName = view.findViewById(R.id.edtRoomNameAdd);
        EditText edtPrice = view.findViewById(R.id.edtRoomPriceAdd);
        EditText edtDesc = view.findViewById(R.id.edtRoomDescAdd);
        EditText edtAdults = view.findViewById(R.id.edtRoomAdultsAdd);
        EditText edtChildren = view.findViewById(R.id.edtRoomChildrenAdd);
        Button btnSave = view.findViewById(R.id.btnSaveRoom);

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString();
            String priceStr = edtPrice.getText().toString();
            String adultsStr = edtAdults.getText().toString();
            String childrenStr = edtChildren.getText().toString();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr)) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            String roomId = mDatabase.child("Rooms").push().getKey();
            Room newRoom = new Room(
                    roomId, name, "30m2", 1, edtDesc.getText().toString(),
                    Double.parseDouble(priceStr), 5.0f, "", "Deluxe",
                    10, Integer.parseInt(adultsStr), Integer.parseInt(childrenStr)
            );

            if (roomId != null) {
                mDatabase.child("Rooms").child(roomId).setValue(newRoom)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Thêm phòng thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
            }
        });

        dialog.show();
    }
}
