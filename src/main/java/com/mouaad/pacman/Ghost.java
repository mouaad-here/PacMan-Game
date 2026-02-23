package com.mouaad.pacman;

import javax.swing.ImageIcon;
import java.awt.*;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;

import java.util.Map;
import java.util.Queue;

//import java.util.Queue;

public class Ghost extends Entity {
    private static Map<Character, Image> cachedNormalImages = new HashMap<>();
    private static Image cachedScaredImage;

    private char type;
    private Image normalImage, scaredImage;
    private boolean isScared = false;
    private long respawnReadyTime = 0;
    private char lastDirection = ' ';

    public Ghost(char type, int x, int y, int tileSize) {
        super(null, x, y, tileSize);
        this.normalImage = loadGhostImage(type);
        if (cachedScaredImage == null) {
            java.net.URL scaredUrl = getClass().getResource("/com/mouaad/pacman/scaredGhost.png");
            cachedScaredImage = (scaredUrl != null) ? new ImageIcon(scaredUrl).getImage() : null;
        }
        this.scaredImage = cachedScaredImage;
        this.image = normalImage;
        this.tileSize = tileSize;
        this.type = type;
    }

    private Image loadGhostImage(char type) {
        if (cachedNormalImages.containsKey(type)) {
            return cachedNormalImages.get(type);
        }

        String path = switch (type) {
            case 'R' -> "redGhost.png";
            case 'Y' -> "orangeGhost.png";
            case 'P' -> "pinkGhost.png";
            case 'B' -> "blueGhost.png";
            default -> "blueGhost.png";
        };
        java.net.URL url = getClass().getResource("/com/mouaad/pacman/" + path);
        Image img = (url != null) ? new ImageIcon(url).getImage() : null;
        cachedNormalImages.put(type, img);
        return img;
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

    public boolean isAtIntersection(char[][] grid) {
        if (!isAtCenterOfTile())
            return false;

        int row = getRow();
        int col = getCol();
        int openings = 0;

        if (isValid(grid, row - 1, col))
            openings++;
        if (isValid(grid, row + 1, col))
            openings++;
        if (isValid(grid, row, col - 1))
            openings++;
        if (isValid(grid, row, col + 1))
            openings++;

        return openings > 2;
    }

    // Dijkstra (Weighted Pathfinding)
    public void calculatePath(char[][] grid, int targetR, int targetC, int[][] densityMap) {
        int startR = getRow();
        int startC = getCol();

        class Node implements Comparable<Node> {
            Point p;
            int dist;

            Node(Point p, int dist) {
                this.p = p;
                this.dist = dist;
            }

            @Override
            public int compareTo(Node o) {
                return Integer.compare(this.dist, o.dist);
            }
        }

        java.util.PriorityQueue<Node> pq = new java.util.PriorityQueue<>();
        Map<Point, Integer> minDistance = new HashMap<>();
        Map<Point, Point> parent = new HashMap<>();

        Point start = new Point(startR, startC);
        pq.add(new Node(start, 0));
        minDistance.put(start, 0);
        parent.put(start, null);

        int[][] dirs = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };

        while (!pq.isEmpty()) {
            Node current = pq.poll();

            if (current.p.x == targetR && current.p.y == targetC) {
                break;
            }

            if (current.dist > minDistance.getOrDefault(current.p, Integer.MAX_VALUE))
                continue;

            for (int[] d : dirs) {
                int nr = current.p.x + d[0];
                int nc = current.p.y + d[1];

                // Prevent 180 degree turn
                if (current.p.equals(start)) {
                    char dir = getDir(d[0], d[1]);
                    if (isOpposite(dir, lastDirection))
                        continue;
                }

                if (isValid(grid, nr, nc)) {
                    int weight = 1;
                    if (nr >= 0 && nr < densityMap.length && nc >= 0 && nc < densityMap[0].length) {
                        weight += densityMap[nr][nc] * 10;
                    }

                    int newDist = current.dist + weight;
                    Point next = new Point(nr, nc);

                    if (newDist < minDistance.getOrDefault(next, Integer.MAX_VALUE)) {
                        minDistance.put(next, newDist);
                        parent.put(next, current.p);
                        pq.add(new Node(next, newDist));
                    }
                }
            }
        }

        // Find closest point if target is outside or unreachable
        Point target = findClosest(parent, targetR, targetC);
        if (target == null)
            return;

        Point step = target;
        while (parent.get(step) != null && !parent.get(step).equals(start)) {
            step = parent.get(step);
        }

        int dr = step.x - startR;
        int dc = step.y - startC;
        lastDirection = currentDirection;
        setDirection(getDir(dr, dc));
    }

    private Point findClosest(Map<Point, Point> parent, int tr, int tc) {
        if (parent.containsKey(new Point(tr, tc)))
            return new Point(tr, tc);

        Point closest = null;
        double minDist = Double.MAX_VALUE;
        for (Point p : parent.keySet()) {
            double d = Math.pow(p.x - tr, 2) + Math.pow(p.y - tc, 2);
            if (d < minDist) {
                minDist = d;
                closest = p;
            }
        }
        return closest;
    }

    private char getDir(int dr, int dc) {
        if (dr == -1)
            return 'U';
        if (dr == 1)
            return 'D';
        if (dc == -1)
            return 'L';
        if (dc == 1)
            return 'R';
        return ' ';
    }

    private boolean isOpposite(char d1, char d2) {
        return (d1 == 'U' && d2 == 'D') || (d1 == 'D' && d2 == 'U') ||
                (d1 == 'L' && d2 == 'R') || (d1 == 'R' && d2 == 'L');
    }

    private boolean isValid(char[][] grid, int r, int c) {
        return r >= 2 && r < grid.length &&
                c >= 0 && c < grid[0].length &&
                grid[r][c] != 'X';
    }

    @Override
    public void move(HashSet<Wall> walls) {
        if (System.currentTimeMillis() < respawnReadyTime) {
            return;
        }

        if (canMoveInDirection(currentDirection, walls)) {
            this.x += velocityX;
            this.y += velocityY;
        } else {
            stopAndSnap();

        }
    }

    public void setScared(boolean scared) {
        this.isScared = scared;
        this.image = scared ? scaredImage : normalImage;
    }

    public boolean isScared() {
        return isScared;
    }

    public void eat() {
        this.x = startX;
        this.y = startY;
        this.isScared = false;
        this.image = normalImage;
        this.respawnReadyTime = System.currentTimeMillis() + 2000;
        stopAndSnap();
    }

    public boolean isWaitingToRespawn() {
        return System.currentTimeMillis() < respawnReadyTime;
    }
}
