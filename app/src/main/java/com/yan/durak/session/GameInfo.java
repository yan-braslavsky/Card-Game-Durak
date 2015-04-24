package com.yan.durak.session;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.session.states.ActivePlayerState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ybra on 17/04/15.
 * <p/>
 * PURPOSE :
 * Holds an information regarding current game state.
 */
public class GameInfo {


    /**
     * Used to save expansion level of player cards during dragging
     */
    private float mDraggingCardExpansionLevel;

    public enum Player {
        BOTTOM_PLAYER, TOP_RIGHT_PLAYER, TOP_LEFT_PLAYER;
    }

    /**
     * The index of the game on server for active player
     */
    private int bottomPlayerGameIndex;

    /**
     * Cards that can be thrown in at this turn
     */
    private ArrayList<CardData> mThrowInPossibleCards;

    /**
     * Cards that still should be retaliated
     */
    private HashMap<Card, Card> mCardsPendingRetaliationMap;

    /**
     * Cards that active player selected to throw in
     */
    private ArrayList<Card> mSelectedThrowInCards;

    private final Map<Integer, Player> mIndexToPlayerMap;

    /**
     * Active player can be in several states
     */
    private ActivePlayerState mActivePlayerState;

    private Card mTrumpCard;

    public GameInfo() {
        mCardsPendingRetaliationMap = new HashMap<>();
        mSelectedThrowInCards = new ArrayList<>();
        mThrowInPossibleCards = new ArrayList<>();
        bottomPlayerGameIndex = -1;

        //by default player is not active unless the state changes
        mActivePlayerState = mActivePlayerState.OTHER_PLAYER_TURN;
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

    public ActivePlayerState getmActivePlayerState() {
        return mActivePlayerState;
    }

    public void setmActivePlayerState(ActivePlayerState mActivePlayerState) {
        this.mActivePlayerState = mActivePlayerState;
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

    public float getDraggingCardExpansionLevel() {
        return mDraggingCardExpansionLevel;
    }

    public void setDraggingCardExpansionLevel(float draggingCardExpansionLevel) {
        this.mDraggingCardExpansionLevel = draggingCardExpansionLevel;
    }
}
