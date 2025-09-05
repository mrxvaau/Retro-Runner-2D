package com.projectoop1aiub.edu.main;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Iterator;


import com.projectoop1aiub.edu.physics.Charecter;
import com.projectoop1aiub.edu.user_input.GameReflex;
import com.projectoop1aiub.edu.user_input.Input_Initializer;
import com.projectoop1aiub.edu.test.GameCore;

import com.projectoop1aiub.edu.main.gost.Obstacles;
import com.projectoop1aiub.edu.main.gost.Player;
import com.projectoop1aiub.edu.main.gost.PowerIncrease;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


@SuppressWarnings("ALL")
public class MainGameEngine extends GameCore
{

    public static void main(String[] args)
    {
        new MainGameEngine().run();
    }

    public static final float GRAVITY = 0.002f;

    private final Point pointCache = new Point();
    private Map map;
    private Rendering rendering;
    private GroundRendering drawer;

    private GameReflex moveLeft;
    private GameReflex moveRight;
    private GameReflex jump;
    private GameReflex exit;
    private int collectedStars=0;
    private int numLives=6;
    private Clip openingMusic;

    public void init()
    {
        super.init();

        // set up user_input manager
        initInput();

        // start resource manager
        rendering = new Rendering(screen.getFullScreenWindow().getGraphicsConfiguration());

        // load resources
        drawer = new GroundRendering();
        drawer.setBackground(rendering.loadImage("background.JPG"));

        // load first map
        map = rendering.loadNextMap();
        playOpeningMusic();
    }

    private void playOpeningMusic() {
        try {
            File audioFile = new File("audio/background_music_loop.wav");
            if (!audioFile.exists()) {
                System.err.println("Audio file not found: " + "audio/background_music_loop.wav");
                return; // Continue without audio
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            openingMusic = AudioSystem.getClip();
            openingMusic.open(audioInputStream);
            openingMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.err.println("Error playing opening music: " + e.getMessage());
        }
    }
    private void stopOpeningMusic() {
        try {
            if (openingMusic != null && openingMusic.isRunning()) {
                openingMusic.stop();
                openingMusic.close();
            }
        } catch (Exception e) {
            System.err.println("Error stopping opening music: " + e.getMessage());
        }
    }

    public void stop() {

        stopOpeningMusic();
        GameOverWindow.show();


    }


    private void initInput() {
        moveLeft = new GameReflex("moveLeft");
        moveRight = new GameReflex("moveRight");
        jump = new GameReflex("jump", GameReflex.DETECT_INITAL_PRESS_ONLY);
        exit = new GameReflex("exit", GameReflex.DETECT_INITAL_PRESS_ONLY);

        Input_Initializer inputInitializer = new Input_Initializer(screen.getFullScreenWindow());
        inputInitializer.setCursor(Input_Initializer.INVISIBLE_CURSOR);

        inputInitializer.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputInitializer.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputInitializer.mapToKey(jump, KeyEvent.VK_UP);
        inputInitializer.mapToKey(exit, KeyEvent.VK_ESCAPE);
    }


    private void checkInput()
    {

        if (exit.isPressed()) {
            stop();
        }

        Player player = (Player)map.getPlayer();
        if (player.isAlive())
        {
            float velocityX = 0;
            if (moveLeft.isPressed())
            {
                velocityX-=player.getMaxSpeed();
            }
            if (moveRight.isPressed()) {
                velocityX+=player.getMaxSpeed();
            }
            if (jump.isPressed()) {
                player.jump(false);
            }
            player.setVelocityX(velocityX);
        }

    }


    public void draw(Graphics2D g) {

        drawer.draw(g, map, screen.getWidth(), screen.getHeight());
        g.setColor(Color.WHITE);
        g.drawString("Press ESC for EXIT.",10.0f,20.0f);
        g.setColor(Color.GREEN);
        g.drawString("Coins: "+collectedStars,300.0f,20.0f);
        g.setColor(Color.YELLOW);
        g.drawString("Lives: "+(numLives),500.0f,20.0f );
        g.setColor(Color.WHITE);
        g.drawString("Home: "+ rendering.currentMap,700.0f,20.0f);

    }


    public Point getTileCollision(Charecter charecter, float newX, float newY)
    {
        float fromX = Math.min(charecter.getX(), newX);
        float fromY = Math.min(charecter.getY(), newY);
        float toX = Math.max(charecter.getX(), newX);
        float toY = Math.max(charecter.getY(), newY);

        // get the tile locations
        int fromTileX = GroundRendering.pixelsToTiles(fromX);
        int fromTileY = GroundRendering.pixelsToTiles(fromY);
        int toTileX = GroundRendering.pixelsToTiles(
                toX + charecter.getWidth() - 1);
        int toTileY = GroundRendering.pixelsToTiles(
                toY + charecter.getHeight() - 1);

        // check each tile for a collision
        for (int x=fromTileX; x<=toTileX; x++) {
            for (int y=fromTileY; y<=toTileY; y++) {
                if (x < 0 || x >= map.getWidth() ||
                        map.getTile(x, y) != null) {
                    // collision found, return the tile
                    pointCache.setLocation(x, y);
                    return pointCache;
                }
            }
        }

        // no collision found
        return null;
    }



    public boolean isCollision(Charecter s1, Charecter s2) {

        if (s1 == s2) {
            return false;
        }

        // if one of the Sprites is a dead Creature, return false
        if (s1 instanceof Obstacles && !((Obstacles)s1).isAlive()) {
            return false;
        }
        if (s2 instanceof Obstacles && !((Obstacles)s2).isAlive()) {
            return false;
        }


        int s1x = Math.round(s1.getX());
        int s1y = Math.round(s1.getY());
        int s2x = Math.round(s2.getX());
        int s2y = Math.round(s2.getY());


        return (s1x < s2x + s2.getWidth() &&
                s2x < s1x + s1.getWidth() &&
                s1y < s2y + s2.getHeight() &&
                s2y < s1y + s1.getHeight());
    }



    public Charecter getSpriteCollision(Charecter charecter) {


        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Charecter otherCharecter = (Charecter)i.next();
            if (isCollision(charecter, otherCharecter)) {

                return otherCharecter;
            }
        }

        // no collision found
        return null;
    }



