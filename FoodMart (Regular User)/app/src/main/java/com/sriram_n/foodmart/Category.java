package com.sriram_n.foodmart;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.sriram_n.foodmart.Common.Common;
import com.sriram_n.foodmart.Interface.ItemClickListener;
import com.sriram_n.foodmart.Service.ListenOrder;
import com.sriram_n.foodmart.ViewHolder.CategoryViewHolder;

import io.paperdb.Paper;

public class Category extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference category;
    RecyclerView recycler_category;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<com.sriram_n.foodmart.Model.Category, CategoryViewHolder> adapter;
    ImageView backImageView, cartImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_category);

        //init firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Categories");

        Paper.init(this);

        layoutManager = new GridLayoutManager(this, 4);
        recycler_category = findViewById(R.id.allCategoryRview);
        recycler_category.addItemDecoration(new GridSpacingItemDecoration(4, dpToPx(16), true));
        recycler_category.setLayoutManager(layoutManager);

        backImageView = findViewById(R.id.back);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(Category.this, Home.class);
                startActivity(back);
                finish();
            }
        });

        cartImageView = findViewById(R.id.imageView4);
        cartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Category.this, Cart.class);
                startActivity(cartIntent);
            }
        });

        if (Common.isConnectedToInternet(this)) {
            loadCategory();
        } else {
            Toast.makeText(this, "Vui lòng kiểm tra kết nối của bạn!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent service = new Intent(Category.this, ListenOrder.class);
        startService(service);
    }

    private void loadCategory() {
        adapter = new FirebaseRecyclerAdapter<com.sriram_n.foodmart.Model.Category, CategoryViewHolder>(com.sriram_n.foodmart.Model.Category.class, R.layout.all_category_row_items, CategoryViewHolder.class, category) {
            @Override
            protected void populateViewHolder(CategoryViewHolder viewHolder, com.sriram_n.foodmart.Model.Category model, int position) {
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageCategory);
                viewHolder.txtCategoryName.setText(model.getName());
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onCLick(View view, int position, boolean isLongClick) {
                        //get category id and send to new activity
                        Intent foodList = new Intent(Category.this, FoodList.class);
                        //since cactegoryid is key, we only need key of the item
                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });
            }
        };
        recycler_category.setAdapter(adapter);
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parentRecyclerView, RecyclerView.State state) {
            int position = parentRecyclerView.getChildAdapterPosition(view); //Item Position
            int column = position % spanCount; //Items Column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            loadCategory();
        }
        return super.onOptionsItemSelected(item);
    }
}
