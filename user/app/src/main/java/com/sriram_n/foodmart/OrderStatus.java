package com.sriram_n.foodmart;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sriram_n.foodmart.Common.Common;
import com.sriram_n.foodmart.Interface.ItemClickListener;
import com.sriram_n.foodmart.Model.Order;
import com.sriram_n.foodmart.Model.Request;
import com.sriram_n.foodmart.ViewHolder.OrderViewHolder;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.text.NumberFormat;
import java.util.Locale;

public class OrderStatus extends AppCompatActivity {
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;
    FirebaseDatabase database;
    DatabaseReference requests;

    Request currentFood;
    MaterialSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        //init firebse
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");
        recyclerView = findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (getIntent().getExtras() == null)
            loadOrders(Common.currentUser.getPhone());
        else
            loadOrders(getIntent().getStringExtra("userPhone"));
    }

    private void loadOrders(String phone) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone")
                        .equalTo(phone)) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onCLick(View view, int position, boolean isLongClick) {
                        showDetail(adapter.getRef(position).getKey(), adapter.getItem(position));
                    }

                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void showDetail(String key, final Request item) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this);

        alertDialog.setTitle("Chi tiết đơn hàng: " + item.getFoods().size() + " sản phẩm\n");
        String ds = "";
        int a = 0;
        int total = 0;
        for (Order order : item.getFoods()) {
            a++;
            ds += a + ") " + order.getProductName() + "  Đơn giá: "
                    + Common.formatPrice(Integer.parseInt(order.getPrice())) + "\n     Số lượng: " + order.getQuantity() + "\n\n";
            total += Integer.parseInt(order.getPrice()) * Integer.parseInt(order.getQuantity());
        }
        String message = "Mã đơn: " + key + "\nTổng giá: " + Common.formatPrice(total) + "\n\n\n";
        alertDialog.setMessage(message + ds);

        alertDialog.show();
    }
}
