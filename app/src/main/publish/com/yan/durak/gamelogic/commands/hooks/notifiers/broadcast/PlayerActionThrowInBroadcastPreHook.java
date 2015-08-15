package com.yan.durak.gamelogic.commands.hooks.notifiers.broadcast;


import com.yan.durak.gamelogic.commands.custom.PlayerThrowInRequestCommand;
import com.yan.durak.gamelogic.commands.hooks.CommandHook;
import com.yan.durak.gamelogic.communication.connection.IRemoteClient;
import com.yan.durak.gamelogic.communication.protocol.messages.PlayerTakesActionMessage;
import com.yan.durak.gamelogic.player.IPlayer;
import com.yan.durak.gamelogic.player.RemotePlayer;

/**
 * Created by Yan-Home on 12/24/2014.
 */
public class PlayerActionThrowInBroadcastPreHook implements CommandHook<PlayerThrowInRequestCommand> {

    @Override
    public Class<PlayerThrowInRequestCommand> getHookTriggerCommandClass() {
        return PlayerThrowInRequestCommand.class;
    }

    @Override
    public void onHookTrigger(final PlayerThrowInRequestCommand hookCommand) {

        //create json string from the message
        final String jsonMsg = new PlayerTakesActionMessage(hookCommand.getThrowingInPlayerIndex(), PlayerTakesActionMessage.PlayerAction.THROW_IN_START).toJsonString();

        for (final IPlayer player : hookCommand.getGameSession().getPlayers()) {
            if (player instanceof RemotePlayer) {
                final RemotePlayer remotePlayer = (RemotePlayer) player;
                final IRemoteClient client = remotePlayer.getSocketClient();
                client.sendMessage(jsonMsg);
            }
        }
    }
}
