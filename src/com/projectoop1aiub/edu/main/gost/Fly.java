package com.projectoop1aiub.edu.main.gost;

import com.projectoop1aiub.edu.physics.Anim;

/**
    A Fly is a Creature that fly slowly in the air.
*/
public class Fly extends Obstacles {

    public Fly(Anim left, Anim right,
               Anim deadLeft, Anim deadRight)
    {
        super(left, right, deadLeft, deadRight);
    }


    public float getMaxSpeed() {
        return 0.2f;
    }


    public boolean isFlying() {
        return isAlive();
    }

}
