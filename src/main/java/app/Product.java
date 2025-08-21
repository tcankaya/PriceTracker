package app;

public class Product {
    private String brand;
    private String url;
    private double lastPrice;
    private String cssSelector;

    public Product() {}

    public Product(String brand, String url, double lastPrice, String cssSelector) {
        this.brand = brand;
        this.url = url;
        this.lastPrice = lastPrice;
        this.cssSelector = cssSelector;
    }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public double getLastPrice() { return lastPrice; }
    public void setLastPrice(double lastPrice) { this.lastPrice = lastPrice; }

    public String getCssSelector() { return cssSelector; }
    public void setCssSelector(String cssSelector) { this.cssSelector = cssSelector; }
}