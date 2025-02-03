package com.projectoop1aiub.edu.main;

import java.awt.Image;
import java.util.LinkedList;
import java.util.Iterator;

import com.projectoop1aiub.edu.physics.Charecter;


public class Map {

    private Image[][] tiles;
    private LinkedList sprites;
    private Charecter player;


    public Map(int width, int height) {
        tiles = new Image[width][height];
        sprites = new LinkedList();
    }



    public int getWidth() {
        return tiles.length;
    }



    public int getHeight() {
        return tiles[0].length;
    }



    public Image getTile(int x, int y) {
        if (x < 0 || x >= getWidth() ||
            y < 0 || y >= getHeight())
        {
            return null;
        }
        else {
            return tiles[x][y];
        }
    }



    public void setTile(int x, int y, Image tile) {
        tiles[x][y] = tile;
    }



    public Charecter getPlayer() {
        return player;
    }



    public void setPlayer(Charecter player) {
        this.player = player;
    }



    public void addSprite(Charecter charecter) {
        sprites.add(charecter);
    }



    public void removeSprite(Charecter charecter) {
        sprites.remove(charecter);
    }


    public Iterator getSprites() {
        return sprites.iterator();
    }

}
