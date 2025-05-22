package com.app.clipphy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.clipphy.Activity.ViewClipphyProductActivity;
import com.app.clipphy.Domain.AllProducts;
import com.app.clipphy.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyClipphyAdapter extends RecyclerView.Adapter<MyClipphyAdapter.ViewHolder> {
    private Context context;
    private ArrayList<AllProducts> clipphyArrayList;
    private final Object productLock = new Object();

    public MyClipphyAdapter(Context context, ArrayList<AllProducts> productsArrayList) {
        this.context = context;
        this.clipphyArrayList = productsArrayList;
    }

    public void loadClipphys() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String sharedPreferenceEmail = sharedPreferences.getString("email", "").trim();


        Log.d("loadClipphys", "Shared Preference Email: " + sharedPreferenceEmail);

        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("loadClipphys", "Task successful");
                        synchronized (productLock) {
                            clipphyArrayList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                boolean isMember = false;
                                Object membersObj = document.get("members");
                                if (membersObj == null) {
                                    Log.d("loadClipphys", "No 'members' field in document: " + document.getId());
                                } else if (membersObj instanceof List) {
                                    List<?> membersList = (List<?>) membersObj;
                                    for (Object memberObj : membersList) {
                                        if (memberObj instanceof String) {
                                            String memberString = (String) memberObj;
                                            // Extract email from the string
                                            int startIdx = memberString.indexOf("(");
                                            int endIdx = memberString.indexOf(")");
                                            if (startIdx != -1 && endIdx != -1 && endIdx > startIdx) {
                                                String email = memberString.substring(startIdx + 1, endIdx).trim();
                                                Log.d("loadClipphys", "Parsed Member email: " + email);

                                                if (sharedPreferenceEmail.equals(email)) {
                                                    isMember = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Log.d("loadClipphys", "'members' is not a list in document: " + document.getId());
                                }
                                if (isMember) {
                                    Log.d("loadClipphys", "Member found: " + document.getId());
                                    AllProducts allProducts = document.toObject(AllProducts.class);
                                    clipphyArrayList.add(allProducts);
                                }
                            }
                        }
                        Log.d("loadClipphys", "Products array size: " + clipphyArrayList.size());
                        notifyDataSetChanged();
                    } else {
                        Log.e("loadClipphys", "Task failed", task.getException());
                    }
                });
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.viewholder_my_clipphys, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        synchronized (productLock) {
            AllProducts product = clipphyArrayList.get(position);
            holder.title.setText(product.getTitle());
            holder.status.setText(product.getStatus());
            holder.count.setText("/"+product.getCount());
            holder.current_count.setText(product.getCurrent_count());
            holder.duration.setText(product.getDuration());
            holder.price.setText("LKR " + product.getPrice());

            int drawableResourceId = context.getResources().getIdentifier(
                    product.getPicAddress(),
                    "drawable",
                    context.getPackageName()
            );
            holder.picAddress.setImageResource(drawableResourceId);

            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, ViewClipphyProductActivity.class);
                intent.putExtra("title", product.getTitle());
                intent.putExtra("price", product.getPrice());
                intent.putExtra("priceSale", product.getPriceSale());
                intent.putExtra("status", product.getStatus());
                intent.putExtra("count", product.getCount());
                intent.putExtra("current_count", product.getCurrent_count());
                intent.putExtra("started_date", product.getStarted_date());
                intent.putExtra("picAddress", product.getPicAddress());
                intent.putExtra("description", product.getDescription());
                intent.putExtra("clipphyDetails", product.getClipphyDetails());
                intent.putExtra("product_Id", product.getProduct_Id());
                context.startActivity(intent);

            });
        }
    }

    @Override
    public int getItemCount() {
        synchronized (productLock) {
            return clipphyArrayList.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView price;
        TextView duration;
        TextView status;
        TextView current_count;
        TextView count;
        ImageView picAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titletxt);
            duration = itemView.findViewById(R.id.durationtxt);
            current_count = itemView.findViewById(R.id.c_counttxt);
            count = itemView.findViewById(R.id.countText);
            status = itemView.findViewById(R.id.statusText);
            price = itemView.findViewById(R.id.priceTxt);
            picAddress = itemView.findViewById(R.id.picAddress);
        }
    }
}
