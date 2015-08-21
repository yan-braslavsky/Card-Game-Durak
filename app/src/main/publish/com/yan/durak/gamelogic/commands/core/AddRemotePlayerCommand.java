package com.yan.durak.gamelogic.commands.core;


import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.cards.CardsHelper;
import com.yan.durak.gamelogic.cards.Pile;
import com.yan.durak.gamelogic.commands.BaseSessionCommand;
import com.yan.durak.gamelogic.commands.custom.AddPileCommand;
import com.yan.durak.gamelogic.communication.connection.ConnectedPlayer;
import com.yan.durak.gamelogic.player.RemotePlayer;

import java.util.ArrayList;

/**
 * Created by Yan-Home on 12/22/2014.
 */
public class AddRemotePlayerCommand extends BaseSessionCommand {

    private ConnectedPlayer mConnectedPlayer;
    private RemotePlayer mAddedPlayer;

    @Override
    public void execute() {

        //add players
        //associate pile index with player
        mAddedPlayer = new RemotePlayer(getGameSession().getPlayers().size(), getGameSession(),
                getGameSession().getPilesStack().size(),
                mConnectedPlayer.getRemoteClient(), mConnectedPlayer.getPlayerMetaData());

        //add players
        getGameSession().getPlayers().add(mAddedPlayer);

        //first we creating a pile for a player
        final AddPileCommand addPileCommand = new AddPileCommand();

        //create pile and tag it as player pile
        final Pile pile = new Pile();
        pile.addTag(Pile.PileTags.PLAYER_PILE_TAG);

        addPileCommand.setPile(pile);
        addPileCommand.setCards(new ArrayList<Card>(CardsHelper.MAX_CARDS_IN_DECK));
        getGameSession().executeCommand(addPileCommand);
    }

    public ConnectedPlayer getConnectedPlayer() {
        return mConnectedPlayer;
    }

    public void setConnectedPlayer(final ConnectedPlayer connectedPlayer) {
        mConnectedPlayer = connectedPlayer;
    }

    public RemotePlayer getAddedPlayer() {
        return mAddedPlayer;
    }


}