package com.app.clipphy.Activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.clipphy.Adapter.AllProductAdapter;
import com.app.clipphy.Domain.AllProducts;
import com.app.clipphy.Domain.GridSpacingItemDecoration;
import com.app.clipphy.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HomeActivity2 extends AppCompatActivity {

    RecyclerView recyclerView1;
    RecyclerView recyclerView2;
    ProgressBar progressBar1,progressBar2;
    ArrayList<AllProducts> availableProductsArrayList = new ArrayList<>();
    ArrayList<AllProducts> otherProductsArrayList = new ArrayList<>();
    AllProductAdapter availableProductAdapter;
    AllProductAdapter otherProductAdapter;

    private NetworkBroadcastReceiver networkBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.viewJoin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Window window= HomeActivity2.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(HomeActivity2.this,R.color.main_color));

        networkBroadcastReceiver = new NetworkBroadcastReceiver();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkBroadcastReceiver, filter);

        Intent intent = getIntent();
        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");

        Log.d("HomeActivity", "Received First name: " + firstName + ", Last name: " + lastName); // Log received values

        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
        if (firstName != null) {
            welcomeTextView.setText("Hi, " + firstName);
        } else {
            Log.e("HomeActivity", "First name is null");
        }

        initRecycleView();
        loadProducts();

        TextView ongoingViewAll = findViewById(R.id.ongoingViewAll);
        ongoingViewAll.setOnClickListener(v -> {
            Intent intent2 = new Intent(HomeActivity2.this, AllOngingProductsActivity.class);
            startActivity(intent2);
        });

        TextView allProductstxt = findViewById(R.id.allProductstxt);
        allProductstxt.setOnClickListener(v -> {
            Intent intent2 = new Intent(HomeActivity2.this, AllProductsActivity.class);
            startActivity(intent2);
        });

        ImageView profBtn = findViewById(R.id.profileimageView);
        profBtn.setOnClickListener(v -> {
            Intent intent2 = new Intent(HomeActivity2.this, ProfileActivity.class);
            startActivity(intent2);
        });

        ImageView profileBtn = findViewById(R.id.profileBtn);
        profileBtn.setOnClickListener(v -> {
            Intent intent2 = new Intent(HomeActivity2.this, ProfileActivity.class);
            startActivity(intent2);
        });

        ImageView homeBtn = findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(v -> {
            Intent intent2 = new Intent(HomeActivity2.this, HomeActivity2.class);
            startActivity(intent2);
        });

        ImageView messageBtn = findViewById(R.id.messageBtn);
        messageBtn.setOnClickListener(v -> {
            Intent intent2 = new Intent(HomeActivity2.this, ProfileActivity.class);
            startActivity(intent2);
        });

        ImageView cartBtn = findViewById(R.id.cartBtn);
        cartBtn.setOnClickListener(v -> {
            Intent intent2 = new Intent(HomeActivity2.this, CartActivity.class);
            startActivity(intent2);
        });
    }

    private void initRecycleView() {
        recyclerView1 = findViewById(R.id.recycleview1);
        progressBar1 = findViewById(R.id.progressBar1);
        progressBar1.setVisibility(View.VISIBLE);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        availableProductAdapter = new AllProductAdapter(this, availableProductsArrayList);
        recyclerView1.setAdapter(availableProductAdapter);

        recyclerView2 = findViewById(R.id.recycleview2);
        progressBar2 = findViewById(R.id.progressBar2);
        progressBar2.setVisibility(View.VISIBLE);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView2.setLayoutManager(layoutManager);
        otherProductAdapter = new AllProductAdapter(this, otherProductsArrayList);
        recyclerView2.setAdapter(otherProductAdapter);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_view_item_spacing);
        recyclerView2.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels));
    }

    private void loadProducts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        availableProductsArrayList.clear();
                        otherProductsArrayList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            AllProducts product = document.toObject(AllProducts.class);
                            if ("Available for Clipphy".equals(product.getClipphyStatus())) {
                                availableProductsArrayList.add(product);
                            } else {
                                otherProductsArrayList.add(product);
                            }
                        }
                        availableProductAdapter.notifyDataSetChanged();
                        otherProductAdapter.notifyDataSetChanged();

                        progressBar1.setVisibility(View.GONE);
                        progressBar2.setVisibility(View.GONE);

                    } else {
                        Log.w("HomeActivity", "Error getting documents.", task.getException());
                    }
                });

    }

    protected void onDestroy() {
        super.onDestroy();
        // Unregister the receiver to avoid memory leaks
        unregisterReceiver(networkBroadcastReceiver);
    }
}
