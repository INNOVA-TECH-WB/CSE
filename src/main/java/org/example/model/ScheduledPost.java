package org.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalTime;
import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import java.io.Serializable;
import java.util.UUID;

public class ScheduledPost implements Serializable {
    private String platform;
    private String content;
    private String id;
    private String VideoPath;
    private String PhotoPath;


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

    public String getVideoPath(){
        return VideoPath;
    }
    public void setVideoPath(String videoPath){
        VideoPath = videoPath;
    }

    public String getPhotoPath(){
        return PhotoPath;
    }

    public void setPhotoPath(String photoPath){
        PhotoPath = photoPath;
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
        String mediaInfo = "";
        if (VideoPath != null && !VideoPath.isEmpty()) {
            mediaInfo = " [Video: " + VideoPath + "]";
        } else if (PhotoPath != null && !PhotoPath.isEmpty()) {
            mediaInfo = " [Photo: " + PhotoPath + "]";

        }
        return "Post on " + platform + " at " + postTime + ": " + content + mediaInfo;

    }
}
