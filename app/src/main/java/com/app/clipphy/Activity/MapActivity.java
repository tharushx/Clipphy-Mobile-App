package com.app.clipphy.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.clipphy.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    private LatLng predefinedLatLng = new LatLng(6.927079, 79.861244); // Colombo coordinates
    private LatLng userSelectedLatLng;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Window window = MapActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(MapActivity.this, R.color.main_color));

        ImageView backIcon2 = findViewById(R.id.backBtn2);
        backIcon2.setOnClickListener(v -> finish());

        SupportMapFragment supportMapFragment = new SupportMapFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameLayout1, supportMapFragment);
        fragmentTransaction.commit();

        Button addPlaceButton = findViewById(R.id.Mapbutton1);
        Button saveButton = findViewById(R.id.Mapbutton2);

        addPlaceButton.setEnabled(true);
        saveButton.setEnabled(false);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap map) {
                googleMap = map;

                // Animate camera to predefined location
                googleMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(predefinedLatLng)
                                        .zoom(15)
                                        .build()
                        )
                );

                addPlaceButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(@NonNull LatLng latLng) {
                                userSelectedLatLng = latLng;

                                googleMap.addMarker(
                                        new MarkerOptions()
                                                .title("Drop Location")
                                                .position(userSelectedLatLng)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.locationpin))
                                ).showInfoWindow();

                                saveButton.setEnabled(true);
                            }
                        });

                        addPlaceButton.setEnabled(false);
                    }
                });

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (userSelectedLatLng != null) {
                            googleMap.addPolyline(
                                    new PolylineOptions()
                                            .add(predefinedLatLng, userSelectedLatLng)
                                            .color(getColor(R.color.orange))
                                            .width(8)
                                            .startCap(new RoundCap())
                                            .endCap(new RoundCap())
                                            .jointType(JointType.ROUND)
                            );
                            saveButton.setEnabled(false);
                            addPlaceButton.setEnabled(true);
                        } else {
                            Log.e("MapActivity", "No user-selected location to draw polyline");
                        }
                    }
                });
            }
        });
    }
}
