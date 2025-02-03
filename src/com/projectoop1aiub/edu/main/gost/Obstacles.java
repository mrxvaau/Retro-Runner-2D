package com.projectoop1aiub.edu.main.gost;

import java.lang.reflect.Constructor;

import com.projectoop1aiub.edu.physics.Anim;
import com.projectoop1aiub.edu.physics.Charecter;


public abstract class Obstacles extends Charecter {


    private static final int DIE_TIME = 1000;

    public static final int STATE_NORMAL = 0;
    public static final int STATE_DYING = 1;
    public static final int STATE_DEAD = 2;

    private Anim left;
    private Anim right;
    private Anim deadLeft;
    private Anim deadRight;
    private int state;
    private long stateTime;


    public Obstacles(Anim left, Anim right,
                     Anim deadLeft, Anim deadRight)
    {
        super(right);
        this.left = left;
        this.right = right;
        this.deadLeft = deadLeft;
        this.deadRight = deadRight;
        state = STATE_NORMAL;
    }


    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(new Object[] {
                (Anim)left.clone(),
                (Anim)right.clone(),
                (Anim)deadLeft.clone(),
                (Anim)deadRight.clone()
            });
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }

    public float getMaxSpeed() {
        return 0;
    }


    public void wakeUp() {
        if (getState() == STATE_NORMAL && getVelocityX() == 0) {
            setVelocityX(-getMaxSpeed());
        }
    }



    public int getState() {
        return state;
    }


    public void setState(int state) {
        if (this.state != state) {
            this.state = state;
            stateTime = 0;
            if (state == STATE_DYING) {
                setVelocityX(0);
                setVelocityY(0);
            }
        }
    }



    public boolean isAlive() {
        return (state == STATE_NORMAL);
    }


    public boolean isFlying() {
        return false;
    }



    public void collideHorizontal() {
        setVelocityX(-getVelocityX());
    }

    public void collideVertical() {
        setVelocityY(0);
    }


    public void update(long elapsedTime) {
        // select the correct Animation
        Anim newAnim = anim;
        if (getVelocityX() < 0) {
            newAnim = left;
        }
        else if (getVelocityX() > 0) {
            newAnim = right;
        }
        if (state == STATE_DYING && newAnim == left) {
            newAnim = deadLeft;
        }
        else if (state == STATE_DYING && newAnim == right) {
            newAnim = deadRight;
        }

        // update the Animation
        if (anim != newAnim) {
            anim = newAnim;
            anim.start();
        }
        else {
            anim.update(elapsedTime);
        }

        // update to "dead" state
        stateTime += elapsedTime;
        if (state == STATE_DYING && stateTime >= DIE_TIME) {
            setState(STATE_DEAD);
        }
    }

}
