package com.yan.durak.gamelogic.player;


import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.cards.Pile;
import com.yan.durak.gamelogic.communication.protocol.data.PlayerMetaData;
import com.yan.durak.gamelogic.game.GameSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Yan-Home on 12/30/2014.
 */
public abstract class BasePlayer implements IPlayer {

    protected final GameSession mGameSession;
    protected int mIndexInGame;
    protected int mPileIndex;
    protected final PlayerMetaData mPlayerMetaData;

    public BasePlayer(final int indexInGame, final GameSession gameSession,
                      final int pileIndex, final PlayerMetaData playerMetaData) {
        mIndexInGame = indexInGame;
        mGameSession = gameSession;
        mPileIndex = pileIndex;
        mPlayerMetaData = playerMetaData;
    }

    @Override
    public int getGameIndex() {
        return mIndexInGame;
    }

    @Override
    public int getPileIndex() {
        return mPileIndex;
    }

    /**
     * Brings together cards from player hand that can be throwed in.
     *
     * @param allowedRanksToThrowIn all ranks that can be throwed in.
     * @return array of cards.
     */
    public List<Card> getPossibleThrowInCards(final Collection<String> allowedRanksToThrowIn) {

        final List<Card> retList = new ArrayList<>();
        final Pile playerPile = mGameSession.getPilesStack().get(getPileIndex());

        //just simply add every possible rank
        for (final Card cardInHand : playerPile.getCardsInPile()) {
            if (allowedRanksToThrowIn.contains(cardInHand.getRank())) {
                retList.add(cardInHand);
            }
        }

        return retList;
    }

    @Override
    public PlayerMetaData getPlayerMetaData() {
        return mPlayerMetaData;
    }
}
