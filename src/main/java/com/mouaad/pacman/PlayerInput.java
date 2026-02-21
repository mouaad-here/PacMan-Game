package com.mouaad.pacman;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class PlayerInput extends KeyAdapter{
    private PacMan game;


    PlayerInput(PacMan game) {
        this.game = game;
    }

@Override
public void keyPressed(KeyEvent e) {

    int code = e.getKeyCode();

    // ===== MENU INPUT =====
    if (game.getCurrentState() == GameState.MENU) {

        switch (code) {
            case KeyEvent.VK_1 -> game.generateNewMap();
            case KeyEvent.VK_2 -> game.startGame();
            case KeyEvent.VK_3 -> game.startVisualization();
            // case KeyEvent.VK_4 -> game.loadRegisteredMap(); // optional
        }

        return;
    }

    // ===== PLAYING INPUT =====
    if (game.getCurrentState() == GameState.PLAYING) {

        if (game.pacman == null) return;

        switch (code) {
            case KeyEvent.VK_UP -> game.pacman.updateInput('U');
            case KeyEvent.VK_DOWN -> game.pacman.updateInput('D');
            case KeyEvent.VK_LEFT -> game.pacman.updateInput('L');
            case KeyEvent.VK_RIGHT -> game.pacman.updateInput('R');
        }
    }

    // ===== ESC = RETURN TO MENU =====
    if (code == KeyEvent.VK_ESCAPE) {
        game.setState(GameState.MENU);
    }
}

}
