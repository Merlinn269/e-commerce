package hanu.a2_2001040120.models;

public class Product {
    private final int id;
    private final String thumbnail;
    private final String titleProduct;
    private final String category;
    private final int price;
    private int quantity;



    public Product(int id, String thumbnail, String titleProduct, String category, int price) {
        this.id = id;
        this.thumbnail = thumbnail;
        this.titleProduct = titleProduct;
        this.category = category;
        this.price = price;
    }

    public Product(int id, String thumbnail, String titleProduct, String category, int quantity, int price) {
        this.id = id;
        this.thumbnail = thumbnail;
        this.titleProduct = titleProduct;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
    }



    public int getId() {
        return id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getTitleProduct() {
        return titleProduct;
    }

    public int getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
