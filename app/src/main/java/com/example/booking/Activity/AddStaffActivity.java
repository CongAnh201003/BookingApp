package com.example.booking.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.booking.Model.User;
import com.example.booking.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddStaffActivity extends AppCompatActivity {

    private TextInputEditText edtName, edtEmail, edtPass, edtPhone;
    private Button btnCreate;
    private ImageButton btnBack;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://bookingapp-933ac-default-rtdb.firebaseio.com/").getReference();

        initUi();

        btnBack.setOnClickListener(v -> finish());
        btnCreate.setOnClickListener(v -> createStaffAccount());
    }

    private void initUi() {
        edtName = findViewById(R.id.edtStaffFullName);
        edtEmail = findViewById(R.id.edtStaffEmail);
        edtPass = findViewById(R.id.edtStaffPassword);
        edtPhone = findViewById(R.id.edtStaffPhone);
        btnCreate = findViewById(R.id.btnCreateStaff);
        btnBack = findViewById(R.id.btnBackAddStaff);
    }

    private void createStaffAccount() {
        String email = edtEmail.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (TextUtils.isEmpty(email) || pass.length() < 6) {
            Toast.makeText(this, "Email hợp lệ và mật khẩu >= 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo tài khoản Auth
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userId = task.getResult().getUser().getUid();
                // Lưu thông tin vào Database với role là Staff
                User staff = new User(email, name, email, phone, "N/A", "N/A", "Nam", "Staff");
                mDatabase.child("Users").child(userId).setValue(staff).addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã tạo tài khoản nhân viên thành công!", Toast.LENGTH_LONG).show();
                    finish();
                });
            } else {
                Toast.makeText(this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
