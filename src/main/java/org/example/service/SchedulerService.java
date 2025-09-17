package org.example.service;

import org.example.model.ScheduledPost;
import org.example.publisher.PostPublisher;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class SchedulerService {
    private final List<ScheduledPost> posts;
    private final Map<String, PostPublisher> publisherMap;
    private final DailyReportService reportService;

    private ScheduledExecutorService scheduler;

    public SchedulerService(List<ScheduledPost> posts,
                            Map<String, PostPublisher> publisherMap,
                            DailyReportService reportService) {
        this.posts = posts;
        this.publisherMap = publisherMap;
        this.reportService = reportService;
    }

    public synchronized void start() {
        if (scheduler == null || scheduler.isShutdown() || scheduler.isTerminated()) {
            scheduler = Executors.newScheduledThreadPool(2);
        }
        // schedule all posts
        for (ScheduledPost post : posts) {
            schedulePost(post);
        }
        // schedule daily report at midnight
        long midnightDelay = computeDelay(LocalTime.MIDNIGHT);
        scheduler.scheduleAtFixedRate(
                reportService::generateReport,
                midnightDelay,
                TimeUnit.DAYS.toMillis(1),
                TimeUnit.MILLISECONDS
        );
        System.out.println("Daily report scheduled at midnight.");
    }

    public synchronized void schedulePost(ScheduledPost post) {
        if (scheduler == null || scheduler.isShutdown()) return;
        long initialDelay = computeDelay(post.getPostTime());
        long period = TimeUnit.DAYS.toMillis(1);
        scheduler.scheduleAtFixedRate(
                () -> postToPlatform(post),
                initialDelay,
                period,
                TimeUnit.MILLISECONDS
        );
        System.out.println("Scheduled post: " + post + " in " + initialDelay / 1000 + " seconds");
    }

    private long computeDelay(LocalTime postTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime postDateTime = now.withHour(postTime.getHour())
                .withMinute(postTime.getMinute())
                .withSecond(0)
                .withNano(0);
        if (postDateTime.isBefore(now)) {
            postDateTime = postDateTime.plusDays(1);
        }
        return Duration.between(now, postDateTime).toMillis();
    }

    private void postToPlatform(ScheduledPost post) {
        PostPublisher publisher = publisherMap.get(post.getPlatform());
        if (publisher != null) {
            publisher.publish(post);
            reportService.logPost(post);
        } else {
            System.out.println("No publisher found for platform: " + post.getPlatform());
        }
    }

    public synchronized void stop() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            System.out.println("Scheduler Stopped");
        }
    }

    // Expose the list so UI can modify it
    public List<ScheduledPost> getPosts() {
        return posts;
    }
}
