package com.mouaad.pacman;

import java.util.*;

public class MapGenerator {
    private int rows, columns;
    private char[][] grid;

    MapGenerator(int rows, int columns){
        this.rows = rows;
        this.columns = columns;
        this.grid = new char[rows][columns];
    }

    public char[][] generate() {
        // fill all the grid with walls
        for(char[] row : grid) Arrays.fill(row,'X');
        // fixed Ghost House


        // Carve
        carve(1,1);

        // Mirror and Validation
        applyMirrorAndRule();

        grid[8][9] = 'R';
        grid[9][8] = 'B';
        grid[9][9] = 'P';
        grid[9][10] = 'Y';


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
            if(nr > 0 && nr < rows - 1 && nc > 0 && nc <= 9) {
                if(grid[nr][nc] == 'X') {
                    grid[r + dr/2][c + dc/2] = ' ';
                    carve(nr,nc);
                }
            }
        }
    }

    private void applyMirrorAndRule(){
        for(int r = 0; r < rows; ++r){
            for(int c = 0; c < 9; ++c){
                if (grid[r][c] == 'X' || grid[r][c] == ' ') {
                    grid[r][18 - c] = grid[r][c];
                }
            }

        }
        grid[7][9] = ' ';
        grid[16][9] = 'M';
        for (int r = 0; r < rows; ++r){
            if (grid[r][8] == ' ' && grid[r][10] == ' '){
                grid[r][9] = ' ';
            }
        }
    }
}
