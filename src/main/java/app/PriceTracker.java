package app;

import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PriceTracker {

    private final int THREAD_COUNT = 4; // number of parallel checks
    private final double TOLERANCE = 0.01;

    public void checkPrices(Notifier notifier) throws IOException {
        List<Product> products = Utils.loadProducts();
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        Report report = new Report(); // initialize report

        List<Future<Void>> futures = new ArrayList<>();

        for (Product product : products) {
            futures.add(executor.submit(() -> {
                WebDriver driver = null;
                try {
                    driver = WebDriverFactory.createDriver();
                    driver.get(product.getUrl());

                    Utils.handleCookies(driver);

                    double currentPrice = Utils.fetchPriceFromPage(driver);

                    if (currentPrice == -1) {
                        String msg = "⚠️ Could not fetch price for " + product.getBrand();
                        System.out.println(msg);
                        report.addMessage(msg);
                        return null;
                    }

                    System.out.println("DEBUG: Brand: " + product.getBrand() +
                            " | LastPrice: " + product.getLastPrice() +
                            " | CurrentPrice: " + currentPrice);

                    if (currentPrice + TOLERANCE < product.getLastPrice()) {
                        String alertMsg = "🔥 Price drop detected for " + product.getBrand() +
                                ": " + product.getLastPrice() + " -> " + currentPrice;
                        report.addMessage(alertMsg);
                        notifier.sendAlert(product, product.getLastPrice(), currentPrice);
                    } else {
                        report.addMessage("✅ Price checked for " + product.getBrand() + ": " + currentPrice);
                    }

                    product.setLastPrice(currentPrice);

                } catch (Exception e) {
                    String errorMsg = "❌ Error processing " + product.getBrand() + ": " + e.getMessage();
                    System.err.println(errorMsg);
                    report.addMessage(errorMsg);
                } finally {
                    if (driver != null) driver.quit();
                }
                return null;
            }));
        }

        for (Future<Void> f : futures) {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("❌ Error in parallel execution: " + e.getMessage());
            }
        }

        Utils.saveProducts(products);
        executor.shutdown();

        // Generate HTML report
        report.generateHtmlReport("price_report.html");

        System.out.println("✅ Updated prices.json and generated report successfully.\n");
    }
}