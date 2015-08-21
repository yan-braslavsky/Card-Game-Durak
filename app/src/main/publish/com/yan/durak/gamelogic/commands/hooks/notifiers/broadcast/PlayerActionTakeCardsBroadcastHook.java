package com.yan.durak.gamelogic.commands.hooks.notifiers.broadcast;


import com.yan.durak.gamelogic.commands.custom.PlayerTakesCardsFromFieldCommand;
import com.yan.durak.gamelogic.commands.hooks.CommandHook;
import com.yan.durak.gamelogic.communication.connection.IRemoteClient;
import com.yan.durak.gamelogic.communication.protocol.messages.PlayerTakesActionMessage;
import com.yan.durak.gamelogic.player.IPlayer;
import com.yan.durak.gamelogic.player.RemotePlayer;

import static com.yan.durak.gamelogic.communication.protocol.messages.PlayerTakesActionMessage.PlayerAction;
import static com.yan.durak.gamelogic.communication.protocol.messages.PlayerTakesActionMessage.PlayerAction.PLAYER_TAKES_CARDS;

/**
 * Created by Yan-Home on 12/24/2014.
 */
public class PlayerActionTakeCardsBroadcastHook implements CommandHook<PlayerTakesCardsFromFieldCommand> {

    @Override
    public Class<PlayerTakesCardsFromFieldCommand> getHookTriggerCommandClass() {
        return PlayerTakesCardsFromFieldCommand.class;
    }

    @Override
    public void onHookTrigger(final PlayerTakesCardsFromFieldCommand hookCommand) {

        //create json string from the message
        final String jsonMsg = new PlayerTakesActionMessage(hookCommand.getTakingPlayer().getGameIndex(), PLAYER_TAKES_CARDS).toJsonString();

        for (int i = 0; i < hookCommand.getGameSession().getPlayers().size(); i++) {
            final IPlayer player = hookCommand.getGameSession().getPlayers().get(i);
            if (player instanceof RemotePlayer) {
                final RemotePlayer remotePlayer = (RemotePlayer) player;
                final IRemoteClient client = remotePlayer.getSocketClient();
                client.sendMessage(jsonMsg);
            }
        }
    }
}
