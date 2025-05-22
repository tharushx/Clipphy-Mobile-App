package com.app.clipphy.Activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.clipphy.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class ViewClipphyProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_clipphy_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backIcon = findViewById(R.id.bcBtn);
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

        ImageView productImage = findViewById(R.id.picAddress);
        TextView productTitle = findViewById(R.id.titletxt);
        TextView productPrice = findViewById(R.id.priceTxt);
        TextView productPriceSale = findViewById(R.id.priceTxt2);
//        EditText productDescription = findViewById(R.id.productDescriptionTxt);
//        EditText clipphyDetailsView = findViewById(R.id.clipphyDetailsTxt);
//        TextView productStatus = findViewById(R.id.statusTxt);

        productTitle.setText(title);
        productPrice.setText("LKR " + price);
        productPriceSale.setText("Monthly Payable - LKR " + price);
//        productStatus.setText(status);
//        productDescription.setText(description);
//        clipphyDetailsView.setText(clipphyDetails);


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



        PieChart pieChart1 = findViewById(R.id.pieChart1);

        ArrayList<PieEntry> pieEntryList = new ArrayList<>();

        float currentCountValue = 0f;

        // Add the current_count value to the pie chart
        if (current_count != null && !current_count.isEmpty()) {
            currentCountValue = Float.parseFloat(current_count);
            pieEntryList.add(new PieEntry(currentCountValue, "Current Count"));
        }

        // Add the remaining count value to the pie chart
        if (count != null && !count.isEmpty()) {
            float totalCountValue = Float.parseFloat(count);
            float remainingCountValue = totalCountValue - currentCountValue;
            pieEntryList.add(new PieEntry(remainingCountValue, "Remaining Count"));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "");

        pieDataSet.setColors(new int[] { R.color.pie_color_2, R.color.pie_color_1 }, this);

        PieData pieData = new PieData(pieDataSet);
        pieChart1.setData(pieData);
        pieData.setValueTextSize(12);

        pieChart1.animateY(2000,Easing.EaseInCirc);

        pieChart1.setCenterText("Clipphy Members Count");

        pieChart1.invalidate(); // refresh

        Legend legend = pieChart1.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(12f);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);

    }
}
