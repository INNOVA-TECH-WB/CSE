package org.example.service;

import org.example.model.ScheduledPost;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyReportService {
    private final List<String> publishedPosts = new ArrayList<>();
    private final String reportsDir = "reports";

    public DailyReportService() {
        createReportsDir();
    }

    private void createReportsDir() {
        File dir = new File(reportsDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                System.out.println("✅ Reports directory created: " + reportsDir);
            } else {
                System.out.println("❌ Failed to create reports directory: " + reportsDir);
            }
        }
    }

    public void logPost(ScheduledPost post) {
        publishedPosts.add("[" + post.getPlatform() + "] " + post.getContent());
    }

    public void generateReport() {
        if (publishedPosts.isEmpty()) {
            System.out.println("No posts today. Skipping report.");
            return;
        }

        String fileName = reportsDir + "/daily_report_" + LocalDate.now() + ".txt";

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("Daily Report for " + LocalDate.now() + "\n");
            writer.write("============================\n\n");
            for (String entry : publishedPosts) {
                writer.write(entry + "\n");
            }
            System.out.println("✅ Report saved: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        publishedPosts.clear(); // reset for the next day
    }
}
