package com.example.booking.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.booking.MainActivity;
import com.example.booking.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    private TextView txtAmount;
    private Button btnFinish;
    private String bookingId;
    private double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        bookingId = getIntent().getStringExtra("bookingId");
        totalPrice = getIntent().getDoubleExtra("totalPrice", 0);

        txtAmount = findViewById(R.id.txtPaymentAmount);
        btnFinish = findViewById(R.id.btnFinishPayment);

        txtAmount.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", totalPrice));

        btnFinish.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {
        // Sau khi khách thanh toán, trạng thái là "Paid" (Chờ Staff duyệt)
        DatabaseReference bookingRef = FirebaseDatabase.getInstance("https://bookingapp-933ac-default-rtdb.firebaseio.com/").getReference("Bookings").child(bookingId);
        
        bookingRef.child("status").setValue("Paid")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PaymentActivity.this, "Thanh toán thành công! Vui lòng chờ nhân viên xác nhận.", Toast.LENGTH_LONG).show();
                    
                    Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(PaymentActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
