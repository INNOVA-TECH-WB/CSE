package org.example.view;

import org.example.model.ScheduledPost;
import org.example.service.DailyReportService;
import org.example.service.PostProvider;
import org.example.service.SchedulerService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.util.List;


public class Dashboard extends JFrame {
    private final SchedulerService scheduler;
    private final DailyReportService reportService;
    private final PostProvider provider;

    private final JLabel statusLabel;
    private final DefaultListModel<String> listModel;
    private final JList<String> postList;

    public Dashboard(List<ScheduledPost> posts,
                     SchedulerService scheduler,
                     DailyReportService reportService,
                     PostProvider provider) {
        this.reportService = reportService;
        this.scheduler = scheduler;
        this.provider = provider;

        setTitle("Social Media Scheduler Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                safeExit();
            }
        });

        setLayout(new BorderLayout());

        // List
        listModel = new DefaultListModel<>();
        for (ScheduledPost post : posts) listModel.addElement("[READY] " + post.toString());
        postList = new JList<>(listModel);
        add(new JScrollPane(postList), BorderLayout.CENTER);

        // Buttons
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

        // Status bar
        statusLabel = new JLabel("Scheduler stopped", SwingConstants.LEFT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel.setForeground(Color.RED);

        JPanel south = new JPanel(new BorderLayout());
        south.add(buttonPanel, BorderLayout.CENTER);
        south.add(statusLabel, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        // Menu
        JMenuBar menubar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem reportItem = new JMenuItem("Generate Report");
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(reportItem);
        fileMenu.add(exitItem);

        JMenu settingsMenu = new JMenu("Settings");
        JMenu themeMenu = new JMenu("Theme");
        JMenuItem lightTheme = new JMenuItem("Light Theme");
        JMenuItem darkTheme = new JMenuItem("Dark Theme");
        themeMenu.add(lightTheme);
        themeMenu.add(darkTheme);
        settingsMenu.add(themeMenu);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        helpMenu.add(aboutItem);

        menubar.add(fileMenu);
        menubar.add(settingsMenu);
        menubar.add(helpMenu);
        setJMenuBar(menubar);

        // Initial button states
        startButton.setEnabled(true);
        stopButton.setEnabled(false);

        // Actions

        darkTheme.addActionListener(e ->
                applyTheme(new Color(30, 30, 30), new Color(220, 220, 220))
        );


        startButton.addActionListener(e -> {
            scheduler.start();
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            reloadList("[SCHEDULED] ");
            setStatus("Scheduler running...", new Color(0, 128, 0));
        });

        stopButton.addActionListener(e -> {
            scheduler.stop();
            stopButton.setEnabled(false);
            startButton.setEnabled(true);
            reloadList("[STOPPED] ");
            setStatus("Scheduler stopped", Color.RED);
        });

        reportButton.addActionListener(e -> {
            reportService.generateReport();
            JOptionPane.showMessageDialog(this, "Report generated");
        });

        exitButton.addActionListener(e -> safeExit());

        refreshButton.addActionListener(e -> {
            reloadList("[REFRESHED] ");
            JOptionPane.showMessageDialog(this, "Post list refreshed!");
        });

        // Add
        addPostButton.addActionListener(e -> {
            JComboBox<String> platformBox = new JComboBox<>(new String[]{"TikTok", "Instagram", "Facebook"});
            JTextField contentField = new JTextField();
            JTextField timeField = new JTextField("14:00");
            JTextField videoField = new JTextField();
            JTextField photoField = new JTextField();

            JButton chooseVideoButton = new JButton("Choose Video");
            chooseVideoButton.addActionListener(ev -> {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    videoField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    photoField.setText(""); // Clear photo field
                }
            });

            JButton choosePhotoButton = new JButton("Choose Photo");
            choosePhotoButton.addActionListener(ev -> {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    photoField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    videoField.setText(""); // Clear video field
                }
            });





            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Platform:"));
            panel.add(platformBox);
            panel.add(new JLabel("Content:"));
            panel.add(contentField);
            panel.add(new JLabel("Time (HH:mm):"));
            panel.add(timeField);
            panel.add(new JLabel("Photo:"));
            panel.add(photoField);
            panel.add(choosePhotoButton);
            panel.add(new JLabel("Video:"));
            panel.add(videoField);
            panel.add(chooseVideoButton);

            int result = JOptionPane.showConfirmDialog(this, panel, "Add New Post", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    String platform = (String) platformBox.getSelectedItem();
                    String content = contentField.getText();
                    java.time.LocalTime postTime = java.time.LocalTime.parse(timeField.getText());

                    ScheduledPost newPost = new ScheduledPost(platform, content, postTime);
                    newPost.setVideoPath(videoField.getText());
                    newPost.setPhotoPath(photoField.getText());

                    scheduler.getPosts().add(newPost);
                    scheduler.schedulePost(newPost);
                    provider.saveScheduledPosts(scheduler.getPosts());

                    listModel.addElement("[SCHEDULED] " + newPost.toString());
                    JOptionPane.showMessageDialog(this, "Added new post");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
                }
            }
        });


        // Remove
        removePostButton.addActionListener(e -> {
            int idx = postList.getSelectedIndex();
            if (idx >= 0) {
                ScheduledPost removed = scheduler.getPosts().remove(idx);
                listModel.remove(idx);
                provider.saveScheduledPosts(scheduler.getPosts());
                JOptionPane.showMessageDialog(this, "Removed post: " + removed);
            } else {
                JOptionPane.showMessageDialog(this, "No post selected.");
            }
        });

        // Edit (This part is not updated for brevity, but you would apply the same logic as "Add Post")
        editPostButton.addActionListener(e -> {
            int idx = postList.getSelectedIndex();
            if (idx >= 0) {
                ScheduledPost post = scheduler.getPosts().get(idx);

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

                int result = JOptionPane.showConfirmDialog(this, panel, "Edit Post", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        post.setPlatform((String) platformBox.getSelectedItem());
                        post.setContent(contentField.getText());
                        post.setPostTime(java.time.LocalTime.parse(timeField.getText()));

                        listModel.set(idx, "[EDITED] " + post.toString());
                        provider.saveScheduledPosts(scheduler.getPosts());
                        JOptionPane.showMessageDialog(this, "Edited post: " + post);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a post to edit.");
            }
        });

        // Theme
        lightTheme.addActionListener(e -> applyTheme(Color.WHITE, Color.BLACK));
        darkTheme.addActionListener(e -> applyTheme(new Color(45, 45, 45), Color.WHITE));

        // About
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Core Scheduler Engine\nVersion 1.0\n© You"));

        // File > Report/Exit
        reportItem.addActionListener(e -> {
            reportService.generateReport();
            JOptionPane.showMessageDialog(this, "Report generated");
        });
        exitItem.addActionListener(e -> safeExit());

        setVisible(true);
    }

    private void reloadList(String prefix) {
        listModel.clear();
        for (ScheduledPost p : scheduler.getPosts()) listModel.addElement(prefix + p.toString());
    }

    private void setStatus(String text, Color color) {
        statusLabel.setText(text);
        statusLabel.setForeground(color);
    }

    private void applyTheme(Color bg, Color fg) {
        getContentPane().setBackground(bg);
        statusLabel.setBackground(bg);
        statusLabel.setForeground(fg);

        postList.setBackground(bg.darker());
        postList.setForeground(fg);

        for (Component c : getContentPane().getComponents()) {
            if (c instanceof JPanel panel) {
                panel.setBackground(bg);
                for (Component c2 : panel.getComponents()) {
                    c2.setBackground(bg);
                    c2.setForeground(fg);
                }
            }
        }
        repaint();


        JMenuBar bar = getJMenuBar();
        if (bar != null) {
            bar.setBackground(bg.darker());
            bar.setForeground(fg);
            for (MenuElement element : bar.getSubElements()) {
                updateMenuElement(element, bg, fg);
            }
        }



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

    private void updateMenuElement(MenuElement element, Color bg, Color fg) {
        Component c = element.getComponent();
        c.setBackground(bg.darker());
        c.setForeground(fg);
        for (MenuElement sub : element.getSubElements()) {
            updateMenuElement(sub, bg, fg);
        }
    }

}