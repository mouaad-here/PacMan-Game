package com.mouaad.pacman;

import java.awt.Image;
import java.awt.Rectangle;
import java.util.Objects;
import java.awt.Graphics;

public class Block {
    protected int x, y, width, height;
    protected Image image;
    protected int tileSize;
    protected Rectangle bounds = new Rectangle();


    // Constructure
    Block(Image image, int x, int y, int tileSize){
        this.x = x;
        this.y = y;
        this.tileSize = tileSize;
        this.image = image;
        this.width = tileSize;
        this.height = tileSize;
    }

    public void draw(Graphics g) {
        if(image != null) {
            g.drawImage(image, x, y, width, height, null);
        }
    }



    public Rectangle getBounds() {
        bounds.setBounds(x, y, width, height);
        return bounds;
    }

    // to detect if we have an object already in a position
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Block)) return false;
        Block block = (Block) o;
        return x == block.x && y == block.y;
    }

    @Override
    public int hashCode(){
        return Objects.hash(x, y);
    }


}
