package com.moutamid.fallsdeliveryadmin.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.moutamid.fallsdeliveryadmin.R;
import com.moutamid.fallsdeliveryadmin.databinding.ActivityOrderDetailBinding;
import com.moutamid.fallsdeliveryadmin.models.OrderModel;
import com.moutamid.fallsdeliveryadmin.utilis.Constants;

public class OrderDetailActivity extends AppCompatActivity {
    ActivityOrderDetailBinding binding;
    GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    OrderModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        binding.toolbar.title.setText(getString(R.string.delivery_orders));

        model = (OrderModel) Stash.getObject(Constants.ORDERS, OrderModel.class);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        binding.username.setText(model.name);
        binding.phone.setText(model.phone);
        binding.address.setText(model.address);

        binding.productName.setText(model.productModel.name);
        binding.quantity.setText("x" + model.quantity);
        binding.price.setText("$" + model.productModel.price);
        binding.total.setText("$" + (model.productModel.price * model.quantity));

        if (model.COD) {
            binding.cod.setText("YES");
            binding.proof.setVisibility(View.GONE);
        } else {
            binding.cod.setText("NO");
            binding.proof.setVisibility(View.VISIBLE);
        }

        binding.complete.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setCancelable(true)
                    .setTitle("Complete Order")
                    .setMessage("Completing this order will permanently remove it from the list, making it inaccessible for future reference.")
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Yes", ((dialog, which) -> {
                        dialog.dismiss();
                        Constants.databaseReference().child(Constants.ORDERS).child(model.userID).child(model.uid).removeValue()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Order Completed Successfully", Toast.LENGTH_SHORT).show();
                                    getOnBackPressedDispatcher().onBackPressed();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }))
                    .show();
        });
        binding.delete.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setCancelable(true)
                    .setTitle("Delete Order")
                    .setMessage("Deleting this order will permanently remove it from the list, making it inaccessible for future reference.")
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Yes", ((dialog, which) -> {
                        dialog.dismiss();
                        Constants.databaseReference().child(Constants.ORDERS).child(model.userID).child(model.uid).removeValue()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Order Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    getOnBackPressedDispatcher().onBackPressed();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }))
                    .show();
        });

        binding.proof.setOnClickListener(v -> {
            showImage();
        });
    }

    private void showImage() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.imageviewer);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(true);
        dialog.show();

        ImageView image = dialog.findViewById(R.id.image);
        View toolbar = dialog.findViewById(R.id.toolbar);
        MaterialCardView back = toolbar.findViewById(R.id.back);
        TextView title = toolbar.findViewById(R.id.title);
        title.setText("Payment Proof");
        back.setOnClickListener(v ->  dialog.dismiss());

        Glide.with(this).load(model.proof).into(image);
    }

    private final OnMapReadyCallback callback = googleMap -> {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        LatLng currentLatLng = new LatLng(Double.parseDouble(model.latitude), Double.parseDouble(model.longitude));
        MarkerOptions markerOptions = new MarkerOptions()
                .position(currentLatLng).title(model.name);
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f));
    };


}