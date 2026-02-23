package com.mouaad.pacman;

import javax.swing.ImageIcon;

public class Wall extends Block {
    public Wall(int x, int y, int tileSize) {
        super(loadImage(), x, y, tileSize);
    }

    private static java.awt.Image cachedImage;

    private static java.awt.Image loadImage() {
        if (cachedImage == null) {
            java.net.URL url = Wall.class.getResource("/com/mouaad/pacman/wall.png");
            cachedImage = (url != null) ? new ImageIcon(url).getImage() : null;
        }
        return cachedImage;
    }
}