    public void update(long elapsedTime) {
        Obstacles player = (Obstacles)map.getPlayer();



        if (player.getState() == Obstacles.STATE_DEAD) {
            map = rendering.reloadMap();
            return;
        }


        checkInput();

        updateCreature(player, elapsedTime);
        player.update(elapsedTime);

        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Charecter charecter = (Charecter)i.next();
            if (charecter instanceof Obstacles obstacles) {
                if (obstacles.getState() == Obstacles.STATE_DEAD) {
                    i.remove();
                } else {
                    updateCreature(obstacles, elapsedTime);
                }
            }
            // normal update
            charecter.update(elapsedTime);
        }
    }

    private void updateCreature(Obstacles obstacles,
                                long elapsedTime) {


        if (!obstacles.isFlying()) {
            obstacles.setVelocityY(obstacles.getVelocityY() +
                    GRAVITY * elapsedTime);
        }

        // change x
        float dx = obstacles.getVelocityX();
        float oldX = obstacles.getX();
        float newX = oldX + dx * elapsedTime;
        Point tile =
                getTileCollision(obstacles, newX, obstacles.getY());
        if (tile == null) {
            obstacles.setX(newX);
        } else {

            if (dx > 0) {
                obstacles.setX(
                        GroundRendering.tilesToPixels(tile.x) -
                                obstacles.getWidth());
            } else if (dx < 0) {
                obstacles.setX(
                        GroundRendering.tilesToPixels(tile.x + 1));
            }
            obstacles.collideHorizontal();
        }
        if (obstacles instanceof Player) {
            checkPlayerCollision((Player) obstacles, false);
        }


        float dy = obstacles.getVelocityY();
        float oldY = obstacles.getY();
        float newY = oldY + dy * elapsedTime;
        tile = getTileCollision(obstacles, obstacles.getX(), newY);
        if (tile == null) {
            obstacles.setY(newY);
        } else {
            // line up with the tile boundary
            if (dy > 0) {
                obstacles.setY(
                        GroundRendering.tilesToPixels(tile.y) -
                                obstacles.getHeight());
            } else if (dy < 0) {
                obstacles.setY(
                        GroundRendering.tilesToPixels(tile.y + 1));
            }
            obstacles.collideVertical();
        }
        if (obstacles instanceof Player) {
            boolean canKill = (oldY < obstacles.getY());
            checkPlayerCollision((Player) obstacles, canKill);
        }

    }



    public void checkPlayerCollision(Player player,
                                     boolean canKill) {
        if (!player.isAlive()) {
            return;
        }

        // check for player collision with other gost
        Charecter collisionCharecter = getSpriteCollision(player);
        if (collisionCharecter instanceof PowerIncrease) {
            acquirePowerUp((PowerIncrease) collisionCharecter);
        } else if (collisionCharecter instanceof Obstacles badguy) {
            if (canKill) {
                // kill the badguy and make player bounce
                badguy.setState(Obstacles.STATE_DYING);
                player.setY(badguy.getY() - player.getHeight());
                player.jump(true);
            } else {
                // player dies!
                player.setState(Obstacles.STATE_DYING);
                numLives--;
                if(numLives==0) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    stop();
                }
            }
        }
    }



    public void acquirePowerUp(PowerIncrease powerIncrease) {

        map.removeSprite(powerIncrease);

        if (powerIncrease instanceof PowerIncrease.Star) {

            collectedStars++;
            if(collectedStars==100)
            {
                numLives++;
                collectedStars=0;
            }

        } else if (powerIncrease instanceof PowerIncrease.Music) {


        } else if (powerIncrease instanceof PowerIncrease.Goal) {


            map = rendering.loadNextMap();

        }
    }


}