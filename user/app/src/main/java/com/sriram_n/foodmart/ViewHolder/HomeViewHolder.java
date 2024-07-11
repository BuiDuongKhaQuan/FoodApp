package com.sriram_n.foodmart.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sriram_n.foodmart.Interface.ItemClickListener;
import com.sriram_n.foodmart.R;

public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtFoodName, txtFoodPrice, txtNameMenu;
    public ImageView imageView, imageView1, imageView2, btnCart;

    private ItemClickListener itemClickListener;

    public HomeViewHolder(@NonNull View itemView) {
        super(itemView);

        txtFoodName = itemView.findViewById(R.id.lblProducrName);
        txtFoodPrice = itemView.findViewById(R.id.lblPrice);
        txtNameMenu = itemView.findViewById(R.id.lblProducrName1);
        imageView = itemView.findViewById(R.id.allCategoryImage);
        imageView1 = itemView.findViewById(R.id.CategoryImage);
        imageView2 = itemView.findViewById(R.id.imageView5);
        btnCart = itemView.findViewById(R.id.imageView3);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onCLick(v,getAdapterPosition(),false);
    }
}
