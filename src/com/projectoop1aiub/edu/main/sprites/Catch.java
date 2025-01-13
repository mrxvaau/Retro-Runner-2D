package com.projectoop1aiub.edu.main.sprites;

import com.projectoop1aiub.edu.physics.Anim;

/**
    A Grub is a Creature that moves slowly on the ground.
*/
public class Catch extends Obstacles {

    public Catch(Anim left, Anim right,
                 Anim deadLeft, Anim deadRight)
    {
        super(left, right, deadLeft, deadRight);
    }


    public float getMaxSpeed() {
        return 0.05f;
    }

}
