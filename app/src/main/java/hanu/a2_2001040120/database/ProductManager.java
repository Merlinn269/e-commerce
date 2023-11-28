package hanu.a2_2001040120.database;

import static hanu.a2_2001040120.database.DbHelper.COLUMN_CATEGORY;
import static hanu.a2_2001040120.database.DbHelper.COLUMN_ID;
import static hanu.a2_2001040120.database.DbHelper.COLUMN_NAME;
import static hanu.a2_2001040120.database.DbHelper.COLUMN_PRICE;
import static hanu.a2_2001040120.database.DbHelper.COLUMN_QUANTITY;
import static hanu.a2_2001040120.database.DbHelper.COLUMN_THUMBNAIL;
import static hanu.a2_2001040120.database.DbHelper.PRODUCT_TABLE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import hanu.a2_2001040120.models.Product;

public class ProductManager {
    private static ProductManager instance;
    DbHelper helper;
    public ProductManager(Context context) {
        helper = new DbHelper(context);
    }

    public static ProductManager getInstance(Context context) {
        if (instance == null) {
            instance = new ProductManager(context);
        }
        return instance;
    }

    public long addToCart(Product product, int quantity) {
        SQLiteDatabase db = helper.getWritableDatabase();

        // Check if the product already exists in the cart
        Cursor cursor = db.query(PRODUCT_TABLE, null, COLUMN_ID + "=?", new String[]{String.valueOf(product.getId())}, null, null, null);
        long rowId;
        if (cursor.moveToFirst()) {
            int quantityIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
            int currentQuantity = cursor.getInt(quantityIndex);
            currentQuantity += quantity;

            rowId = updateQuantity(product, currentQuantity);

        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_ID, product.getId());
            contentValues.put(COLUMN_THUMBNAIL, product.getThumbnail());
            contentValues.put(COLUMN_NAME, product.getTitleProduct());
            contentValues.put(COLUMN_CATEGORY, product.getCategory());
            contentValues.put(COLUMN_PRICE, product.getPrice());
            contentValues.put(COLUMN_QUANTITY, product.getQuantity()+1);

            // Product does not exist in the cart, add a new product
            rowId = db.insert(PRODUCT_TABLE, null, contentValues);
        }
        cursor.close();
        db.close();
        return rowId;
    }

    public long updateQuantity(Product product, int quantity) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_QUANTITY, quantity);
        long affectedRow = db.update(PRODUCT_TABLE, contentValues, COLUMN_ID+" =?", new String[]{String.valueOf(product.getId())});
        db.close();
        return affectedRow;
    }

    public void deleteProduct (int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(PRODUCT_TABLE, COLUMN_ID + " =?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Product> loadProducts() {
        SQLiteDatabase database = helper.getReadableDatabase();
        List<Product> products = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + PRODUCT_TABLE, null);
        int idIndex = cursor.getColumnIndex(COLUMN_ID);
        int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
        int thumbnailIndex = cursor.getColumnIndex(COLUMN_THUMBNAIL);
        int categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY);
        int quantityIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
        int priceIndex = cursor.getColumnIndex(COLUMN_PRICE);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(idIndex);
            String thumbnail = cursor.getString(thumbnailIndex);
            String name = cursor.getString(nameIndex);
            String category = cursor.getString(categoryIndex);
            int quantity = cursor.getInt(quantityIndex);
            int price = cursor.getInt(priceIndex);

            Product product = new Product(id, thumbnail, name, category, quantity, price);
            products.add(product);
            cursor.moveToNext();
        }
        cursor.close();
        database.close();
        return products;
    }
}
