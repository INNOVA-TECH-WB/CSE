package org.example;

import org.example.model.ScheduledPost;
import org.example.publisher.FacebookPublisher;
import org.example.publisher.InstagramPublisher;
import org.example.publisher.PostPublisher;
import org.example.publisher.TikTokPublisher;
import org.example.service.*;
import org.example.view.Dashboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        PostProvider provider = new JsonPostProvider("posts.json");
        List<ScheduledPost> posts = provider.getScheduledPosts();

        if (posts == null || posts.isEmpty()) {
            System.out.println("No scheduled posts found!");
            // continue anyway so user can add posts via UI
        }

        Map<String, PostPublisher> publishers = new HashMap<>();
        publishers.put("Instagram", new InstagramPublisher());
        publishers.put("TikTok", new TikTokPublisher());
        publishers.put("Facebook", new FacebookPublisher());

        DailyReportService reportService = new DailyReportService();
        SchedulerService scheduler = new SchedulerService(posts, publishers, reportService);

        System.out.println("Press Start...");
        new Dashboard(posts, scheduler, reportService, provider);
    }
}
