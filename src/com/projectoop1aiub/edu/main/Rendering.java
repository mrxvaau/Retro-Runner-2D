package com.projectoop1aiub.edu.main;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;


import com.projectoop1aiub.edu.physics.Anim;
import com.projectoop1aiub.edu.physics.Charecter;
import com.projectoop1aiub.edu.main.gost.Fly;
import com.projectoop1aiub.edu.main.gost.Catch;
import com.projectoop1aiub.edu.main.gost.Player;
import com.projectoop1aiub.edu.main.gost.PowerIncrease;



public class Rendering
{
    private ArrayList tiles;
    public int currentMap;
    private GraphicsConfiguration gc;


    private Charecter playerCharecter;
    private Charecter musicCharecter;
    private Charecter coinCharecter;
    private Charecter goalCharecter;
    private Charecter grubCharecter;
    private Charecter flyCharecter;


    public Rendering(GraphicsConfiguration gc)
    {
        this.gc = gc;
        loadTileImages();
        loadCreatureSprites();
        loadPowerUpSprites();
    }



    public Image loadImage(String name) 
    {
        String filename = "images/" + name;
        return new ImageIcon(filename).getImage();
    }


    public Image getMirrorImage(Image image) 
    {
        return getScaledImage(image, -1, 1);
    }


    public Image getFlippedImage(Image image) 
    {
        return getScaledImage(image, 1, -1);
    }


    private Image getScaledImage(Image image, float x, float y) 
    {


        AffineTransform transform = new AffineTransform();
        transform.scale(x, y);
        transform.translate(
            (x-1) * image.getWidth(null) / 2,
            (y-1) * image.getHeight(null) / 2);


        Image newImage = gc.createCompatibleImage(
            image.getWidth(null),
            image.getHeight(null),
            Transparency.BITMASK);


        Graphics2D g = (Graphics2D)newImage.getGraphics();
        g.drawImage(image, transform, null);
        g.dispose();

        return newImage;
    }


    public Map loadNextMap()
    {
        Map map = null;
        while (map == null) 
        {
            currentMap++;
            try {
                map = loadMap(
                    "maps/map" + currentMap + ".txt");
            }
            catch (IOException ex) 
            {
                if (currentMap == 2) 
                {
                    // no maps to load!
                    return null;
                }
                  currentMap = 0;
                map = null;
            }
        }

        return map;
    }


