package com.yan.durak.session;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.session.states.ActivePlayerState;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ybra on 17/04/15.
 * <p/>
 * PURPOSE :
 * Holds an information regarding current game state.
 */
public class GameSession {

    /**
     * The index of the game on server for active player
     */
    private int activePlayerGameIndex;

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

    /**
     * Active player can be in several states
     */
    private ActivePlayerState mActivePlayerState;

    public GameSession() {
        mCardsPendingRetaliationMap = new HashMap<>();
        mSelectedThrowInCards = new ArrayList<>();
        mThrowInPossibleCards = new ArrayList<>();
        activePlayerGameIndex = -1;

        //by default player is not active unless the state changes
        mActivePlayerState = mActivePlayerState.OTHER_PLAYER_TURN;
    }

    public int getActivePlayerGameIndex() {
        return activePlayerGameIndex;
    }

    public void setActivePlayerGameIndex(int activePlayerGameIndex) {
        this.activePlayerGameIndex = activePlayerGameIndex;
    }

    public ArrayList<CardData> getThrowInPossibleCards() {
        return mThrowInPossibleCards;
    }

    public HashMap<Card, Card> getCardsPendingRetaliationMap() {
        return mCardsPendingRetaliationMap;
    }

    public ArrayList<Card> getSelectedThrowInCards() {
        return mSelectedThrowInCards;
    }

    public ActivePlayerState getActivePlayerState() {
        return mActivePlayerState;
    }

    public void setActivePlayerState(ActivePlayerState activePlayerState) {
        mActivePlayerState = activePlayerState;
    }
}
