package com.sriram_n.foodmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.sriram_n.foodmart.Common.Common;
import com.sriram_n.foodmart.Database.Database;
import com.sriram_n.foodmart.Interface.ItemClickListener;
import com.sriram_n.foodmart.Model.Category;
import com.sriram_n.foodmart.Model.Food;
import com.sriram_n.foodmart.Model.Order;
import com.sriram_n.foodmart.Service.ListenOrder;
import com.sriram_n.foodmart.ViewHolder.HomeViewHolder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;

    FirebaseDatabase database;
    DatabaseReference category, food;
    TextView txtFullName;
    RecyclerView recycler_menu, recycler_category, recycler_food, recycler_search;
    RecyclerView.LayoutManager layoutManager, layoutManager1;

    FirebaseRecyclerAdapter<Category, HomeViewHolder> adapter;

    FirebaseRecyclerAdapter<Food, HomeViewHolder> adapter1;
    MaterialSearchBar materialSearchBar;
    List<String> suggestList = new ArrayList<>();
    ImageView allCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Trang chủ");
        setSupportActionBar(toolbar);

        //init firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Categories");
        food = database.getReference("Foods");

        Paper.init(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Home.this, Cart.class);
                startActivity(cartIntent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set name for user
        View headerView = navigationView.getHeaderView(0);
        txtFullName = headerView.findViewById(R.id.txtFullName);
        txtFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent profileIntent = new Intent(Home.this, Profile.class);
                startActivity(profileIntent);

            }
        });
        //Load menu
        recycler_menu = (RecyclerView) findViewById(R.id.discountedRecycler);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler_menu.setLayoutManager(layoutManager);

        recycler_category = (RecyclerView) findViewById(R.id.catagoryRecycler);
        recycler_category.setHasFixedSize(true);
        layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler_category.setLayoutManager(layoutManager1);

        GridLayoutManager layoutManager2 = new GridLayoutManager(this, 2);
        food.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                layoutManager2.setMeasuredDimension(recycler_food.getWidth(), Math.toIntExact(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });

        recycler_food = (RecyclerView) findViewById(R.id.recently_items);
        recycler_food.setHasFixedSize(true);
        recycler_food.setLayoutManager(layoutManager2);
        if (Common.isConnectedToInternet(this)) {
            loadMenu();
            loadCategory();
            loadFood();

        } else {
            Toast.makeText(this, "Vui lòng kiểm tra kết nối của bạn!", Toast.LENGTH_SHORT).show();
            return;
        }


        recycler_search = findViewById(R.id.recycler_food);
        materialSearchBar = findViewById(R.id.searchBar);
        materialSearchBar.setHint("Nhập tên món ăn");
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(5);

        allCategory = findViewById(R.id.allCategoryImage);
        allCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, com.sriram_n.foodmart.Category.class);
                startActivity(intent);
            }
        });

        //register service
        Intent service = new Intent(Home.this, ListenOrder.class);
        startService(service);
    }

    private void loadFood() {
        adapter1 = new FirebaseRecyclerAdapter<Food, HomeViewHolder>(Food.class, R.layout.food_item, HomeViewHolder.class, food) {
            @Override
            protected void populateViewHolder(HomeViewHolder viewHolder, Food model, int position) {
                int price = Integer.parseInt(model.getPrice());
                viewHolder.txtFoodName.setText(model.getName());
                viewHolder.txtFoodPrice.setText(Common.formatPrice(price));
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView2);

                viewHolder.btnCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference ref = getRef(position);
                        String foodId = ref.getKey();
                        Order order = new Database(getBaseContext()).checkFood(foodId);
                        if (order != null) {
                            int quantity = Integer.parseInt(order.getQuantity()) + 1;
                            new Database(getBaseContext()).updateToCart(String.valueOf(quantity), order.getProductId());
                            Toast.makeText(Home.this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                        } else {
                            new Database(getBaseContext()).addToCart(new Order(
                                    foodId,
                                    model.getName(),
                                    "1",
                                    model.getPrice(),
                                    model.getDiscount(),
                                    model.getImage()
                            ));
                            Toast.makeText(Home.this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onCLick(View view, int position, boolean isLongClick) {
                        //start new activity
                        Intent foodDetail = new Intent(Home.this, FoodDetail.class);
                        //sending foodId  to new activity
                        foodDetail.putExtra("FoodId", adapter1.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }
        };
        recycler_food.setAdapter(adapter1);
    }

    private void loadCategory() {
        adapter = new FirebaseRecyclerAdapter<Category, HomeViewHolder>(Category.class, R.layout.category_row_items, HomeViewHolder.class, category) {
            @Override
            protected void populateViewHolder(HomeViewHolder viewHolder, Category model, int position) {
//                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView1);
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onCLick(View view, int position, boolean isLongClick) {
                        //get category id and send to new activity
                        Intent foodList = new Intent(Home.this, FoodList.class);
                        //since cactegoryid is key, we only need key of the item
                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });
            }
        };
        recycler_category.setAdapter(adapter);
    }

    private void loadMenu() {
        adapter = new FirebaseRecyclerAdapter<Category, HomeViewHolder>(Category.class, R.layout.discounted_row_items, HomeViewHolder.class, category) {
            @Override
            protected void populateViewHolder(HomeViewHolder viewHolder, Category model, int position) {
                viewHolder.txtNameMenu.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);
                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onCLick(View view, int position, boolean isLongClick) {
                        Toast.makeText(Home.this, "" + clickItem.getName(), Toast.LENGTH_SHORT).show();

                        //get category id and send to new activity
                        Intent foodList = new Intent(Home.this, FoodList.class);
                        //since cactegoryid is key, we only need key of the item
                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });
            }
        };
        recycler_menu.setAdapter(adapter);
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
            loadMenu();
            loadCategory();
            loadFood();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementsWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(Home.this, Cart.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_orders) {
            Intent orderIntent = new Intent(Home.this, OrderStatus.class);
            startActivity(orderIntent);

        } else if (id == R.id.nav_log_out) {

            //Delete remembered user
            Paper.book().destroy();

            //Logout
            Intent signIn = new Intent(Home.this, sign_in_tab.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
