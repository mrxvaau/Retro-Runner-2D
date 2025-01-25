package com.projectoop1aiub.edu.main;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * StartMenu provides a starting point for the game.
 */
public class StartMenu {

    private static final String PROMO_CODE_FILE = "promo_codes.txt";
    private static boolean isPromoCodesGenerated = false;
    private static boolean isPremiumUser = false;
    private Clip openingMusic;

    public static void main(String[] args) {
        if (!isPromoCodesGenerated) {
            generatePromoCodes();
            isPromoCodesGenerated = true;
        }
        new StartMenu().displayMenu();
    }

    public void displayMenu() {
        JFrame frame = new JFrame("Start Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1));

        JButton startGameButton = new JButton("Start Game");
        JButton highScoreButton = new JButton("Highscore");
        JButton optionsButton = new JButton("Options");
        JButton buyPremiumButton = new JButton("Buy Premium");
        JButton exitButton = new JButton("Exit");

        startGameButton.addActionListener(e -> startGame());

        exitButton.addActionListener(e -> System.exit(0));

        panel.add(startGameButton);
        panel.add(highScoreButton);
        panel.add(optionsButton);
        panel.add(buyPremiumButton);
        panel.add(exitButton);

        frame.add(panel);
        frame.setVisible(true);

        playOpeningMusic("audio/opening.wav");
    }

    private void startGame() {
        stopOpeningMusic();
        System.out.println("Starting the game...");
        new Thread(() -> {
            try {
                MainGameEngine.main(null); // Launch the Main Game Engine
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to start the game.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    private void playOpeningMusic(String filePath) {
        try {
            File audioFile = new File(filePath);
            if (!audioFile.exists()) {
                System.err.println("Audio file not found: " + filePath);
                return; // Continue without audio
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            openingMusic = AudioSystem.getClip();
            openingMusic.open(audioInputStream);
            openingMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.err.println("Error playing opening music: " + e.getMessage());
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
                String code = "CODE" + (int) (Math.random() * 10000);
                codes.add(code);
            }
            for (String code : codes) {
                writer.write(code);
                writer.newLine();
            }
            System.out.println("Promo codes generated.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
