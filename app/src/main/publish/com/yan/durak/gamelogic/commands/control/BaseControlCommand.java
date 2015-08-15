package com.yan.durak.gamelogic.commands.control;


import com.yan.durak.gamelogic.commands.BaseSessionCommand;
import com.yan.durak.gamelogic.commands.SessionCommand;

/**
 * Created by Yan-Home on 12/22/2014.
 * <p/>
 * This is a base class for a command that controls other command
 */
public abstract class BaseControlCommand<T extends BaseSessionCommand> extends BaseSessionCommand {

    protected T searchForRecentControlledCommand() {

        //iterate backwards and find the latest controlled command
        for (int i = getGameSession().getExecutedCommandsStack().size() - 1; i >= 0; i--) {
            final SessionCommand sessionCommand = getGameSession().getExecutedCommandsStack().get(i);
            if (sessionCommand.getClass().isAssignableFrom(getControlledCommandClass())) {
                return (T) sessionCommand;
            }
        }
        return null;
    }

    protected abstract Class<T> getControlledCommandClass();
}
