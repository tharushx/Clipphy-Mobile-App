package com.app.clipphy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.app.clipphy.Activity.ViewProductActivity;
import com.app.clipphy.Domain.AllProducts;
import com.app.clipphy.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AllProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_AVAILABLE_PRODUCT = 0;
    private static final int VIEW_TYPE_NOT_AVAILABLE_PRODUCT = 1;

    private Context context;
    private ArrayList<AllProducts> productsArrayList;
    private final Object productLock = new Object();

    public AllProductAdapter(Context context, ArrayList<AllProducts> productsArrayList) {
        this.context = context;
        this.productsArrayList = productsArrayList;
    }

    public void loadOngoingProducts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        synchronized (productLock) {
                            productsArrayList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                AllProducts allProducts = document.toObject(AllProducts.class);
                                productsArrayList.add(allProducts);
                            }
                        }
                        notifyDataSetChanged();
                    } else {
                        Log.e("ongoing", "Not Connected");
                    }
                });
    }

    @Override
    public int getItemViewType(int position) {
        AllProducts product = productsArrayList.get(position);
        if ("Available for Clipphy".equals(product.getClipphyStatus())) {
            return VIEW_TYPE_AVAILABLE_PRODUCT;
        } else {
            return VIEW_TYPE_NOT_AVAILABLE_PRODUCT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_NOT_AVAILABLE_PRODUCT) {
            View view = LayoutInflater.from(context).inflate(R.layout.viewholder_all_list, parent, false);
            return new NotAvailableViewholder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.viewholder_ongoing_list, parent, false);
            return new OngoingViewholder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AllProducts product = productsArrayList.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_NOT_AVAILABLE_PRODUCT) {
            NotAvailableViewholder notAvailableHolder = (NotAvailableViewholder) holder;
            notAvailableHolder.title.setText(product.getTitle());
            notAvailableHolder.price.setText("LKR " + product.getPrice());

            int drawableResourceId = context.getResources().getIdentifier(
                    product.getPicAddress(),
                    "drawable",
                    context.getPackageName()
            );
            notAvailableHolder.picAddress.setImageResource(drawableResourceId);
            notAvailableHolder.ratingBar.setRating(product.getRatingBar());

            notAvailableHolder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, ViewProductActivity.class);
                intent.putExtra("title", product.getTitle());
                intent.putExtra("price", product.getPrice());
                intent.putExtra("priceSale", product.getPriceSale());
                intent.putExtra("picAddress", product.getPicAddress());
                intent.putExtra("description", product.getDescription());
                intent.putExtra("clipphyDetails", product.getClipphyDetails());
                intent.putExtra("product_Id", product.getProduct_Id());
                context.startActivity(intent);
            });
        } else {
            OngoingViewholder ongoingHolder = (OngoingViewholder) holder;
            ongoingHolder.title.setText(product.getTitle());
            ongoingHolder.price.setText("LKR " + product.getPrice());

            if (product.getCurrent_count() != null && product.getCount() != null && product.getCurrent_count().equals(product.getCount())) {
                ongoingHolder.status.setText("Started");
                ongoingHolder.label.setBackgroundResource(R.drawable.label_started);
            } else {
                ongoingHolder.status.setText("Pending");
                ongoingHolder.label.setBackgroundResource(R.drawable.label_pending);
            }

            ongoingHolder.count.setText("/" + product.getCount());

            if (product.getCurrent_count() != null) {
                ongoingHolder.current_count.setText(product.getCurrent_count());
            } else {
                ongoingHolder.current_count.setText("N/A");
            }

            int drawableResourceId = context.getResources().getIdentifier(
                    product.getPicAddress(),
                    "drawable",
                    context.getPackageName()
            );
            ongoingHolder.picAddress.setImageResource(drawableResourceId);
            ongoingHolder.ratingBar.setRating(product.getRatingBar());

            ongoingHolder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, ViewProductActivity.class);
                intent.putExtra("title", product.getTitle());
                intent.putExtra("price", product.getPrice());
                intent.putExtra("priceSale", product.getPriceSale());
                intent.putExtra("picAddress", product.getPicAddress());
                intent.putExtra("count", product.getCount());
                intent.putExtra("current_count", product.getCurrent_count());
                intent.putExtra("status", ongoingHolder.status.getText().toString());
                intent.putExtra("duration", product.getDuration());
                intent.putExtra("description", product.getDescription());
                intent.putExtra("clipphyDetails", product.getClipphyDetails());
                intent.putExtra("product_Id", product.getProduct_Id());
                intent.putExtra("clipphyStatus", product.getClipphyStatus());
                context.startActivity(intent);
            });
        }

    }

    @Override
    public int getItemCount() {
        synchronized (productLock) {
            return productsArrayList.size();
        }
    }

    public static class OngoingViewholder extends RecyclerView.ViewHolder {
        TextView title;
        TextView price;
        TextView status;
        TextView count;
        TextView current_count;
        ImageView picAddress;
        RatingBar ratingBar;
        ConstraintLayout label;

        public OngoingViewholder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.productTitle);
            count = itemView.findViewById(R.id.countText);
            current_count = itemView.findViewById(R.id.countText2);
            status = itemView.findViewById(R.id.statusText);
            price = itemView.findViewById(R.id.price);
            picAddress = itemView.findViewById(R.id.picAddress);
            ratingBar = itemView.findViewById(R.id.productRating);
            label = itemView.findViewById(R.id.label);
        }
    }

    public static class NotAvailableViewholder extends RecyclerView.ViewHolder {
        TextView title;
        TextView price;
        ImageView picAddress;
        RatingBar ratingBar;

        public NotAvailableViewholder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.productTitle);
            price = itemView.findViewById(R.id.price);
            picAddress = itemView.findViewById(R.id.picAddress);
            ratingBar = itemView.findViewById(R.id.productRating);
        }
    }
}
