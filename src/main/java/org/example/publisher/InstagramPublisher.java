package org.example;

import org.example.model.ScheduledPost;

public class InstagramPublisher implements PostPublisher {
    @Override
    public void publish(ScheduledPost post) {
        System.out.println("[Instagram] " + post.getContent());
        // TODO:replace with real Instagram API call
    }
}
