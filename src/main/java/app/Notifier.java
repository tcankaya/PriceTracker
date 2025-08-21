package app;

public class Notifier {
    public void sendAlert(Product p, double oldPrice, double newPrice) {
        System.out.println("🔥 Price drop detected for " + p.getBrand() + "!");
        System.out.println("Old price: " + oldPrice + " → New price: " + newPrice);
        // You can extend this: send email, Telegram, or push notification
    }
}