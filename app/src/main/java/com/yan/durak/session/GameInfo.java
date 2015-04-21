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
public class GameInfo {

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

    /**
     * Active player can be in several states
     */
    private ActivePlayerState mActivePlayerState;
    private int mTopLeftPlayerPileIndex;
    private int mTopRightPlayerPileIndex;
    private int mBottomPlayerPileIndex;
    private Card mTrumpCard;

    public GameInfo() {
        mCardsPendingRetaliationMap = new HashMap<>();
        mSelectedThrowInCards = new ArrayList<>();
        mThrowInPossibleCards = new ArrayList<>();
        bottomPlayerGameIndex = -1;

        //by default player is not active unless the state changes
        mActivePlayerState = mActivePlayerState.OTHER_PLAYER_TURN;
    }

    public int getBottomPlayerGameIndex() {
        return bottomPlayerGameIndex;
    }

    public void setBottomPlayerGameIndex(int activePlayerGameIndex) {
        this.bottomPlayerGameIndex = activePlayerGameIndex;
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


//    public void setTopLeftPlayerPileIndex(int topLeftPlayerPileIndex) {
//        mTopLeftPlayerPileIndex = topLeftPlayerPileIndex;
//    }
//
//    public int getTopLeftPlayerPileIndex() {
//        return mTopLeftPlayerPileIndex;
//    }
//
//    public void setTopRightPlayerPileIndex(int topRightPlayerPileIndex) {
//        mTopRightPlayerPileIndex = topRightPlayerPileIndex;
//    }
//
//    public int getTopRightPlayerPileIndex() {
//        return mTopRightPlayerPileIndex;
//    }
//
//    public void setBottomPlayerPileIndex(int bottomPlayerPileIndex) {
//        mBottomPlayerPileIndex = bottomPlayerPileIndex;
//    }
//
//    public int getBottomPlayerPileIndex() {
//        return mBottomPlayerPileIndex;
//    }

    public void setTrumpCard(Card trumpCard) {
        mTrumpCard = trumpCard;
    }

    public Card getTrumpCard() {
        return mTrumpCard;
    }



//    public int getStockPileIndex() {
//        return 0;
//    }


}
