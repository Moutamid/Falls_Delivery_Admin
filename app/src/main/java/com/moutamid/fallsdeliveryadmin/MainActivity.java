package com.moutamid.fallsdeliveryadmin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.moutamid.fallsdeliveryadmin.databinding.ActivityMainBinding;
import com.moutamid.fallsdeliveryadmin.ui.CategoryActivity;
import com.moutamid.fallsdeliveryadmin.ui.OrdersActivity;
import com.moutamid.fallsdeliveryadmin.ui.PharmaActivity;
import com.moutamid.fallsdeliveryadmin.ui.RestaurantActivity;
import com.moutamid.fallsdeliveryadmin.utilis.Constants;

public class MainActivity extends AppCompatActivity {
ActivityMainBinding binding;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.checkApp(this);

        binding.orders.setOnClickListener(v -> startActivity(new Intent(this, OrdersActivity.class)));
        binding.pharma.setOnClickListener(v -> startActivity(new Intent(this, PharmaActivity.class)));
        binding.resturant.setOnClickListener(v -> startActivity(new Intent(this, RestaurantActivity.class)));
        binding.category.setOnClickListener(v -> startActivity(new Intent(this, CategoryActivity.class)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (
                    (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) &&
                            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION);
                shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION);
                shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS);
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION);
                shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION);
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 111);
            }
        }

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> {
            Log.d(TAG, "getToken: " + s);
            Constants.databaseReference().child(Constants.USER).child(Constants.auth().getCurrentUser().getUid()).child("fcmToken").setValue(s)
                    .addOnSuccessListener(unused -> Log.d(TAG, "onCreate: value set"))
                    .addOnFailureListener(e -> Log.d(TAG, "error: " + e.getLocalizedMessage()));
        }).addOnFailureListener(e -> {
            Log.d(TAG, "Token Error: " + e.getLocalizedMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        });

        // new FCMNotificationHelper(this).sendNotification(ID, "Order Confirmation", "Your Order is confirmed and ready to dispatch");

    }
}