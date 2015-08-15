package com.yan.durak.gamelogic.commands.hooks.notifiers.broadcast;


import com.yan.durak.gamelogic.commands.custom.PlayerAttackRequestCommand;
import com.yan.durak.gamelogic.commands.hooks.CommandHook;
import com.yan.durak.gamelogic.communication.connection.IRemoteClient;
import com.yan.durak.gamelogic.communication.protocol.messages.PlayerTakesActionMessage;
import com.yan.durak.gamelogic.player.Player;
import com.yan.durak.gamelogic.player.RemotePlayer;

/**
 * Created by Yan-Home on 12/24/2014.
 */
public class PlayerActionAttackBroadcastHook implements CommandHook<PlayerAttackRequestCommand> {


    @Override
    public Class<PlayerAttackRequestCommand> getHookTriggerCommandClass() {
        return PlayerAttackRequestCommand.class;
    }

    @Override
    public void onHookTrigger(PlayerAttackRequestCommand hookCommand) {

        //create json string from the message
        String jsonMsg = new PlayerTakesActionMessage(hookCommand.getAttackingPlayerIndex(), PlayerTakesActionMessage.PlayerAction.ATTACK_START).toJsonString();

        for (Player player : hookCommand.getGameSession().getPlayers()) {
            if (player instanceof RemotePlayer) {
                RemotePlayer remotePlayer = (RemotePlayer) player;
                IRemoteClient client = remotePlayer.getSocketClient();
                client.sendMessage(jsonMsg);
            }
        }
    }
}
