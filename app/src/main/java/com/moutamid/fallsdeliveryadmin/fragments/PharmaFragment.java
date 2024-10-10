package com.moutamid.fallsdeliveryadmin.fragments;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.fallsdeliveryadmin.R;
import com.moutamid.fallsdeliveryadmin.adapters.CategoryAdapter;
import com.moutamid.fallsdeliveryadmin.databinding.FragmentPharmaBinding;
import com.moutamid.fallsdeliveryadmin.models.CategoryModel;
import com.moutamid.fallsdeliveryadmin.utilis.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

public class PharmaFragment extends Fragment {
    FragmentPharmaBinding binding;
    CategoryAdapter adapter;
    ArrayList<CategoryModel> list;

    public PharmaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPharmaBinding.inflate(getLayoutInflater(), container, false);

        binding.addNew.setOnClickListener(v -> showDialog());

        list = new ArrayList<>();

        binding.categories.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.categories.setHasFixedSize(false);

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

        Constants.databaseReference().child(Constants.PHARMACY_CATEGORY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        CategoryModel topicsModel = dataSnapshot.getValue(CategoryModel.class);
                        list.add(topicsModel);
                    }
                    if (!list.isEmpty()){
                        list.sort(Comparator.comparing(categoryModel -> categoryModel.name));
                        binding.categories.setVisibility(View.VISIBLE);
                        binding.noLayout.setVisibility(View.GONE);
                    } else {
                        binding.categories.setVisibility(View.GONE);
                        binding.noLayout.setVisibility(View.VISIBLE);
                    }

                    adapter = new CategoryAdapter(requireContext(), list);
                    binding.categories.setAdapter(adapter);
                } else {
                    binding.categories.setVisibility(View.GONE);
                    binding.noLayout.setVisibility(View.VISIBLE);
                    adapter = new CategoryAdapter(requireContext(), new ArrayList<>());
                    binding.categories.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    private void showDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_topic);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();

        TextInputLayout topic = dialog.findViewById(R.id.name);
        Button complete = dialog.findViewById(R.id.complete);

        complete.setOnClickListener(v -> {
            String topicName = topic.getEditText().getText().toString();
            if (!topicName.isEmpty()) {
                dialog.dismiss();
                CategoryModel model = new CategoryModel(UUID.randomUUID().toString(), topicName, Constants.PHARMACY_CATEGORY);
                Constants.databaseReference().child(Constants.PHARMACY_CATEGORY).child(model.uid).setValue(model)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(requireContext(), "Category Added Successfully", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                topic.setErrorEnabled(true);
                topic.setError("Category name is empty");
            }
        });
    }

}