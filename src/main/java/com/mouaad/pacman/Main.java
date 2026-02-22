package com.mouaad.pacman;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {

        JFrame frame = new JFrame("Pac-Man");

        PacMan pacmanGame = new PacMan();

        frame.add(pacmanGame);

        frame.pack();
        // The game is not resizable
        frame.setResizable(false);

        frame.setVisible(true);

        pacmanGame.requestFocusInWindow();

        // The window is centered
        frame.setLocationRelativeTo(null);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}
