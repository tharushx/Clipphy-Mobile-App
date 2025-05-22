package com.app.clipphy.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.clipphy.Domain.AllProducts;
import com.app.clipphy.R;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private static final String TAG = "CartAdapter"; // Define TAG here

    private Context context;
    private ArrayList<AllProducts> listCart;
    private ChangeNumberItemListner changeNumberItemListner;
    private ManagementCart managementCart;

    public CartAdapter(Context context, ArrayList<AllProducts> listCart, ChangeNumberItemListner changeNumberItemListner, ManagementCart managementCart) {
        this.context = context;
        this.listCart = listCart;
        this.changeNumberItemListner = changeNumberItemListner;
        this.managementCart = managementCart;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AllProducts product = listCart.get(position);

        // Log product details
        Log.d(TAG, "Binding product at position " + position + ": " + product.getTitle());
        Log.d(TAG, "Product details - Title: " + product.getTitle() + ", Price: " + product.getPrice() + ", Number in cart: " + product.getNumberincart());

        String picAddress = product.getPicAddress();
        if (picAddress != null && !picAddress.isEmpty()) {
            int drawableResourceId = context.getResources().getIdentifier(
                    picAddress, "drawable", context.getPackageName()
            );

            if (drawableResourceId != 0) {
                holder.productImage.setImageResource(drawableResourceId);
            } else {
                Log.e(TAG, "Drawable resource not found for picAddress: " + picAddress);
            }
        } else {
            Log.e(TAG, "picAddress is null or empty");
        }

        holder.productTitle.setText(product.getTitle());
        holder.productPrice.setText(String.valueOf(product.getPrice()));
        holder.numberItem.setText(String.valueOf(product.getNumberincart()));

        holder.plusItem.setOnClickListener(v -> {
            managementCart.plusNumberItem(listCart, position, changeNumberItemListner);
            notifyItemChanged(position);
            Log.d(TAG, "Plus button clicked for position " + position);
        });

        holder.minusItem.setOnClickListener(v -> {
            managementCart.minusNumberItem(listCart, position, changeNumberItemListner);
            Log.d(TAG, "Minus button clicked for position " + position + ", Number in cart: " + product.getNumberincart());
            if (Integer.parseInt(product.getNumberincart()) == 0) {
                listCart.remove(position);
                notifyItemRemoved(position);
                Log.d(TAG, "Item removed at position " + position);
            } else {
                notifyItemChanged(position);
                Log.d(TAG, "Item updated at position " + position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return listCart.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productTitle, productPrice, numberItem, plusItem, minusItem;
        ImageView productImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.titleTxt);
            productPrice = itemView.findViewById(R.id.feeEachItem);
            numberItem = itemView.findViewById(R.id.numberItemTxt);
            plusItem = itemView.findViewById(R.id.plusCartBtn);
            minusItem = itemView.findViewById(R.id.minusCartBtn);
            productImage = itemView.findViewById(R.id.picAddress); // Ensure the correct ID
        }
    }
}
