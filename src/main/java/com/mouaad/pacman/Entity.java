package com.mouaad.pacman;

import java.awt.Image;
import java.util.HashSet;

public class Entity extends Block {

    public int velocityX, velocityY;
    public int speed = 4;

    // Direction buffering
    public char currentDirection = 'R';
    public char pendingDirection = 'R';

    public Entity(Image image, int x, int y, int size) {
        super(image, x, y, size);
    }

    public void move(HashSet<Wall> walls){

        if(canMoveInDirection(pendingDirection, walls)){
            setDirection(pendingDirection);
        }

        if(canMoveInDirection(currentDirection, walls)){
            this.x += velocityX;
            this.y += velocityY;
        }else {
            stopAndSnap();
                // this.velocityX = 0;
                // this.velocityY = 0;
        }
    }

    protected void setDirection(char dir) {
        this.currentDirection = dir;
        this.velocityX = 0;
        this.velocityY = 0;

        if(dir == 'U') this.velocityY = -speed;
        else if (dir == 'D') this.velocityY = speed;
        else if (dir == 'L') this.velocityX = -speed;
        else if (dir == 'R') this.velocityX = speed;
    }
    public boolean canMoveInDirection(char dir, HashSet<Wall> walls) {
        int nextX = this.x;
        int nextY = this.y;

        if (dir == 'U') nextY -= speed;
        else if (dir == 'D') nextY += speed;
        else if (dir == 'L') nextX -= speed;
        else if (dir == 'R') nextX += speed;

        return !isCollidingWithWalls(nextX, nextY, walls);
    }
    private boolean isCollidingWithWalls(int nextX, int nextY, HashSet<Wall> walls){
        // I'm gonna add here padding to improve the check
        return isWallAt(nextX, nextY, walls) || isWallAt(nextX + width -1, nextY, walls) ||
                isWallAt(nextX, nextY + height - 1, walls) || isWallAt(nextX + width -1, nextY + height -1,walls);

    }

    private boolean isWallAt(int px, int py, HashSet<Wall> walls) {
        int gridX = (px / tileSize) * tileSize;
        int gridY = (py / tileSize) * tileSize;
        return walls.contains(new Block(null, gridX, gridY, tileSize));
    }


    private void stopAndSnap() {
        this.velocityX = 0;
        this.velocityY = 0;
        this.x = Math.round((float) x / tileSize) * tileSize;
        this.y = Math.round((float) y / tileSize) * tileSize;
    }
}
