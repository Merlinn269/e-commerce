package hanu.a2_2001040120.adapter;

import static hanu.a2_2001040120.adapter.ProductAdapter.downloadImage;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import hanu.a2_2001040120.Constants;
import hanu.a2_2001040120.R;
import hanu.a2_2001040120.database.ProductManager;
import hanu.a2_2001040120.models.Product;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {
    List<Product> productList;
    private OnProductQuantityChangeListener onProductQuantityChangeListener;
    public CartAdapter(List<Product> productList) {
        this.productList = productList;
    }

    public void setOnProductQuantityChangeListener(OnProductQuantityChangeListener listener) {
        this.onProductQuantityChangeListener = listener;
    }

    @NonNull
    @Override
    public CartAdapter.CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class CartHolder extends RecyclerView.ViewHolder {
         TextView tvQuantity, tvTitleProduct, tvPrice, tvSumPrice;
         ImageView imgThumbnail;
         ImageButton btnPlus, btnMinus;
         ProductManager productManager;
         int sumPrice, quantity;
        public CartHolder(@NonNull View itemView) {
            super(itemView);
            tvQuantity = itemView.findViewById(R.id.quantity);
            tvTitleProduct = itemView.findViewById(R.id.tv_titleProduct);
            tvPrice = itemView.findViewById(R.id.tv_productPrice);
            tvSumPrice = itemView.findViewById(R.id.tv_sumPrice);
            imgThumbnail = itemView.findViewById(R.id.img_product);
            btnMinus = itemView.findViewById(R.id.minus);
            btnPlus = itemView.findViewById(R.id.plus);
            productManager = ProductManager.getInstance(itemView.getContext());
        }
        @SuppressLint("NotifyDataSetChanged")
        public void bind(Product product) {
            // product name
            tvTitleProduct.setText(product.getTitleProduct());

            // product price
            NumberFormat format = new DecimalFormat(" ₫ #,###");
            tvPrice.setText(format.format(product.getPrice()));

            // product image
            String linkImage = product.getThumbnail();
            Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
            Constants.executor.execute(() -> {
                Bitmap bitmap = downloadImage(linkImage);
                if (bitmap != null) {
                    handler.post(() -> imgThumbnail.setImageBitmap(bitmap));
                }
            });

            quantity = product.getQuantity();
            // add quantity product (+)
            btnPlus.setOnClickListener(view -> {
                quantity++;
                product.setQuantity(quantity);
                notifyDataSetChanged();
                productManager.updateQuantity(product,product.getQuantity());
                if (onProductQuantityChangeListener != null) {
                    onProductQuantityChangeListener.onProductQuantityChange();
                }
            });

            // remove quantity product (-)
            btnMinus.setOnClickListener(view -> {
                if (quantity > 1) {
                    quantity--;
                    product.setQuantity(quantity);
                    notifyDataSetChanged();
                    productManager.updateQuantity(product, product.getQuantity());
                } else {
                    productManager.deleteProduct(product.getId());
                    productList.remove(product);
                    notifyDataSetChanged();
                }
                if (onProductQuantityChangeListener != null) {
                    onProductQuantityChangeListener.onProductQuantityChange();
                }

            });
            sumPrice = product.getPrice() * product.getQuantity();
            tvSumPrice.setText(format.format(sumPrice));
            tvQuantity.setText(String.valueOf(product.getQuantity()));


        }


    }

    public interface OnProductQuantityChangeListener {
        void onProductQuantityChange();
    }


}
