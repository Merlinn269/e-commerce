package hanu.a2_2001040120.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "cart.db";
    public static final int VERSION = 1;
    public static final String PRODUCT_TABLE = "product_table";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_THUMBNAIL = "thumbnail";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_QUANTITY = "quantity";

    // CONSTRUCTOR
    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + PRODUCT_TABLE + " ( "
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_THUMBNAIL + " TEXT NOT NULL, "
                    + COLUMN_NAME + " TEXT NOT NULL, "
                    + COLUMN_CATEGORY + " TEXT NOT NULL, "
                    + COLUMN_PRICE + " INTEGER NOT NULL, "
                    + COLUMN_QUANTITY + " INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCT_TABLE);
        onCreate(db);
    }

}
