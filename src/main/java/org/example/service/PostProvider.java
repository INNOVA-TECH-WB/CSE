package org.example.service;

import org.example.model.ScheduledPost;
import java.util.List;

public interface PostProvider {
    List<ScheduledPost> getScheduledPosts();
    void saveScheduledPosts(List<ScheduledPost> posts);
}