    public Map reloadMap()
    {
        try {
            return loadMap(
                "maps/map" + currentMap + ".txt");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private Map loadMap(String filename)
        throws IOException
    {
        ArrayList lines = new ArrayList();
        int width = 0;
        int height = 0;


        BufferedReader reader = new BufferedReader(
            new FileReader(filename));
        while (true) {
            String line = reader.readLine();

            if (line == null) {
                reader.close();
                break;
            }


            if (!line.startsWith("#")) {
                lines.add(line);
                width = Math.max(width, line.length());
            }
        }


        height = lines.size();
        Map newMap = new Map(width, height);
        for (int y=0; y<height; y++) {
            String line = (String)lines.get(y);
            for (int x=0; x<line.length(); x++) {
                char ch = line.charAt(x);


                int tile = ch - 'A';
                if (tile >= 0 && tile < tiles.size()) {
                    newMap.setTile(x, y, (Image)tiles.get(tile));
                }


                else if (ch == 'o') {
                    addSprite(newMap, coinCharecter, x, y);
                }
                else if (ch == '!') {
                    addSprite(newMap, musicCharecter, x, y);
                }
                else if (ch == '*') {
                    addSprite(newMap, goalCharecter, x, y);
                }
                else if (ch == '1') {
                    addSprite(newMap, grubCharecter, x, y);
                }
                else if (ch == '2') {
                    addSprite(newMap, flyCharecter, x, y);
                }
            }
        }


        Charecter player = (Charecter) playerCharecter.clone();
        player.setX(GroundRendering.tilesToPixels(3));
        player.setY(lines.size());
        newMap.setPlayer(player);

        return newMap;
    }


    private void addSprite(Map map,
                           Charecter hostCharecter, int tileX, int tileY)
    {
        if (hostCharecter != null) {

            Charecter charecter = (Charecter) hostCharecter.clone();

            charecter.setX(
                GroundRendering.tilesToPixels(tileX) +
                (GroundRendering.tilesToPixels(1) -
                charecter.getWidth()) / 2);


            charecter.setY(
                GroundRendering.tilesToPixels(tileY + 1) -
                charecter.getHeight());


            map.addSprite(charecter);
        }
    }



    public void loadTileImages()
    {

        tiles = new ArrayList();
        char ch = 'A';
        
        while (true) 
        {
            String name = ch + ".png";
            File file = new File("images/" + name);
            if (!file.exists()) 
                break;
            
            tiles.add(loadImage(name));
            ch++;
        }
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public void loadCreatureSprites() 
    {

        Image[][] images = new Image[4][];


        images[0] = new Image[] {
            loadImage("player.PNG"),         
            loadImage("fly1.png"),
            loadImage("fly2.png"),
            loadImage("fly3.png"),
            loadImage("grub1.png"),
            loadImage("grub2.png"),
        };

        images[1] = new Image[images[0].length];
        images[2] = new Image[images[0].length];
        images[3] = new Image[images[0].length];
        
        for (int i=0; i<images[0].length; i++) 
        {

            images[1][i] = getMirrorImage(images[0][i]);

            images[2][i] = getFlippedImage(images[0][i]);

            images[3][i] = getFlippedImage(images[1][i]);
        }


        Anim[] playerAnim = new Anim[4];
        Anim[] flyAnim = new Anim[4];
        Anim[] grubAnim = new Anim[4];
        
        for (int i=0; i<4; i++) 
        {
            playerAnim[i] = createPlayerAnim (images[i][0]);
            flyAnim[i] = createFlyAnim (images[i][1], images[i][1], images[i][3]);
            grubAnim[i] = createGrubAnim (images[i][4], images[i][5]);
        }


        playerCharecter = new Player(playerAnim[0], playerAnim[1],playerAnim[2], playerAnim[3]);
        flyCharecter = new Fly(flyAnim[0], flyAnim[1],flyAnim[2], flyAnim[3]);
        grubCharecter = new Catch(grubAnim[0], grubAnim[1],grubAnim[2], grubAnim[3]);
    }


    private Anim createPlayerAnim(Image player)
    {
        Anim anim = new Anim();
        anim.addFrame(player, 250);
     
        return anim;
    }


    private Anim createFlyAnim(Image img1, Image img2, Image img3)
    {
        Anim anim = new Anim();
        anim.addFrame(img1, 50);
        anim.addFrame(img2, 50);
        anim.addFrame(img3, 50);
        anim.addFrame(img2, 50);
        return anim;
    }


    private Anim createGrubAnim(Image img1, Image img2)
    {
        Anim anim = new Anim();
        anim.addFrame(img1, 250);
        anim.addFrame(img2, 250);
        return anim;
    }


    private void loadPowerUpSprites() 
    {

        Anim anim = new Anim();
        anim.addFrame(loadImage("heart.png"), 150);
        goalCharecter = new PowerIncrease.Goal(anim);


        anim = new Anim();
        anim.addFrame(loadImage("coin1.PNG"),250 ) ;  
        anim.addFrame(loadImage("coin2.PNG"),250);
        anim.addFrame(loadImage("coin3.PNG"),250);
        anim.addFrame(loadImage("coin4.PNG"),250);
        anim.addFrame(loadImage("coin5.PNG"),250);
        coinCharecter = new PowerIncrease.Star(anim);


        anim = new Anim();
        anim.addFrame(loadImage("music1.png"), 150);
        anim.addFrame(loadImage("music2.png"), 150);
        anim.addFrame(loadImage("music3.png"), 150);
        anim.addFrame(loadImage("music2.png"), 150);
        musicCharecter = new PowerIncrease.Music(anim);
    }

}
