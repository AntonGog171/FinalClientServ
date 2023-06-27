package proj;

public class Product {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public String getCharacteristic() {
        return characteristic;
    }

    public void setCharacteristic(String characteristic) {
        this.characteristic = characteristic;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    private int id;
    private String name;
    private String characteristic;
    private String manufacturer;
    private double price;
    private int quantity;
    private String category;

    public Product(int id, String name, double price, int quantity, String category, String characteristic, String manufacturer) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.characteristic = characteristic;
        this.manufacturer = manufacturer;
    }

    public Product(){}

    @Override
    public String toString() {
        return "Product{" +
                "\"id\":" + id +
                ", \"name\":\"" + name + '\"' +
                ", \"characteristic\":\"" + characteristic + '\"' +
                ", \"manufacturer\":\"" + manufacturer + '\"' +
                ", \"price\":" + price +
                ", \"quantity\":" + quantity +
                ", \"category\":\"" + category + '\"' +
                '}';
    }

}
