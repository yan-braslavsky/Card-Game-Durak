package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.data.RetaliationSetData;
import com.yan.durak.gamelogic.communication.protocol.messages.RetaliationInvalidProtocolMessage;
import com.yan.durak.models.PileModel;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.services.PileLayouterManagerService;
import com.yan.durak.services.PileManagerService;

import glengine.yan.glengine.service.ServiceLocator;

/**
 * Created by ybra on 17/04/15.
 */
public class RetaliationInvalidMsgSubProcessor extends BaseMsgSubProcessor<RetaliationInvalidProtocolMessage> {

    public RetaliationInvalidMsgSubProcessor() {
        super();
    }

    @Override
    public void processMessage(RetaliationInvalidProtocolMessage serverMessage) {

        PileManagerService pileManagerService = ServiceLocator.locateService(PileManagerService.class);
        //we need to find all cards that are wrongly retaliated
        for (RetaliationSetData retaliationSetData : serverMessage.getMessageData().getInvalidRetaliationsList()) {

            PileModel fieldPile = pileManagerService.findFieldPileWithCardByRankAndSuit(retaliationSetData.getCoveringCardData().getRank(), retaliationSetData.getCoveringCardData().getSuit());
            Card coveringCard = fieldPile.findCardByRankAndSuit(retaliationSetData.getCoveringCardData().getRank(), retaliationSetData.getCoveringCardData().getSuit());

            //remove the card from field pile
            fieldPile.removeCard(coveringCard);

            //put the card to player pile
            pileManagerService.getBottomPlayerPile().addCard(coveringCard);
        }

        //layout bottom player pile
        ServiceLocator.locateService(PileLayouterManagerService.class).getPileLayouterForPile(pileManagerService.getBottomPlayerPile()).layout();
    }
}
