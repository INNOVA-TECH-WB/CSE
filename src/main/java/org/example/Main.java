package org.example;

import org.example.model.ScheduledPost;
import org.example.publisher.FacebookPublisher;
import org.example.publisher.InstagramPublisher;
import org.example.publisher.PostPublisher;
import org.example.publisher.TikTokPublisher;
import org.example.service.DailyReportService;
import org.example.service.JsonPostProvider;
import org.example.service.PostProvider;
import org.example.service.SchedulerService;
import org.example.view.Dashboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        PostProvider provider = new JsonPostProvider("posts.json");
        List<ScheduledPost> posts = provider.getScheduledPosts();

        // Load posts from JSON in resources


        if (posts == null || posts.isEmpty()) {
            System.out.println("No scheduled posts found!");
            return;
        }
        Map<String, PostPublisher> publishers = new HashMap<>();
        publishers.put("Instagram", new InstagramPublisher());
        publishers.put("TikTok", new TikTokPublisher());
        publishers.put("Facebook", new FacebookPublisher());

        DailyReportService raportService = new DailyReportService();

        // Start the scheduler
        SchedulerService scheduler = new SchedulerService(posts, publishers,raportService);
        //scheduler.start(); // this is off because the scheduler starts from gui dashboard

        System.out.println("Press Start...");


        new Dashboard(posts, scheduler,raportService);
    }
}
