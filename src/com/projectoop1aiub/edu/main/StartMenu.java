package com.projectoop1aiub.edu.main;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class StartMenu {
    private static final String PROMO_CODE_FILE = "promo_codes.txt";
    private static final String BACKGROUND_PATH = "images/background.jpg";
    private static final Dimension FRAME_SIZE = new Dimension(960, 540); // Half of 1920x1080
    private static boolean isPromoCodesGenerated = false;
    private Clip openingMusic;
    private JFrame frame;

    public static void main(String[] args) {
        if (!isPromoCodesGenerated) {
            generatePromoCodes();
            isPromoCodesGenerated = true;
        }
        SwingUtilities.invokeLater(() -> new StartMenu().displayMenu());
    }

    public void displayMenu() {
        frame = new JFrame("Game Launcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FRAME_SIZE);
        frame.setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(FRAME_SIZE);

        // Background Image Handling
        try {
            Image backgroundImage = new ImageIcon(BACKGROUND_PATH).getImage();
            JLabel backgroundLabel = new JLabel(new ImageIcon(backgroundImage.getScaledInstance(
                    FRAME_SIZE.width, FRAME_SIZE.height, Image.SCALE_SMOOTH)));
            backgroundLabel.setBounds(0, 0, FRAME_SIZE.width, FRAME_SIZE.height);
            layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);
        } catch (Exception e) {
            System.err.println("Background image not found - using fallback color");
            layeredPane.setBackground(new Color(30, 30, 30));
            layeredPane.setOpaque(true);
        }

        // Main Content Panel
        JPanel contentPanel = createContentPanel();
        contentPanel.setBounds(
                (FRAME_SIZE.width - 400) / 2,
                (FRAME_SIZE.height - 350) / 2,
                400, 350
        );
        layeredPane.add(contentPanel, JLayeredPane.PALETTE_LAYER);

        frame.add(layeredPane);
        frame.setVisible(true);
        playOpeningMusic("audio/opening.wav");
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 0, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setOpaque(false);

        Font buttonFont = new Font("Arial", Font.BOLD, 18);
        Color buttonColor = new Color(255, 255, 255, 200);
        Color textColor = new Color(50, 50, 50);

        String[] buttonTexts = {
                "Start Game", "Highscores", "Options", "Premium", "Exit"
        };

        for (String text : buttonTexts) {
            JButton button = createStyledButton(text, buttonFont, buttonColor, textColor);
            button.addActionListener(e -> handleButtonAction(text));
            panel.add(button);
        }

        return panel;
    }

    private JButton createStyledButton(String text, Font font, Color bgColor, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.SrcOver.derive(0.9f));
                super.paintComponent(g2);
                g2.dispose();
            }
        };

        button.setFont(font);
        button.setForeground(textColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150, 100), 2),
                new EmptyBorder(10, 25, 10, 25)
        ));

        // Hover Effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(245, 245, 245, 220));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                        new EmptyBorder(10, 25, 10, 25)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(150, 150, 150, 100), 2),
                        new EmptyBorder(10, 25, 10, 25)
                ));
            }
        });

        return button;
    }

    private void handleButtonAction(String command) {
        switch (command) {
            case "Start Game":
                startGame();
                break;
            case "Highscores":
                JOptionPane.showMessageDialog(frame, "Highscore feature coming soon!");
                break;
            case "Options":
                JOptionPane.showMessageDialog(frame, "Options menu under development");
                break;
            case "Premium":
                showPremiumDialog();
                break;
            case "Exit":
                System.exit(0);
                break;
        }
    }

    private void showPremiumDialog() {
        String code = JOptionPane.showInputDialog(frame,
                "Enter premium code:", "Premium Access", JOptionPane.PLAIN_MESSAGE);
        if (code != null && !code.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Code validation feature under development", "Premium", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void startGame() {
        stopOpeningMusic();
        frame.dispose();
        new Thread(() -> {
            try {
                // Replace with your game initialization logic
                //JOptionPane.showMessageDialog(null, "Launching game...");
                MainGameEngine.main(new String[0]);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to start game!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    private void playOpeningMusic(String filePath) {
        try {
            File audioFile = new File(filePath);
            if (!audioFile.exists()) {
                System.err.println("Audio file not found: " + filePath);
                return;
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            openingMusic = AudioSystem.getClip();
            openingMusic.open(audioInputStream);
            openingMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.err.println("Error playing music: " + e.getMessage());
        }
    }

    private void stopOpeningMusic() {
        if (openingMusic != null && openingMusic.isRunning()) {
            openingMusic.stop();
            openingMusic.close();
        }
    }

    private static void generatePromoCodes() {
        File file = new File(PROMO_CODE_FILE);
        if (file.exists()) return;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROMO_CODE_FILE))) {
            Set<String> codes = new HashSet<>();
            while (codes.size() < 100) {
                String code = "PREMIUM" + String.format("%04d", (int) (Math.random() * 10000));
                codes.add(code);
            }
            for (String code : codes) {
                writer.write(code);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Failed to generate promo codes!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}