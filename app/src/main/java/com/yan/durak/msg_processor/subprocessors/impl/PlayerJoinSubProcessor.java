package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.communication.protocol.data.PlayerData;
import com.yan.durak.gamelogic.communication.protocol.messages.PlayerJoinProtocolMessage;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.services.PileManagerService;
import com.yan.durak.services.hud.HudManagementService;
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
    public void processMessage(final PlayerJoinProtocolMessage serverMessage) {
        final GameInfo gameInfo = ServiceLocator.locateService(GameInfo.class);

        //find out what index bottom player has
        final int bottomPlayerIndex = gameInfo.getPlayerIndex(GameInfo.PlayerLocation.BOTTOM_PLAYER);
        final int totalPlayersInGame = serverMessage.getMessageData().getTotalPlayersInGame();

        placePlayer(bottomPlayerIndex,serverMessage.getMessageData().getJoinedPlayerData(),totalPlayersInGame);
    }

    private void placePlayer(final int bottomPlayerIndex, final PlayerData joinedPlayer,
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

    private void placeAsTopRight(final PlayerData joinedPlayer) {
        ServiceLocator.locateService(PileManagerService.class).setTopRightPlayerPileIndex(joinedPlayer.getPlayerPileIndex());
        ServiceLocator.locateService(GameInfo.class).setGameIndexForPlayer(GameInfo.PlayerLocation.TOP_RIGHT_PLAYER, joinedPlayer.getPlayerIndexInGame());

        //set the name and avatar icon for player
        ServiceLocator.locateService(HudManagementService.class).setNameForPlayer(GameInfo.PlayerLocation.TOP_RIGHT_PLAYER, joinedPlayer.getPlayerMetaData().getPlayerNickname());
        ServiceLocator.locateService(HudManagementService.class).setIconForPlayer(GameInfo.PlayerLocation.TOP_RIGHT_PLAYER, joinedPlayer.getPlayerMetaData().getPlayerAvatarResource());
    }

    private void placeAsTopLeft(final PlayerData joinedPlayer) {
        ServiceLocator.locateService(PileManagerService.class).setTopLeftPlayerPileIndex(joinedPlayer.getPlayerPileIndex());
        ServiceLocator.locateService(GameInfo.class).setGameIndexForPlayer(GameInfo.PlayerLocation.TOP_LEFT_PLAYER, joinedPlayer.getPlayerIndexInGame());

        //set the name and avatar icon for player
        ServiceLocator.locateService(HudManagementService.class).setNameForPlayer(GameInfo.PlayerLocation.TOP_LEFT_PLAYER, joinedPlayer.getPlayerMetaData().getPlayerNickname());
        ServiceLocator.locateService(HudManagementService.class).setIconForPlayer(GameInfo.PlayerLocation.TOP_LEFT_PLAYER, joinedPlayer.getPlayerMetaData().getPlayerAvatarResource());
    }
}