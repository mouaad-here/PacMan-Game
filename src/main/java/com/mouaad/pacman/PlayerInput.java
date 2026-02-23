package com.mouaad.pacman;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PlayerInput extends KeyAdapter {
    private PacMan game;

    PlayerInput(PacMan game) {
        this.game = game;
    }

    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();
        char keyChar = e.getKeyChar();

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

            if (game.pacman == null)
                return;

            switch (code) {
                case KeyEvent.VK_UP -> game.pacman.updateInput('U');
                case KeyEvent.VK_DOWN -> game.pacman.updateInput('D');
                case KeyEvent.VK_LEFT -> game.pacman.updateInput('L');
                case KeyEvent.VK_RIGHT -> game.pacman.updateInput('R');
            }
        }

        if (game.getCurrentState() == GameState.SAVING_MAP) {
            if (code == KeyEvent.VK_ENTER) {
                game.confirmSaveMap();
            } else if (code == KeyEvent.VK_BACK_SPACE) {
                game.handleBackspace();
            } else if (Character.isLetterOrDigit(keyChar) || keyChar == '_' || keyChar == '-') {
                game.handleTyping(keyChar);
            }
            return;
        }

        if (game.getCurrentState() == GameState.REGISTERED_MAPS) {
            if (code == KeyEvent.VK_ESCAPE) {
                game.setState(GameState.MENU);
            }
            return;
        }

        if (game.getCurrentState() == GameState.GAME_OVER || game.getCurrentState() == GameState.VICTORY) {
            switch (code) {
                case KeyEvent.VK_R -> game.startGame();
                case KeyEvent.VK_M, KeyEvent.VK_ESCAPE -> game.setState(GameState.MENU);
            }
            return;
        }

        // ===== ESC = RETURN TO MENU =====
        if (code == KeyEvent.VK_ESCAPE) {
            game.setState(GameState.MENU);
        }
    }

}
