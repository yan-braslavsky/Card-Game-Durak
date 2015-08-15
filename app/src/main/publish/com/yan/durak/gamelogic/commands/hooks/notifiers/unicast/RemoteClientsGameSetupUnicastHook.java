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
import com.yan.durak.gamelogic.player.Player;
import com.yan.durak.gamelogic.player.RemotePlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yan-Home on 12/24/2014.
 */
public class RemoteClientsGameSetupUnicastHook implements CommandHook<AddRemotePlayerCommand> {

    @Override
    public Class<AddRemotePlayerCommand> getHookTriggerCommandClass() {
        return AddRemotePlayerCommand.class;
    }

    @Override
    public void onHookTrigger(AddRemotePlayerCommand hookCommand) {

        RemotePlayer addedPlayer = hookCommand.getAddedPlayer();

        //obtain remote client from the added player
        IRemoteClient client = addedPlayer.getSocketClient();

        //obtain trump card
        List<Card> cardsInStockPile = hookCommand.getGameSession().findPileByTag(Pile.PileTags.STOCK_PILE_TAG).getCardsInPile();

        //get the trump card
        Card trumpCard = cardsInStockPile.get(0);

        //creating list of all other players that already joined
        List<PlayerData> alreadyJoinedPlayers = new ArrayList<>(IGameRules.MAX_PLAYERS_IN_GAME);
        for (Player player : hookCommand.getGameSession().getPlayers()) {
            if (player.getGameIndex() != addedPlayer.getGameIndex()) {
                alreadyJoinedPlayers.add(new PlayerData(player.getGameIndex(), player.getPileIndex()));
            }
        }

        //create my player data
        PlayerData myPlayerData = new PlayerData(addedPlayer.getGameIndex(), addedPlayer.getPileIndex());

        //prepare game setup message
        String jsonMsg = new GameSetupProtocolMessage(
                myPlayerData, new CardData(trumpCard.getRank(),
                trumpCard.getSuit()), alreadyJoinedPlayers,
                hookCommand.getGameSession().getGameRules().getTotalPlayersInGameAmount()).toJsonString();

        //send game setup message to client
        client.sendMessage(jsonMsg);
    }
}
