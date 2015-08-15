package com.yan.durak.gamelogic.commands.hooks.notifiers.broadcast;


import com.yan.durak.gamelogic.commands.composite.FinishGameCommand;
import com.yan.durak.gamelogic.commands.hooks.CommandHook;
import com.yan.durak.gamelogic.communication.connection.IRemoteClient;
import com.yan.durak.gamelogic.communication.protocol.data.PlayerData;
import com.yan.durak.gamelogic.communication.protocol.messages.GameOverProtocolMessage;
import com.yan.durak.gamelogic.player.IPlayer;
import com.yan.durak.gamelogic.player.RemotePlayer;

/**
 * Created by Yan-Home on 12/24/2014.
 * <p/>
 * This can be used as a post hook , to notify clients of game over results.
 */
public class GameOverBroadcastHook implements CommandHook<FinishGameCommand> {

    @Override
    public Class<FinishGameCommand> getHookTriggerCommandClass() {
        return FinishGameCommand.class;
    }

    @Override
    public void onHookTrigger(final FinishGameCommand hookCommand) {

        //get loosing player data
        final int loosingPlayerIndex = hookCommand.getLoosingPlayerIndex();
        final IPlayer loosingPlayer = hookCommand.getGameSession().getPlayers().get(loosingPlayerIndex);
        final int loosingPlayerPileIndex = loosingPlayer.getPileIndex();

        //create json string from the message
        final String jsonMsg = new GameOverProtocolMessage(new PlayerData(loosingPlayerIndex, loosingPlayerPileIndex,
                loosingPlayer.getPlayerMetaData())).toJsonString();

        for (final IPlayer player : hookCommand.getGameSession().getPlayers()) {
            if (player instanceof RemotePlayer) {
                final RemotePlayer remotePlayer = (RemotePlayer) player;
                final IRemoteClient client = remotePlayer.getSocketClient();
                client.sendMessage(jsonMsg);
            }
        }
    }
}
