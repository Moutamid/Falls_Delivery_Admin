package com.moutamid.fallsdeliveryadmin.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.fallsdeliveryadmin.R;
import com.moutamid.fallsdeliveryadmin.adapters.OrdersAdapter;
import com.moutamid.fallsdeliveryadmin.databinding.ActivityOrdersBinding;
import com.moutamid.fallsdeliveryadmin.models.OrderModel;
import com.moutamid.fallsdeliveryadmin.utilis.Constants;

import java.util.ArrayList;
import java.util.Comparator;

public class OrdersActivity extends AppCompatActivity {
    ActivityOrdersBinding binding;
    OrdersAdapter adapter;
    ArrayList<OrderModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        binding.toolbar.title.setText(getString(R.string.delivery_orders));

        list = new ArrayList<>();

        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setHasFixedSize(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
        Constants.showDialog();
        getData();
    }

    private void getData() {
        Constants.databaseReference().child(Constants.ORDERS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Constants.dismissDialog();
                        if (dataSnapshot.exists()) {
                            list.clear();
                            for (DataSnapshot snapshot2 : dataSnapshot.getChildren()){
                                for (DataSnapshot snapshot : snapshot2.getChildren()){
                                    OrderModel model = snapshot.getValue(OrderModel.class);
                                    list.add(model);
                                }
                            }

                            if (!list.isEmpty()){
                                list.sort(Comparator.comparing(categoryModel -> categoryModel.productModel.name));
                                binding.recycler.setVisibility(View.VISIBLE);
                                binding.noLayout.setVisibility(View.GONE);
                            } else {
                                binding.recycler.setVisibility(View.GONE);
                                binding.noLayout.setVisibility(View.VISIBLE);
                            }

                            adapter = new OrdersAdapter(OrdersActivity.this, list);
                            binding.recycler.setAdapter(adapter);
                        } else {
                            binding.recycler.setVisibility(View.GONE);
                            binding.noLayout.setVisibility(View.VISIBLE);
                            adapter = new OrdersAdapter(OrdersActivity.this, new ArrayList<>());
                            binding.recycler.setAdapter(adapter);
                            Toast.makeText(OrdersActivity.this, "No new orders found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        Constants.dismissDialog();
                        Toast.makeText(OrdersActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}