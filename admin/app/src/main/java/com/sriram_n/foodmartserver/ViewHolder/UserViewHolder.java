package com.sriram_n.foodmartserver.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sriram_n.foodmartserver.Interface.ItemClickListener;
import com.sriram_n.foodmartserver.R;

public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{

    public TextView txtUserName, txtUserPhone;

    private ItemClickListener itemClickListener;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);

        txtUserPhone = itemView.findViewById(R.id.user_phone);
        txtUserName = itemView.findViewById(R.id.user_name);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Chọn hàng động: ");
        contextMenu.add(0,0,getAdapterPosition(), "Cập nhật");
        contextMenu.add(0,1,getAdapterPosition(), "Xóa");

    }
}
