package com.mouaad.pacman;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import javax.swing.*;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;

enum GhostMode {
    CHASE, SCATTER
}

public class PacMan extends JPanel {
    private GameState currentState = GameState.MENU;
    private GameRenderer renderer = new GameRenderer();
    private char[][] originalMap;
    private char[][] workingMap;
    private Rectangle btnNewMap, btnRegMap, btnAlgo, btnPlay;
    private Rectangle btnRestart, btnGameOverMenu;
    private List<MapGenerator.Pair> vizPath;
    private int vizStep = 0;
    private boolean gameStarted = false;
    private long frightenedEndTime = 0;
    private String saveNameInput = "";
    private String[] availableMaps = {};
    private List<Rectangle> mapButtons = new ArrayList<>();
    private Rectangle btnBackAction, btnSaveTrigger;

    // Director Mode
    private GhostMode currentGhostMode = GhostMode.CHASE;
    private long lastModeSwitchTime = 0;
    private int[][] ghostDensityMap;

    // Constant variable for the app
    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    // UI element
    private int score = 0;
    private int lives = 3;
    public Image livesImage;
    private static Image cachedLives;

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
        ghostDensityMap = new int[rowCount][columnCount];

        if (cachedLives == null) {
            cachedLives = getImageResource("lives.png");
        }
        livesImage = cachedLives;

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
                    if (btnNewMap.contains(click))
                        generateNewMap();
                    else if (btnPlay.contains(click))
                        startGame();
                    else if (btnAlgo.contains(click))
                        startVisualization();
                    else if (btnRegMap.contains(click)) {
                        handleRegisteredMapAction();
                    }
                } else if (currentState == GameState.REGISTERED_MAPS) {
                    Point click = e.getPoint();
                    if (btnBackAction.contains(click)) {
                        currentState = GameState.MENU;
                    } else if (btnSaveTrigger.contains(click)) {
                        if (originalMap == null) {
                            JOptionPane.showMessageDialog(PacMan.this, "No map generated yet! Start a new map first.");
                        } else {
                            saveNameInput = "";
                            currentState = GameState.SAVING_MAP;
                        }
                    } else {
                        for (int i = 0; i < mapButtons.size(); i++) {
                            if (mapButtons.get(i).contains(click)) {
                                String selectedMap = availableMaps[i];
                                try {
                                    originalMap = MapStorage.loadMap(selectedMap, rowCount, columnCount);
                                    workingMap = deepCopy(originalMap);
                                    loadMapFromGrid(originalMap);
                                    vizPath = null;
                                    vizStep = 0;
                                    currentState = GameState.MENU;
                                    repaint();
                                } catch (IOException ex) {
                                    JOptionPane.showMessageDialog(PacMan.this, "Error loading map: " + ex.getMessage());
                                }
                                break;
                            }
                        }
                    }
                } else if (currentState == GameState.SAVING_MAP) {
                    Point click = e.getPoint();
                    if (btnBackAction.contains(click)) {
                        currentState = GameState.REGISTERED_MAPS;
                        refreshMapButtons();
                    } else if (btnSaveTrigger.contains(click)) {
                        confirmSaveMap();
                    }
                } else if (currentState == GameState.GAME_OVER) {
                    Point click = e.getPoint();
                    if (btnRestart.contains(click)) {
                        startGame();
                    } else if (btnGameOverMenu.contains(click)) {
                        currentState = GameState.MENU;
                    }
                } else if (currentState == GameState.VICTORY) {
                    Point click = e.getPoint();
                    if (btnRestart.contains(click)) {
                        startGame();
                    } else if (btnGameOverMenu.contains(click)) {
                        currentState = GameState.MENU;
                    }
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

    public void generateNewMap() {
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
        btnRegMap = new Rectangle(centerX, 420, btnWidth, btnHeight);

        btnBackAction = new Rectangle(centerX, 550, btnWidth, btnHeight);
        btnSaveTrigger = new Rectangle(centerX, 150, btnWidth, btnHeight);

        btnRestart = new Rectangle(centerX, 300, btnWidth, btnHeight);
        btnGameOverMenu = new Rectangle(centerX, 370, btnWidth, btnHeight);
    }

    private void refreshMapButtons() {
        availableMaps = MapStorage.getRegisteredMapNames();
        mapButtons.clear();
        int startY = 220;
        for (int i = 0; i < availableMaps.length && i < 6; i++) {
            mapButtons.add(new Rectangle(centerX, startY + (i * 70), btnWidth, btnHeight));
        }
    }

    public void startGame() {
        score = 0;
        lives = 3;
        gameStarted = false;
        this.workingMap = deepCopy(originalMap);

        // 3. Create the ACTUAL objects (this links the Ghosts to the current Pacman)
        loadMapFromGrid(this.workingMap);
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
                } else if (tile == ' ')
                    foods.add(new Food(x, y, tileSize, false));
                else if (tile == 'M')
                    pacman = new PacmanPlayer(x, y, tileSize);
                else if (isGhost(tile)) {
                    System.out.println("Ghost " + tile + " spawned at: " + x + "," + y);
                    ghosts.add(new Ghost(tile, x, y, tileSize));
                }

            }
        }

    }

    private boolean isGhost(char tile) {
        return "BRYP".indexOf(tile) != -1;
    }

    private void updateGame() {
        if (pacman == null) {
            System.out.println("DEBUG: Ghosts are moving but Pacman is NULL!");
        }

        if (currentState == GameState.PLAYING) {
            // Update Director Logic (Mode Switch every 10 seconds)
            if (gameStarted) {
                long now = System.currentTimeMillis();
                if (now - lastModeSwitchTime > 10000) {
                    currentGhostMode = (currentGhostMode == GhostMode.CHASE) ? GhostMode.SCATTER : GhostMode.CHASE;
                    lastModeSwitchTime = now;
                    // Force ghosts to reconsider paths on mode switch
                }
            }

            // Update Density Map
            for (int r = 0; r < rowCount; r++) {
                for (int c = 0; c < columnCount; c++) {
                    ghostDensityMap[r][c] = 0;
                }
            }
            for (Ghost g : ghosts) {
                if (!g.isWaitingToRespawn()) {
                    int r = g.getRow();
                    int c = g.getCol();
                    if (r >= 0 && r < rowCount && c >= 0 && c < columnCount) {
                        ghostDensityMap[r][c]++;
                    }
                }
            }

            pacman.move(walls);
            if (!gameStarted && (pacman.velocityX != 0 || pacman.velocityY != 0)) {
                gameStarted = true;
            }

            for (Ghost ghost : ghosts) {
                if (ghost.isWaitingToRespawn())
                    continue;

                if (ghost.isAtIntersection(workingMap)
                        || (ghost.isAtCenterOfTile() && ghost.velocityX == 0 && ghost.velocityY == 0)) {
                    int targetR = 0;
                    int targetC = 0;

                    if (currentGhostMode == GhostMode.SCATTER && !ghost.isScared()) {
                        switch (ghost.getType()) {
                            case 'R' -> {
                                targetR = -3;
                                targetC = columnCount - 1;
                            }
                            case 'P' -> {
                                targetR = -3;
                                targetC = 0;
                            }
                            case 'B' -> {
                                targetR = rowCount + 3;
                                targetC = columnCount - 1;
                            }
                            case 'Y' -> {
                                targetR = rowCount + 3;
                                targetC = 0;
                            }
                        }
                    } else if (ghost.isScared()) {
                        targetR = (int) (Math.random() * rowCount);
                        targetC = (int) (Math.random() * columnCount);
                    } else {
                        // CHASE Mode
                        switch (ghost.getType()) {
                            case 'R' -> {
                                targetR = pacman.getRow();
                                targetC = pacman.getCol();
                            }
                            case 'P' -> {
                                targetR = pacman.getRow();
                                targetC = pacman.getCol();
                                switch (pacman.getDirection()) {
                                    case 'U' -> targetR -= 4;
                                    case 'D' -> targetR += 4;
                                    case 'L' -> targetC -= 4;
                                    case 'R' -> targetC += 4;
                                }
                            }
                            case 'B' -> {
                                int p2R = pacman.getRow();
                                int p2C = pacman.getCol();
                                switch (pacman.getDirection()) {
                                    case 'U' -> p2R -= 2;
                                    case 'D' -> p2R += 2;
                                    case 'L' -> p2C -= 2;
                                    case 'R' -> p2C += 2;
                                }
                                Ghost red = null;
                                for (Ghost g : ghosts)
                                    if (g.getType() == 'R')
                                        red = g;
                                if (red != null) {
                                    targetR = p2R + (p2R - red.getRow());
                                    targetC = p2C + (p2C - red.getCol());
                                } else {
                                    targetR = p2R;
                                    targetC = p2C;
                                }
                            }
                            case 'Y' -> {
                                double dist = Math.sqrt(Math.pow(ghost.getRow() - pacman.getRow(), 2)
                                        + Math.pow(ghost.getCol() - pacman.getCol(), 2));
                                if (dist > 8) {
                                    targetR = pacman.getRow();
                                    targetC = pacman.getCol();
                                } else {
                                    targetR = rowCount + 3;
                                    targetC = 0;
                                }
                            }
                        }
                    }
                    ghost.calculatePath(workingMap, targetR, targetC, ghostDensityMap);
                }

                if (gameStarted) {
                    ghost.move(walls);
                }

                if (pacman.getBounds().intersects(ghost.getBounds())) {
                    if (ghost.isScared()) {
                        score += 200;
                        ghost.eat();
                    } else if (!ghost.isWaitingToRespawn()) {
                        handlePacmanDeath();
                        return;
                    }
                }
            }

            if (System.currentTimeMillis() > frightenedEndTime && frightenedEndTime != 0) {
                for (Ghost ghost : ghosts) {
                    ghost.setScared(false);
                }
                frightenedEndTime = 0;
            }

            checkFoodCollision();

            if (foods.isEmpty()) {
                currentState = GameState.VICTORY;
                gameStarted = false;
            }
        } else if (currentState == GameState.ALGORITHM_VISUALIZATION) {

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
                if (food.isPower()) {
                    scareGhosts();
                }
                iterator.remove();
                score += 1;
                break;
            }
        }
    }

    public void startVisualization() {
        if (this.originalMap == null)
            return;

        // Find Pacman's position in originalMap
        int startR = 16, startC = 9; // Default
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                if (originalMap[r][c] == 'M') {
                    startR = r;
                    startC = c;
                    break;
                }
            }
        }

        if (this.vizPath == null) {
            MapGenerator gen = new MapGenerator(rowCount, columnCount);
            gen.setGrid(this.originalMap);
            gen.verifyConnectivity(startR, startC); // Use standard Pacman start pos
            this.vizPath = gen.getVisitOrder();
        }

        this.currentState = GameState.ALGORITHM_VISUALIZATION;
        this.workingMap = deepCopy(originalMap);
        loadMapFromGrid(this.originalMap);
        foods.removeIf(Food::isPower);
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

                if (tile == 'X')
                    walls.add(new Wall(x, y, tileSize));
                else if (tile == ' ')
                    foods.add(new Food(x, y, tileSize, false));
                else if (tile == 'O')
                    foods.add(new Food(x, y, tileSize, true));
                else if (tile == 'M')
                    pacman = new PacmanPlayer(x, y, tileSize);
                else if (isGhost(tile))
                    ghosts.add(new Ghost(tile, x, y, tileSize));
            }
        }
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setState(GameState state) {
        this.currentState = state;
    }

    private Image getImageResource(String name) {
        java.net.URL url = getClass().getResource("/com/mouaad/pacman/" + name);
        return (url != null) ? new ImageIcon(url).getImage() : null;
    }

    public void handleRegisteredMapAction() {
        refreshMapButtons();
        currentState = GameState.REGISTERED_MAPS;
    }

    public void confirmSaveMap() {
        if (saveNameInput.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name!");
            return;
        }
        try {
            MapStorage.saveMap(saveNameInput.trim(), originalMap);
            currentState = GameState.REGISTERED_MAPS;
            refreshMapButtons();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving map: " + ex.getMessage());
        }
    }

    public void handleTyping(char c) {
        if (saveNameInput.length() < 20) {
            saveNameInput += c;
            repaint();
        }
    }

    public void handleBackspace() {
        if (!saveNameInput.isEmpty()) {
            saveNameInput = saveNameInput.substring(0, saveNameInput.length() - 1);
            repaint();
        }
    }

    private void handlePacmanDeath() {
        lives--;
        gameStarted = false;
        if (lives > 0) {
            resetEntities();
        } else {
            currentState = GameState.GAME_OVER;
        }
    }

    private void resetEntities() {
        if (pacman != null)
            pacman.reset();
        for (Ghost ghost : ghosts) {
            ghost.reset();
            ghost.setScared(false);
        }
    }

    private void scareGhosts() {
        frightenedEndTime = System.currentTimeMillis() + 10000; // 10 seconds
        for (Ghost ghost : ghosts) {
            ghost.setScared(true);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        double frightenedTimeRemaining = Math.max(0, (frightenedEndTime - System.currentTimeMillis()) / 1000.0);

        renderer.render(g, currentState, pacman, walls, ghosts, foods,
                btnNewMap, btnPlay, btnRegMap, btnAlgo, btnRestart, btnGameOverMenu,
                boardWidth, boardHeight, this, vizPath, vizStep,
                score, lives, livesImage, frightenedTimeRemaining,
                availableMaps, saveNameInput, mapButtons,
                btnBackAction, btnSaveTrigger);
    }
}