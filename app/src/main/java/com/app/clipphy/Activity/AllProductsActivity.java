package com.app.clipphy.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

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
import com.app.clipphy.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AllProductsActivity extends AppCompatActivity {

    RecyclerView ongoingRecycle;
    ArrayList<AllProducts> productsArrayList = new ArrayList<>();
    AllProductAdapter allProductAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_onging_products);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Window window=AllProductsActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(AllProductsActivity.this,R.color.main_color));

        // Ensure RecyclerView is properly initialized
        ongoingRecycle = findViewById(R.id.allRecycle);
        if (ongoingRecycle != null) {
            initRecycleView();
            loadProducts();
        }

        ImageView backIcon = findViewById(R.id.backBtn);
        backIcon.setOnClickListener(v -> finish());
    }

    private void initRecycleView() {
        ongoingRecycle.setLayoutManager(new GridLayoutManager(this, 2));
        productsArrayList = new ArrayList<>();
        allProductAdapter = new AllProductAdapter(this, productsArrayList);
        ongoingRecycle.setAdapter(allProductAdapter);
    }

    private void loadProducts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productsArrayList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            AllProducts product = document.toObject(AllProducts.class);
                            if ("Available for Clipphy".equals(product.getClipphyStatus())) {
                                // Process as needed
                            } else {
                                productsArrayList.add(product);
                            }
                        }
                        allProductAdapter.notifyDataSetChanged();
                    } else {
                        Log.w("HomeActivity", "Error getting documents.", task.getException());
                    }
                });
    }
}
