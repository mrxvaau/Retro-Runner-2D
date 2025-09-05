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
    private static final String BACKGROUND_PATH = "images/background.JPG";
    private static final Dimension FRAME_SIZE = new Dimension(960, 540); // Half of 1920x1080
    private static boolean isPromoCodesGenerated = false;

    private Clip openingMusic;
    private JFrame frame;

    // === Retro UI fields (styling only) ===
    private Font pixelFont; // Loaded from fonts/PressStart2P.ttf if available

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

        // Load pixel/arcade font (fallback to default if not found)
        loadPixelFont();

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(FRAME_SIZE);

        // --- Background Image ---
        try {
            Image backgroundImage = new ImageIcon(BACKGROUND_PATH).getImage();
            JLabel backgroundLabel = new JLabel(new ImageIcon(
                    backgroundImage.getScaledInstance(FRAME_SIZE.width, FRAME_SIZE.height, Image.SCALE_SMOOTH)
            ));
            backgroundLabel.setBounds(0, 0, FRAME_SIZE.width, FRAME_SIZE.height);
            layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);
        } catch (Exception e) {
            System.err.println("Background image not found - using fallback color");
            layeredPane.setBackground(new Color(30, 30, 30));
            layeredPane.setOpaque(true);
        }

        // --- Title Banner (retro) ---
        JPanel titleBanner = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

                int w = getWidth();
                int h = getHeight();

                // Pixel banner: layered rectangles
                g2.setColor(new Color(0, 132, 204)); // cyan/sky
                g2.fillRect(0, 0, w, h);
                g2.setColor(new Color(255, 165, 0)); // orange frame
                g2.fillRect(0, 0, w, 6);
                g2.fillRect(0, h - 6, w, 6);
                g2.fillRect(0, 0, 6, h);
                g2.fillRect(w - 6, 0, 6, h);

                // Inner strip for depth
                g2.setColor(new Color(9, 85, 150));
                g2.fillRect(8, 8, w - 16, h - 16);

                // Title text
                g2.setFont(getPixelFont(28f));
                String title = "RETRO RUNNER 2D";
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(title)) / 2;
                int ty = (h + fm.getAscent()) / 2 - 4;

                // Shadow + highlight for retro pop
                g2.setColor(Color.black);
                g2.drawString(title, tx + 2, ty + 2);
                g2.setColor(Color.white);
                g2.drawString(title, tx, ty);

                g2.dispose();
            }
        };
        titleBanner.setOpaque(false);
        titleBanner.setBounds((FRAME_SIZE.width - 600) / 2, 18, 600, 70);
        layeredPane.add(titleBanner, JLayeredPane.PALETTE_LAYER);

        // --- Main Content Panel (buttons) ---
        JPanel contentPanel = createContentPanel();
        contentPanel.setBounds(
                (FRAME_SIZE.width - 420) / 2,
                (FRAME_SIZE.height - 330) / 2 + 10,
                420, 330
        );
        layeredPane.add(contentPanel, JLayeredPane.MODAL_LAYER);

        frame.add(layeredPane);
        frame.setVisible(true);
        playOpeningMusic();
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 0, 14));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setOpaque(false);

        // Retro color palette
        Color baseButton = new Color(255, 255, 255, 210); // light panel base
        Color textColor = new Color(22, 22, 22);

        String[] buttonTexts = {"Start Game", "Highscores", "Options", "Premium", "Exit"};

        for (String text : buttonTexts) {
            JButton button = createStyledButton(text, getPixelFont(18f), baseButton, textColor);
            button.addActionListener(_ -> handleButtonAction(text));
            panel.add(button);
        }
        return panel;
    }

    private JButton createStyledButton(String text, Font font, Color bgColor, Color textColor) {
        JButton button = new JButton(text) {
            private boolean hover = false;
            private boolean pressed = false;

            {
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setOpaque(false);

                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                    @Override public void mousePressed(MouseEvent e) { pressed = true; repaint(); }
                    @Override public void mouseReleased(MouseEvent e){ pressed = false; repaint(); }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

                int w = getWidth();
                int h = getHeight();

                // Background plate
                Color plate = hover ? new Color(255, 246, 180, 230) : bgColor; // NES-like yellow on hover
                if (pressed) plate = new Color(230, 230, 230, 220);
                g2.setColor(plate);
                g2.fillRect(0, 0, w, h);

                // Chunky black outline
                g2.setColor(Color.black);
                g2.fillRect(0, 0, w, 4);
                g2.fillRect(0, h - 4, w, 4);
                g2.fillRect(0, 0, 4, h);
                g2.fillRect(w - 4, 0, 4, h);

                // Inner colored border for depth
                g2.setColor(hover ? new Color(255, 185, 54) : new Color(180, 180, 180));
                g2.fillRect(4, 4, w - 8, 3);
                g2.fillRect(4, h - 7, w - 8, 3);
                g2.fillRect(4, 4, 3, h - 8);
                g2.fillRect(w - 7, 4, 3, h - 8);

                // Text
                g2.setFont(font);
                g2.setColor(textColor);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(getText())) / 2;
                int ty = (h + fm.getAscent()) / 2 - 3;

                // Retro shadow + highlight
                g2.setColor(Color.black);
                g2.drawString(getText(), tx + 2, ty + 2);
                g2.setColor(new Color(240, 240, 240));
                g2.drawString(getText(), tx, ty);

                g2.dispose();
            }
        };

        button.setPreferredSize(new Dimension(400, 54));
        button.setFont(font);
        // Keep your hover border behavior via existing listeners (we don’t change your logic)

        // Your original hover borders/colors preserved (styling adapted)
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override public void mouseExited(MouseEvent e) {
                button.setCursor(Cursor.getDefaultCursor());
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
            case "Options":
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
        String score;
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
        scoreLabel.setFont(getPixelFont(16f));

        JButton backButton = new JButton("Back");
        backButton.setFont(getPixelFont(14f));
        backButton.addActionListener(_ -> highscoreDialog.dispose());

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
        File premiumFile = new File("PremiumYes.txt");
        if (!premiumFile.exists()) {
            JOptionPane.showMessageDialog(frame, "You are not a premium user!", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] options = {"Goku", "Smurf", "Sailor"};
        int choice = JOptionPane.showOptionDialog(frame,
                "<html><b><font color='green'>Premium Membership Activated</font></b><br>Choose your character</html>",
                "Access Granted", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[2]);

        if (choice == -1) {
            return;
        }

        String selectedCharacter;
        String characterFileName = switch (choice) {
            case 0 -> {
                selectedCharacter = "Goku";
                yield "images/player1.PNG";
            }
            case 1 -> {
                selectedCharacter = "Smurf";
                yield "images/player2.PNG";
            }
            default -> {
                selectedCharacter = "Sailor";
                yield "images/player3.PNG";
            }
        };

        File sourceFile = new File(characterFileName);
        File destinationFile = new File("images/player.PNG");

        try {
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
                MainGameEngine.main(new String[0]);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to start game!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    private void playOpeningMusic() {
        try {
            File audioFile = new File("audio/opening.wav");
            if (!audioFile.exists()) {
                System.err.println("Audio file not found: " + "audio/opening.wav");
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

    // ===== Helpers: pixel font loading (styling only) =====
    private void loadPixelFont() {
        try (InputStream is = new FileInputStream("fonts/PressStart2P.ttf")) {
            Font base = Font.createFont(Font.TRUETYPE_FONT, is);
            pixelFont = base.deriveFont(18f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(base);
        } catch (Exception e) {
            // Fallback if font file not found
            pixelFont = new Font("Dialog", Font.BOLD, 18);
        }
    }

    private Font getPixelFont(float size) {
        if (pixelFont == null) {
            return new Font("Dialog", Font.BOLD, Math.round(size));
        }
        return pixelFont.deriveFont(size);
    }
}
