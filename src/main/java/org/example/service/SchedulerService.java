package org.example.service;

import org.example.publisher.PostPublisher;
import org.example.model.ScheduledPost;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class SchedulerService {
    private final List<ScheduledPost> posts;
    private ScheduledExecutorService scheduler; // not final, restartable
    private final Map<String, PostPublisher> publisherMap;
    private final DailyReportService raportService;
    private boolean running = false;

    public SchedulerService(List<ScheduledPost> posts,
                            Map<String, PostPublisher> publisherMap,
                            DailyReportService raportService) {
        this.raportService = raportService;
        this.publisherMap = publisherMap;
        this.posts = posts;
    }

    public void start() {
        if (running) {
            System.out.println("Scheduler already running.");
            return;
        }
        if (scheduler == null || scheduler.isShutdown() || scheduler.isTerminated()) {
            scheduler = Executors.newScheduledThreadPool(2); // 2 threads (posts + reports)
        }

        for (ScheduledPost post : posts) {
            long initialDelay = computeDelay(post.getPostTime());
            long period = TimeUnit.DAYS.toMillis(1);

            scheduler.scheduleAtFixedRate(() -> postToPlatform(post),
                    initialDelay,
                    period,
                    TimeUnit.MILLISECONDS);

            System.out.println("Scheduled post: " + post + " in " + initialDelay / 1000 + " seconds");
        }

        long midnightDelay = computeDelay(LocalTime.MIDNIGHT);
        long oneDay = TimeUnit.DAYS.toMillis(1);

        scheduler.scheduleAtFixedRate(
                raportService::generateReport,
                midnightDelay,
                oneDay,
                TimeUnit.MILLISECONDS
        );

        System.out.println("Daily report scheduled at midnight.");
    }

    private long computeDelay(LocalTime postTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime postDateTime = now.withHour(postTime.getHour())
                .withMinute(postTime.getMinute())
                .withSecond(postTime.getSecond())
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
            raportService.logPost(post);
        } else {
            System.out.println("❌ No publisher found for platform: " + post.getPlatform());
        }
    }

    public void stop() {
        if (!running) {

            return;
        }
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            running = false;
            System.out.println("Scheduler stopped");
        }
    }

    public List<ScheduledPost> getPosts() {
        return posts;
    }
}
