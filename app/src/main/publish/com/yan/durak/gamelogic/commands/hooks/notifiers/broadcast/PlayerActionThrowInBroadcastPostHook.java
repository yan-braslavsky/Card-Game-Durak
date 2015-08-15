package com.yan.durak.gamelogic.commands.hooks.notifiers.broadcast;


import com.yan.durak.gamelogic.commands.custom.PlayerThrowInRequestCommand;
import com.yan.durak.gamelogic.commands.hooks.CommandHook;
import com.yan.durak.gamelogic.communication.connection.IRemoteClient;
import com.yan.durak.gamelogic.communication.protocol.messages.PlayerTakesActionMessage;
import com.yan.durak.gamelogic.player.Player;
import com.yan.durak.gamelogic.player.RemotePlayer;

/**
 * Created by Yan-Home on 12/24/2014.
 */
public class PlayerActionThrowInBroadcastPostHook implements CommandHook<PlayerThrowInRequestCommand> {

    @Override
    public Class<PlayerThrowInRequestCommand> getHookTriggerCommandClass() {
        return PlayerThrowInRequestCommand.class;
    }

    @Override
    public void onHookTrigger(PlayerThrowInRequestCommand hookCommand) {

        //decide what kind of throw in action player did based on cards that were selected by player
        //if no cards were selected , means he passed his throw in turn
        PlayerTakesActionMessage.PlayerAction playerThrowInAction = hookCommand.getThrowInCards().isEmpty() ?
                PlayerTakesActionMessage.PlayerAction.THROW_IN_PASS : PlayerTakesActionMessage.PlayerAction.THROW_IN_END;

        //create json string from the message
        String jsonMsg = new PlayerTakesActionMessage(hookCommand.getThrowingInPlayerIndex(), playerThrowInAction).toJsonString();

        for (Player player : hookCommand.getGameSession().getPlayers()) {
            if (player instanceof RemotePlayer) {
                RemotePlayer remotePlayer = (RemotePlayer) player;
                IRemoteClient client = remotePlayer.getSocketClient();
                client.sendMessage(jsonMsg);
            }
        }
    }
}
