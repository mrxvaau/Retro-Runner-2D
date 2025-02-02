package com.projectoop1aiub.edu.main;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class GameOverFrame {
    private static final Dimension SIZE = new Dimension(800, 600);  // Fixed frame size
    private static Clip bgMusic;

    public static void show() {
        JFrame frame = new JFrame("Game Over");
        frame.setSize(SIZE);
        frame.setLocationRelativeTo(null); // This ensures the frame opens at the center of the screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel with background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    // Load the background image
                    Image bgImage = new ImageIcon("images/game_over.png").getImage();
                    // Scale the image to fit the frame size (800x600)
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        // Game Over text
        JLabel title = new JLabel(" ", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 48));  // Adjusted font size for better visibility
        title.setForeground(Color.RED);
        title.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 200, 50, 200));

        JButton btnRestart = createButton("Play Again");
        JButton btnExit = createButton("Exit");

        btnRestart.addActionListener(e -> {
            stopMusic();
            frame.dispose();
            MainGameEngine.main(new String[0]);
        });

        btnExit.addActionListener(e -> {
            stopMusic();
            System.exit(0);
        });

        buttonPanel.add(btnRestart);
        buttonPanel.add(btnExit);

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);
        playMusic("audio/game_over_music.wav");
    }

    private static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 20));  // Smaller font size
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(50, 50, 50, 200));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 40));  // Smaller button size
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));
        return button;
    }

    private static void playMusic(String path) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(path));
            bgMusic = AudioSystem.getClip();
            bgMusic.open(audioStream);
            bgMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.err.println("Error loading audio: " + e.getMessage());
        }
    }

    private static void stopMusic() {
        if (bgMusic != null && bgMusic.isRunning()) {
            bgMusic.stop();
            bgMusic.close();
        }
    }
}
