package com.yan.durak.gamelogic.commands.hooks.notifiers.broadcast;


import com.yan.durak.gamelogic.commands.core.AddRemotePlayerCommand;
import com.yan.durak.gamelogic.commands.hooks.CommandHook;
import com.yan.durak.gamelogic.communication.connection.IRemoteClient;
import com.yan.durak.gamelogic.communication.protocol.data.PlayerData;
import com.yan.durak.gamelogic.communication.protocol.messages.PlayerJoinProtocolMessage;
import com.yan.durak.gamelogic.player.IPlayer;
import com.yan.durak.gamelogic.player.RemotePlayer;

import java.util.List;

/**
 * Created by Yan-Home on 12/24/2014.
 */
public class RemoteClientJoinsGameBroadcastHook implements CommandHook<AddRemotePlayerCommand> {

    @Override
    public Class<AddRemotePlayerCommand> getHookTriggerCommandClass() {
        return AddRemotePlayerCommand.class;
    }

    @Override
    public void onHookTrigger(final AddRemotePlayerCommand hookCommand) {

        //get player that joined
        final RemotePlayer joinedPlayer = hookCommand.getAddedPlayer();

        //create player data for joined player
        final PlayerData joinedPlayerData = new PlayerData(joinedPlayer.getGameIndex(),
                joinedPlayer.getPileIndex(),joinedPlayer.getPlayerMetaData());

        //get all other players that have been already in the game
        final List<IPlayer> otherPlayers = hookCommand.getGameSession().getPlayers();

        //notify other players that are already in the game that
        //new remote player have joined the game
        for (final IPlayer otherPlayer : otherPlayers) {
            //we don't need to notify bots , only remote players
            //that are different from the one that have joined
            if (!(otherPlayer instanceof RemotePlayer) ||
                    (joinedPlayer.getGameIndex() == otherPlayer.getGameIndex()))
                continue;

            //obtain remote client from the added player
            final IRemoteClient client = ((RemotePlayer) otherPlayer).getSocketClient();

            //prepare game player joined message
            final String jsonMsg = new PlayerJoinProtocolMessage(joinedPlayerData,
                    hookCommand.getGameSession().getGameRules().getTotalPlayersInGameAmount()).toJsonString();

            //send game setup message to client
            client.sendMessage(jsonMsg);
        }
    }
}
