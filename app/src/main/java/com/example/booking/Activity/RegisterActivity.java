package com.example.booking.Activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.booking.Model.User;
import com.example.booking.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText edtTenDangNhap, edtMatKhau, edtMatKhauhai, edtHoten, edtEmail, edtPhoneNumber, edtIdentityNumber, edtDateOfBirth;
    private AutoCompleteTextView actvGender;
    private Button btnDangKy;
    private ImageView imgBack;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initUi();
        initFirebase();
        setupGenderDropdown();
        setupDatePicker();

        imgBack.setOnClickListener(v -> finish());
        btnDangKy.setOnClickListener(v -> registerUser());
    }

    private void initUi() {
        edtTenDangNhap = findViewById(R.id.edtTenDangNhap);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        edtMatKhauhai = findViewById(R.id.edtMatKhauhai);
        edtHoten = findViewById(R.id.edtHoten);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtIdentityNumber = findViewById(R.id.edtIdentityNumber);
        edtDateOfBirth = findViewById(R.id.edtDateOfBirth);
        actvGender = findViewById(R.id.actvGender);
        btnDangKy = findViewById(R.id.btnDangKy);
        imgBack = findViewById(R.id.imgBack);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("Users");
    }

    private void setupGenderDropdown() {
        String[] genders = new String[]{"Nam", "Nữ", "Khác"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genders);
        actvGender.setAdapter(adapter);
    }

    private void setupDatePicker() {
        edtDateOfBirth.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> edtDateOfBirth.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1),
                    year, month, day);
            datePickerDialog.show();
        });
    }

    private void registerUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtMatKhau.getText().toString().trim();
        String confirmPassword = edtMatKhauhai.getText().toString().trim();
        String username = edtTenDangNhap.getText().toString().trim();
        String fullName = edtHoten.getText().toString().trim();
        String phone = edtPhoneNumber.getText().toString().trim();
        String idNumber = edtIdentityNumber.getText().toString().trim();
        String dob = edtDateOfBirth.getText().toString().trim();
        String gender = actvGender.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username) || TextUtils.isEmpty(fullName)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ các trường bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải từ 6 ký tự trở lên", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            writeNewUser(user.getUid(), username, fullName, email, phone, idNumber, dob, gender);
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void writeNewUser(String userId, String username, String fullName, String email, String phone, String idNumber, String dob, String gender) {
        User user = new User(username, fullName, email, phone, idNumber, dob, gender);
        mDatabase.child(userId).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity
                })
                .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Lưu dữ liệu thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
