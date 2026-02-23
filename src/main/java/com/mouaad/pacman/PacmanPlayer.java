package com.mouaad.pacman;

import javax.swing.ImageIcon;
import java.awt.Image;

public class PacmanPlayer extends Entity {
    private Image upImage, downImage, leftImage, rightImage;
    private static Image cachedUp, cachedDown, cachedLeft, cachedRight;

    public PacmanPlayer(int x, int y, int tileSize) {
        super(null, x, y, tileSize);
        loadImage();
        if (rightImage != null)
            this.image = rightImage;
    }

    private void loadImage() {
        if (cachedUp == null)
            cachedUp = getImageResource("pacmanUp.png");
        if (cachedDown == null)
            cachedDown = getImageResource("pacmanDown.png");
        if (cachedLeft == null)
            cachedLeft = getImageResource("pacmanLeft.png");
        if (cachedRight == null)
            cachedRight = getImageResource("pacmanRight.png");

        upImage = cachedUp;
        downImage = cachedDown;
        leftImage = cachedLeft;
        rightImage = cachedRight;
    }

    private java.awt.Image getImageResource(String name) {
        java.net.URL url = getClass().getResource("/com/mouaad/pacman/" + name);
        return (url != null) ? new ImageIcon(url).getImage() : null;
    }

    // update the pendingDirection to prevents pacman from stopping at turns
    public void updateInput(char input) {
        this.pendingDirection = input;
    }

    // Helpers

    public int getRow() {
        return y / tileSize;
    }

    public int getCol() {
        return x / tileSize;
    }

    @Override
    protected void setDirection(char dir) {
        super.setDirection(dir);

        if (dir == 'U')
            this.image = upImage;
        else if (dir == 'D')
            this.image = downImage;
        else if (dir == 'L')
            this.image = leftImage;
        else if (dir == 'R')
            this.image = rightImage;
    }

}
