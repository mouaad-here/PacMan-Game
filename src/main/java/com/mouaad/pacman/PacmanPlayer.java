package com.mouaad.pacman;

import javax.swing.ImageIcon;
import java.awt.Image;

public class PacmanPlayer extends Entity {
    private Image upImage, downImage, leftImage, rightImage;
    public PacmanPlayer(int x, int y, int tileSize) {
        super(new ImageIcon(PacmanPlayer.class.getResource("pacmanRight.png")).getImage(),x, y, tileSize);
        loadImage();
    }

    private void loadImage() {
        upImage = new ImageIcon(getClass().getResource("pacmanUp.png")).getImage();
        downImage= new ImageIcon(getClass().getResource("pacmanDown.png")).getImage();
        leftImage = new ImageIcon(getClass().getResource("pacmanLeft.png")).getImage();
        rightImage = new ImageIcon(getClass().getResource("pacmanRight.png")).getImage();
    }
    // update the pendingDirection to prevents pacman from stopping at turns
    public void updateInput(char input) {
        this.pendingDirection = input;
    }




    @Override
    protected void setDirection(char dir) {
        super.setDirection(dir);
        
        if (dir == 'U') this.image = upImage;
        else if (dir == 'D') this.image = downImage;
        else if (dir == 'L') this.image = leftImage;
        else if (dir == 'R') this.image = rightImage;
    }

}
