package org.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;

public class ScheduledPost {
    private String platform;
    private String content;

    // Ensure HH:mm formatting in JSON
    @JsonFormat(pattern = "HH:mm")
    private LocalTime postTime;

    // Jackson needs a no-args constructor
    public ScheduledPost() { }

    public ScheduledPost(String platform, String content, LocalTime postTime) {
        this.platform = platform;
        this.content = content;
        this.postTime = postTime;
    }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalTime getPostTime() { return postTime; }
    public void setPostTime(LocalTime postTime) { this.postTime = postTime; }

    @Override
    public String toString() {
        return "Post on " + platform + " at " + postTime + ": " + content;
    }
}
