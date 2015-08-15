package com.yan.durak.session;

import com.yan.durak.activities.GameActivity;
import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.session.states.IActivePlayerState;
import com.yan.durak.session.states.impl.OtherPlayerTurnState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import glengine.yan.glengine.service.IService;
import glengine.yan.glengine.util.object_pool.YANObjectPool;

/**
 * Created by ybra on 17/04/15.
 * <p/>
 * PURPOSE :
 * Holds an information regarding current game state.
 */
public class GameInfo implements IService {

    /**
     * A representation of player location in the game
     */
    public enum PlayerLocation {
        BOTTOM_PLAYER, TOP_RIGHT_PLAYER, TOP_LEFT_PLAYER
    }

    /**
     * Cards that can be thrown in at this turn
     */
    private ArrayList<CardData> mThrowInPossibleCards;

    /**
     * Cards that active player selected to throw in
     */
    private ArrayList<Card> mSelectedThrowInCards;

    /**
     * Used to map between player and info related to player
     */
    private final HashMap<PlayerLocation, PlayerInfo> mPlayerToPlayerInfoMap;

    /**
     * Used to map between index of the player to player enum
     */
    private final Map<Integer, PlayerLocation> mIndexToPlayerMap;

    /**
     * Active player can be in several states
     */
    private IActivePlayerState mActivePlayerState;

    /**
     * The trump card used in the game
     */
    private Card mTrumpCard;



    public GameInfo() {

        mSelectedThrowInCards = new ArrayList<>();
        mThrowInPossibleCards = new ArrayList<>();

        //by default player is not active unless the state changes
        mActivePlayerState = YANObjectPool.getInstance().obtain(OtherPlayerTurnState.class);
        mIndexToPlayerMap = new HashMap<>(3);
        mPlayerToPlayerInfoMap = new HashMap<>(3);
    }


    public void setTrumpCard(final Card trumpCard) {
        mTrumpCard = trumpCard;
    }

    public Card getTrumpCard() {
        return mTrumpCard;
    }

    public void setGameIndexForPlayer(final PlayerLocation player, final int indexInGame) {
        mIndexToPlayerMap.put(indexInGame, player);
    }

    public IActivePlayerState getActivePlayerState() {
        return mActivePlayerState;
    }

    public void setActivePlayerState(final IActivePlayerState activePlayerState) {
        //return previous state to the pool
        YANObjectPool.getInstance().offer(mActivePlayerState);
        activePlayerState.resetState();

        if (mActivePlayerState.getStateDefinition().equals(activePlayerState.getStateDefinition())) {
            //We don't want to set the same state over and over again
            return;
        }

        mActivePlayerState = activePlayerState;
        mActivePlayerState.applyState();
    }

    public PlayerLocation getPlayerForIndex(final int playerIndex) {
        return mIndexToPlayerMap.get(playerIndex);
    }

    public PlayerInfo getPlayerInfoForPlayer(final PlayerLocation player) {
        return mPlayerToPlayerInfoMap.get(player);
    }

    public void setPlayerInfoForPlayer(final PlayerLocation player, final PlayerInfo playerInfo) {
        mPlayerToPlayerInfoMap.put(player, playerInfo);
    }

    public int getPlayerIndex(final PlayerLocation player) {

        for (final Map.Entry<Integer, PlayerLocation> entry : mIndexToPlayerMap.entrySet()) {
            if (player == entry.getValue())
                return entry.getKey();
        }

        return -1;
    }

    @Override
    public void clearServiceData() {
        //Does Nothing
    }

    /**
     * Represents a meta data for the player.
     * His stats , name , avatars etc.
     */
    public static class PlayerInfo {
        private final String mAvatarImageName;
        private final String mPlayerName;

        public PlayerInfo(final String avatarImageName, final String playerName) {
            mAvatarImageName = avatarImageName;
            mPlayerName = playerName;
        }

        public String getAvatarImageResource() {
            return mAvatarImageName;
        }

        public String getPlayerName() {
            return mPlayerName;
        }
    }
}