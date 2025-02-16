package com.projectoop1aiub.edu.main;

import java.awt.*;
import java.util.Iterator;

import com.projectoop1aiub.edu.physics.Charecter;
import com.projectoop1aiub.edu.main.gost.Obstacles;


public class GroundRendering
{

    private static final int TILE_SIZE = 64;

    private static final int TILE_SIZE_BITS = 6;

    private Image background;


    public static int pixelsToTiles(float pixels) {
        return pixelsToTiles(Math.round(pixels));
    }



    public static int pixelsToTiles(int pixels) {
        // use shifting to get correct values for negative pixels
        return pixels >> TILE_SIZE_BITS;


    }



    public static int tilesToPixels(int numTiles) {

        return numTiles << TILE_SIZE_BITS;

    }



    public void setBackground(Image background) {
        this.background = background;
    }



    public void draw(Graphics2D g, Map map,
        int screenWidth, int screenHeight)
    {
        Charecter player = map.getPlayer();
        int mapWidth = tilesToPixels(map.getWidth());


        int offsetX = screenWidth / 2 -
            Math.round(player.getX()) - TILE_SIZE;
        offsetX = Math.min(offsetX, 0);
        offsetX = Math.max(offsetX, screenWidth - mapWidth);


        int offsetY = screenHeight -
            tilesToPixels(map.getHeight());


        if (background == null ||
            screenHeight > background.getHeight(null))
        {
            g.setColor(Color.black);
            g.fillRect(0, 0, screenWidth, screenHeight);
        }


        if (background != null) {
            int x = offsetX *
                (screenWidth - background.getWidth(null)) /
                (screenWidth - mapWidth);
            int y = screenHeight - background.getHeight(null);

            g.drawImage(background, x, y, null);
        
        }


        int firstTileX = pixelsToTiles(-offsetX);
        int lastTileX = firstTileX +
            pixelsToTiles(screenWidth) + 1;
        for (int y=0; y<map.getHeight(); y++) {
            for (int x=firstTileX; x <= lastTileX; x++) {
                Image image = map.getTile(x, y);
                if (image != null) {
                    g.drawImage(image,
                        tilesToPixels(x) + offsetX,
                        tilesToPixels(y) + offsetY,
                        null);
                }
            }
        }

        // draw player
        g.drawImage(player.getImage(),
            Math.round(player.getX()) + offsetX,
            Math.round(player.getY()) + offsetY,
            null);

        // draw gost
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Charecter charecter = (Charecter)i.next();
            int x = Math.round(charecter.getX()) + offsetX;
            int y = Math.round(charecter.getY()) + offsetY;
            g.drawImage(charecter.getImage(), x, y, null);


            if (charecter instanceof Obstacles &&
                x >= 0 && x < screenWidth)
            {
                ((Obstacles) charecter).wakeUp();
            }
        }
    }

}
