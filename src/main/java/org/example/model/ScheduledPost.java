package org.example.model;
import java.time.LocalTime;

public class ScheduledPost {
    private String Platform;
    private String Content;
    private LocalTime PostTime;
    public ScheduledPost(String Platform, String Content, LocalTime Time) {
        this.Platform = Platform;
        this.Content = Content;
        this.PostTime = Time;
    }
    public String getPlatform() {
        return Platform;
    }
    public String getContent() {
        return Content;
    }
    //public LocalTime getTime() {
      //  return PostTime;
    //}
    @Override
    public String toString() {
        return "Post on " + Platform + " at " + PostTime + ": " + Content;
    }


    public LocalTime getPostTime() {
        return PostTime;
    }
}
