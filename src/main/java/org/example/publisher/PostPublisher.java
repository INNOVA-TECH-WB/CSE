package org.example;

import org.example.model.ScheduledPost;

public interface PostPublisher {
    void publish(ScheduledPost post);
}
