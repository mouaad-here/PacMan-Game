package com.mouaad.pacman;

import java.awt.*;
import javax.swing.ImageIcon;

public class Food extends Block {
    private boolean isPower;

    private static java.awt.Image cachedPowerImage;

    public Food(int x, int y, int tileSize, boolean isPower) {
        super(null, x + (tileSize / 2 - 2), y + (tileSize / 2 - 2), tileSize);
        this.isPower = isPower;
        if (isPower) {
            if (cachedPowerImage == null) {
                java.net.URL url = getClass().getResource("/com/mouaad/pacman/powerFood.png");
                if (url != null)
                    cachedPowerImage = new ImageIcon(url).getImage();
            }
            this.image = cachedPowerImage;
            this.width = tileSize / 2;
            this.height = tileSize / 2;
            this.x = x + (tileSize - this.width) / 2;
            this.y = y + (tileSize - this.height) / 2;
        } else {
            this.width = 4;
            this.height = 4;
        }
    }

    @Override
    public void draw(Graphics g) {
        if (isPower) {
            super.draw(g);
        } else {
            g.setColor(Color.white);
            g.fillRect(x, y, width, height);
        }
    }

    public boolean isPower() {
        return isPower;
    }
}
