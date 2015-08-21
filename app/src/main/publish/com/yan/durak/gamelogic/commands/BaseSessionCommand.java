package com.yan.durak.gamelogic.commands;


import com.yan.durak.gamelogic.game.GameSession;

/**
 * Created by ybra on 19.12.2014.
 * <p/>
 * Base implementation of the SessionCommand
 */
public abstract class BaseSessionCommand implements SessionCommand {
    private GameSession mGameSession;

    @Override
    public void setGameSession(final GameSession gameSession)
    {
        mGameSession = gameSession;
    }

    public GameSession getGameSession() {
        return mGameSession;
    }
}
