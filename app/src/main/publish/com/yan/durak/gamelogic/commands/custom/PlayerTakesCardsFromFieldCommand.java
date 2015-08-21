package com.yan.durak.gamelogic.commands.custom;


import com.yan.durak.gamelogic.commands.BaseSessionCommand;
import com.yan.durak.gamelogic.player.IPlayer;

/**
 * Created by Yan-Home on 12/23/2014.
 *
 * This is a void command created just to state the player intention to take cards
 */
public class PlayerTakesCardsFromFieldCommand extends BaseSessionCommand {

    private IPlayer mTakingPlayer;

    @Override
    public void execute() {
        //Void...
    }


    public void setTakingPlayer(final IPlayer takingPlayer) {
        mTakingPlayer = takingPlayer;
    }

    public IPlayer getTakingPlayer() {
        return mTakingPlayer;
    }
}
