package com.mouaad.pacman;

import java.awt.Image;
import java.util.Objects;

public class Block {
    int x, y, width, height;
    Image image;

    // Constructure
    Block(Image image, int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.image = image;
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
