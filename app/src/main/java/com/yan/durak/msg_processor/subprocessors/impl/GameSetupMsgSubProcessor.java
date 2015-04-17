package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.gamelogic.communication.protocol.messages.GameSetupProtocolMessage;
import com.yan.durak.msg_processor.MsgProcessor;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.nodes.CardNode;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by ybra on 17/04/15.
 */
public class GameSetupMsgSubProcessor extends BaseMsgSubProcessor<GameSetupProtocolMessage> {

    public GameSetupMsgSubProcessor(MsgProcessor mMsgProcessor) {
        super(mMsgProcessor);
    }

    @Override
    public void processMessage(GameSetupProtocolMessage serverMessage) {
        //FIXME : Do not store this info on the screen
        //rather make some kind of "player game profile" or a "game session" to store the info there
        mMsgProcessor.getPrototypeGameScreen().getGameSession().setMyGameIndex(serverMessage.getMessageData().getMyPlayerData().getPlayerIndexInGame());

        //depending on my player index we need to identify indexes of all players
        int bottomPlayerPileIndex = serverMessage.getMessageData().getMyPlayerData().getPlayerPileIndex();
        int topPlayerToTheRightPileIndex = (bottomPlayerPileIndex + 1);
        int topLeftPlayerToTheLeftPileIndex = (bottomPlayerPileIndex + 2);

        //correct other players positions
        if ((topPlayerToTheRightPileIndex / 5) > 0)
            topPlayerToTheRightPileIndex = (topPlayerToTheRightPileIndex % 5) + 2;

        if ((topLeftPlayerToTheLeftPileIndex / 5) > 0)
            topLeftPlayerToTheLeftPileIndex = (topLeftPlayerToTheLeftPileIndex % 5) + 2;

        //TODO :load all pile indexes from server ?
        mMsgProcessor.getPrototypeGameScreen().getCardsScreenFragment().setPilesIndexes(0, 1, bottomPlayerPileIndex, topPlayerToTheRightPileIndex, topLeftPlayerToTheLeftPileIndex);

        //extract trump card
        CardData trumpCardData = serverMessage.getMessageData().getTrumpCard();

        //FIXME : Do not store this info on the fragment
        //Rather on some kind of "game session object"
        mMsgProcessor.getPrototypeGameScreen().getCardsScreenFragment().setTrumpCard(new Card(trumpCardData.getRank(), trumpCardData.getSuit()));
        mMsgProcessor.getPrototypeGameScreen().getCardsScreenFragment().layoutNodes(mMsgProcessor.getPrototypeGameScreen().getRenderer().getSurfaceSize());

        //we need to position a card that behaves as a mask for the stock pile
        positionMaskCard(trumpCardData);

        //FIXME : Do not store this info on the fragment
        //Rather on some kind of "game session object"
        //set the suit of the trump on the hud to be visible even when cards are gone
        mMsgProcessor.getPrototypeGameScreen().getHudNodesFragment().setTrumpSuit(trumpCardData.getSuit());
    }


    public void positionMaskCard(CardData trumpCardNode) {

        Card trumpCard = new Card(trumpCardNode.getRank(), trumpCardNode.getSuit());

        //position the mask at the same place with the stock pile cards
        Collection<Card> cardsInStockPile = mMsgProcessor.getPrototypeGameScreen().getCardsScreenFragment().getCardsInPileWithIndex(0);
        Iterator<Card> iterator = cardsInStockPile.iterator();
        Card randomCardInStockPile = iterator.next();
        if (trumpCard.equals(randomCardInStockPile)) {
            randomCardInStockPile = iterator.next();
        }
        CardNode randomCardNodeInStockPile = mMsgProcessor.getPrototypeGameScreen().getCardsScreenFragment().getCardToNodesMap().get(randomCardInStockPile);

        //position the stock pile mask
        mMsgProcessor.getPrototypeGameScreen().getHudNodesFragment().setMaskCardTransform(
                randomCardNodeInStockPile.getPosition().getX(), randomCardNodeInStockPile.getPosition().getY(),
                randomCardNodeInStockPile.getSize().getX(), randomCardNodeInStockPile.getSize().getY(),
                randomCardNodeInStockPile.getRotationZ());
    }
}
