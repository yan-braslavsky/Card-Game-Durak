package com.yan.durak.msg_processor.states;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.gamelogic.communication.protocol.data.RetaliationSetData;
import com.yan.durak.gamelogic.communication.protocol.messages.CardMovedProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.GameOverProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.GameSetupProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.PlayerTakesActionMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestCardForAttackMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestRetaliatePilesMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestThrowInsMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RetaliationInvalidProtocolMessage;
import com.yan.durak.input.cards.states.CardsTouchProcessorMultipleChoiceState;
import com.yan.durak.msg_processor.MsgProcessor;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.screen_fragments.hud.IHudScreenFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ybra on 16/04/15.
 */
public class MsgProcessorDefaultState extends MsgProcessorBaseState {

    public MsgProcessorDefaultState(MsgProcessor mMsgProcessor) {
        super(mMsgProcessor);
    }

    @Override
    public void handleCardMoveMessage(CardMovedProtocolMessage serverMessage) {
        //extract data
        Card movedCard = new Card(serverMessage.getMessageData().getMovedCard().getRank(), serverMessage.getMessageData().getMovedCard().getSuit());
        int fromPile = serverMessage.getMessageData().getFromPileIndex();
        int toPile = serverMessage.getMessageData().getToPileIndex();

        //FIXME : Do not delegate this logic to the fragment
        //Rather make the logic here ...
        //execute the move
        mMsgProcessor.getPrototypeGameScreen().getCardsScreenFragment().moveCardFromPileToPile(movedCard, fromPile, toPile);
    }

    @Override
    public void handleRequestCardForAttackMessage(RequestCardForAttackMessage serverMessage) {

        //FIXME : Do not store this info on the screen
        //rather transition to other processor state
        mMsgProcessor.getPrototypeGameScreen().setCardForAttackRequested(true);
    }

    @Override
    public void handleRequestRetaliatePilesMessage(RequestRetaliatePilesMessage serverMessage) {

        //FIXME : Do not store this info on the screen
        //rather transition to other processor state
        mMsgProcessor.getPrototypeGameScreen().setRequestedRetaliation(true);
        mMsgProcessor.getPrototypeGameScreen().getCardsPendingRetaliationMap().clear();

        for (List<CardData> cardDataList : serverMessage.getMessageData().getPilesBeforeRetaliation()) {
            for (CardData cardData : cardDataList) {
                mMsgProcessor.getPrototypeGameScreen().getCardsPendingRetaliationMap().put(new Card(cardData.getRank(), cardData.getSuit()), null);
            }
        }

        //in that case we want the hud to present us with option to take the card
        mMsgProcessor.getPrototypeGameScreen().getHudNodesFragment().showTakeButton();
    }

    @Override
    public void handleGameSetupMessage(GameSetupProtocolMessage serverMessage) {

        //FIXME : Do not store this info on the screen
        //rather make some kind of "player game profile" or a "game session" to store the info there
        mMsgProcessor.getPrototypeGameScreen().setMyGameIndex(serverMessage.getMessageData().getMyPlayerData().getPlayerIndexInGame());

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
        mMsgProcessor.getPrototypeGameScreen().positionMaskCard(trumpCardData);

        //FIXME : Do not store this info on the fragment
        //Rather on some kind of "game session object"
        //set the suit of the trump on the hud to be visible even when cards are gone
        mMsgProcessor.getPrototypeGameScreen().getHudNodesFragment().setTrumpSuit(trumpCardData.getSuit());
    }

    @Override
    public void handlePlayerTakesActionMessage(PlayerTakesActionMessage serverMessage) {

        int actionPlayerIndex = serverMessage.getMessageData().getPlayerIndex();

        //since we don't have reference to players indexes in the game
        //we translating the player index to pile index
        int actionPlayerPileIndex = (actionPlayerIndex + 2) % 5;
        @IHudScreenFragment.HudNode int cockPosition = IHudScreenFragment.COCK_BOTTOM_RIGHT_INDEX;
        if (actionPlayerPileIndex == mMsgProcessor.getPrototypeGameScreen().getCardsScreenFragment().getBottomPlayerPileIndex()) {
            cockPosition = IHudScreenFragment.COCK_BOTTOM_RIGHT_INDEX;
        } else if (actionPlayerPileIndex == mMsgProcessor.getPrototypeGameScreen().getCardsScreenFragment().getTopRightPlayerPileIndex()) {
            cockPosition = IHudScreenFragment.COCK_TOP_RIGHT_INDEX;
        } else if (actionPlayerPileIndex == mMsgProcessor.getPrototypeGameScreen().getCardsScreenFragment().getTopLeftPlayerPileIndex()) {
            cockPosition = IHudScreenFragment.COCK_TOP_LEFT_INDEX;
        }
        mMsgProcessor.getPrototypeGameScreen().getHudNodesFragment().resetCockAnimation(cockPosition);
    }

