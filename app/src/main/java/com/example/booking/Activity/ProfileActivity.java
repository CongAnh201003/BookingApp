package com.example.booking.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.booking.Model.User;
import com.example.booking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtFullName, txtEmail, txtPhone, txtRole;
    private Button btnLogout, btnMakeAdmin;
    private ImageButton btnBack;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://bookingapp-933ac-default-rtdb.firebaseio.com/").getReference();

        initUi();
        loadUserProfile();

        btnBack.setOnClickListener(v -> finish());
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Nút bí mật để nâng cấp tài khoản test thành Admin
        btnMakeAdmin.setOnClickListener(v -> {
            String userId = mAuth.getUid();
            if (userId != null) {
                mDatabase.child("Users").child(userId).child("role").setValue("Admin")
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Đã trở thành Admin! Hãy đăng nhập lại.", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                            finish();
                        });
            }
        });
    }

    private void initUi() {
        txtFullName = findViewById(R.id.txtProfileFullName);
        txtEmail = findViewById(R.id.txtProfileEmail);
        txtPhone = findViewById(R.id.txtProfilePhone);
        txtRole = findViewById(R.id.txtProfileRole);
        btnLogout = findViewById(R.id.btnProfileLogout);
        btnBack = findViewById(R.id.btnProfileBack);
        btnMakeAdmin = new Button(this); // Tạo tạm trong code hoặc thêm vào XML
    }

    private void loadUserProfile() {
        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        txtFullName.setText(user.getFullName());
                        txtEmail.setText("Email: " + user.getEmail());
                        txtPhone.setText("SĐT: " + user.getPhoneNumber());
                        txtRole.setText("Chức vụ: " + (user.getRole() != null ? user.getRole() : "Customer"));
                        
                        // Nếu là email của bạn thì hiện nút đặc biệt
                        if ("dat103@gmail.com".equals(user.getEmail())) {
                            btnMakeAdmin.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
