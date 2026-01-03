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
        frame.setResizable(false);
        // the game is not resizable
        frame.setVisible(true);
        // set the location of the window relative to another component
        // to center a window on the screen pass null
        //to center relative to a component pass the component as argument
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}
