package com.mouaad.pacman;

import java.awt.*;

public class Food extends Block {
        // I'm gonna add here like a powerFood img

        public Food(int x, int y, int tileSize) {

            super(null, x + (tileSize/2 - 2), y + (tileSize/2 - 2), tileSize);
            // We manually set width/height to 4 for the pellet itself
            this.width = 4;
            this.height = 4;
        }

        @Override
        public void draw(Graphics g) {
            g.setColor(Color.white);
            g.fillRect(x, y, width, height);
        }
}

