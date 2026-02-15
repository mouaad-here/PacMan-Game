package com.mouaad.pacman;

import java.util.*;

public class MapGenerator {
    private int rows, columns;
    private char[][] grid;
    record Pair(int r, int c) {};

    MapGenerator(int rows, int columns){
        this.rows = rows;
        this.columns = columns;
        this.grid = new char[rows][columns];
    }

    public char[][] generate() {

        // fill all the grid with walls
        for(char[] row : grid) Arrays.fill(row,'X');

        // Carve
        carve(3,1);

        // remove dead ends
        removeDeadEnds();

        // Mirror and Validation
        applyMirrorAndRule();


        // fixed Ghost House
        grid[8][9] = 'R';
        grid[9][8] = 'B';
        grid[9][9] = 'P';
        grid[9][10] = 'Y';
        grid[7][9] = ' ';

        // Pacman start position
        grid[16][9] = 'M';

        return grid;
    }

    private void carve(int r, int c) {
        // Start Path
        grid[r][c] = ' ';

        // Randomly shuffle directions : Up, Down, Right, Lift
        Integer[] dirs = {0, 1, 2, 3};
        Collections.shuffle(Arrays.asList(dirs));

        for(int dir : dirs) {
            // we move 2 steps in any direction
            int dr = (dir == 0) ? -2 : (dir == 1) ? 2 : 0;
            int dc = (dir == 2) ? -2 : (dir == 3) ? 2 : 0;

            int nr = r + dr;
            int nc = c + dc;

            // Check we only need to fill the left half of the array
            if(nr > 2 && nr < rows - 1 && nc > 0 && nc <= columns / 2) {
                if(grid[nr][nc] == 'X') {
                    grid[r + dr/2][c + dc/2] = ' ';
                    carve(nr,nc);
                }
            }
        }
    }

    private void applyMirrorAndRule(){
        for(int r = 2; r < rows; ++r){
            for(int c = 0; c < columns / 2; ++c){
                if (grid[r][c] == 'X' || grid[r][c] == ' ') {
                    grid[r][18 - c] = grid[r][c];
                }
            }

        }

        for (int r = 2; r < rows; ++r){
            if (grid[r][8] == ' ' && grid[r][10] == ' '){
                grid[r][9] = ' ';
            }
        }
    }
    private void removeDeadEnds() {

        Random rand = new Random();
        // Detect the dead Ends that mean positions that have only 1 path,
        // and then choose random wall neighbor position and assign to it ' '
        // and that create a cyclic map
        for(int r = 2; r < rows - 1; ++r){
            for(int c = 1; c < columns / 2; ++c){

                if(grid[r][c] == ' ') {
                    int pathCount = 0;
                    ArrayList<Pair> wallNeighbors = new ArrayList<>();
                    int[][] dirc = {{r-1, c}, {r+1, c}, {r, c-1}, {r, c+1}};
                    for (int[] pos : dirc) {
                        ;
                        int nr = pos[0];
                        int nc = pos[1];

                        if (isValid(nr, nc)) {
                            if (grid[nr][nc] == ' ') {
                                pathCount++;
                            } else if (grid[nr][nc] == 'X') {
                                wallNeighbors.add(new Pair(nr, nc));
                            }
                        }

                    }

                    if (pathCount == 1 && !wallNeighbors.isEmpty()) {
                        Pair toBreak = wallNeighbors.get(rand.nextInt(wallNeighbors.size()));
                        if (toBreak.r() > 0 && toBreak.r() < rows - 1 && toBreak.c() > 0) {
                            grid[toBreak.r()][toBreak.c()] = ' ';
                        }
                    }
                }
            }
        }

    }
    private boolean isValid(int nr, int nc){
        return (nr > 2 && nr < rows - 1) && (nc > 0 && nc <= columns / 2);
    }
}
