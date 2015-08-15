package com.yan.durak.gamelogic.commands.hooks.notifiers.broadcast;


import com.yan.durak.gamelogic.commands.custom.PlayerRetaliationRequestCommand;
import com.yan.durak.gamelogic.commands.hooks.CommandHook;
import com.yan.durak.gamelogic.communication.connection.IRemoteClient;
import com.yan.durak.gamelogic.communication.protocol.messages.PlayerTakesActionMessage;
import com.yan.durak.gamelogic.player.Player;
import com.yan.durak.gamelogic.player.RemotePlayer;

/**
 * Created by Yan-Home on 12/24/2014.
 */
public class PlayerActionRetaliateBroadcastHook implements CommandHook<PlayerRetaliationRequestCommand> {


    @Override
    public Class<PlayerRetaliationRequestCommand> getHookTriggerCommandClass() {
        return PlayerRetaliationRequestCommand.class;
    }

    @Override
    public void onHookTrigger(PlayerRetaliationRequestCommand hookCommand) {

        //create json string from the message
        String jsonMsg = new PlayerTakesActionMessage(hookCommand.getPlayerIndex(), PlayerTakesActionMessage.PlayerAction.RETALIATION_START).toJsonString();

        for (Player player : hookCommand.getGameSession().getPlayers()) {
            if (player instanceof RemotePlayer) {
                RemotePlayer remotePlayer = (RemotePlayer) player;
                IRemoteClient client = remotePlayer.getSocketClient();
                client.sendMessage(jsonMsg);
            }
        }
    }
}