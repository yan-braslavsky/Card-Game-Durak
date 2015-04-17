package com.yan.durak.session;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ybra on 17/04/15.
 * <p/>
 * PURPOSE :
 * Holds an information regarding current game state.
 */
public class GameSession {

    private int myGameIndex;

    private boolean mRequestThrowIn;
    private boolean mCardForAttackRequested;
    private boolean mRequestedRetaliation;

    private ArrayList<CardData> mThrowInPossibleCards;
    private HashMap<Card, Card> mCardsPendingRetaliationMap;
    private ArrayList<Card> mSelectedThrowInCards;

    public GameSession() {
        mCardsPendingRetaliationMap = new HashMap<>();
        mSelectedThrowInCards = new ArrayList<>();
        mThrowInPossibleCards = new ArrayList<>();
        myGameIndex = -1;
        mRequestThrowIn = false;
        mCardForAttackRequested = false;
        mRequestedRetaliation = false;
    }

    public int getMyGameIndex() {
        return myGameIndex;
    }

    public void setMyGameIndex(int myGameIndex) {
        this.myGameIndex = myGameIndex;
    }

    public boolean isRequestThrowIn() {
        return mRequestThrowIn;
    }

    public void setRequestThrowIn(boolean requestThrowIn) {
        mRequestThrowIn = requestThrowIn;
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

    public boolean isCardForAttackRequested() {
        return mCardForAttackRequested;
    }

    public void setCardForAttackRequested(boolean cardForAttackRequested) {
        mCardForAttackRequested = cardForAttackRequested;
    }

    public boolean isRequestedRetaliation() {
        return mRequestedRetaliation;
    }

    public void setRequestedRetaliation(boolean requestedRetaliation) {
        mRequestedRetaliation = requestedRetaliation;
    }
}
