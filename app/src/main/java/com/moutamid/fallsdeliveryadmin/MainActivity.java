package com.moutamid.fallsdeliveryadmin;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.moutamid.fallsdeliveryadmin.databinding.ActivityMainBinding;
import com.moutamid.fallsdeliveryadmin.ui.CategoryActivity;
import com.moutamid.fallsdeliveryadmin.ui.OrdersActivity;
import com.moutamid.fallsdeliveryadmin.ui.PharmaActivity;
import com.moutamid.fallsdeliveryadmin.ui.RestaurantActivity;
import com.moutamid.fallsdeliveryadmin.utilis.Constants;

public class MainActivity extends AppCompatActivity {
ActivityMainBinding binding;
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

    }
}