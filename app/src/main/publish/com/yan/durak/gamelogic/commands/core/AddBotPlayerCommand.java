package com.yan.durak.gamelogic.commands.core;


import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.cards.Pile;
import com.yan.durak.gamelogic.commands.BaseSessionCommand;
import com.yan.durak.gamelogic.commands.custom.AddPileCommand;
import com.yan.durak.gamelogic.communication.protocol.data.PlayerMetaData;
import com.yan.durak.gamelogic.player.BotPlayer;

import java.util.ArrayList;

/**
 * Created by Yan-Home on 12/22/2014.
 */
public class AddBotPlayerCommand extends BaseSessionCommand {

    private BotPlayer mAddedBotPlayer;
    private PlayerMetaData mPlayerMetaData;

    @Override
    public void execute() {

        //add players
        //associate pile index with player
        mAddedBotPlayer = new BotPlayer(getGameSession().getPlayers().size(), getGameSession(),
                getGameSession().getPilesStack().size(),mPlayerMetaData);

        //add players
        getGameSession().getPlayers().add(mAddedBotPlayer);

        //first we creating a pile for a player
        final AddPileCommand addPileCommand = new AddPileCommand();

        //create pile and tag it as player pile
        final Pile pile = new Pile();
        pile.addTag(Pile.PileTags.PLAYER_PILE_TAG);

        addPileCommand.setPile(pile);
        addPileCommand.setCards(new ArrayList<Card>());
        getGameSession().executeCommand(addPileCommand);
    }

    public BotPlayer getAddedBotPlayer() {
        return mAddedBotPlayer;
    }

    public void setPlayerMetaData(final PlayerMetaData playerMetaData) {
        mPlayerMetaData = playerMetaData;
    }
}