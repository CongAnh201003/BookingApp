package com.example.booking.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.booking.Model.User;
import com.example.booking.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddStaffActivity extends AppCompatActivity {

    private TextInputEditText edtName, edtEmail, edtPass, edtPhone;
    private TextInputLayout layoutPass;
    private Button btnAction;
    private ImageButton btnBack;
    private TextView txtTitle;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    
    private User userToEdit;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://bookingapp-933ac-default-rtdb.firebaseio.com/").getReference();

        // Kiểm tra xem là chế độ Thêm hay Sửa
        if (getIntent().hasExtra("user_data")) {
            userToEdit = (User) getIntent().getSerializableExtra("user_data");
            isEditMode = true;
        }

        initUi();

        btnBack.setOnClickListener(v -> finish());
        btnAction.setOnClickListener(v -> {
            if (isEditMode) {
                updateStaffAccount();
            } else {
                createStaffAccount();
            }
        });
    }

    private void initUi() {
        edtName = findViewById(R.id.edtStaffFullName);
        edtEmail = findViewById(R.id.edtStaffEmail);
        edtPass = findViewById(R.id.edtStaffPassword);
        edtPhone = findViewById(R.id.edtStaffPhone);
        layoutPass = (TextInputLayout) edtPass.getParent().getParent(); // TextInputLayout chứa edtPass
        btnAction = findViewById(R.id.btnCreateStaff);
        btnBack = findViewById(R.id.btnBackAddStaff);
        txtTitle = findViewById(R.id.headerAddStaff).findViewById(android.R.id.text1); // Hoặc dùng findViewById bình thường nếu có ID

        if (isEditMode && userToEdit != null) {
            // Chỉnh giao diện Sửa
            // Nếu bạn không tìm thấy ID text1, tôi sẽ mặc định gán text cho Button
            btnAction.setText("CẬP NHẬT THÔNG TIN");
            edtName.setText(userToEdit.getFullName());
            edtEmail.setText(userToEdit.getEmail());
            edtEmail.setEnabled(false); // Không cho sửa Email vì nó là Username Auth
            edtPhone.setText(userToEdit.getPhoneNumber());
            layoutPass.setVisibility(View.GONE); // Không cho sửa pass ở đây
        }
    }

    private void createStaffAccount() {
        String email = edtEmail.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (TextUtils.isEmpty(email) || pass.length() < 6) {
            Toast.makeText(this, "Vui lòng nhập email và mật khẩu >= 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userId = task.getResult().getUser().getUid();
                User staff = new User(email, name, email, phone, "N/A", "N/A", "Nam", "Staff");
                mDatabase.child("Users").child(userId).setValue(staff).addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã tạo tài khoản thành công!", Toast.LENGTH_LONG).show();
                    finish();
                });
            } else {
                Toast.makeText(this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStaffAccount() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Họ tên không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật thông tin trong Database (không đổi email và role)
        mDatabase.child("Users").child(userToEdit.getUsername()).child("fullName").setValue(name);
        mDatabase.child("Users").child(userToEdit.getUsername()).child("phoneNumber").setValue(phone)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}
