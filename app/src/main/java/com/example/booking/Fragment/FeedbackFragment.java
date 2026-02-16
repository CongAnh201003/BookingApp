package com.example.booking.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.booking.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FeedbackFragment extends Fragment {

    private TextInputEditText edtFeedback;
    private Button btnSend;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        edtFeedback = view.findViewById(R.id.edtFeedback);
        btnSend = view.findViewById(R.id.btnSendFeedback);
        mDatabase = FirebaseDatabase.getInstance("https://bookingapp-933ac-default-rtdb.firebaseio.com/").getReference();

        btnSend.setOnClickListener(v -> sendFeedback());

        return view;
    }

    private void sendFeedback() {
        String content = edtFeedback.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(getActivity(), "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getUid();
        String feedbackId = mDatabase.child("Feedbacks").push().getKey();

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("content", content);
        data.put("timestamp", System.currentTimeMillis());

        if (feedbackId != null) {
            mDatabase.child("Feedbacks").child(feedbackId).setValue(data)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "Cảm ơn bạn đã phản hồi!", Toast.LENGTH_SHORT).show();
                        edtFeedback.setText("");
                    });
        }
    }
}
