package org.example.publisher;


import org.example.model.ScheduledPost;

public class FacebookPublisher implements PostPublisher {
    @Override
    public void publish(ScheduledPost post) {
        System.out.println("[Facebook] " + post.getContent());
        // TODO: replace with real Facebook API call
    }
}
