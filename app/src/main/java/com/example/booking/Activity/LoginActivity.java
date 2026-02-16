package com.example.booking.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.booking.MainActivity;
import com.example.booking.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtEmail, edtMatKhau;
    private Button btnDangNhap;
    private TextView txtRegister;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://bookingapp-933ac-default-rtdb.firebaseio.com/").getReference();

        // Kiểm tra nếu đã đăng nhập thì điều hướng ngay
        if (mAuth.getCurrentUser() != null) {
            checkUserRoleAndNavigate(mAuth.getCurrentUser().getUid());
            return; 
        }

        setupLoginUi();
    }

    private void setupLoginUi() {
        setContentView(R.layout.activity_login);
        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        txtRegister = findViewById(R.id.txtRegister);

        btnDangNhap.setOnClickListener(v -> loginUser());
        txtRegister.setOnClickListener(v -> {
            Log.d("LOGIN_DEBUG", "Navigating to RegisterActivity");
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtMatKhau.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        checkUserRoleAndNavigate(mAuth.getCurrentUser().getUid());
                    } else {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRoleAndNavigate(String userId) {
        mDatabase.child("Users").child(userId).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = snapshot.getValue(String.class);
                
                if (role == null) {
                    mAuth.signOut();
                    setupLoginUi(); // Khởi tạo lại UI và gán sự kiện cho các nút
                    return;
                }

                Intent intent;
                switch (role) {
                    case "Admin":
                        intent = new Intent(LoginActivity.this, AdminActivity.class);
                        break;
                    case "Staff":
                        intent = new Intent(LoginActivity.this, StaffActivity.class);
                        break;
                    default:
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                        break;
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                setupLoginUi();
            }
        });
    }
}
