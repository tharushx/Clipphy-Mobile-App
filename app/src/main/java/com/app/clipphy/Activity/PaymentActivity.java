package com.app.clipphy.Activity;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.clipphy.R;

import org.jetbrains.annotations.Nullable;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;

public class PaymentActivity extends AppCompatActivity {

    private static final int PAYHERE_REQUEST = 11010;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get product details from Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String price = intent.getStringExtra("price").replace(",", ""); // Remove commas
        String product_Id = intent.getStringExtra("product_Id");
        String description = intent.getStringExtra("description");
        String picAddress = intent.getStringExtra("picAddress");

        TextView title1 = findViewById(R.id.title1);
        title1.setText(title);

        TextView price1 = findViewById(R.id.price1);
        price1.setText("LKR " + price);

        TextView description1 = findViewById(R.id.description1);
        description1.setText(description);

        ImageView imageView1 = findViewById(R.id.imageView1);

        if (picAddress != null && !picAddress.isEmpty()) {
            int drawableResourceId = getApplicationContext().getResources().getIdentifier(
                    picAddress, "drawable", getApplicationContext().getPackageName()
            );

            if (drawableResourceId != 0) {
                imageView1.setImageResource(drawableResourceId);
            } else {
                Log.e(TAG, "Drawable resource not found for picAddress: " + picAddress);
            }
        } else {
            Log.e(TAG, "picAddress is null or empty");
        }

        // Initialize payment request
        InitRequest req = new InitRequest();
        req.setMerchantId("1229576"); // Replace with your sandbox Merchant ID
        req.setCurrency("LKR");       // Currency code LKR/USD/GBP/EUR/AUD
        req.setAmount(Double.parseDouble("100")); // Use the price from the product details
        req.setOrderId(product_Id);  // Use the product ID as the unique reference ID
        req.setItemsDescription(title); // Use the product title as the item description
        req.setCustom1("This is the custom message 1");
        req.setCustom2("This is the custom message 2");
        req.getCustomer().setFirstName("CustomerFirstName");
        req.getCustomer().setLastName("CustomerLastName");
        req.getCustomer().setEmail("customer@example.com");
        req.getCustomer().setPhone("+94771234567");
        req.getCustomer().getAddress().setAddress("No.1, Galle Road");
        req.getCustomer().getAddress().setCity("Colombo");
        req.getCustomer().getAddress().setCountry("Sri Lanka");

        req.setNotifyUrl("http://localhost"); // Notify Url
        req.getCustomer().getDeliveryAddress().setAddress("No.2, Kandy Road");
        req.getCustomer().getDeliveryAddress().setCity("Kadawatha");
        req.getCustomer().getDeliveryAddress().setCountry("Sri Lanka");

        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);

        Intent intentbuy = new Intent(PaymentActivity.this, PHMainActivity.class);
        intentbuy.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        startActivityForResult(intentbuy, PAYHERE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYHERE_REQUEST && data != null) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
            String msg;
            TextView textView = findViewById(R.id.textView45);
            if (resultCode == Activity.RESULT_OK) {
                if (response != null && response.isSuccess()) {
                    msg = "Payment successful: " + response.getData().toString();
                    Log.d(TAG, msg);
                    textView.setText(msg); // Update the UI with the success message
                } else {
                    msg = "Payment failed: " + response.toString();
                    Log.d(TAG, msg);
                    textView.setText(msg); // Update the UI with the failure message
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (response != null) {
                    textView.setText(response.toString()); // Update the UI with the canceled message
                } else {
                    textView.setText("User canceled the request");
                }
            }
        }
    }
}
