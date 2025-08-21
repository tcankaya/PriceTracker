package app;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {
        PriceTracker tracker = new PriceTracker();
        Notifier notifier = new Notifier();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("⏱ Checking prices...");

                try {
                    // Load products from JSON
                    List<Product> products = tracker.loadProducts();

                    for (Product p : products) {
                        double currentPrice = tracker.fetchPrice(p.getUrl(), true);

                        if (currentPrice == -1) {
                            System.out.println("⚠️ Could not fetch price for " + p.getBrand());
                            continue;
                        }

                        // Debug info
                        System.out.println("DEBUG: Brand: " + p.getBrand() +
                                " | LastPrice: " + p.getLastPrice() +
                                " | CurrentPrice: " + currentPrice);

                        // Floating point tolerance
                        double tolerance = 0.01;

                        // Notify if price dropped
                        if (currentPrice + tolerance < p.getLastPrice()) {
                            notifier.sendAlert(p, p.getLastPrice(), currentPrice);
                        }

                        // Update lastPrice after checking
                        p.setLastPrice(currentPrice);
                    }

                    // Save updated JSON
                    tracker.saveProducts(products);
                    System.out.println("✅ Updated prices.json successfully.\n");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Timer timer = new Timer();
        long delay = 0;             // start immediately
        long period = 1 * 60 * 1000; // every 1 minute

        timer.scheduleAtFixedRate(task, delay, period);
        System.out.println("🟢 Price Tracker started. Checking every 1 minute...");
    }
}