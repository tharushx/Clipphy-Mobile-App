package com.app.clipphy.Activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.clipphy.Adapter.MyClipphyAdapter;
import com.app.clipphy.Domain.AllProducts;
import com.app.clipphy.R;

import java.util.ArrayList;

public class MyClipphyActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyClipphyAdapter adapter;
    private ArrayList<AllProducts> clipphyArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_clipphy);

        // Edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backIcon2 = findViewById(R.id.backBtn2);
        backIcon2.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.cliphhysRecycleView);
        clipphyArrayList = new ArrayList<>();
        adapter = new MyClipphyAdapter(this, clipphyArrayList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.loadClipphys();
    }
}
