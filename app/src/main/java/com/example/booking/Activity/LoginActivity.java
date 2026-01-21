package com.example.booking.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;import androidx.appcompat.app.AppCompatActivity;
import com.example.booking.MainActivity;
import com.example.booking.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtEmail, edtMatKhau;
    private Button btnDangNhap;
    private TextView txtRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ View
        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        txtRegister = findViewById(R.id.txtRegister);

        // Chuyển sang trang đăng ký
        txtRegister.setOnClickListener(v -> {
            // 1. Log ra cửa sổ Logcat để kiểm tra chắc chắn nút đã nhận
            android.util.Log.d("DEBUG_CLICK", "Nut dang ky da duoc bam!");

            // 2. Hiện thông báo lên màn hình điện thoại
            Toast.makeText(LoginActivity.this, "Đang mở màn hình đăng ký...", Toast.LENGTH_SHORT).show();

            // 3. Lệnh chuyển màn
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnDangNhap.setOnClickListener(v -> loginUser());
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
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}