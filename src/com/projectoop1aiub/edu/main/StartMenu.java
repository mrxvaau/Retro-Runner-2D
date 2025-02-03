package com.projectoop1aiub.edu.main;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
                ShowHighscores();
                break;
            case "Change characters ":
                showOptions();
                break;
            case "Premium":
                showPremiumDialog();
                break;
            case "Exit":
                System.exit(0);
                break;
        }
    }

    private void ShowHighscores() {
        // Read score from score.txt
        String score = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader("score.txt"));
            score = reader.readLine();
            reader.close();
        } catch (IOException e) {
            score = "Error reading score!";
        }


        JDialog highscoreDialog = new JDialog(frame, "Highscore", true);
        highscoreDialog.setSize(300, 150);
        highscoreDialog.setLocationRelativeTo(frame);


        JLabel scoreLabel = new JLabel("Highscore: " + score, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));


        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> highscoreDialog.dispose());


        highscoreDialog.setLayout(new BorderLayout());
        highscoreDialog.add(scoreLabel, BorderLayout.CENTER);
        highscoreDialog.add(backButton, BorderLayout.SOUTH);


        highscoreDialog.setVisible(true);
    }

    public void showPremiumDialog() {
        String code = JOptionPane.showInputDialog(frame,
                "Enter premium code:", "Premium Access", JOptionPane.PLAIN_MESSAGE);

        if (code != null && !code.isEmpty()) {
            try {

                BufferedReader reader = new BufferedReader(new FileReader("promo_codes.txt"));
                String line;
                boolean isPremium = false;

                while ((line = reader.readLine()) != null) {
                    if (line.trim().equals(code)) {
                        isPremium = true;
                        break;
                    }
                }
                reader.close();

                if (isPremium) {

                    JOptionPane.showMessageDialog(frame,
                            "You are a premium user!", "Premium", JOptionPane.INFORMATION_MESSAGE);


                    File premiumFile = new File("PremiumYes.txt");
                    if (!premiumFile.exists()) {
                        premiumFile.createNewFile();
                    }
                } else {

                    JOptionPane.showMessageDialog(frame,
                            "Invalid code. Please try again.", "Premium", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {

                JOptionPane.showMessageDialog(frame,
                        "An error occurred while processing the code.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showOptions() {
        // Check if premiums.Xt file exists
        File premiumFile = new File("PremiumYes.txt");
        if (!premiumFile.exists()) {
            JOptionPane.showMessageDialog(frame, "You are not a premium user!", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Options dialog for selecting a character
        String[] options = {"Goku", "Smurf", "Sailor"};
        int choice = JOptionPane.showOptionDialog(frame,
                "<html><b><font color='green'>Premium Membership Activated</font></b><br>Choose your character</html>",
                "Access Granted", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[2]);

        if (choice == -1) {

            return;
        }

        String selectedCharacter;
        String characterFileName;


        switch (choice) {
            case 0: // Goku
                selectedCharacter = "Goku";
                characterFileName = "images/player1.png";
                break;
            case 1: // Smurf
                selectedCharacter = "Smurf";
                characterFileName = "images/player2.png";
                break;
            case 2: // Sailor
            default:
                selectedCharacter = "Sailor";
                characterFileName = "images/player3.png";
                break;
        }

        File sourceFile = new File(characterFileName);
        File destinationFile = new File("images/player.png");

        try {
            // Copy the selected character image to the player.png location
            Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            JOptionPane.showMessageDialog(frame, selectedCharacter + " has been selected successfully!", "Character Selected", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Failed to select character. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
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
