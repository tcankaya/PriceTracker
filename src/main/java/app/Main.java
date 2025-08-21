package app;

import java.io.IOException;
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
                    tracker.checkPrices(notifier);
                } catch (IOException e) {
                    System.err.println("⚠️ Failed to check prices: " + e.getMessage());
                }
            }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, Config.getDelay(), Config.getPeriod());
        System.out.println("🟢 Price Tracker started. Checking prices...");
    }
}