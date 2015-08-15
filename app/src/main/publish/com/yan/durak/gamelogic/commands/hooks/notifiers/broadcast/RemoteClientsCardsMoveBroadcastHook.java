package com.yan.durak.gamelogic.commands.hooks.notifiers.broadcast;


import com.yan.durak.gamelogic.commands.core.MoveCardFromPileToPileCommand;
import com.yan.durak.gamelogic.commands.hooks.CommandHook;
import com.yan.durak.gamelogic.communication.connection.IRemoteClient;
import com.yan.durak.gamelogic.communication.protocol.messages.CardMovedProtocolMessage;
import com.yan.durak.gamelogic.player.IPlayer;
import com.yan.durak.gamelogic.player.RemotePlayer;

/**
 * Created by Yan-Home on 12/24/2014.
 */
public class RemoteClientsCardsMoveBroadcastHook implements CommandHook<MoveCardFromPileToPileCommand> {

    @Override
    public Class<MoveCardFromPileToPileCommand> getHookTriggerCommandClass() {
        return MoveCardFromPileToPileCommand.class;
    }

    @Override
    public void onHookTrigger(final MoveCardFromPileToPileCommand hookCommand) {
        final String jsonMsg = new CardMovedProtocolMessage(hookCommand.getCardToMove().getRank(), hookCommand.getCardToMove().getSuit(), hookCommand.getFromPileIndex(), hookCommand.getToPileIndex()).toJsonString();

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