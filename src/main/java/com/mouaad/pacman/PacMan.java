package com.mouaad.pacman;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import javax.swing.*;
import java.awt.Image;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;


public class PacMan extends JPanel {
    private GameState currentState = GameState.MENU;
    private GameRenderer renderer = new GameRenderer();
    private char[][] currentMap;
    private Rectangle btnNewMap, btnRegMap, btnAlgo;

    // Constant variable for the app
    private int rowCount = 23;
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
        initMenuButtons();

        // Input
        PlayerInput input = new PlayerInput(this);
        this.addKeyListener(input);
        this.setFocusable(true);
        this.requestFocusInWindow();
        
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (currentState == GameState.MENU) {
                    if (e.getKeyCode() == KeyEvent.VK_1) startGame(true);  // New Map
                    if (e.getKeyCode() == KeyEvent.VK_2) startGame(false); // Registered Map
                    if (e.getKeyCode() == KeyEvent.VK_3) startVisualization();
                }
            }
        });
        
        MouseAdapter menuHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (currentState == GameState.MENU) {
                    Point click = e.getPoint();
                    if (btnNewMap.contains(click)) startGame(true);
                    else if (btnRegMap.contains(click)) startGame(false);
                    else if (btnAlgo.contains(click)) startVisualization();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (currentState == GameState.MENU) {
                    // This forces the renderer to check the mouse position and change the color to yellow
                    repaint(); 
                }
            }
        };

        // Register the handler to both listener types
        this.addMouseListener(menuHandler);
        this.addMouseMotionListener(menuHandler);
                
        loadMap();
        
        Timer gameLoop = new Timer(16, e -> {
            updateGame();
            repaint();
        });
        
        gameLoop.start();
     
    }

    private void initMenuButtons() {
    int btnWidth = 350;
    int btnHeight = 50;
    int centerX = (boardWidth - btnWidth) / 2;
    
    // Position buttons using the extra space in your 23-row layout
    btnNewMap = new Rectangle(centerX, 250, btnWidth, btnHeight);
    btnRegMap = new Rectangle(centerX, 320, btnWidth, btnHeight);
    btnAlgo = new Rectangle(centerX, 390, btnWidth, btnHeight);
    }
    
    public void startGame(boolean useNewMap) {
        currentState = GameState.GENERATING_MAP;
        
        if (useNewMap) {
            MapGenerator generator = new MapGenerator(rowCount, columnCount);
            this.currentMap = generator.generate();
        } else {
            // Logic to load your "registered" (saved) map
            // this.currentMap = loadRegisteredMap();
        }
        
        // loadMapFromGrid(currentMap);
        currentState = GameState.PLAYING;
        repaint();
    }

    public void loadMap() {
        MapGenerator generator = new MapGenerator(rowCount, columnCount);
        char[][] dynamicGrid = generator.generate();

        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();

        for (int r = 2; r < rowCount; r++) {
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
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.render(g, currentState, pacman, walls, ghosts, foods, 
                        btnNewMap, btnRegMap, btnAlgo, boardWidth, boardHeight, this);
    }


    protected void startVisualization() {
    this.currentState = GameState.ALGORITHM_VISUALIZATION;
    // Logic for the flood-fill "parcours" goes here
    repaint();
    }
}