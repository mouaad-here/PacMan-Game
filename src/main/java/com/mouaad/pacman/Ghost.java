package com.mouaad.pacman;

import javax.swing.ImageIcon;
import java.awt.*;

//import java.util.HashSet;

public class Ghost  extends Entity{

    public Ghost(char type, int x, int y, int tileSize){
        super(null, x, y, tileSize);
        this.image = loadGhostImage(type);
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

    // here the Mvm logic for the ghosts.
}
