package org.example.model;
import org.example.model.ScheduledPost;
import org.example.service.DailyReportService;
import org.example.publisher.PostPublisher;
import org.quartz.*;

import java.time.LocalTime;
import java.util.Map;
public class PublishJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap data = context.getJobDetail().getJobDataMap();

        String postId = data.getString("postId");
        String platform = data.getString("platform");
        String content = data.getString("content");
        String time = data.getString("time"); // "HH:mm"

        try{
            SchedulerContext schedcontext = context.getScheduler().getContext();
            @SuppressWarnings("unchecked")
            Map<String,PostPublisher> publisherMap = (Map<String,PostPublisher>) schedcontext.get("publisherMap");
            DailyReportService raportService = (DailyReportService) schedcontext.get("raportService");
            PostPublisher postPublisher = publisherMap.get(platform);
            if(postPublisher == null){
                System.err.println("No publisher for platform: " + platform);


            }

            ScheduledPost post = new ScheduledPost(platform,content,LocalTime.parse(time));
            postPublisher.publish(post);
            if(raportService != null){
                raportService.logPost(post);
                System.out.println("Published postId=" + postId + " platform=" + platform);
            }


        } catch (SchedulerException se) {
            throw new JobExecutionException(se);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
