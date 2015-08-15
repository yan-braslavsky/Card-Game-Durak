package com.yan.durak.gamelogic.commands.custom;


import com.yan.durak.gamelogic.commands.BaseSessionCommand;
import com.yan.durak.gamelogic.player.Player;

/**
 * Created by Yan-Home on 12/23/2014.
 *
 * This is a void command created just to state the player intention to take cards
 */
public class PlayerTakesCardsFromFieldCommand extends BaseSessionCommand {

    private Player mTakingPlayer;

    @Override
    public void execute() {
        //Void...
    }


    public void setTakingPlayer(Player takingPlayer) {
        mTakingPlayer = takingPlayer;
    }

    public Player getTakingPlayer() {
        return mTakingPlayer;
    }
}
