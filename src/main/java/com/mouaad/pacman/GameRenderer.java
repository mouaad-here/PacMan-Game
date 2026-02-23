package com.mouaad.pacman;

import java.awt.*;
import java.util.Set;
import java.util.List;

public class GameRenderer {

    public void render(Graphics g, GameState state, PacmanPlayer pacman,
            Set<Wall> walls, Set<Ghost> ghosts, Set<Food> foods,
            Rectangle btnNew, Rectangle btnPlay, Rectangle btnReg, Rectangle btnAlgo,
            Rectangle btnRestart, Rectangle btnGameOverMenu,
            int width, int height, Component parent, List<MapGenerator.Pair> vizPath, int vizStep,
            int score, int lives, Image livesImage, double frightenedTimeRemaining,
            String[] mapNames, String saveNameInput, List<Rectangle> mapButtons,
            Rectangle btnBack, Rectangle btnSaveAction) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (state) {

            case MENU -> {
                drawMenu(g2, btnNew, btnPlay, btnReg, btnAlgo, width, parent);
            }

            case PLAYING -> {
                for (Wall wall : walls)
                    wall.draw(g2);
                for (Food food : foods)
                    food.draw(g2);
                for (Ghost ghost : ghosts)
                    ghost.draw(g2);
                if (pacman != null)
                    pacman.draw(g2);

                drawUI(g2, score, lives, width, livesImage, frightenedTimeRemaining);
            }

            case ALGORITHM_VISUALIZATION -> {
                for (Wall wall : walls)
                    wall.draw(g2);
                for (Food food : foods)
                    food.draw(g2); // optional
                drawFloodFill(g2, vizPath, vizStep, 32);
            }

            case GAME_OVER -> {
                drawGameOver(g2, score, btnRestart, btnGameOverMenu, width, parent);
            }

            case REGISTERED_MAPS -> {
                drawRegisteredMaps(g2, mapNames, mapButtons, btnBack, btnSaveAction, width, height, parent);
            }

            case SAVING_MAP -> {
                drawSaveMap(g2, saveNameInput, btnSaveAction, btnBack, width, height, parent);
            }

            case VICTORY -> {
                drawVictory(g2, score, btnRestart, btnGameOverMenu, width, parent);
            }
        }
    }

    private void drawGameOver(Graphics2D g2, int score, Rectangle btnRestart, Rectangle btnMenu, int width,
            Component parent) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, width, 1000); // Overlay

        g2.setColor(Color.RED);
        g2.setFont(new Font("Monospaced", Font.BOLD, 40));
        g2.drawString("GAME OVER", (width / 2) - 100, 200);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 20));
        g2.drawString("FINAL SCORE: " + score, (width / 2) - 80, 260);

        drawButton(g2, btnRestart, "RESTART", parent);
        drawButton(g2, btnMenu, "RETURN TO MENU", parent);
    }

    private void drawVictory(Graphics2D g2, int score, Rectangle btnRestart, Rectangle btnMenu, int width,
            Component parent) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, width, 1000); // Overlay

        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Monospaced", Font.BOLD, 40));
        g2.drawString("VICTORY!", (width / 2) - 80, 200);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 20));
        g2.drawString("FINAL SCORE: " + score, (width / 2) - 80, 260);

        drawButton(g2, btnRestart, "PLAY AGAIN", parent);
        drawButton(g2, btnMenu, "RETURN TO MENU", parent);
    }

    private void drawMenu(Graphics2D g2, Rectangle b1, Rectangle b2,
            Rectangle b3, Rectangle b4, int width, Component parent) {

        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Monospaced", Font.BOLD, 40));
        g2.drawString("PAC-MAN", (width / 2) - 85, 120);

        drawButton(g2, b1, "START NEW MAP", parent);
        drawButton(g2, b2, "Play", parent);
        drawButton(g2, b3, "REGISTERED MAP", parent);
        drawButton(g2, b4, "VISUALIZE ALGO", parent);
    }

    private void drawButton(Graphics2D g2, Rectangle rect, String text, Component parent) {
        Point mousePos = parent.getMousePosition();
        boolean isHovered = (mousePos != null && rect.contains(mousePos));

        g2.setColor(isHovered ? Color.YELLOW : Color.DARK_GRAY);
        g2.fill(rect);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.draw(rect);

        g2.setFont(new Font("Monospaced", Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        int textX = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int textY = rect.y + (rect.height - fm.getHeight()) / 2 + fm.getAscent();
        g2.setColor(isHovered ? Color.BLACK : Color.WHITE);
        g2.drawString(text, textX, textY);
    }

    private void drawFloodFill(Graphics2D g2, List<MapGenerator.Pair> path, int step, int tileSize) {
        if (path == null)
            return;

        for (int i = 0; i < step; i++) {
            MapGenerator.Pair p = path.get(i);
            // Use a semi-transparent color so it looks like a "scan"
            g2.setColor(new Color(0, 255, 255, 100));
            g2.fillRect(p.c() * tileSize, p.r() * tileSize, tileSize, tileSize);

            // Optional: draw a border around the "current" leading edge
            if (i == step - 1) {
                g2.setColor(Color.WHITE);
                g2.drawRect(p.c() * tileSize, p.r() * tileSize, tileSize, tileSize);
            }
        }
    }

    public void drawUI(Graphics g, int score, int lives, int boardWidth, Image lifImage,
            double frightenedTimeRemaining) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        // Score
        g.drawString("SCORE: " + score, 16, 48);

        // Power Pellet Timer
        if (frightenedTimeRemaining > 0) {
            g.setColor(Color.YELLOW);
            String timerText = String.format("POWER: %.1f s", frightenedTimeRemaining);
            g.drawString(timerText, (boardWidth - g.getFontMetrics().stringWidth(timerText)) / 2, 48);
            g.setColor(Color.WHITE);
        }

        // Draw lives icons on the right (top-right)
        int startX = boardWidth - 88;

        // Draw slots 0, 1, 2. If lives = 2, we want to remove slot 0 (the leftmost).
        for (int i = 0; i < 3; ++i) {
            if (i >= (3 - lives)) {
                g.drawImage(lifImage, startX + (i * 25), 32, 20, 20, null);
            }
        }
    }

    private void drawRegisteredMaps(Graphics2D g2, String[] mapNames, List<Rectangle> mapButtons,
            Rectangle btnBack, Rectangle btnSave, int width, int height, Component parent) {
        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRect(0, 0, width, height);

        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Monospaced", Font.BOLD, 36));
        g2.drawString("REGISTERED MAPS", (width / 2) - 150, 60);

        drawButton(g2, btnSave, "SAVE CURRENT MAP", parent);

        for (int i = 0; i < mapButtons.size() && i < mapNames.length; i++) {
            drawButton(g2, mapButtons.get(i), mapNames[i], parent);
        }

        drawButton(g2, btnBack, "BACK TO MENU", parent);
    }

    private void drawSaveMap(Graphics2D g2, String saveNameInput, Rectangle btnSave, Rectangle btnBack,
            int width, int height, Component parent) {
        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRect(0, 0, width, height);

        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Monospaced", Font.BOLD, 36));
        g2.drawString("SAVE CURRENT MAP", (width / 2) - 160, 100);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 24));
        g2.drawString("ENTER NAME:", (width / 2) - 100, 180);

        // Input field simulation
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect((width / 2) - 150, 200, 300, 50);
        g2.setColor(Color.WHITE);
        g2.drawRect((width / 2) - 150, 200, 300, 50);
        g2.drawString(saveNameInput + "_", (width / 2) - 140, 235);

        drawButton(g2, btnSave, "CONFIRM SAVE", parent);
        drawButton(g2, btnBack, "CANCEL", parent);
    }
}