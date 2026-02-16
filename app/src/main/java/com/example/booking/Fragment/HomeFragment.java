package com.example.booking.Fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booking.Activity.ProfileActivity;
import com.example.booking.Adapter.RoomAdapter;
import com.example.booking.Model.Room;
import com.example.booking.R;
import com.google.android.material.imageview.ShapeableImageView;
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

public class HomeFragment extends Fragment {

    private TextView txtUserName, tvMainCheckIn, tvMainCheckOut;
    private EditText edtMainGuests;
    private Button btnMainSearch;
    private RecyclerView rvRooms;
    private ShapeableImageView btnProfile;

    private RoomAdapter adapter;
    private List<Room> roomList = new ArrayList<>();
    private List<Room> filteredList = new ArrayList<>();

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Calendar calendarIn, calendarOut;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final String DATABASE_URL = "https://bookingapp-933ac-default-rtdb.firebaseio.com/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initUi(view);
        initFirebase();
        setupSearchLogic();
        loadAllRoomsFromFirebase();

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ProfileActivity.class));
        });

        return view;
    }

    private void initUi(View view) {
        txtUserName = view.findViewById(R.id.txtUserName);
        tvMainCheckIn = view.findViewById(R.id.tvMainCheckIn);
        tvMainCheckOut = view.findViewById(R.id.tvMainCheckOut);
        edtMainGuests = view.findViewById(R.id.edtMainGuests);
        btnMainSearch = view.findViewById(R.id.btnMainSearch);
        rvRooms = view.findViewById(R.id.rvRooms);
        btnProfile = view.findViewById(R.id.btnProfile);

        adapter = new RoomAdapter(getActivity(), filteredList);
        rvRooms.setLayoutManager(new LinearLayoutManager(getActivity()));
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
            mDatabase.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String fullName = snapshot.child("fullName").getValue(String.class);
                        txtUserName.setText(fullName != null ? fullName : "Khách hàng");
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
                Toast.makeText(getActivity(), "Vui lòng nhập số người", Toast.LENGTH_SHORT).show();
                return;
            }

            hideKeyboard();

            try {
                int guests = Integer.parseInt(guestsStr);
                performSearch(guests);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Số người không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }

    private void showDatePicker(boolean isCheckIn) {
        Calendar c = isCheckIn ? calendarIn : calendarOut;
        new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void performSearch(int totalGuests) {
        filteredList.clear();
        for (Room room : roomList) {
            int maxCapacity = room.getCapacityAdults() + room.getCapacityChildren();
            if (maxCapacity >= totalGuests || totalGuests == 0) {
                filteredList.add(room);
            }
        }
        adapter.notifyDataSetChanged();
        rvRooms.scrollToPosition(0);
    }
}
