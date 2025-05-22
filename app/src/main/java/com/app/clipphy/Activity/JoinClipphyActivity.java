package com.app.clipphy.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.clipphy.Domain.AllProducts;
import com.app.clipphy.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JoinClipphyActivity extends AppCompatActivity {

    private String title;
    private String price;
    private String priceSale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("JoinClipphyActivity", "onCreate called");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join_clipphy);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.viewJoin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backIcon3 = findViewById(R.id.backBtn3);
        backIcon3.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        price = intent.getStringExtra("price");
        priceSale = intent.getStringExtra("priceSale");
        String description = intent.getStringExtra("description");
        String count = intent.getStringExtra("count");
        String current_count = intent.getStringExtra("current_count");
        String status = intent.getStringExtra("status");
        String duration = intent.getStringExtra("duration");
        String clipphyDetails = intent.getStringExtra("clipphyDetails");
        String picAddress = intent.getStringExtra("picAddress");

        Log.d("JoinClipphyActivity", "Received price: " + priceSale);

        TextView productTitle = findViewById(R.id.textView19);
        TextView productPrice = findViewById(R.id.textView20);
        TextView ProductpriceSale = findViewById(R.id.textView10);
        ImageView productImage = findViewById(R.id.homeBtn);
        TextView productCount = findViewById(R.id.textView30);
        TextView productCountCurrent = findViewById(R.id.textView26);
        TextView productStatus = findViewById(R.id.textView24);
        TextView productDuration = findViewById(R.id.textView28);
        TextView productClipphyDetails = findViewById(R.id.editTextTextMultiLine);

        productTitle.setText(title);
        productPrice.setText("LKR " + price);
        ProductpriceSale.setText("LKR " + priceSale);
        productCount.setText("of "+count);
        productCountCurrent.setText(current_count);
        productStatus.setText(status);
        productDuration.setText(duration);
        productClipphyDetails.setText(clipphyDetails);

        if (picAddress != null && !picAddress.isEmpty()) {
            int drawableResourceId = getResources().getIdentifier(picAddress, "drawable", getPackageName());
            if (drawableResourceId != 0) {
                productImage.setImageResource(drawableResourceId);
            } else {
                Log.e("JoinClipphyActivity", "Drawable resource not found for picAddress: " + picAddress);
            }
        } else {
            Log.e("JoinClipphyActivity", "picAddress is null or empty");
        }

        Button joinBtn = findViewById(R.id.joinBtn);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("JoinClipphyActivity", "Join button clicked");

                Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.button_animation);
                v.startAnimation(animation);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        JoinClipphyActivity.this.checkUserPersonalInformationAndJoin(title, price);
                    }
                }, animation.getDuration());
            }
        });

    }

    private void checkUserPersonalInformationAndJoin(String title, String price) {
        Log.d("JoinClipphyActivity", "checkUserPersonalInformationAndJoin called");
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String firstName = sharedPreferences.getString("firstName", null);
        String lastName = sharedPreferences.getString("lastName", null);
        String email = sharedPreferences.getString("email", null);
        Log.d("JoinClipphyActivity", "SharedPreferences - First Name: " + firstName + ", Last Name: " + lastName + ", Email: " + email);

        if (email != null && firstName != null && lastName != null) {
            Log.d("JoinClipphyActivity", "Querying document for email: " + email);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0); // Assuming email is unique and there’s only one match
                            Log.d("JoinClipphyActivity", "Fetching user document from Firestore");

                            String docFirstName = documentSnapshot.getString("firstName");
                            String docLastName = documentSnapshot.getString("lastName");
                            String nic = documentSnapshot.getString("nic");
                            String mobile = documentSnapshot.getString("mobile");
                            String address = documentSnapshot.contains("address1") ? documentSnapshot.getString("address1") : documentSnapshot.getString("address");

                            Log.d("JoinClipphyActivity", "Document Information - First Name: " + docFirstName + ", Last Name: " + docLastName);
                            Log.d("JoinClipphyActivity", "User Information - NIC: " + nic + ", Mobile: " + mobile + ", Address: " + address);

                            if (firstName.equals(docFirstName) && lastName.equals(docLastName)) {
                                if (nic == null || nic.isEmpty() || mobile == null || mobile.isEmpty() || address == null || address.isEmpty()) {
                                    showAlertAndNavigateToUpdateInformation();
                                } else {
                                    checkIfAlreadyJoinedAndAddContributor(title, price);
                                }
                            } else {
                                Log.w("JoinClipphyActivity", "First Name or Last Name does not match");
                                // Handle mismatch case
                            }
                        } else {
                            Log.w("JoinClipphyActivity", "User document does not exist");
                        }
                    })
                    .addOnFailureListener(e -> Log.w("JoinClipphyActivity", "Error fetching user information", e));
        } else {
            Log.e("JoinClipphyActivity", "Email, First Name, or Last Name is null");
        }
    }

    private void showAlertAndNavigateToUpdateInformation() {
        Log.d("JoinClipphyActivity", "Showing alert to navigate to update information");
        new AlertDialog.Builder(this)
                .setTitle("Incomplete Information")
                .setMessage("Please update your personal information to join the product.")
                .setPositiveButton("Update Information", (dialog, which) -> {
                    Intent intent = new Intent(JoinClipphyActivity.this, ProfileActivity.class); // Assuming you have an activity for updating info
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void checkIfAlreadyJoinedAndAddContributor(String title, String price) {
        Log.d("JoinClipphyActivity", "checkIfAlreadyJoinedAndAddContributor called");
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String firstName = sharedPreferences.getString("firstName", null);
        String lastName = sharedPreferences.getString("lastName", null);
        String email = sharedPreferences.getString("email", null);

        if (firstName != null && lastName != null && email != null) {
            String member = firstName + " " + lastName + " (" + email + ")";
            Log.d("JoinClipphyActivity", "Member to add: " + member);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("products")
                    .whereEqualTo("title", title)
                    .whereEqualTo("price", price)
                    .get()
                    .addOnCompleteListener(task -> {
                        Log.d("JoinClipphyActivity", "Fetching product document from Firestore");
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("JoinClipphyActivity", "Product document found");
                                AllProducts product = document.toObject(AllProducts.class);

                                if (product.getMembers() != null && product.getMembers().contains(member)) {
                                    showAlertAlreadyJoined();
                                    return;
                                }

                                int currentCount = 0;
                                int totalCount = 0;
                                if (product.getCurrent_count() != null) {
                                    currentCount = Integer.parseInt(product.getCurrent_count());
                                }
                                if (product.getCount() != null) {
                                    totalCount = Integer.parseInt(product.getCount());
                                }

                                if (currentCount >= totalCount) {
                                    showAlertMaxMembersExceeded();

                                    // Add current date to database using server timestamp
                                    db.collection("products")
                                            .document(document.getId())
                                            .update("date", FieldValue.serverTimestamp())
                                            .addOnSuccessListener(aVoid -> Log.d("JoinClipphyActivity1", "Date exceeded added"))
                                            .addOnFailureListener(e -> Log.w("JoinClipphyActivity1", "Error adding dateExceeded", e));
                                } else {
                                    int newCount = currentCount + 1;

                                    db.collection("products")
                                            .document(document.getId())
                                            .update(
                                                    "current_count", String.valueOf(newCount),
                                                    "members", FieldValue.arrayUnion(member)
                                            )
                                            .addOnSuccessListener(aVoid -> showAlertJoinSuccess())
                                            .addOnFailureListener(e -> Log.w("JoinClipphyActivity", "Error adding contributor or updating current_count", e));
                                }
                            }
                        } else {
                            Log.w("JoinClipphyActivity", "Product not found or query failed", task.getException());
                        }
                    });
        } else {
            Log.e("JoinClipphyActivity", "Customer name or email is null");
        }
    }



    private void showAlertAlreadyJoined() {
        Log.d("JoinClipphyActivity", "Showing alert: Already Joined");
        new AlertDialog.Builder(this)
                .setTitle("Already Joined")
                .setMessage("You have already joined this product.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showAlertMaxMembersExceeded() {
        Log.d("JoinClipphyActivity", "Showing alert: Max Members Exceeded");
        new AlertDialog.Builder(this)
                .setTitle("Join Failed")
                .setMessage("The maximum number of members for this product has been reached.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showAlertJoinSuccess() {
        Log.d("JoinClipphyActivity", "Showing alert: Join Successful");
        new AlertDialog.Builder(this)
                .setTitle("Join Successful")
                .setMessage("You have successfully joined the product.")
                .setPositiveButton("OK", null)
                .show();
    }
}
