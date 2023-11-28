package hanu.a2_2001040120.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import hanu.a2_2001040120.R;
import hanu.a2_2001040120.adapter.CartAdapter;
import hanu.a2_2001040120.database.ProductManager;
import hanu.a2_2001040120.models.Product;

public class CartActivity extends AppCompatActivity {
    private Toolbar cartToolbar;
    private ProductManager productManager;
    private RecyclerView cartRecyclerview;
    private TextView totalPrice;
    private List<Product> productList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        init();

        // set data
        productList = productManager.loadProducts();

        // adapter
        CartAdapter cartAdapter = new CartAdapter(productList);
        cartRecyclerview.setAdapter(cartAdapter);
        cartRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        // xử lý toolbar
        setSupportActionBar(cartToolbar);
        cartToolbar.setNavigationOnClickListener(view -> onBackPressed());

        // handle total price of product list
        updateTotalValue();
        cartAdapter.setOnProductQuantityChangeListener(this::updateTotalValue);

    }

    private void updateTotalValue() {
        int totalValue = 0;
        for (Product product : productList) {
            totalValue += product.getPrice() * product.getQuantity();
        }

        NumberFormat format = new DecimalFormat(" ₫ #,###");
        totalPrice.setText(format.format(totalValue));
    }

    protected void init() {
        productManager = ProductManager.getInstance(this);
        cartToolbar = findViewById(R.id.toolbar1);
        cartRecyclerview = findViewById(R.id.recyclerView_cartList);
        totalPrice = findViewById(R.id.tv_totalPrice);
    }
}