package com.mouaad.pacman;

import javax.swing.ImageIcon;
import java.awt.*;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;

import java.util.Map;
import java.util.Queue;

//import java.util.HashSet;

public class Ghost extends Entity {

    private char type;

    public Ghost(char type, int x, int y, int tileSize) {
        super(null, x, y, tileSize);
        this.image = loadGhostImage(type);
        this.tileSize = tileSize;
        this.type = type;
    }

    private Image loadGhostImage(char type) {
        String path = switch (type) {
            case 'R' -> "redGhost.png";
            case 'Y' -> "orangeGhost.png";
            case 'P' -> "pinkGhost.png";
            case 'B' -> "blueGhost.png";
            default -> "blueGhost.png";
        };
        return new ImageIcon(getClass().getResource(path)).getImage();
    }

    // Helpers
    public int getRow() {
        return y / tileSize;
    }

    public int getCol() {
        return x / tileSize;
    }

    public boolean isAtCenterOfTile() {
        return x % tileSize == 0 && y % tileSize == 0;
    }

    public char getType() {
        return type;
    }

    // BFS path finding
    public void calculatePath(char[][] grid, int targetR, int targetC) {
        int startR = getRow();
        int startC = getCol();

        Map<Point, Point> parent = new HashMap<>();
        Queue<Point> queue = new LinkedList<>();

        Point start = new Point(startR, startC);
        queue.add(start);
        parent.put(start, null);

        int[][] dirs = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.x == targetR && current.y == targetC) {
                break;
            }

            for (int[] d : dirs) {
                int nr = current.x + d[0];
                int nc = current.y + d[1];

                if (isValid(grid, nr, nc)) {
                    Point next = new Point(nr, nc);
                    if (!parent.containsKey(next)) {
                        parent.put(next, current);
                        queue.add(next);
                    }
                }
            }
        }
        Point target = new Point(targetR, targetC);

        if (!parent.containsKey(target))
            return;

        Point step = target;

        while (parent.get(step) != null && !parent.get(step).equals(start)) {
            step = parent.get(step);
        }

        int dr = step.x - startR;
        int dc = step.y - startC;
        if (dr == -1)
            setDirection('U');
        else if (dr == 1)
            setDirection('D');
        else if (dc == -1)
            setDirection('L');
        else if (dc == 1)
            setDirection('R');
    }

    private boolean isValid(char[][] grid, int r, int c) {
        return r >= 2 && r < grid.length &&
                c >= 0 && c < grid[0].length &&
                grid[r][c] != 'X';
    }

    @Override
    public void move(HashSet<Wall> walls) {

        if (canMoveInDirection(currentDirection, walls)) {
            this.x += velocityX;
            this.y += velocityY;
        } else {
            stopAndSnap();

        }
    }
}
