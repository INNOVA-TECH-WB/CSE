package org.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalTime;
import java.io.Serializable;
import java.util.UUID;

public class ScheduledPost implements Serializable {
    private String platform;
    private String content;
    private String id;


    @JsonFormat(pattern = "HH:mm")
    private LocalTime postTime;

    //its for json
    public ScheduledPost() { }

    public ScheduledPost(String platform, String content, LocalTime postTime) {
        this.id = UUID.randomUUID().toString();
        this.platform = platform;
        this.content = content;
        this.postTime = postTime;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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
