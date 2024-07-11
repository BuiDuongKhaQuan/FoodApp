package com.sriram_n.foodmartserver;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.sriram_n.foodmartserver.Common.Common;
import com.sriram_n.foodmartserver.Interface.ItemClickListener;
import com.sriram_n.foodmartserver.Model.User;
import com.sriram_n.foodmartserver.ViewHolder.UserViewHolder;

public class UserStatus extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<User, UserViewHolder> adapter;

    FirebaseDatabase db;
    DatabaseReference requests;

    MaterialSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_status);

        db = FirebaseDatabase.getInstance();
        requests = db.getReference("User");

        recyclerView = findViewById(R.id.listUsers);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadUsers();
    }

    private void loadUsers() {
        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(
                User.class,
                R.layout.user_layout,
                UserViewHolder.class,
                requests
        ) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, User model, int position) {
                viewHolder.txtUserName.setText(model.getName());
                viewHolder.txtUserPhone.setText(adapter.getRef(position).getKey());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.DELETE)){
            deleteUser(adapter.getRef(item.getOrder()).getKey());}

        return super.onContextItemSelected(item);
    }

    private void deleteUser(String key) {
        requests.child(key).removeValue();
    }

//    private void showUpdateDialog(String key, final User item) {
//        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserStatus.this);
//        alertDialog.setTitle("Cập nhật đơn hàng");
//        alertDialog.setMessage("Hãy chọn trạng thái đơn hàng");
//
//        LayoutInflater inflater =  this.getLayoutInflater();
//        final View view = inflater.inflate(R.layout.update_order_layout,null);
//
//        spinner = view.findViewById(R.id.statusSpinner);
//        spinner.setItems("Đơn hàng đã đặt", "Đơn hàng đang chuẩn bị", "Đơn hàng đang vận chuyển");
//
//        alertDialog.setView(view);
//
//        final String localKey = key;
//        alertDialog.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int i) {
//                dialog.dismiss();
////                item.setStatus(String.valueOf(spinner.getSelectedIndex()));
////
////                requests.child(localKey).setValue(item);
//            }
//        });
//
//        alertDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        alertDialog.show();
//    }
}