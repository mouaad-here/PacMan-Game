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
        if (game.pacman == null) return;

        if(code == KeyEvent.VK_UP){
            game.pacman.updateInput('U');
        }

        else if(code == KeyEvent.VK_DOWN){
            game.pacman.updateInput('D');
        }

        else if(code == KeyEvent.VK_LEFT){
            game.pacman.updateInput('L');
        }

        else if(code == KeyEvent.VK_RIGHT){
            game.pacman.updateInput('R');
        }else if (code == KeyEvent.VK_1 ) {
            game.startGame(true);
        }else if (code == KeyEvent.VK_2 ) {
            game.startGame(false);
        }else if (code == KeyEvent.VK_3) {
            game.startVisualization();
        }
    }

}
