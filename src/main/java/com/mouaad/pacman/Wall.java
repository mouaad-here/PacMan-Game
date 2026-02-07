package com.mouaad.pacman;

import javax.swing.ImageIcon;

public class Wall extends Block {
    public Wall(int x, int y, int tileSize) {
        super(new ImageIcon(Wall.class.getResource("wall.png")).getImage(), x, y, tileSize);
    }
}
