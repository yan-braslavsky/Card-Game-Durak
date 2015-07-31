package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.communication.protocol.data.PlayerData;
import com.yan.durak.gamelogic.communication.protocol.messages.PlayerJoinProtocolMessage;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.services.PileManagerService;
import com.yan.durak.session.GameInfo;

import glengine.yan.glengine.service.ServiceLocator;

/**
 * Created by ybra on 17/04/15.
 */
public class PlayerJoinSubProcessor extends BaseMsgSubProcessor<PlayerJoinProtocolMessage> {

    public PlayerJoinSubProcessor() {
        super();
    }

    @Override
    public void processMessage(PlayerJoinProtocolMessage serverMessage) {
        GameInfo gameInfo = ServiceLocator.locateService(GameInfo.class);

        //find out what index bottom player has
        final int bottomPlayerIndex = gameInfo.getPlayerIndex(GameInfo.Player.BOTTOM_PLAYER);
        final int totalPlayersInGame = serverMessage.getMessageData().getTotalPlayersInGame();

        placePlayer(bottomPlayerIndex,serverMessage.getMessageData().getJoinedPlayerData(),totalPlayersInGame);
    }

    private void placePlayer(int bottomPlayerIndex, PlayerData joinedPlayer,
                             final int totalPlayersInGame) {
        int topLeftPlayerIndex = bottomPlayerIndex + 1;
        int topRightPlayerIndex = bottomPlayerIndex + 2;

        //correct other players positions
        if ((topRightPlayerIndex / totalPlayersInGame) > 0)
            topRightPlayerIndex = (topRightPlayerIndex % totalPlayersInGame);

        if ((topLeftPlayerIndex / totalPlayersInGame) > 0)
            topLeftPlayerIndex = (topLeftPlayerIndex % totalPlayersInGame);

        if (joinedPlayer.getPlayerIndexInGame() == topLeftPlayerIndex) {
            placeAsTopLeft(joinedPlayer);
        } else if (joinedPlayer.getPlayerIndexInGame() == topRightPlayerIndex) {
            placeAsTopRight(joinedPlayer);
        } else
            throw new IllegalStateException("Couldn't identify player position");
    }

    private void placeAsTopRight(PlayerData joinedPlayer) {
        ServiceLocator.locateService(PileManagerService.class).setTopRightPlayerPileIndex(joinedPlayer.getPlayerPileIndex());
        ServiceLocator.locateService(GameInfo.class).setGameIndexForPlayer(GameInfo.Player.TOP_RIGHT_PLAYER, joinedPlayer.getPlayerIndexInGame());
    }

    private void placeAsTopLeft(PlayerData joinedPlayer) {
        ServiceLocator.locateService(PileManagerService.class).setTopLeftPlayerPileIndex(joinedPlayer.getPlayerPileIndex());
        ServiceLocator.locateService(GameInfo.class).setGameIndexForPlayer(GameInfo.Player.TOP_LEFT_PLAYER, joinedPlayer.getPlayerIndexInGame());
    }
}