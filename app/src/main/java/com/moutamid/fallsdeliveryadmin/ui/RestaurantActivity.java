package com.moutamid.fallsdeliveryadmin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.moutamid.fallsdeliveryadmin.adapters.CategoryAdapter;
import com.moutamid.fallsdeliveryadmin.adapters.ProductAdapter;
import com.moutamid.fallsdeliveryadmin.databinding.ActivityRestaurantBinding;
import com.moutamid.fallsdeliveryadmin.models.CategoryModel;
import com.moutamid.fallsdeliveryadmin.models.ProductModel;
import com.moutamid.fallsdeliveryadmin.utilis.Constants;

import java.util.ArrayList;
import java.util.Comparator;

public class RestaurantActivity extends AppCompatActivity {
    ActivityRestaurantBinding binding;
    ProductAdapter adapter;
    ArrayList<ProductModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        binding.toolbar.title.setText(getString(R.string.marula_cafe));

        binding.addNew.setOnClickListener(v -> startActivity(new Intent(this, AddNewActivity.class).putExtra(Constants.TYPE, Constants.RESTAURANT)));

        list = new ArrayList<>();

        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setHasFixedSize(false);

        binding.search.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Constants.initDialog(this);
        Constants.showDialog();

        Constants.databaseReference().child(Constants.RESTAURANT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Constants.dismissDialog();
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()){
                            ProductModel topicsModel = dataSnapshot2.getValue(ProductModel.class);
                            list.add(topicsModel);
                        }
                    }
                    if (!list.isEmpty()){
                        list.sort(Comparator.comparing(categoryModel -> categoryModel.name));
                        binding.recycler.setVisibility(View.VISIBLE);
                        binding.noLayout.setVisibility(View.GONE);
                    } else {
                        binding.recycler.setVisibility(View.GONE);
                        binding.noLayout.setVisibility(View.VISIBLE);
                    }

                    adapter = new ProductAdapter(RestaurantActivity.this, list);
                    binding.recycler.setAdapter(adapter);
                } else {
                    binding.recycler.setVisibility(View.GONE);
                    binding.noLayout.setVisibility(View.VISIBLE);
                    adapter = new ProductAdapter(RestaurantActivity.this, new ArrayList<>());
                    binding.recycler.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Constants.dismissDialog();
                Toast.makeText(RestaurantActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}