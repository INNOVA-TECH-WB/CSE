package org.example;
import org.example.model.ScheduledPost;

import java.util.List;
public interface PostProvider {
    List<ScheduledPost> getScheduledPosts();

}
