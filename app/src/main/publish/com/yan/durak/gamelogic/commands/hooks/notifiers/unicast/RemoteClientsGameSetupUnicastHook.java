package com.yan.durak.gamelogic.commands.hooks.notifiers.unicast;


import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.cards.Pile;
import com.yan.durak.gamelogic.commands.core.AddRemotePlayerCommand;
import com.yan.durak.gamelogic.commands.hooks.CommandHook;
import com.yan.durak.gamelogic.communication.connection.IRemoteClient;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.gamelogic.communication.protocol.data.PlayerData;
import com.yan.durak.gamelogic.communication.protocol.messages.GameSetupProtocolMessage;
import com.yan.durak.gamelogic.game.IGameRules;
import com.yan.durak.gamelogic.player.IPlayer;
import com.yan.durak.gamelogic.player.RemotePlayer;

import java.util.ArrayList;
import java.util.List;

import static com.yan.durak.gamelogic.cards.Pile.PileTags;
import static com.yan.durak.gamelogic.cards.Pile.PileTags.STOCK_PILE_TAG;
import static com.yan.durak.gamelogic.game.IGameRules.MAX_PLAYERS_IN_GAME;

/**
 * Created by Yan-Home on 12/24/2014.
 */
public class RemoteClientsGameSetupUnicastHook implements CommandHook<AddRemotePlayerCommand> {

    @Override
    public Class<AddRemotePlayerCommand> getHookTriggerCommandClass() {
        return AddRemotePlayerCommand.class;
    }

    @Override
    public void onHookTrigger(final AddRemotePlayerCommand hookCommand) {

        final RemotePlayer addedPlayer = hookCommand.getAddedPlayer();

        //obtain remote client from the added player
        final IRemoteClient client = addedPlayer.getSocketClient();

        //obtain trump card
        final List<Card> cardsInStockPile = hookCommand.getGameSession().findPileByTag(STOCK_PILE_TAG).getCardsInPile();

        //get the trump card
        final Card trumpCard = cardsInStockPile.get(0);

        //creating list of all other players that already joined
        final List<PlayerData> alreadyJoinedPlayers = new ArrayList<>(MAX_PLAYERS_IN_GAME);
        for (int i = 0; i < hookCommand.getGameSession().getPlayers().size(); i++) {
            final IPlayer player = hookCommand.getGameSession().getPlayers().get(i);
            if (player.getGameIndex() != addedPlayer.getGameIndex()) {
                alreadyJoinedPlayers.add(new PlayerData(player.getGameIndex(),
                        player.getPileIndex(), player.getPlayerMetaData()));
            }
        }

        //create my player data
        final PlayerData myPlayerData = new PlayerData(addedPlayer.getGameIndex(),
                addedPlayer.getPileIndex(), addedPlayer.getPlayerMetaData());

        //prepare game setup message
        final String jsonMsg = new GameSetupProtocolMessage(
                myPlayerData, new CardData(trumpCard.getRank(),
                trumpCard.getSuit()), alreadyJoinedPlayers,
                hookCommand.getGameSession().getGameRules().getTotalPlayersInGameAmount()).toJsonString();

        //send game setup message to client
        client.sendMessage(jsonMsg);
    }
}
