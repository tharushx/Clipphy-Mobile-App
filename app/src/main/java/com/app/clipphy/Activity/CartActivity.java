package com.app.clipphy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.clipphy.Adapter.CartAdapter;
import com.app.clipphy.Adapter.ChangeNumberItemListner;
import com.app.clipphy.Adapter.ManagementCart;
import com.app.clipphy.R;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartView;
    private ManagementCart managementCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.viewJoin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        managementCart = new ManagementCart(this); // Initialize ManagementCart instance

        setVariable();
        initList();

        Button NavHomeBtn = findViewById(R.id.NavHomeBtn);
        NavHomeBtn.setOnClickListener(v -> {
            Intent intent2 = new Intent(CartActivity.this, HomeActivity.class);
            startActivity(intent2);
        });
    }

    private void initList() {
        if (managementCart.getListCart().isEmpty()) {
            findViewById(R.id.emptyTxt).setVisibility(View.VISIBLE);
            findViewById(R.id.NavHomeBtn).setVisibility(View.VISIBLE);
            findViewById(R.id.cartScrollview).setVisibility(View.GONE);
        } else {
            findViewById(R.id.emptyTxt).setVisibility(View.GONE);
            findViewById(R.id.NavHomeBtn).setVisibility(View.GONE);
            findViewById(R.id.cartScrollview).setVisibility(View.VISIBLE);
        }

        cartView = findViewById(R.id.cartView);
        cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        cartView.setAdapter(new CartAdapter(this, managementCart.getListCart(), new ChangeNumberItemListner() {
            @Override
            public void change() {
                calculatorCart();
            }
        }, managementCart));
    }

    private void setVariable() {
        ImageView backIcon = findViewById(R.id.backBtn4);
        backIcon.setOnClickListener(v -> finish());
    }

    private void calculatorCart() {
        double delivery = 10;

        double total = Math.round(managementCart.getTotalFee() + delivery);

        TextView deliveryTextView = findViewById(R.id.textView47);
        TextView totalTextView = findViewById(R.id.textView52);

        deliveryTextView.setText("LKR " + delivery);
        totalTextView.setText("LKR " + total);
    }
}
