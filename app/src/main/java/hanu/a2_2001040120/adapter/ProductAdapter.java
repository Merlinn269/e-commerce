package hanu.a2_2001040120.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;
import androidx.recyclerview.widget.RecyclerView;



import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import hanu.a2_2001040120.Constants;
import hanu.a2_2001040120.R;
import hanu.a2_2001040120.models.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {
    List<Product> productList;
    Context context;
    ProductClickedListener productClickedListener;

    public ProductAdapter(ProductClickedListener productClickedListener) {
        this.productClickedListener = productClickedListener;
    }


    public void  setProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilteredList(List<Product> filteredList) {
        this.productList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductHolder(LayoutInflater.from(context).inflate(R.layout.item_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static Bitmap downloadImage(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream is = connection.getInputStream();
            return BitmapFactory.decodeStream(is);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    class ProductHolder extends RecyclerView.ViewHolder {

        ImageView imgViewProduct;
        TextView tvProductName, tvProductPrice;
        ImageButton imgBtnCart;

        public ProductHolder(@NonNull View itemView) {
            super(itemView);

            imgViewProduct = itemView.findViewById(R.id.imgView_product);
            tvProductName = itemView.findViewById(R.id.tv_name);
            tvProductPrice = itemView.findViewById(R.id.tv_price);
            imgBtnCart = itemView.findViewById(R.id.imgBtnCart);
        }

        public void bind(Product product) {
            String linkImage = product.getThumbnail();
            Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
            Constants.executor.execute(() -> {
                Bitmap bitmap = downloadImage(linkImage);
                if (bitmap != null) {
                    handler.post(() -> imgViewProduct.setImageBitmap(bitmap));
                }
            });
            tvProductName.setText(product.getTitleProduct());
            NumberFormat format = new DecimalFormat(" ₫ #,###");
            tvProductPrice.setText(format.format(product.getPrice()));
            imgBtnCart.setImageResource(R.drawable.shopping_cart);
            imgBtnCart.setOnClickListener(view -> productClickedListener.onAddToCartBtnClicked(product));
        }
    }
    public interface ProductClickedListener {
        void onAddToCartBtnClicked(Product product);
    }
}
