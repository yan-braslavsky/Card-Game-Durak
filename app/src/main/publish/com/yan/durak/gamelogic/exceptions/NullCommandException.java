package com.yan.durak.gamelogic.exceptions;

/**
 * Created by Yan-Home on 12/21/2014.
 */
public class NullCommandException extends RuntimeException {
    public NullCommandException() {
        super("The command is null !");
    }
}