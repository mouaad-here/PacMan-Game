package com.mouaad.pacman;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import javax.swing.*;
import java.awt.Image;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class PacMan extends JPanel {

    // Constant variable for the app
    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;


    // the building blocks of the game
    HashSet<Wall> walls;
    HashSet<Food> foods;
    HashSet<Ghost> ghosts;
    PacmanPlayer pacman;


    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.black);

        // Input
        PlayerInput input = new PlayerInput(this);
        this.addKeyListener(input);
        this.setFocusable(true);
        this.requestFocusInWindow();

        loadMap();

        Timer gameLoop = new Timer(16, e -> {
                updateGame();
                repaint();
        });

        gameLoop.start();
    }

    public void loadMap() {
        MapGenerator generator = new MapGenerator(rowCount, columnCount);
        char[][] dynamicGrid = generator.generate();

        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char tile = dynamicGrid[r][c];
                int x = c * tileSize;
                int y = r * tileSize;

                // Delegating object creation to specialized classes
                if (tile == 'X') {
                    Wall w = new Wall(x, y, tileSize);
                    walls.add(w);
                    if (w.image == null || w.image.getWidth(null) <= 0) {
                        System.out.println("ALERT: Wall image failed to load at " + x + "," + y);
                    }
                }
                else if (tile == ' ') foods.add(new Food(x, y, tileSize));
                else if (tile == 'M') pacman = new PacmanPlayer(x, y, tileSize);
                else if (isGhost(tile)) ghosts.add(new Ghost(tile, x, y, tileSize));
            }
        }
    }

    private boolean isGhost(char tile) {
        return "BRYP".indexOf(tile) != -1;
    }

    private void updateGame() {
        // Move Pacman with wall collision
        pacman.move(walls);

        // Move Ghosts
//        for (Ghost ghost : ghosts) {
//            ghost.updateABFS(walls, pacman);
//            ghost.move(walls, tileSize);
//        }
//
//        checkInteractions();
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        pacman.draw(g);
        for(Wall wall: walls) wall.draw(g);
        for(Ghost ghost: ghosts) ghost.draw(g);
        for(Food food:foods) food.draw(g);
    }

}