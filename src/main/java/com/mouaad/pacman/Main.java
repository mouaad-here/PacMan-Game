package com.mouaad.pacman;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args){
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        JFrame frame = new JFrame("Pac-Man");

        PacMan pacmanGame = new PacMan();
        pacmanGame.loadMap();
        frame.add(pacmanGame);

        frame.pack();
        // The game is not resizable
        frame.setResizable(false);

        frame.setVisible(true);

        // The window is centered
        frame.setLocationRelativeTo(null);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}
