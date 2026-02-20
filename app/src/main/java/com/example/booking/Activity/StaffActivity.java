package com.example.booking.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.booking.Fragment.StaffNewRequestsFragment;
import com.example.booking.Fragment.StaffMyHistoryFragment;
import com.example.booking.Fragment.StaffReportFragment;
import com.example.booking.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class StaffActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);

        bottomNav = findViewById(R.id.staffBottomNavigation);
        Button btnLogout = findViewById(R.id.btnStaffLogout);

        // Mặc định tab đầu tiên
        loadFragment(new StaffNewRequestsFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();
            if (id == R.id.staff_new_requests) {
                fragment = new StaffNewRequestsFragment();
            } else if (id == R.id.staff_my_history) {
                fragment = new StaffMyHistoryFragment();
            } else if (id == R.id.staff_report) {
                fragment = new StaffReportFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(StaffActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.staff_fragment_container, fragment)
                .commit();
    }
}
