package com.sriram_n.foodmart.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sriram_n.foodmart.Interface.ItemClickListener;
import com.sriram_n.foodmart.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtCategoryName;
    public ImageView imageCategory;

    private ItemClickListener itemClickListener;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);

        txtCategoryName = itemView.findViewById(R.id.lblProducrName2);
        imageCategory = itemView.findViewById(R.id.allCategoryImage);
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
