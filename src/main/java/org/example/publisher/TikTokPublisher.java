package org.example;

import org.example.model.ScheduledPost;

public class TikTokPublisher implements PostPublisher {
    @Override
    public void publish(ScheduledPost post) {
        System.out.println("[TikTok] " + post.getContent());
        // TODO: replace with real TikTok API call
    }
}
