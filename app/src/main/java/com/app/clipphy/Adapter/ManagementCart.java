package com.app.clipphy.Adapter;

import android.content.Context;
import android.widget.Toast;

import com.app.clipphy.Domain.AllProducts;

import java.util.ArrayList;

public class ManagementCart {
    private Context context;
    private TinyDB tinyDB;

    public ManagementCart(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
    }

    public void insertFood(AllProducts item) {
        ArrayList<AllProducts> listpop = getListCart();
        boolean existAlready = false;
        int n = 0;
        for (int i = 0; i < listpop.size(); i++) {
            // Add a null check before calling equals
            if (listpop.get(i).getTitle() != null && listpop.get(i).getTitle().equals(item.getTitle())) {
                existAlready = true;
                n = i;
                break;
            }
        }
        if (existAlready) {
            listpop.get(n).setNumberincart(item.getNumberincart());
        } else {
            listpop.add(item);
        }
        tinyDB.putListObject("CartList", listpop);
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show();
    }


    public ArrayList<AllProducts> getListCart() {
        ArrayList<AllProducts> list = tinyDB.getListObject("CartList", AllProducts.class);
        return list != null ? list : new ArrayList<>();
    }

    public Double getTotalFee() {
        ArrayList<AllProducts> listItem = getListCart();
        double fee = 0;
        for (AllProducts product : listItem) {
            fee += Double.parseDouble(product.getPrice()) * Integer.parseInt(product.getNumberincart());
        }
        return fee;
    }

    public void minusNumberItem(ArrayList<AllProducts> listItem, int position, ChangeNumberItemListner changeNumberItemsListener) {
        if (Integer.parseInt(listItem.get(position).getNumberincart()) == 1) {
            listItem.remove(position);
        } else {
            listItem.get(position).setNumberincart(String.valueOf(Integer.parseInt(listItem.get(position).getNumberincart()) - 1));
        }
        tinyDB.putListObject("CartList", listItem);
        changeNumberItemsListener.change();
    }

    public void plusNumberItem(ArrayList<AllProducts> listItem, int position, ChangeNumberItemListner changeNumberItemsListener) {
        listItem.get(position).setNumberincart(String.valueOf(Integer.parseInt(listItem.get(position).getNumberincart()) + 1));
        tinyDB.putListObject("CartList", listItem);
        changeNumberItemsListener.change();
    }
}
