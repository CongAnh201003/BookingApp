package com.example.booking.Activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
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

    // TÌM ĐẾN HÀM initFirebase() TRONG RegisterActivity.java
    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();

        // Xóa cái link cũ (link console firebase) và thay bằng link Database này:
        String databaseUrl = "https://bookingapp-933ac-default-rtdb.firebaseio.com/";

        database = FirebaseDatabase.getInstance(databaseUrl);
        mDatabase = database.getReference("Users");
    }

    private void setupGenderDropdown() {
        String[] genders = new String[]{"Nam", "Nữ", "Khác"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genders);
        actvGender.setAdapter(adapter);
    }

    private void setupDatePicker() {
        edtDateOfBirth.setFocusable(false); // Ngăn hiện bàn phím
        edtDateOfBirth.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            new DatePickerDialog(RegisterActivity.this,
                    (view, year, month, day) -> edtDateOfBirth.setText(day + "/" + (month + 1) + "/" + year),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void registerUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtMatKhau.getText().toString().trim();
        String confirmPassword = edtMatKhauhai.getText().toString().trim();

        if (TextUtils.isEmpty(email) || password.length() < 6) {
            Toast.makeText(this, "Email không được để trống và Mật khẩu > 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            writeNewUser(user.getUid());
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Đăng ký lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void writeNewUser(String userId) {
        User user = new User(
                edtTenDangNhap.getText().toString(),
                edtHoten.getText().toString(),
                edtEmail.getText().toString(),
                edtPhoneNumber.getText().toString(),
                edtIdentityNumber.getText().toString(),
                edtDateOfBirth.getText().toString(),
                actvGender.getText().toString()
        );
        mDatabase.child(userId).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}