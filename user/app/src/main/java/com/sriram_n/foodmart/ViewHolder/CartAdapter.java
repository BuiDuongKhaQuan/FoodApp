package com.sriram_n.foodmart.ViewHolder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;
import com.sriram_n.foodmart.Cart;
import com.sriram_n.foodmart.Common.Common;
import com.sriram_n.foodmart.Database.Database;
import com.sriram_n.foodmart.Interface.ItemClickListener;
import com.sriram_n.foodmart.Model.Food;
import com.sriram_n.foodmart.Model.Order;
import com.sriram_n.foodmart.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        , View.OnCreateContextMenuListener {

    public TextView txt_cart_name, txt_price, txt_quantity;
    public ImageView img_cart_count, img_product, btnPlus, btnMinus;
    private ItemClickListener itemClickListener;

    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_cart_name = itemView.findViewById(R.id.cart_item_name);
        txt_price = itemView.findViewById(R.id.cart_item_Price);
        txt_quantity = itemView.findViewById(R.id.txtQuantity);
        img_cart_count = itemView.findViewById(R.id.cart_item_count);
        img_product = itemView.findViewById(R.id.txtImg);
        btnPlus = itemView.findViewById(R.id.btnPlus);
        btnMinus = itemView.findViewById(R.id.btnMinus);


        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Tùy chọn");
        menu.add(0, 0, getAdapterPosition(), Common.DELETE);
    }
}

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
    private TextView txtTotalPrice;
    private List<Order> listData;
    private Context context;

    public CartAdapter(List<Order> listData, Context context, TextView txtTotalPrice) {
        this.listData = listData;
        this.context = context;
        this.txtTotalPrice = txtTotalPrice;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);
        return new CartViewHolder(itemView);
    }

    Locale locale = new Locale("vi", "VN");
    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Picasso.with(context).load(listData.get(position).getImage()).into(holder.img_product);

        int price = Integer.parseInt(listData.get(position).getPrice());
        holder.txt_price.setText(fmt.format(price));
        holder.txt_quantity.setText(listData.get(position).getQuantity());
        holder.txt_cart_name.setText(listData.get(position).getProductName());
        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int quantity = Integer.parseInt(listData.get(position).getQuantity()) + 1;
                Order product = listData.get(position);
                new Database(v.getContext()).updateToCart(String.valueOf(quantity), product.getProductId());
                product.setQuantity(String.valueOf(quantity));
                holder.txt_quantity.setText(String.valueOf(quantity));
                updateTotalOrderValue();
            }
        });
        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int quantity = Integer.parseInt(listData.get(position).getQuantity());
                Order product = listData.get(position);
                if (quantity == 1) {
                    new Database(v.getContext()).deleteCart(product.getProductId());
                    listData.remove(position);
                    notifyDataSetChanged();
                } else {
                    quantity--;
                    new Database(v.getContext()).updateToCart(String.valueOf(quantity), product.getProductId());
                    listData.get(position).setQuantity(String.valueOf(quantity));
                    holder.txt_quantity.setText(String.valueOf(quantity));
                }
                updateTotalOrderValue();
            }
        });
    }

    private void updateTotalOrderValue() {
        int totalValue = 0;
        for (Order order : listData) {
            int price = Integer.parseInt(order.getPrice()) * Integer.parseInt(order.getQuantity());
            totalValue += price;
        }
        // Cập nhật giá trị đơn hàng trong TextView txtTotalPrice
        txtTotalPrice.setText(fmt.format(totalValue));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
