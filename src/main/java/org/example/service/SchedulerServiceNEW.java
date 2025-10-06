package org.example.service;


import org.example.model.ScheduledPost;
import org.example.model.PublishJob;
import org.example.publisher.PostPublisher;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;



public class SchedulerServiceNEW {
    private final List<ScheduledPost> posts;
    private final Map<String, PostPublisher> publisherMap;
    private final DailyReportService reportService;
    private final PostProvider postProvider;

    private Scheduler scheduler;
    private boolean running = false;
    public SchedulerServiceNEW(List<ScheduledPost> posts, Map<String, PostPublisher> publisherMap, DailyReportService reportService, PostProvider postProvider) {
        this.posts = posts;
        this.publisherMap = publisherMap;
        this.reportService = reportService;
        this.postProvider = postProvider;

    }
    public synchronized void start() throws SchedulerException {
        if (running) {
            return;
        }
        StdSchedulerFactory factory = new StdSchedulerFactory();
        scheduler = factory.getScheduler();

        scheduler.getContext().put("publisherMap", publisherMap);
        scheduler.getContext().put("reportService", reportService);

        scheduler.start();

        running = true;
        System.out.println("Quartz scheduler started.");
    }

    public synchronized void stop() throws SchedulerException {
        if(scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown(true);
        }
        running = false;
    }
    public synchronized void schedulePost(ScheduledPost post) {
        try{
            if(scheduler == null || scheduler.isShutdown() ) {
                throw new SchedulerException("scheduler is null or shutdown");
            }
            JobKey jobKey= JobKey.jobKey(post.getId(),"posts");
            try {
                if (scheduler.checkExists(jobKey)) {
                    scheduler.deleteJob(jobKey);
                }
            } catch (SchedulerException ignored){}

            JobDataMap Data = new JobDataMap();
            Data.put("postId", post.getId());
            Data.put("platform", post.getPlatform());
            Data.put("content", post.getContent());
            Data.put("time", LocalTime.now());

            JobDetail detail = newJob(PublishJob.class)
                    .withIdentity(jobKey)
                    .usingJobData(Data)
                    .storeDurably(false)
                    .build();





        } catch (SchedulerException e) {

        }
    }
}
///////////////////////////////////////?////////////////////////////?/////?/////?/?////?///////////////?/?//?/?/??///?////?/?///