package com.projectoop1aiub.edu.main.gost;

import java.lang.reflect.Constructor;

import com.projectoop1aiub.edu.physics.Anim;
import com.projectoop1aiub.edu.physics.Charecter;

/**
    A PowerUp class is a Sprite that the player can pick up.
*/
public abstract class PowerIncrease extends Charecter {

    public PowerIncrease(Anim anim) {
        super(anim);
    }

    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(
                new Object[] {(Anim)anim.clone()});
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }


    /**
        A Star PowerUp. Gives the player points.
    */
    public static class Star extends PowerIncrease {
        public Star(Anim anim) {
            super(anim);
        }
    }


    /**
        A Music PowerUp. Changes the game music.
    */
    public static class Music extends PowerIncrease {
        public Music(Anim anim) {
            super(anim);
        }
    }


    /**
        A Goal PowerUp. Advances to the next map.
    */
    public static class Goal extends PowerIncrease {
        public Goal(Anim anim) {
            super(anim);
        }
    }

}
