package org.example.publisher;

import org.example.model.ScheduledPost;

public interface PostPublisher {
    void publish(ScheduledPost post);
}
