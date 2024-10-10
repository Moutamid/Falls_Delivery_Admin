package com.moutamid.fallsdeliveryadmin.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.database.DataSnapshot;
import com.moutamid.fallsdeliveryadmin.R;
import com.moutamid.fallsdeliveryadmin.databinding.ActivityAddNewBinding;
import com.moutamid.fallsdeliveryadmin.models.CategoryModel;
import com.moutamid.fallsdeliveryadmin.models.ProductModel;
import com.moutamid.fallsdeliveryadmin.utilis.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddNewActivity extends AppCompatActivity {
    ActivityAddNewBinding binding;
    Uri image;
    public String type;
    private static final String TAG = "AddNewActivity";
    ProductModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Stash.clear(Constants.PASS);
                finish();
            }
        });

        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        type = getIntent().getStringExtra(Constants.TYPE);

        model = (ProductModel) Stash.getObject(Constants.PASS, ProductModel.class);

        if (model != null) {
            binding.toolbar.title.setText("Update");
            Glide.with(this).load(model.image).into(binding.picture);
            binding.name.getEditText().setText(model.name);
            binding.price.getEditText().setText(String.valueOf(model.price));
            binding.category.getEditText().setText(model.category);
            binding.description.getEditText().setText(model.description);
        } else {
            binding.toolbar.title.setText("Add New");
        }

        binding.image.setOnClickListener(v -> {
            ImagePicker.with(this).cropSquare().compress(1024).maxResultSize(1080, 1080).start();
        });

        binding.add.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                if (model == null) {
                    model = new ProductModel();
                    model.UID = UUID.randomUUID().toString();
                }
                if (image != null)
                    uploadImage();
                else {
                    uploadData(model.image);
                }
            }
        });

    }

    private void uploadImage() {
        Constants.storageReference().child("IMAGES")
                .child(new SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault()).format(new Date()))
                .putFile(image)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> uploadData(uri.toString()));
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadData(String link) {
        model.image = link;
        model.type = type;
        model.name = binding.name.getEditText().getText().toString().trim();
        model.category = binding.category.getEditText().getText().toString().trim();
        model.price = Double.parseDouble(binding.price.getEditText().getText().toString().trim());
        model.description = binding.description.getEditText().getText().toString().trim();

        Constants.databaseReference().child(type).child(model.category).child(model.UID).setValue(model)
                .addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    Stash.clear(Constants.PASS);
                    Toast.makeText(this, "Product Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    getOnBackPressedDispatcher().onBackPressed();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean valid() {
        if (binding.name.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Name is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.description.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Description is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.price.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Price is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (model == null)
            if (image == null) {
                Toast.makeText(this, "Please upload a image", Toast.LENGTH_SHORT).show();
                return false;
            }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
        Constants.showDialog();
        ArrayList<CategoryModel> list = new ArrayList<>();
        Log.d(TAG, "onResume: " + type + "_CATEGORY");
        Constants.databaseReference().child(type + "_CATEGORY").get().addOnSuccessListener(dataSnapshot -> {
            Constants.dismissDialog();
            if (dataSnapshot.exists()) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CategoryModel topicsModel = snapshot.getValue(CategoryModel.class);
                    list.add(topicsModel);
                }
                ArrayList<String> categories = new ArrayList<>();
                list.stream().map(categoryModel -> categoryModel.name).forEach(categories::add);
                Log.d(TAG, "onResume: " + categories.size());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
                binding.categoriesList.setAdapter(adapter);
            }
        }).addOnFailureListener(e -> {
            Constants.dismissDialog();
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            image = data.getData();
            Glide.with(this).load(image).placeholder(R.drawable.image_upload).into(binding.picture);
        }
    }
}