package hanu.a2_2001040120.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.os.HandlerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import hanu.a2_2001040120.Constants;
import hanu.a2_2001040120.R;
import hanu.a2_2001040120.adapter.ProductAdapter;
import hanu.a2_2001040120.database.ProductManager;
import hanu.a2_2001040120.models.Product;

public class ProductListActivity extends AppCompatActivity implements ProductAdapter.ProductClickedListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private ConstraintLayout productListLayout;
    private ProductManager productManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list);
        init();

        // tool bar
        setSupportActionBar(toolbar);

        // search
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        //fetch data from api and load image using handler and executors
        String url = "https://hanu-congnv.github.io/mpr-cart-api/products.json";
        Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
        Constants.executor.execute(() -> {
            String json = loadJSON(url);

            handler.post(() -> {
                if (json == null) {
                    Toast.makeText(ProductListActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    JSONArray root = new JSONArray(json);
                    for (int i = 0; i < root.length(); i++) {
                        JSONObject object = root.getJSONObject(i);
                        Product product = new Product(object.getInt("id"), object.getString("thumbnail"), object.getString("name"), object.getString("category"), object.getInt("unitPrice"));
                        productList.add(product);
                    }

                    // grid view
                    recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 2));
                    // set adapter
                    productAdapter.setProductAdapter(productList, ProductListActivity.this);
                    recyclerView.setAdapter(productAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });
        });
        // grid view
        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 2));
        // set adapter
        productAdapter.setProductAdapter(productList, ProductListActivity.this);
        recyclerView.setAdapter(productAdapter);

    }

    // filter for search
    private void filterList(String text) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getTitleProduct().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(product);
            }
        }
        if (!filteredList.isEmpty()) {
            productAdapter.setFilteredList(filteredList);
        }
    }

    // load data from url api -> string
    public String loadJSON(String link) {
        URL url;
        HttpURLConnection urlConnection;
        try {
            url = new URL(link);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            InputStream is = urlConnection.getInputStream();
            Scanner sc = new Scanner(is);
            StringBuilder result = new StringBuilder();
            String line;
            while(sc.hasNextLine()) {
                line = sc.nextLine();
                result.append(line);
            }
            return result.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cart) {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);

        }
        return true;
    }

    protected void init() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        productListLayout = findViewById(R.id.productListLayout);
        productList = new ArrayList<>();
        productManager = ProductManager.getInstance(this);
        productAdapter = new ProductAdapter(this);
    }

    @Override
    public void onAddToCartBtnClicked(Product product) {
        long id = productManager.addToCart(product, 1);
        if (id == -1) {
            Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
        } else {
            makeSnackBar();
        }
    }
    // tạo snack bar cho btn add to cart thành công
    private void makeSnackBar() {
        Snackbar.make(productListLayout, "Product Added To Cart", Snackbar.LENGTH_SHORT)
                .setAction("Go to Cart", view -> startActivity(new Intent(ProductListActivity.this, CartActivity.class))).show();
    }




}