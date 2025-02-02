package com.projectoop1aiub.edu.main;
import java.io.*;

public class ScoreBoard {
    private static final String SCORE_FILE = "score.txt";

    public static void saveScore(int score) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE));
            writer.write("Score: " + score);
            writer.close();
        } catch (Exception e) {
            System.err.println("Error saving score: " + e.getMessage());
        }
    }

    public static int loadScore() {
        int score = 0;
        try {
            File scoreFile = new File(SCORE_FILE);
            if (scoreFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(SCORE_FILE));
                String line = reader.readLine();
                if (line != null) {
                    score = Integer.parseInt(line.split(": ")[1]);
                }
                reader.close();
            }
        } catch (Exception e) {
            System.err.println("Error loading score: " + e.getMessage());
        }
        return score;
    }
}
