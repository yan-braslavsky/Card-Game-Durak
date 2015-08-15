package com.yan.durak.gamelogic.exceptions;

/**
 * Created by Yan-Home on 12/21/2014.
 */
public class SameAttackerAsDefenderException extends RuntimeException {


    public SameAttackerAsDefenderException(final int attackerIndex, final int defenderIndex) {
        super("index of attacker is " + attackerIndex + " the same as index of defender " + defenderIndex);
    }
}