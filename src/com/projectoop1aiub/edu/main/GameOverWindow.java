package com.projectoop1aiub.edu.main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class GameOverWindow {
    public static void show () {

        JFrame frame = new JFrame("Game Window");
        frame.setSize(1024, 1024);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);


        GamePanel panel = new GamePanel();
        frame.add(panel);

        frame.setVisible(true);
    }
}

class GamePanel extends JPanel {
    private final Image backgroundImage;

    public GamePanel() {
        // Load background image (using placeholder here for background)
        backgroundImage = new ImageIcon("images/game_over.png").getImage(); // Make sure the image path is correct

        // Set layout to null for custom positioning of buttons
        setLayout(null);

        // Create buttons with actions
        JButton playAgainButton = new JButton("Play Again");
        playAgainButton.setBounds(400, 600, 200, 50); // x, y, width, height
        playAgainButton.setFocusPainted(false);
        playAgainButton.setFont(new Font("Arial", Font.BOLD, 18));
        playAgainButton.setBackground(new Color(34, 193, 195));
        playAgainButton.setForeground(Color.WHITE);
        playAgainButton.setBorder(BorderFactory.createLineBorder(new Color(34, 193, 195), 2));
        playAgainButton.addMouseListener(new ButtonHoverEffect(playAgainButton));
        playAgainButton.addActionListener(_ -> {
            playAgainAction();  // Call the playAgain method
        });

        JButton exitButton = new JButton("Exit");
        exitButton.setBounds(400, 700, 200, 50); // x, y, width, height
        exitButton.setFocusPainted(false);
        exitButton.setFont(new Font("Arial", Font.BOLD, 18));
        exitButton.setBackground(new Color(255, 99, 71));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBorder(BorderFactory.createLineBorder(new Color(255, 99, 71), 2));
        exitButton.addMouseListener(new ButtonHoverEffect(exitButton));
        exitButton.addActionListener(_ -> {
            exitAction();  // Call the exit method
        });

        // Adding placeholder text to the buttons
        JLabel playAgainPlaceholder = new JLabel(" ");
        playAgainPlaceholder.setBounds(620, 600, 200, 50);  // Displayed on the screen as placeholder
        playAgainPlaceholder.setForeground(Color.GRAY);
        playAgainPlaceholder.setFont(new Font("Arial", Font.ITALIC, 14));

        JLabel exitPlaceholder = new JLabel(" ");
        exitPlaceholder.setBounds(620, 700, 200, 50);  // Displayed on the screen as placeholder
        exitPlaceholder.setForeground(Color.GRAY);
        exitPlaceholder.setFont(new Font("Arial", Font.ITALIC, 14));

        // Add buttons and placeholders to the panel
        add(playAgainButton);
        add(exitButton);
        add(playAgainPlaceholder);
        add(exitPlaceholder);

        // Play background music
        playBackgroundMusic();
    }

    // Action for Play Again button
    private void playAgainAction() {
        JOptionPane.showMessageDialog(this, "Play Again button clicked! Reset the game...");
        MainGameEngine.main(new String[0]);
    }

    // Action for Exit button
    private void exitAction() {
        int confirmed = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit Game", JOptionPane.YES_NO_OPTION);
        if (confirmed == JOptionPane.YES_OPTION) {
            System.exit(0);  // Close the application
        }
    }

    // Method to play background music
    private void playBackgroundMusic() {
        try {
            // Load the audio file
            File musicFile = new File("audio/game_over_music.wav");  // Update the path if necessary
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            // Start playing the music in a loop
            clip.loop(Clip.LOOP_CONTINUOUSLY);  // You can change to `clip.start()` if you want it to play once
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace(); // Handle any audio exceptions
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Draw background image
    }
}

class ButtonHoverEffect extends MouseAdapter {
    private final JButton button;

    public ButtonHoverEffect(JButton button) {
        this.button = button;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Change button style on hover
        button.setBackground(new Color(211, 6, 6, 197)); // Lighter shade for hover effect
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Reset to original background color when hover is over
        if (button.getText().equals("Play Again")) {
            button.setBackground(new Color(17, 37, 189));
        } else {
            button.setBackground(new Color(216, 14, 148));
        }
    }
}
