package com.yan.durak.gamelogic.commands.hooks.notifiers.broadcast;


import com.yan.durak.gamelogic.commands.core.AddBotPlayerCommand;
import com.yan.durak.gamelogic.commands.hooks.CommandHook;
import com.yan.durak.gamelogic.communication.connection.IRemoteClient;
import com.yan.durak.gamelogic.communication.protocol.data.PlayerData;
import com.yan.durak.gamelogic.communication.protocol.messages.PlayerJoinProtocolMessage;
import com.yan.durak.gamelogic.player.BotPlayer;
import com.yan.durak.gamelogic.player.Player;
import com.yan.durak.gamelogic.player.RemotePlayer;

import java.util.List;

/**
 * Created by Yan-Home on 12/24/2014.
 */
public class BotJoinsGameBroadcastHook implements CommandHook<AddBotPlayerCommand> {

    @Override
    public Class<AddBotPlayerCommand> getHookTriggerCommandClass() {
        return AddBotPlayerCommand.class;
    }

    @Override
    public void onHookTrigger(AddBotPlayerCommand hookCommand) {

        //get player that joined
        BotPlayer joinedPlayer = hookCommand.getAddedBotPlayer();

        //create player data for joined player
        PlayerData joinedPlayerData = new PlayerData(joinedPlayer.getGameIndex(), joinedPlayer.getPileIndex());

        //get all other players that have been already in the game
        List<Player> otherPlayers = hookCommand.getGameSession().getPlayers();

        //notify other players that are already in the game that
        //new remote player have joined the game
        for (Player otherPlayer : otherPlayers) {
            //we don't need to notify bots , only remote players
            //that are different from the one that have joined
            if (!(otherPlayer instanceof RemotePlayer) ||
                    (joinedPlayer.getGameIndex() == otherPlayer.getGameIndex()))
                continue;

            //obtain remote client from the added player
            IRemoteClient client = ((RemotePlayer) otherPlayer).getSocketClient();

            //prepare game player joined message
            String jsonMsg = new PlayerJoinProtocolMessage(joinedPlayerData,
                    hookCommand.getGameSession().getGameRules().getTotalPlayersInGameAmount()).toJsonString();

            //send game setup message to client
            client.sendMessage(jsonMsg);
        }
    }
}
