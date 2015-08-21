package com.yan.durak.gamelogic.commands.hooks;


import com.yan.durak.gamelogic.commands.SessionCommand;

/**
 * Created by Yan-Home on 12/23/2014.
 *
 * @param <H> represents command to hook upon.
 */
public interface CommandHook<H extends SessionCommand> {

    /**
     * Defines a class , superclass or interface of the command that represents a hook trigger.
     * Every time hook trigger command is checked , the hook will be triggered.
     */
    Class<H> getHookTriggerCommandClass();

    /**
     * Called when hook is triggered
     *
     * @param hookCommand is a command that triggered the hook
     */
    void onHookTrigger(H hookCommand);
}
