package com.projectoop1aiub.edu.main.gost;

import com.projectoop1aiub.edu.physics.Anim;


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
