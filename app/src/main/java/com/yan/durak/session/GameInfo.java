package com.yan.durak.session;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.service.IService;
import com.yan.durak.session.states.IActivePlayerState;
import com.yan.durak.session.states.impl.OtherPlayerTurnState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import glengine.yan.glengine.util.object_pool.YANObjectPool;

/**
 * Created by ybra on 17/04/15.
 * <p/>
 * PURPOSE :
 * Holds an information regarding current game state.
 */
public class GameInfo implements IService {

    public enum Player {
        BOTTOM_PLAYER, TOP_RIGHT_PLAYER, TOP_LEFT_PLAYER;
    }

    /**
     * Cards that can be thrown in at this turn
     */
    private ArrayList<CardData> mThrowInPossibleCards;

    /**
     * Cards that active player selected to throw in
     */
    private ArrayList<Card> mSelectedThrowInCards;


    private final Map<Integer, Player> mIndexToPlayerMap;

    /**
     * Active player can be in several states
     */
    private IActivePlayerState mActivePlayerState;

    private Card mTrumpCard;

    public GameInfo() {

        mSelectedThrowInCards = new ArrayList<>();
        mThrowInPossibleCards = new ArrayList<>();

        //by default player is not active unless the state changes
        mActivePlayerState = YANObjectPool.getInstance().obtain(OtherPlayerTurnState.class);
        mIndexToPlayerMap = new HashMap<>();
    }


    public void setTrumpCard(Card trumpCard) {
        mTrumpCard = trumpCard;
    }

    public Card getTrumpCard() {
        return mTrumpCard;
    }

    public void setPlayerIndexes(int bottomPlayerIndex, int topRightPlayerIndex, int topLeftPlayerIndex) {
        mIndexToPlayerMap.put(bottomPlayerIndex, Player.BOTTOM_PLAYER);
        mIndexToPlayerMap.put(topRightPlayerIndex, Player.TOP_RIGHT_PLAYER);
        mIndexToPlayerMap.put(topLeftPlayerIndex, Player.TOP_LEFT_PLAYER);
    }

    public IActivePlayerState getActivePlayerState() {
        return mActivePlayerState;
    }

    public void setActivePlayerState(IActivePlayerState activePlayerState) {
        //return previous state to the pool
        YANObjectPool.getInstance().offer(mActivePlayerState);
        activePlayerState.resetState();
        mActivePlayerState = activePlayerState;
        mActivePlayerState.applyState();
    }

    public Player getPlayerForIndex(int playerIndex) {
        return mIndexToPlayerMap.get(playerIndex);
    }

    public int getPlayerIndex(Player player) {

        for (Map.Entry<Integer, Player> entry : mIndexToPlayerMap.entrySet()) {
            if (player == entry.getValue())
                return entry.getKey();
        }

        return -1;
    }

}