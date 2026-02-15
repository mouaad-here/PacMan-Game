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
        initMenuButtons();

    this.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            if (currentState == GameState.MENU) {
                Point click = e.getPoint();
                if (btnNewMap.contains(click)) startGame(true);
                else if (btnRegMap.contains(click)) startGame(false);
                else if (btnAlgo.contains(click)) startVisualization();
            }
        }
    });
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

    // @Override
    // public void paintComponent(Graphics g){
    //     super.paintComponent(g);
    //     pacman.draw(g);
    //     for(Wall wall: walls) wall.draw(g);
    //     for(Ghost ghost: ghosts) ghost.draw(g);
    //     for(Food food:foods) food.draw(g);
    // }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (currentState == GameState.MENU) {
            drawMenu(g);
        } else {
            // Existing drawing logic
            pacman.draw(g);
            for(Wall wall: walls) wall.draw(g);
            for(Ghost ghost: ghosts) ghost.draw(g);
            for(Food food: foods) food.draw(g);
            
            // if (currentState == GameState.VISUALIZING_ALGO) {
            //     // drawAlgorithmOverlay(g);
            // }
        }
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

    private void drawMenu(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Header
        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Monospaced", Font.BOLD, 40));
        g2.drawString("PAC-MAN", (boardWidth / 2) - 85, 120);

        // Draw interactive buttons
        drawButton(g2, btnNewMap, "START NEW MAP");
        drawButton(g2, btnRegMap, "REGISTERED MAP");
        drawButton(g2, btnAlgo, "VISUALIZE ALGO");
    }

    private void drawButton(Graphics2D g2, Rectangle rect, String text) {
        // Check mouse position for hover effect (requires a MouseMotionListener)
        Point mousePos = getMousePosition();
        boolean isHovered = (mousePos != null && rect.contains(mousePos));

        g2.setColor(isHovered ? Color.YELLOW : Color.DARK_GRAY);
        g2.fill(rect);
        
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.draw(rect);

        g2.setFont(new Font("Monospaced", Font.BOLD, 18));
        g2.setColor(isHovered ? Color.BLACK : Color.WHITE);
        
        // Center text in button
        FontMetrics fm = g2.getFontMetrics();
        int textX = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int textY = rect.y + (rect.height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, textX, textY);
    }

    protected void startVisualization() {
    this.currentState = GameState.ALGORITHM_VISUALIZATION;
    // Logic for the flood-fill "parcours" goes here
    repaint();
}
}