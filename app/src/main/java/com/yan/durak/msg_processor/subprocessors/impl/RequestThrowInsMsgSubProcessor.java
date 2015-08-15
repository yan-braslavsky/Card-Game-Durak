package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.communication.sender.GameServerMessageSender;
import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestThrowInsMessage;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.services.CardNodesManagerService;
import com.yan.durak.services.PileManagerService;
import com.yan.durak.services.PlayerMoveService;
import com.yan.durak.services.hud.HudManagementService;
import com.yan.durak.session.GameInfo;
import com.yan.durak.session.states.impl.ThrowInState;

import java.util.ArrayList;

import glengine.yan.glengine.nodes.YANButtonNode;
import glengine.yan.glengine.service.ServiceLocator;
import glengine.yan.glengine.util.object_pool.YANObjectPool;

/**
 * Created by ybra on 17/04/15.
 */
public class RequestThrowInsMsgSubProcessor extends BaseMsgSubProcessor<RequestThrowInsMessage> {


    private final GameServerMessageSender mMessageSender;

    public RequestThrowInsMsgSubProcessor(final GameServerMessageSender messageSender) {
        super();
        mMessageSender = messageSender;

        //setup finish button click listener
        setupFinishButton();
    }

    private void setupFinishButton() {
        //click listener is cached by the hud fragment , so no need to cache it locally
        ServiceLocator.locateService(HudManagementService.class).setFinishButtonClickListener(new YANButtonNode.YanButtonNodeClickListener() {

            @Override
            public void onButtonClick() {
                //use the player service to preform a throw in move
                ServiceLocator.locateService(PlayerMoveService.class).throwInWhatIsSelected();
            }
        });
    }

    @Override
    public void processMessage(final RequestThrowInsMessage serverMessage) {

        //Set the state to throw in
        ServiceLocator.locateService(GameInfo.class).setActivePlayerState(YANObjectPool.getInstance().obtain(ThrowInState.class));

        final PileManagerService pileManagerService = ServiceLocator.locateService(PileManagerService.class);
        final ThrowInState throwInState = (ThrowInState) ServiceLocator.locateService(GameInfo.class).getActivePlayerState();
        final CardNodesManagerService cardNodesManager = ServiceLocator.locateService(CardNodesManagerService.class);

        //cache state cards array
        final ArrayList<Card> allowedCardsToThrowIn = throwInState.getAllowedCardsToThrowIn();

        //set max cards to throw in
        throwInState.setMaxCardsToThrowInAmount(serverMessage.getMessageData().getMaxThrowInCardsAmount());

        //fill allowed cards for throw in state
        for (final CardData cardData : serverMessage.getMessageData().getPossibleThrowInCards()) {
            final Card card = pileManagerService.getBottomPlayerPile().findCardByRankAndSuit(cardData.getRank(), cardData.getSuit());
            allowedCardsToThrowIn.add(card);
        }

        //deactivate node cards that are not allowed to be thrown in
        for (final Card cardInPile : pileManagerService.getBottomPlayerPile().getCardsInPile()) {

            //in case this cards is not allowed currently to be thrown in
            if (!allowedCardsToThrowIn.contains(cardInPile)) {

                //disable the node
                final CardNode cardNode = cardNodesManager.getCardNodeForCard(cardInPile);
                cardNodesManager.disableCardNode(cardNode);
            }
        }

        //raise finish button
        ServiceLocator.locateService(HudManagementService.class).showFinishButton();
    }
}
