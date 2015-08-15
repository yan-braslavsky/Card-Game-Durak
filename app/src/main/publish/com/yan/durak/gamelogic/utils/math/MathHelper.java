package com.yan.durak.gamelogic.utils.math;

/**
 * Created by Yan-Home on 12/21/2014.
 */
public class MathHelper {

    public static final int randomInRange(int min, int max) {
        return (int) (Math.random() < 0.5 ? ((1 - Math.random()) * (max - min) + min) : (Math.random() * (max - min) + min));
    }
}
