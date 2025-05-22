package com.app.clipphy.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.app.clipphy.Adapter.ViewPagerAdapter;
import com.app.clipphy.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    RelativeLayout PicImagebutton, PicImageUtilityButton;
    ViewPager viewPager, viewPagerUtility;
    Uri ImageUri, UtilityImageUri;
    ArrayList<Uri> ChooseImageList, ChooseUtilityImageList;
    ImageView profileImageView, editIcon;
    TextView profilename;
    EditText nicEditText, address1EditText, address2EditText, postalCodeEditText, mobileText, nameText;

    private Spinner spinnerDistricts, spinnerProvinces;
    private FirebaseFirestore db;
    private ArrayList<String> districtList, provinceList;
    private ArrayAdapter<String> districtAdapter, provinceAdapter;

    private static final int PROFILE_IMAGE_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.viewJoin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Window window=ProfileActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(ProfileActivity.this,R.color.main_color));

        DrawerLayout drawerLayout1 = findViewById(R.id.drawerLayout1);
        Toolbar toolbar1 = findViewById(R.id.toolbar1);
        ImageView toolbarImg = findViewById(R.id.toolbarImg);
        NavigationView navigationView1 = findViewById(R.id.navigationView1);

        toolbarImg.setOnClickListener(view -> {
            if (drawerLayout1.isDrawerOpen(navigationView1)) {
                drawerLayout1.closeDrawer(navigationView1);
            } else {
                drawerLayout1.openDrawer(navigationView1);
            }
        });

        navigationView1.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.nav_menu1){
                    Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                }else if (item.getItemId() == R.id.nav_menu2){
                    Intent intent = new Intent(ProfileActivity.this, MapActivity.class);
                    startActivity(intent);
                }
                return true;
            }

        });


        spinnerDistricts = findViewById(R.id.spinner3);
        spinnerProvinces = findViewById(R.id.spinner4);
        districtList = new ArrayList<>();
        provinceList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        loadProvinces();

        spinnerProvinces.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedProvince = provinceList.get(position);
                loadDistricts(selectedProvince);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, provinceList);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvinces.setAdapter(provinceAdapter);

        districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districtList);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistricts.setAdapter(districtAdapter);

        nameText = findViewById(R.id.nameText);
        profilename = findViewById(R.id.profilename);
        nicEditText = findViewById(R.id.nicEditText);
        mobileText = findViewById(R.id.mobileText);
        address1EditText = findViewById(R.id.address1EditText);
        address2EditText = findViewById(R.id.address2EditText);
        postalCodeEditText = findViewById(R.id.postalCodeEditText);
        profileImageView = findViewById(R.id.profileImageView);
        profileImageView = findViewById(R.id.profileImageView);
        editIcon = findViewById(R.id.editIcon);

        editIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PROFILE_IMAGE_REQUEST_CODE);
        });

        ImageView backIcon2 = findViewById(R.id.backBtn2);
        backIcon2.setOnClickListener(v -> finish());


        ImageView myClipphyBtn = findViewById(R.id.myClipphyBtn);
        myClipphyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MyClipphyActivity.class);
            startActivity(intent);
        });

        Button saveChangesButton = findViewById(R.id.saveChangesButton);
        saveChangesButton.setOnClickListener(v -> {
            String email = getEmailFromSharedPreferences().trim();
            if (email != null && !email.isEmpty()) {
                Log.d("ProfileActivity", "Querying Firestore with email: " + email);
                String nic = nicEditText.getText().toString().trim();
                String mobile = mobileText.getText().toString().trim();
                String address1 = address1EditText.getText().toString().trim();
                String address2 = address2EditText.getText().toString().trim();
                String postalCode = postalCodeEditText.getText().toString().trim();
                String province = spinnerProvinces.getSelectedItem().toString();
                String district = spinnerDistricts.getSelectedItem().toString();

                if (nic.isEmpty() || mobile.isEmpty() || address1.isEmpty() || address2.isEmpty() || province.isEmpty() || district.isEmpty() || postalCode.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Please complete all fields before saving.", Toast.LENGTH_SHORT).show();
                    return; // Return early if any field is empty
                }

                if (ImageUri != null) {
                    saveImageToStorage(ImageUri);
                }
                if (UtilityImageUri != null) {
                    saveImageToStorage(UtilityImageUri);
                }

                saveUserDetailsToFirestore(email, nic, mobile, address1, address2, province, district, postalCode);
            } else {
                Toast.makeText(ProfileActivity.this, "No email found in shared preferences", Toast.LENGTH_SHORT).show();
            }
        });

        String userEmail = getEmailFromSharedPreferences(); // Assuming you have this method to get the email
        getUserDocumentId(userEmail, new FirestoreCallback() {
            @Override
            public void onRetrieve(String documentId, String firstName, String lastName, String nic, String mobile, String address1, String address2, String province, String district, String postalCode) {
                nameText.setText(firstName + " " + lastName);
                profilename.setText(firstName + " " + lastName);
                nicEditText.setText(nic);
                mobileText.setText(mobile);
                address1EditText.setText(address1);
                address2EditText.setText(address2);
                postalCodeEditText.setText(postalCode);

                if (provinceList.contains(province)) {
                    int provincePosition = provinceList.indexOf(province);
                    spinnerProvinces.setSelection(provincePosition);

                    loadDistricts(province);
                    spinnerDistricts.post(() -> {
                        if (districtList.contains(district)) {
                            int districtPosition = districtList.indexOf(district);
                            spinnerDistricts.setSelection(districtPosition);
                        }
                    });
                }
            }

            @Override
            public void onUpdate(boolean success) {
                if (success) {
                    Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCallback(String documentId) {
                // This can be used for update scenarios if needed
            }
        });

        loadProvinces();
    }

    private String getEmailFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        Log.d("ProfileActivity", "Retrieved email from SharedPreferences: " + email);
        return email;
    }

    private interface FirestoreCallback {
        void onRetrieve(String documentId, String firstName, String lastName, String nic, String mobile, String address1, String address2, String province, String district, String postalCode);
        void onUpdate(boolean success);
        void onCallback(String documentId);
    }

    private void getUserDocumentId(String email, FirestoreCallback callback) {
        db.collection("users").whereEqualTo("email", email).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            String firstName = document.getString("firstName");
                            String lastName = document.getString("lastName");
                            String nic = document.getString("nic");
                            String mobile = document.getString("mobile");
                            String address1 = document.getString("address1");
                            String address2 = document.getString("address2");
                            String province = document.getString("province");
                            String district = document.getString("district");
                            String postalCode = document.getString("postalCode");
                            Log.d("ProfileActivity", "Retrieved document ID: " + documentId);
                            callback.onRetrieve(documentId, firstName, lastName, nic, mobile, address1, address2, province, district, postalCode);
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "No user found with this email", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Failed to query Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUserDetailsToFirestore(String email, String nic, String mobile, String address1, String address2, String province, String district, String postalCode) {

        if (nic.isEmpty() || mobile.isEmpty() || address1.isEmpty() || address2.isEmpty() || province.isEmpty() || district.isEmpty() || postalCode.isEmpty()) {
            Toast.makeText(ProfileActivity.this, "Please complete all fields before saving.", Toast.LENGTH_SHORT).show();
            return; // Return early if any field is empty
        }

        getUserDocumentId(email, new FirestoreCallback() {
            @Override
            public void onRetrieve(String documentId, String firstName, String lastName, String nic, String mobile, String address1, String address2, String province, String district, String postalCode) {
                // Not used in this context
            }

            @Override
            public void onUpdate(boolean success) {
                if (success) {
                    Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCallback(String documentId) {
                Map<String, Object> userDetails = new HashMap<>();
                userDetails.put("nic", nic);
                userDetails.put("mobile", mobile);
                userDetails.put("address1", address1);
                userDetails.put("address2", address2);
                userDetails.put("province", province);
                userDetails.put("district", district);
                userDetails.put("postalCode", postalCode);

                db.collection("users").document(documentId)
                        .update(userDetails)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("ProfileActivity", "Profile updated successfully!");
                            onUpdate(true);
                        })
                        .addOnFailureListener(e -> {
                            Log.e("ProfileActivity", "Failed to update profile: " + e.getMessage());
                            onUpdate(false);
                        });
            }
        });
    }

    private void loadProvinces() {
        db.collection("provinces").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                provinceList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    provinceList.add(document.getId());
                }
                provinceAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(ProfileActivity.this, "Failed to load provinces", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDistricts(String province) {
        db.collection("provinces").document(province).collection("districts").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                districtList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    districtList.add(document.getId());
                }
                districtAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(ProfileActivity.this, "Failed to load districts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void PicImageFromGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count && i < 2; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    if (requestCode == 1) {
                        ImageUri = uri;
                        ChooseImageList.add(ImageUri);
                        saveImageToStorage(ImageUri);
                    } else if (requestCode == 2) {
                        UtilityImageUri = uri;
                        ChooseUtilityImageList.add(UtilityImageUri);
                        saveImageToStorage(UtilityImageUri);
                    }
                }
            } else if (data.getData() != null) {
                Uri uri = data.getData();
                if (requestCode == 1) {
                    ImageUri = uri;
                    ChooseImageList.add(ImageUri);
                    saveImageToStorage(ImageUri);

                    profileImageView.setImageURI(ImageUri);
                    Log.d("ProfileActivity", "Profile image URI: " + ImageUri.toString());

                } else if (requestCode == 2) {
                    UtilityImageUri = uri;
                    ChooseUtilityImageList.add(UtilityImageUri);
                    saveImageToStorage(UtilityImageUri);
                }
            }
            SetAdapter();
        }
    }

    private void SetAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, ChooseImageList);
        viewPager.setAdapter(adapter);

        ViewPagerAdapter adapterUtility = new ViewPagerAdapter(this, ChooseUtilityImageList);
        viewPagerUtility.setAdapter(adapterUtility);
    }

    private void saveImageToStorage(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Clipphy");
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
            File file = new File(storageDir, fileName);
            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
