package com.sriram_n.foodmart.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sriram_n.foodmart.Interface.ItemClickListener;
import com.sriram_n.foodmart.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView food_name, food_price;
    public ImageView food_image, btnCart;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);

        food_price = itemView.findViewById(R.id.lblPrice);
        food_name = itemView.findViewById(R.id.lblProducrName);
        food_image = itemView.findViewById(R.id.imageView5);
        btnCart = itemView.findViewById(R.id.imageView3);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onCLick(v,getAdapterPosition(),false);
    }
}
