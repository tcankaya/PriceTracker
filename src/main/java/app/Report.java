package app;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Report {

    private final List<String> messages = new ArrayList<>();

    public void addMessage(String message) {
        messages.add(message);
    }

    public void generateHtmlReport(String filePath) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html>\n<head>\n")
                .append("<meta charset='UTF-8'>\n")
                .append("<title>Price Tracker Report</title>\n")
                .append("<style>body{font-family:Arial;} .alert{color:red;} .info{color:green;}</style>\n")
                .append("</head>\n<body>\n")
                .append("<h1>Price Tracker Report</h1>\n<ul>\n");

        for (String msg : messages) {
            String cssClass = msg.toLowerCase().contains("drop") ? "alert" : "info";
            html.append("<li class='").append(cssClass).append("'>").append(msg).append("</li>\n");
        }

        html.append("</ul>\n</body>\n</html>");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(html.toString());
            System.out.println("✅ Report generated at " + filePath);
        } catch (IOException e) {
            System.err.println("❌ Failed to write report: " + e.getMessage());
        }
    }
}