package org.example.view;

import org.example.model.ScheduledPost;
import org.example.service.DailyReportService;
import org.example.service.SchedulerService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.util.List;

public class Dashboard extends JFrame {
    private final SchedulerService scheduler;
    private final DailyReportService reportService;
    private final JLabel statusLabel; // status bar label

    public Dashboard(List<ScheduledPost> posts, SchedulerService scheduler, DailyReportService reportService) {
        this.reportService = reportService;
        this.scheduler = scheduler;

        setTitle("Social Media Scheduler Dashboard");
        setSize(1920, 1024);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // safe exit
        setLocationRelativeTo(null);

        // ask confirmation when window closes
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                safeExit();
            }
        });

        setLayout(new BorderLayout());

        // list of posts
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (ScheduledPost post : posts) {
            listModel.addElement("[READY] " + post.toString());
        }
        JList<String> postList = new JList<>(listModel);
        add(new JScrollPane(postList), BorderLayout.CENTER);

        // buttons
        JPanel buttonPanel = new JPanel();
        JButton startButton = new JButton("▶ Start Scheduler");
        JButton stopButton = new JButton("⏹ Stop Scheduler");
        JButton reportButton = new JButton("📄 Generate Report");
        JButton exitButton = new JButton("🚪 Safe Exit");
        JButton addPostButton = new JButton("➕ Add Post");

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(reportButton);
        buttonPanel.add(exitButton);
        buttonPanel.add(addPostButton);

        // status bar (bottom of window)
        statusLabel = new JLabel("Scheduler stopped", SwingConstants.LEFT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel.setForeground(Color.RED);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        southPanel.add(statusLabel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        // initial states
        startButton.setEnabled(true);
        stopButton.setEnabled(false);

        // actions
        startButton.addActionListener(e -> {
            scheduler.start();
            startButton.setEnabled(false);
            stopButton.setEnabled(true);

            listModel.clear();
            for (ScheduledPost post : scheduler.getPosts()) {
                listModel.addElement("[SCHEDULED] " + post.toString());
            }
            statusLabel.setText("Scheduler running...");
            statusLabel.setForeground(new Color(0, 128, 0));
            JOptionPane.showMessageDialog(this, "Scheduler started");
        });

        stopButton.addActionListener(e -> {
            scheduler.stop();
            stopButton.setEnabled(false);
            startButton.setEnabled(true);

            listModel.clear();
            for (ScheduledPost post : scheduler.getPosts()) {
                listModel.addElement("[STOPPED] " + post.toString());
            }
            statusLabel.setText("Scheduler stopped");
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, "Scheduler stopped");
        });

        reportButton.addActionListener(e -> {
            reportService.generateReport();
            JOptionPane.showMessageDialog(this, "Report generated");
        });
        addPostButton.addActionListener(e -> {
            JComboBox<String> platformBox = new JComboBox<>(new String[]{"TikTok", "Instagram", "Facebook"});
            JTextField timeField = new JTextField("14:00"); // default time
            JTextField contentField = new JTextField();

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Platform:"));
            panel.add(platformBox);
            panel.add(new JLabel("Content:"));
            panel.add(contentField);
            panel.add(new JLabel("Time (HH:mm):"));
            panel.add(timeField);


            int result = JOptionPane.showConfirmDialog(Dashboard.this,panel,"add new post",JOptionPane.OK_CANCEL_OPTION);
            if(result == JOptionPane.OK_OPTION){
                try{
                    String platform = (String) platformBox.getSelectedItem();
                    String content = contentField.getText();
                    String timeText = timeField.getText();

                    java.time.LocalTime postTime = java.time.LocalTime.parse(timeText);
                    ScheduledPost newPost = new ScheduledPost(platform, content, postTime);

                    scheduler.getPosts().add(newPost);
                    scheduler.schedulePost(newPost);
                    listModel.addElement("[SCHEDULED] " + newPost.toString());

                    JOptionPane.showMessageDialog(this, "Added new post");
                }catch (Exception ex){
                    JOptionPane.showMessageDialog(Dashboard.this, "Invalid input: " + ex.getMessage());
                }
            }

        });



        exitButton.addActionListener(e -> safeExit());

        setVisible(true);
    }

    private void safeExit() {
        int confirm = JOptionPane.showConfirmDialog(
                this, "Are you sure you want to exit?\nFinal report will be generated.",
                "Confirm Exit", JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            scheduler.stop();
            reportService.generateReport();
            System.exit(0);
        }
    }



}


//// nina kocham cie
