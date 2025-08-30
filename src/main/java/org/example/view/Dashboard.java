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
    private final DefaultListModel<String> listModel; // shared list model
    private final JList<String> postList; // list UI

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

        // --- POST LIST ---
        listModel = new DefaultListModel<>();
        for (ScheduledPost post : posts) {
            listModel.addElement("[READY] " + post.toString());
        }
        postList = new JList<>(listModel);
        add(new JScrollPane(postList), BorderLayout.CENTER);

        // --- BUTTON PANEL ---
        JPanel buttonPanel = new JPanel();
        JButton startButton = new JButton("▶ Start Scheduler");
        JButton stopButton = new JButton("⏹ Stop Scheduler");
        JButton reportButton = new JButton("📄 Generate Report");
        JButton exitButton = new JButton("🚪 Safe Exit");
        JButton addPostButton = new JButton("➕ Add Post");
        JButton removePostButton = new JButton("🗑 Remove Post");
        JButton editPostButton = new JButton("✏ Edit Post");
        JButton refreshButton = new JButton("🔄 Refresh List");

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(reportButton);
        buttonPanel.add(exitButton);
        buttonPanel.add(addPostButton);
        buttonPanel.add(removePostButton);
        buttonPanel.add(editPostButton);
        buttonPanel.add(refreshButton);

        // --- STATUS BAR ---
        statusLabel = new JLabel("Scheduler stopped", SwingConstants.LEFT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel.setForeground(Color.RED);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        southPanel.add(statusLabel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        // --- MENU BAR ---
        JMenuBar menubar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        JMenuItem reportItem = new JMenuItem("Generate Report");
        fileMenu.add(reportItem);
        fileMenu.add(exitItem);

        // Settings menu
        JMenu settingsMenu = new JMenu("Settings");
        JMenuItem clearItem = new JMenuItem("Clear Posts");
        JMenu themeMenu = new JMenu("Theme");
        JMenuItem lightTheme = new JMenuItem("Light Theme");
        JMenuItem darkTheme = new JMenuItem("Dark Theme");
        themeMenu.add(lightTheme);
        themeMenu.add(darkTheme);
        settingsMenu.add(themeMenu);
        settingsMenu.add(clearItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        helpMenu.add(aboutItem);

        // Add menus
        menubar.add(fileMenu);
        menubar.add(settingsMenu);
        menubar.add(helpMenu);
        setJMenuBar(menubar);

        // --- INITIAL STATES ---
        startButton.setEnabled(true);
        stopButton.setEnabled(false);

        // --- ACTIONS ---
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

            int result = JOptionPane.showConfirmDialog(Dashboard.this, panel, "Add New Post", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    String platform = (String) platformBox.getSelectedItem();
                    String content = contentField.getText();
                    String timeText = timeField.getText();

                    java.time.LocalTime postTime = java.time.LocalTime.parse(timeText);
                    ScheduledPost newPost = new ScheduledPost(platform, content, postTime);

                    scheduler.getPosts().add(newPost);
                    scheduler.schedulePost(newPost);
                    listModel.addElement("[SCHEDULED] " + newPost.toString());

                    savePostsToJson();
                    JOptionPane.showMessageDialog(this, "Added new post");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Dashboard.this, "Invalid input: " + ex.getMessage());
                }
            }
        });

        exitButton.addActionListener(e -> safeExit());

        refreshButton.addActionListener(e -> {
            listModel.clear();
            for (ScheduledPost post : scheduler.getPosts()) {
                listModel.addElement("[REFRESHED] " + post.toString());
            }
            JOptionPane.showMessageDialog(this, "Post list refreshed!");
        });

        removePostButton.addActionListener(e -> {
            int index = postList.getSelectedIndex();
            if (index >= 0) {
                ScheduledPost removed = scheduler.getPosts().remove(index);
                listModel.remove(index);
                savePostsToJson();
                JOptionPane.showMessageDialog(this, "Removed post: " + removed.toString());
            } else {
                JOptionPane.showMessageDialog(this, "No post selected");
            }
        });

        editPostButton.addActionListener(e -> {
            int index = postList.getSelectedIndex();
            if (index >= 0) {
                ScheduledPost post = scheduler.getPosts().get(index);
                JComboBox<String> platformBox = new JComboBox<>(new String[]{"TikTok", "Instagram", "Facebook"});
                platformBox.setSelectedItem(post.getPlatform());

                JTextField contentField = new JTextField(post.getContent());
                JTextField timeField = new JTextField(post.getPostTime().toString());

                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("Platform:"));
                panel.add(platformBox);
                panel.add(new JLabel("Content:"));
                panel.add(contentField);
                panel.add(new JLabel("Time (HH:mm):"));
                panel.add(timeField);

                int result = JOptionPane.showConfirmDialog(Dashboard.this, panel, "Edit Post", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        String platform = (String) platformBox.getSelectedItem();
                        String content = contentField.getText();
                        java.time.LocalTime postTime = java.time.LocalTime.parse(timeField.getText());

                        // update post
                        post.setPlatform(platform);
                        post.setContent(content);
                        post.setPostTime(postTime);

                        // refresh list
                        listModel.set(index, "[EDITED] " + post.toString());
                        savePostsToJson();

                        JOptionPane.showMessageDialog(this, "Edited post: " + post.toString());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(Dashboard.this, "Invalid input: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a post to edit.");
            }
        });

        // Theme switching
        lightTheme.addActionListener(e -> applyTheme(Color.WHITE, Color.BLACK));
        darkTheme.addActionListener(e -> applyTheme(new Color(45, 45, 45), Color.WHITE));

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

    private void applyTheme(Color bg, Color fg) {
        getContentPane().setBackground(bg);
        statusLabel.setForeground(fg);
        statusLabel.setBackground(bg);
        repaint();
    }

    private void savePostsToJson() {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("posts.json");
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), scheduler.getPosts());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to save posts.json: " + ex.getMessage());
        }
    }
}
