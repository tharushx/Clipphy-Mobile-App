package com.app.clipphy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.clipphy.Adapter.AllProductAdapter;
import com.app.clipphy.Adapter.ManagementCart;
import com.app.clipphy.Domain.AllProducts;
import com.app.clipphy.R;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;

public class ViewProductActivity extends AppCompatActivity {

    private static final String TAG = "ViewProductActivity";
    private static final int PAYHERE_REQUEST = 11010;

    private ManagementCart managementCart;

    RecyclerView recycleViewProduct;
    ArrayList<AllProducts> productsArrayList = new ArrayList<>();
    AllProductAdapter allProductAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.label), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Window window=ViewProductActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(ViewProductActivity.this,R.color.main_color));

        managementCart = new ManagementCart(this);
        initRecycleView();
        loadOngoingProducts();

        ImageView backIcon = findViewById(R.id.backBtn);
        backIcon.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String price = intent.getStringExtra("price");
        String priceSale = intent.getStringExtra("priceSale");
        String count = intent.getStringExtra("count");
        String current_count = intent.getStringExtra("current_count");
        String status = intent.getStringExtra("status");
        String duration = intent.getStringExtra("duration");
        String description = intent.getStringExtra("description");
        String clipphyDetails = intent.getStringExtra("clipphyDetails");
        String picAddress = intent.getStringExtra("picAddress");
        String product_Id = intent.getStringExtra("product_Id");
        String clipphyStatus = intent.getStringExtra("clipphyStatus");

        AppCompatButton viewjoinBtn = findViewById(R.id.joinBtn);
        BottomAppBar bottomAppBarProduct = findViewById(R.id.bottomAppBarProduct);

        if ("Available for Clipphy".equals(clipphyStatus)) {
            viewjoinBtn.setVisibility(View.VISIBLE);
            viewjoinBtn.setOnClickListener(v -> {
                Intent newIntent = new Intent(ViewProductActivity.this, JoinClipphyActivity.class);
                newIntent.putExtra("title", title);
                newIntent.putExtra("price", price);
                newIntent.putExtra("priceSale", priceSale);
                newIntent.putExtra("description", description);
                newIntent.putExtra("count", count);
                newIntent.putExtra("current_count", current_count);
                newIntent.putExtra("status", status);
                newIntent.putExtra("duration", duration);
                newIntent.putExtra("clipphyDetails", clipphyDetails);
                newIntent.putExtra("picAddress", picAddress);
                newIntent.putExtra("product_Id", product_Id);
                startActivity(newIntent);
            });
        } else {
            viewjoinBtn.setVisibility(View.GONE);
            bottomAppBarProduct.setVisibility(View.GONE);
        }

        Button buyNowButton = findViewById(R.id.buyNowBtn);
        buyNowButton.setOnClickListener(v -> {
            Intent intent2 = new Intent(ViewProductActivity.this, PaymentActivity.class);
            intent2.putExtra("title", title);
            intent2.putExtra("price", price);
            intent2.putExtra("priceSale", priceSale);
            intent2.putExtra("product_Id", product_Id);
            intent2.putExtra("description", description);
            intent2.putExtra("picAddress", picAddress);

            startActivity(intent2);
        });


        ConstraintLayout constraintLayout = findViewById(R.id.label);
        ImageView productImage = findViewById(R.id.picAddress);
        TextView productTitle = findViewById(R.id.titleTxt);
        TextView productPrice = findViewById(R.id.priceTxt);
        EditText productDescription = findViewById(R.id.productDescriptionTxt);
        EditText clipphyDetailsView = findViewById(R.id.clipphyDetailsTxt);
        TextView productStatus = findViewById(R.id.statusTxt);

        productTitle.setText(title);
        productPrice.setText("LKR "+price);
        productStatus.setText(status);
        productDescription.setText(description);
        clipphyDetailsView.setText(clipphyStatus);

        Log.d("JoinClipphyActivity", "Received price: " + priceSale);

        if (picAddress != null && !picAddress.isEmpty()) {
            int drawableResourceId = getApplicationContext().getResources().getIdentifier(
                    picAddress, "drawable", getApplicationContext().getPackageName()
            );

            if (drawableResourceId != 0) {
                productImage.setImageResource(drawableResourceId);
            } else {
                Log.e(TAG, "Drawable resource not found for picAddress: " + picAddress);
            }
        } else {
            Log.e(TAG, "picAddress is null or empty");
        }

        Button addToCartButton = findViewById(R.id.addToCartBtn);
        addToCartButton.setOnClickListener(v -> {
            AllProducts product = new AllProducts(title, status, count, price, picAddress, 0, "1");
            managementCart.insertFood(product);
        });

        ImageView viewCart = findViewById(R.id.viewCart);
        viewCart.setOnClickListener(v -> {
            Intent newIntent = new Intent(ViewProductActivity.this, CartActivity.class);
            startActivity(newIntent);
        });
    }


    private void initRecycleView() {
        recycleViewProduct = findViewById(R.id.recycleViewProduct);
        recycleViewProduct.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        productsArrayList = new ArrayList<>();
        allProductAdapter = new AllProductAdapter(this, productsArrayList);
        recycleViewProduct.setAdapter(allProductAdapter);
    }

    private void loadOngoingProducts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ongoingProducts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            AllProducts product = document.toObject(AllProducts.class);
                            productsArrayList.add(product);
                        }
                        allProductAdapter.notifyDataSetChanged();
                    } else {
                        Log.w("HomeActivity", "Error getting documents.", task.getException());
                    }
                });
    }
}