    @Override
    public void handleInvalidRetaliationMessage(RetaliationInvalidProtocolMessage serverMessage) {

        //TODO : cache the value for efficiency
        ArrayList<Card> cardsToRemoveTagFrom = new ArrayList<>();

        //remove from map all invalid retaliations
        for (RetaliationSetData retaliationSetData : serverMessage.getMessageData().getInvalidRetaliationsList()) {

            Card coveredCard = new Card(retaliationSetData.getCoveredCardData().getRank(), retaliationSetData.getCoveredCardData().getSuit());
            Card coveringCard = new Card(retaliationSetData.getCoveringCardData().getRank(), retaliationSetData.getCoveringCardData().getSuit());

            //FIXME : Do not keep that info in the screen rather on the state
            mMsgProcessor.getPrototypeGameScreen().getCardsPendingRetaliationMap().remove(coveredCard);

            cardsToRemoveTagFrom.add(coveredCard);
            cardsToRemoveTagFrom.add(coveringCard);
        }

        for (Card card : cardsToRemoveTagFrom) {
            CardNode cardNode = mMsgProcessor.getPrototypeGameScreen().getCardsScreenFragment().getCardToNodesMap().get(card);
            cardNode.removeTag(CardNode.TAG_TEMPORALLY_COVERED);
        }

        mMsgProcessor.getPrototypeGameScreen().layoutBottomPlayerCards();
    }

    @Override
    public void handleRequestThrowInsMessageMessage(RequestThrowInsMessage serverMessage) {

        //FIXME : That entire method requires rewriting

        //we attaching finish button to screen
        //player can finish with his throw ins any time by pressing the button
        mMsgProcessor.getPrototypeGameScreen().getHudNodesFragment().showBitoButton();

        //FIXME : Do not keep that info in the screen rather on the state
        mMsgProcessor.getPrototypeGameScreen().setRequestThrowIn(true);
        mMsgProcessor.getPrototypeGameScreen().setThrowInCardsAllowed(serverMessage.getMessageData().getPossibleThrowInCards().size());

        //TODO : Make more efficient !
        ArrayList<CardNode> availableCards = new ArrayList<>();
        for (CardData cardData : serverMessage.getMessageData().getPossibleThrowInCards()) {
            for (CardNode playerCardNode : mMsgProcessor.getPrototypeGameScreen().getCardsScreenFragment().getBottomPlayerCardNodes()) {
                if (cardData.getRank().equals(playerCardNode.getCard().getRank()) && cardData.getSuit().equals(playerCardNode.getCard().getSuit())) {
                    availableCards.add(playerCardNode);
                }
            }
        }

        //FIXME : Something ugly is going on here...
        mMsgProcessor.getPrototypeGameScreen().setThrowInInputProcessorState(new CardsTouchProcessorMultipleChoiceState(mMsgProcessor.getPrototypeGameScreen().getCardsTouchProcessor(), availableCards));
        mMsgProcessor.getPrototypeGameScreen().getCardsTouchProcessor().setCardsTouchProcessorState(mMsgProcessor.getPrototypeGameScreen().getThrowInInputProcessorState());

        //FIXME : Do not keep that info in the screen rather on the state
        mMsgProcessor.getPrototypeGameScreen().getSelectedThrowInCards().clear();
        mMsgProcessor.getPrototypeGameScreen().getThrowInPossibleCards().clear();
        mMsgProcessor.getPrototypeGameScreen().getThrowInPossibleCards().addAll(serverMessage.getMessageData().getPossibleThrowInCards());

    }

    @Override
    public void handleGameOverMessage(GameOverProtocolMessage serverMessage) {
        boolean iLostTheGame = (mMsgProcessor.getPrototypeGameScreen().getMyGameIndex() == serverMessage.getMessageData().getLoosingPlayer().getPlayerIndexInGame());
        if (iLostTheGame) {
            mMsgProcessor.getPrototypeGameScreen().getHudNodesFragment().showYouLooseMessage();
        } else {
            mMsgProcessor.getPrototypeGameScreen().getHudNodesFragment().showYouWonMessage();
        }
    }
}
