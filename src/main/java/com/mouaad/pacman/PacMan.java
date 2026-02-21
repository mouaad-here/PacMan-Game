package com.mouaad.pacman;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import javax.swing.*;
import java.awt.Image;
import java.awt.event.KeyListener;
import java.awt.font.ImageGraphicAttribute;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Iterator;

public class PacMan extends JPanel {
    private GameState currentState = GameState.MENU;
    private GameRenderer renderer = new GameRenderer();
    private char[][] originalMap;
    private char[][] workingMap;
    private Rectangle btnNewMap, btnRegMap, btnAlgo, btnPlay;
    private List<MapGenerator.Pair> vizPath;
    private int vizStep = 0;

    // Constant variable for the app
    private int rowCount = 23;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    // UI element
    private int score = 0;
    private int lives = 3;
    public Image livesImage;

    // the building blocks of the game
    HashSet<Wall> walls;
    HashSet<Food> foods;
    HashSet<Ghost> ghosts;
    PacmanPlayer pacman;
    int btnWidth = 350;
    int btnHeight = 50;
    int centerX = (boardWidth - btnWidth) / 2;
    
    PacMan() {
        // initialise the blocks 
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();
        
        livesImage = new ImageIcon(getClass().getResource("lives.png")).getImage();

        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.black);
        initMenuButtons();

        // Input
        PlayerInput input = new PlayerInput(this);
        this.addKeyListener(input);
        this.setFocusable(true);
        this.requestFocusInWindow();
        
        generateNewMap();

        this.addKeyListener(new KeyAdapter() {
        
        @Override
        public void keyPressed(KeyEvent e) {
            if (currentState == GameState.MENU) {
                score = 0;
                originalMap = workingMap;
                loadMapFromGrid(originalMap);

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_1:
                        generateNewMap();
                        break;
                        
                        case KeyEvent.VK_2:
                            startGame(); 
                            break;
                            
                            case KeyEvent.VK_3:
                                startVisualization();
                        break;

                    case KeyEvent.VK_4:
                        // loadRegisteredMap(); 
                        break;
                }
            }
        }
    });
        
        MouseAdapter menuHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (currentState == GameState.MENU) {
                    Point click = e.getPoint();
                    if (btnNewMap.contains(click)) generateNewMap();
                    else if (btnPlay.contains(click)) currentState = GameState.PLAYING;
                    else if (btnAlgo.contains(click)) startVisualization();
                    else if (btnRegMap.contains(click)) {};
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (currentState == GameState.MENU) {
                    repaint(); 
                }
            }
        };

        // Register the handler to both listener types
        this.addMouseListener(menuHandler);
    
        this.addMouseMotionListener(menuHandler);

        
        Timer gameLoop = new Timer(16, e -> {
            updateGame();
            repaint();
        });
        
        gameLoop.start();
     
    }
    public void generateNewMap(){
    MapGenerator generator = new MapGenerator(rowCount, columnCount);
    this.originalMap = generator.generate();
    this.workingMap = deepCopy(originalMap);
    loadMapFromGrid(this.originalMap);
    this.vizPath = generator.getVisitOrder();
    this.vizStep = 0;
    }

    private char[][] deepCopy(char[][] originalMap) {
            char[][] copy = new char[originalMap.length][];
        for (int i = 0; i < originalMap.length; i++) {
            copy[i] = originalMap[i].clone();
        }
        return copy;
    }

    private void initMenuButtons() {
    int btnWidth = 350;
    int btnHeight = 50;
    int centerX = (boardWidth - btnWidth) / 2;

    btnNewMap = new Rectangle(centerX, 220, btnWidth, btnHeight);
    btnPlay = new Rectangle(centerX, 290, btnWidth, btnHeight);
    btnAlgo = new Rectangle(centerX, 360, btnWidth, btnHeight);
    btnRegMap = new Rectangle(centerX, 430, btnWidth, btnHeight);
    }
    
    public void startGame() {
        currentState = GameState.PLAYING;
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

        if (currentState == GameState.PLAYING) {

            pacman.move(walls);

            checkFoodCollision();

            if (foods.isEmpty()) {
                System.out.println("VICTORY!");
                currentState = GameState.MENU; // temporary
            }
        }

        else if (currentState == GameState.ALGORITHM_VISUALIZATION) {

            if (vizPath != null && vizStep < vizPath.size()) {
                vizStep++;
            }
        }
    }

    private void checkFoodCollision() {

        Iterator<Food> iterator = foods.iterator();

        while (iterator.hasNext()) {
            Food food = iterator.next();

            if (pacman.getBounds().intersects(food.getBounds())) {
                iterator.remove(); 
                score += 1;
                break;
            }
        }
    }
    public void startVisualization() {
        this.currentState = GameState.ALGORITHM_VISUALIZATION;
        this.workingMap = deepCopy(originalMap);
        loadMapFromGrid(this.originalMap);
        this.vizStep = 0;
    }


    public void loadMapFromGrid(char[][] grid) {
        walls.clear();
        foods.clear();
        ghosts.clear();

        for (int r = 2; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char tile = grid[r][c];
                int x = c * tileSize;
                int y = r * tileSize;

                if (tile == 'X') walls.add(new Wall(x, y, tileSize));
                else if (tile == ' ') foods.add(new Food(x, y, tileSize));
                else if (tile == 'M') pacman = new PacmanPlayer(x, y, tileSize);
                else if (isGhost(tile)) ghosts.add(new Ghost(tile, x, y, tileSize));
            }
        }
    }

    public GameState getCurrentState() {
    return currentState;
    }

    public void setState(GameState state) {
    this.currentState = state;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        renderer.render(g, currentState, pacman, walls, ghosts, foods, 
                        btnNewMap,btnPlay, btnRegMap, btnAlgo, boardWidth, boardHeight, this, vizPath, vizStep,
                        score, lives, livesImage);
    }
}