package com.yan.durak.gamelogic.exceptions;

/**
 * Created by Yan-Home on 12/21/2014.
 */
public class EmptyPileException extends RuntimeException {
    public EmptyPileException(int pileIndex) {
        super("the pile " +  pileIndex + " is empty !");
    }
}