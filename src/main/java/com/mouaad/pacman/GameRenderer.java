package com.mouaad.pacman;

import java.awt.*;
import java.util.Set;
import java.util.List;

public class GameRenderer {

    public void render(Graphics g, GameState state, PacmanPlayer pacman, 
                       Set<Wall> walls, Set<Ghost> ghosts, Set<Food> foods, 
                       Rectangle btnNew, Rectangle btnReg, Rectangle btnAlgo, 
                       int width, int height, Component parent, List<MapGenerator.Pair> vizPath, int vizStep) {
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (state == GameState.MENU) {
            drawMenu(g2, btnNew, btnReg, btnAlgo, width, parent);
        } else {
            if(pacman != null) pacman.draw(g2);
            for(Wall wall : walls) wall.draw(g2);
            for(Ghost ghost : ghosts) ghost.draw(g2);
            for(Food food : foods) food.draw(g2);
        }

        if (state == GameState.ALGORITHM_VISUALIZATION) {
            for(Wall wall : walls) wall.draw(g2);
            drawFloodFill(g2, vizPath, vizStep, 32);
        }
    }

    private void drawMenu(Graphics2D g2, Rectangle b1, Rectangle b2, 
                          Rectangle b3, int width,Component parent) {
        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Monospaced", Font.BOLD, 40));
        g2.drawString("PAC-MAN", (width / 2) - 85, 120);

        drawButton(g2, b1, "START NEW MAP", parent);
        drawButton(g2, b2, "REGISTERED MAP", parent);
        drawButton(g2, b3, "VISUALIZE ALGO", parent);
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
    if (path == null) return;
    
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
}