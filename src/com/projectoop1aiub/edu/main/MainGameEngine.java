package com.projectoop1aiub.edu.main;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Iterator;


import com.projectoop1aiub.edu.physics.Charecter;
import com.projectoop1aiub.edu.user_input.GameReflex;
import com.projectoop1aiub.edu.user_input.Input_Initializer;
import com.projectoop1aiub.edu.test.GameCore;

import com.projectoop1aiub.edu.main.gost.Obstacles;
import com.projectoop1aiub.edu.main.gost.Player;
import com.projectoop1aiub.edu.main.gost.PowerIncrease;

/**
 * GameManager manages all parts of the game.
 */
public class MainGameEngine extends GameCore
{

    public static void main(String[] args)
    {
        new MainGameEngine().run();
    }

    public static final float GRAVITY = 0.002f;

    private Point pointCache = new Point();
    private Map map;
    private Rendering rendering;
    private Input_Initializer inputInitializer;
    private GroundRendering drawer;

    private GameReflex moveLeft;
    private GameReflex moveRight;
    private GameReflex jump;
    private GameReflex exit;
    private int collectedStars=0;
    private int numLives=6;

    public void init()
    {
        super.init();

        // set up user_input manager
        initInput();

        // start resource manager
        rendering = new Rendering(screen.getFullScreenWindow().getGraphicsConfiguration());

        // load resources
        drawer = new GroundRendering();
        drawer.setBackground(rendering.loadImage("background.jpg"));

        // load first map
        map = rendering.loadNextMap();
    }


    /**
     * Closes any resurces used by the GameManager.
     */
    public void stop() {
        super.stop();

    }


    private void initInput() {
        moveLeft = new GameReflex("moveLeft");
        moveRight = new GameReflex("moveRight");
        jump = new GameReflex("jump", GameReflex.DETECT_INITAL_PRESS_ONLY);
        exit = new GameReflex("exit", GameReflex.DETECT_INITAL_PRESS_ONLY);

        inputInitializer = new Input_Initializer(screen.getFullScreenWindow());
        inputInitializer.setCursor(Input_Initializer.INVISIBLE_CURSOR);

        inputInitializer.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputInitializer.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputInitializer.mapToKey(jump, KeyEvent.VK_SPACE);
        inputInitializer.mapToKey(exit, KeyEvent.VK_ESCAPE);
    }


    private void checkInput(long elapsedTime)
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


    /**
     * Gets the current map.
     */
    public Map getMap() {
        return map;
    }

    /**
     * Gets the tile that a Sprites collides with. Only the
     * Sprite's X or Y should be changed, not both. Returns null
     * if no collision is detected.
     */
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


    /**
     * Checks if two Sprites collide with one another. Returns
     * false if the two Sprites are the same. Returns false if
     * one of the Sprites is a Creature that is not alive.
     */
    public boolean isCollision(Charecter s1, Charecter s2) {
        // if the Sprites are the same, return false
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

        // get the pixel location of the Sprites
        int s1x = Math.round(s1.getX());
        int s1y = Math.round(s1.getY());
        int s2x = Math.round(s2.getX());
        int s2y = Math.round(s2.getY());

        // check if the two gost' boundaries intersect
        return (s1x < s2x + s2.getWidth() &&
                s2x < s1x + s1.getWidth() &&
                s1y < s2y + s2.getHeight() &&
                s2y < s1y + s1.getHeight());
    }


    /**
     * Gets the Sprite that collides with the specified Sprite,
     * or null if no Sprite collides with the specified Sprite.
     */
    public Charecter getSpriteCollision(Charecter charecter) {

        // run through the list of Sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Charecter otherCharecter = (Charecter)i.next();
            if (isCollision(charecter, otherCharecter)) {
                // collision found, return the Sprite
                return otherCharecter;
            }
        }

        // no collision found
        return null;
    }


    /**
     * Updates Animation, position, and velocity of all Sprites
     * in the current map.
     */
    public void update(long elapsedTime) {
        Obstacles player = (Obstacles)map.getPlayer();


        // player is dead! start map over
        if (player.getState() == Obstacles.STATE_DEAD) {
            map = rendering.reloadMap();
            return;
        }

        // get keyboard/mouse user_input
        checkInput(elapsedTime);

        // update player
        updateCreature(player, elapsedTime);
        player.update(elapsedTime);

        // update other gost
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Charecter charecter = (Charecter)i.next();
            if (charecter instanceof Obstacles) {
                Obstacles obstacles = (Obstacles) charecter;
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


    /**
     * Updates the creature, applying gravity for creatures that
     * aren't flying, and checks collisions.
     */
    private void updateCreature(Obstacles obstacles,
                                long elapsedTime) {

        // apply gravity
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
            // line up with the tile boundary
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

        // change y
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


    /**
     * Checks for Player collision with other Sprites. If
     * canKill is true, collisions with Creatures will kill
     * them.
     */
    public void checkPlayerCollision(Player player,
            boolean canKill) {
        if (!player.isAlive()) {
            return;
        }

        // check for player collision with other gost
        Charecter collisionCharecter = getSpriteCollision(player);
        if (collisionCharecter instanceof PowerIncrease) {
            acquirePowerUp((PowerIncrease) collisionCharecter);
        } else if (collisionCharecter instanceof Obstacles) {
            Obstacles badguy = (Obstacles) collisionCharecter;
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


    /**
     * Gives the player the speicifed power up and removes it
     * from the map.
     */
    public void acquirePowerUp(PowerIncrease powerIncrease) {
        // remove it from the map
        map.removeSprite(powerIncrease);

        if (powerIncrease instanceof PowerIncrease.Star) {
            // do something here, like give the player points
            collectedStars++;
            if(collectedStars==100)
            {
                numLives++;
                collectedStars=0;
            }

        } else if (powerIncrease instanceof PowerIncrease.Music) {
            // change the music

        } else if (powerIncrease instanceof PowerIncrease.Goal) {
            // advance to next map

            map = rendering.loadNextMap();

        }
    }


}