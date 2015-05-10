package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.communication.sender.GameServerMessageSender;
import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestThrowInsMessage;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.service.ServiceLocator;
import com.yan.durak.service.services.CardNodesManagerService;
import com.yan.durak.service.services.HudManagementService;
import com.yan.durak.service.services.PileLayouterManagerService;
import com.yan.durak.service.services.PileManagerService;
import com.yan.durak.session.GameInfo;
import com.yan.durak.session.states.impl.OtherPlayerTurnState;
import com.yan.durak.session.states.impl.ThrowInState;

import java.util.ArrayList;

import glengine.yan.glengine.nodes.YANButtonNode;
import glengine.yan.glengine.util.object_pool.YANObjectPool;

/**
 * Created by ybra on 17/04/15.
 */
public class RequestThrowInsMsgSubProcessor extends BaseMsgSubProcessor<RequestThrowInsMessage> {


    private final GameServerMessageSender mMessageSender;

    public RequestThrowInsMsgSubProcessor(final GameServerMessageSender messageSender) {
        super();
        this.mMessageSender = messageSender;

        //setup finish button click listener
        setupFinishButton();
    }

    private void setupFinishButton() {
        //click listener is cached by the hud fragment , so no need to cache it locally
        ServiceLocator.locateService(HudManagementService.class).setFinishButtonClickListener(new YANButtonNode.YanButtonNodeClickListener() {

            @Override
            public void onButtonClick() {
                //hide the button
                ServiceLocator.locateService(HudManagementService.class).hideFinishButton();

                //cache services
                PileManagerService pileManagerService = ServiceLocator.locateService(PileManagerService.class);
                CardNodesManagerService cardNodesManagerService = ServiceLocator.locateService(CardNodesManagerService.class);

                //get throw in state
                ThrowInState throwInState = (ThrowInState) ServiceLocator.locateService(GameInfo.class).getActivePlayerState();

                //enable all disabled cards
                ArrayList<Card> allowedCardsToThrowIn = throwInState.getAllowedCardsToThrowIn();
                for (Card cardInPile : pileManagerService.getBottomPlayerPile().getCardsInPile()) {
                    //in case this cards is not allowed currently to be thrown in
                    if (!allowedCardsToThrowIn.contains(cardInPile)) {
                        //enable the node back
                        CardNode cardNode = cardNodesManagerService.getCardNodeForCard(cardInPile);
                        cardNodesManagerService.enableCardNode(cardNode);
                    }
                }

                //disable the hand of the player by setting another state
                ServiceLocator.locateService(GameInfo.class).setActivePlayerState(YANObjectPool.getInstance().obtain(OtherPlayerTurnState.class));

                //layout the bottom player pile
                ServiceLocator.locateService(PileLayouterManagerService.class).getPileLayouterForPile(pileManagerService.getBottomPlayerPile()).layout();

                //send the response to server
                ServiceLocator.locateService(GameServerMessageSender.class).sendThrowInResponse(throwInState.getChosenCardsToThrowIn());
            }
        });
    }

    @Override
    public void processMessage(RequestThrowInsMessage serverMessage) {

        //Set the state to throw in
        ServiceLocator.locateService(GameInfo.class).setActivePlayerState(YANObjectPool.getInstance().obtain(ThrowInState.class));

        PileManagerService pileManagerService = ServiceLocator.locateService(PileManagerService.class);
        ThrowInState throwInState = (ThrowInState) ServiceLocator.locateService(GameInfo.class).getActivePlayerState();
        CardNodesManagerService cardNodesManager = ServiceLocator.locateService(CardNodesManagerService.class);

        //cache state cards array
        ArrayList<Card> allowedCardsToThrowIn = throwInState.getAllowedCardsToThrowIn();

        //set max cards to throw in
        throwInState.setMaxCardsToThrowInAmount(serverMessage.getMessageData().getMaxThrowInCardsAmount());

        //fill allowed cards for throw in state
        for (CardData cardData : serverMessage.getMessageData().getPossibleThrowInCards()) {
            Card card = pileManagerService.getBottomPlayerPile().findCardByRankAndSuit(cardData.getRank(), cardData.getSuit());
            allowedCardsToThrowIn.add(card);
        }

        //deactivate node cards that are not allowed to be thrown in
        for (Card cardInPile : pileManagerService.getBottomPlayerPile().getCardsInPile()) {

            //in case this cards is not allowed currently to be thrown in
            if (!allowedCardsToThrowIn.contains(cardInPile)) {

                //disable the node
                CardNode cardNode = cardNodesManager.getCardNodeForCard(cardInPile);
                cardNodesManager.disableCardNode(cardNode);
            }
        }

        //raise finish button
        ServiceLocator.locateService(HudManagementService.class).showFinishButton();
    }
}
