package com.yan.durak.gamelogic.player;


import com.google.gson.Gson;
import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.cards.Pile;
import com.yan.durak.gamelogic.communication.connection.IRemoteClient;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.gamelogic.communication.protocol.data.PlayerMetaData;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestCardForAttackMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestRetaliatePilesMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestThrowInsMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.ResponseCardForAttackMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.ResponseRetaliatePilesMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.ResponseThrowInsMessage;
import com.yan.durak.gamelogic.game.GameSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Yan-Home on 12/30/2014.
 */
public class RemotePlayer extends BasePlayer {

    private IRemoteClient mSocketClient;
    private Gson mGson;

    public RemotePlayer(final int indexInGame, final GameSession gameSession,
                        final int pileIndex, final IRemoteClient remoteClient,
                        final PlayerMetaData playerMetaData) {
        super(indexInGame, gameSession, pileIndex, playerMetaData);
        mSocketClient = remoteClient;
        mGson = new Gson();
    }

    @Override
    public Card getCardForAttack() {

        //send a message to remote client requesting a card for attack
        final List<Card> cardsInHand = mGameSession.getPilesStack().get(getPileIndex()).getCardsInPile();
        final RequestCardForAttackMessage request = new RequestCardForAttackMessage(cardsInHand);
        mSocketClient.sendMessage(request.toJsonString());

        //waiting for client response (blocking)
        final String response = mSocketClient.readMessage();

        //get the card from the response
        final Card cardForAttack = extractCardForAttackFromResponse(response);

        //TODO : validate that player has the card in hand ?

        return cardForAttack;
    }

    private Card extractCardForAttackFromResponse(final String response) {
        final ResponseCardForAttackMessage responseMessage = mGson.fromJson(response, ResponseCardForAttackMessage.class);
        final String rank = responseMessage.getMessageData().getCardForAttack().getRank();
        final String suit = responseMessage.getMessageData().getCardForAttack().getSuit();
        return new Card(rank, suit);
    }

    @Override
    public List<Pile> retaliatePiles(final List<Pile> pilesToRetaliate) {

        final List<List<Card>> pilesAsCardLists = convertToCardsList(pilesToRetaliate);

        //send a message to remote client requesting to cover piles
        final RequestRetaliatePilesMessage request = new RequestRetaliatePilesMessage(pilesAsCardLists);
        mSocketClient.sendMessage(request.toJsonString());

        //waiting for client response (blocking)
        final String response = mSocketClient.readMessage();

        //TODO : use message to assemble the new piles list
        final List<Pile> retaliatedPiles = extractRetaliatedPilesFromResponse(response);

        //TODO : validate that player has the card in hand ?

        return retaliatedPiles;
    }

    private List<List<Card>> convertToCardsList(final List<Pile> pilesToRetaliate) {

        final ArrayList<List<Card>> ret = new ArrayList<>();

        for (final Pile pile : pilesToRetaliate) {
            final List<Card> cardList = new ArrayList<>();
            for (int i = 0; i < pile.getCardsInPile().size(); i++) {
                final Card card = pile.getCardsInPile().get(i);
                cardList.add(card);
            }
            ret.add(cardList);
        }

        return ret;
    }

    @Override
    public List<Card> getThrowInCards(final Collection<String> allowedRanksToThrowIn, final int allowedAmountOfCardsToThrowIn) {

        final List<Card> possibleThrowInCards = getPossibleThrowInCards(allowedRanksToThrowIn);

        //in case there is no cards that can be possibly throwed in
        //player will not be requested to throw in
        if (possibleThrowInCards.isEmpty())
            return possibleThrowInCards;

        //send a message to remote client requesting to cover piles
        final RequestThrowInsMessage request = new RequestThrowInsMessage(possibleThrowInCards, allowedAmountOfCardsToThrowIn);
        mSocketClient.sendMessage(request.toJsonString());

        //waiting for client response (blocking)
        final String response = mSocketClient.readMessage();

        return extractThrowInsFromResponse(response);
    }

    private List<Card> extractThrowInsFromResponse(final String response) {

        final ResponseThrowInsMessage responseMessage = mGson.fromJson(response, ResponseThrowInsMessage.class);
        if (responseMessage == null) {
            //TODO : do something !
            //probably player had disconnected and must be substituted with bots
        }

        final List<CardData> selectedThrowIns = responseMessage.getMessageData().getSelectedThrowInCards();
        final List<Card> retList = new ArrayList<>();

        for (int i = 0; i < selectedThrowIns.size(); i++) {
            final CardData cardData = selectedThrowIns.get(i);
            retList.add(new Card(cardData.getRank(), cardData.getSuit()));
        }

        return retList;
    }


    private List<Pile> extractRetaliatedPilesFromResponse(final String response) {
        final ResponseRetaliatePilesMessage responseMessage = mGson.fromJson(response, ResponseRetaliatePilesMessage.class);
        if (responseMessage == null) {
            //TODO : do something !
            //probably player had disconnected and must be substituted with bots
        }

        final List<List<CardData>> pilesAfterRetaliation = responseMessage.getMessageData().getPilesAfterRetaliation();
        final List<Pile> retList = new ArrayList<>();

        for (final List<CardData> cardDataList : pilesAfterRetaliation) {
            final Pile pile = new Pile();
            for (int i = 0; i < cardDataList.size(); i++) {
                final CardData cardData = cardDataList.get(i);
                pile.addCardToPile(new Card(cardData.getRank(), cardData.getSuit()));
            }
            retList.add(pile);
        }
        return retList;
    }

    public IRemoteClient getSocketClient() {
        return mSocketClient;
    }
}
