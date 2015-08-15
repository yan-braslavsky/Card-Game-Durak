package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.communication.sender.GameServerMessageSender;
import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestRetaliatePilesMessage;
import com.yan.durak.models.PileModel;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.services.PlayerMoveService;
import com.yan.durak.services.hud.HudManagementService;
import com.yan.durak.services.PileLayouterManagerService;
import com.yan.durak.services.PileManagerService;
import com.yan.durak.session.GameInfo;
import com.yan.durak.session.states.impl.AttackState;
import com.yan.durak.session.states.impl.OtherPlayerTurnState;
import com.yan.durak.session.states.impl.RetaliationState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import glengine.yan.glengine.nodes.YANButtonNode;
import glengine.yan.glengine.service.ServiceLocator;
import glengine.yan.glengine.util.object_pool.YANObjectPool;

/**
 * Created by ybra on 17/04/15.
 */
public class RequestRetaliatePilesMsgSubProcessor extends BaseMsgSubProcessor<RequestRetaliatePilesMessage> {

    private final GameInfo mGameInfo;

    public RequestRetaliatePilesMsgSubProcessor(final GameInfo gameInfo, final GameServerMessageSender messageSender) {
        super();
        mGameInfo = gameInfo;

        //take button can be used to take all the field piles to player's hand
        setupTakeButton(gameInfo);
    }

    private void setupTakeButton(final GameInfo gameInfo) {
        //take click listener is cached by the hud fragment , so no need to cache it locally
        ServiceLocator.locateService(HudManagementService.class).setTakeButtonClickListener(new YANButtonNode.YanButtonNodeClickListener() {
            @Override
            public void onButtonClick() {
                ServiceLocator.locateService(PlayerMoveService.class).makePlayerTakesCardsMove();
            }
        });
    }

    @Override
    public void processMessage(final RequestRetaliatePilesMessage serverMessage) {

        if (!(mGameInfo.getActivePlayerState() instanceof RetaliationState))
            throw new IllegalStateException("Currently game must be at Retaliation state , but was at " + mGameInfo.getActivePlayerState());

        final PileManagerService pileManager = ServiceLocator.locateService(PileManagerService.class);

        final RetaliationState retaliationState = (RetaliationState) mGameInfo.getActivePlayerState();
        retaliationState.resetState();

        //retaliation set should be clean at this point
        final List<RetaliationState.RetaliationSet> pendingRetaliationSets = retaliationState.getPendingRetaliationCardSets();
        final List<RetaliationState.RetaliationSet> alreadyRetaliatedSets = retaliationState.getRetaliatedCardSets();

        //each list/pile should contain only one card that is pending retaliation
        for (final List<CardData> cardDatas : serverMessage.getMessageData().getPilesBeforeRetaliation()) {

            //find the relevant field card
            final PileModel fieldPile = pileManager.findFieldPileWithCardByRankAndSuit(cardDatas.get(0).getRank(), cardDatas.get(0).getSuit());

            //obtain retaliation set
            final RetaliationState.RetaliationSet retSet = YANObjectPool.getInstance().obtain(RetaliationState.RetaliationSet.class);

            //server returns us all the piles that are on the field currently.
            //but in case we have already retaliated some piles , and getting this request due to invalid retaliation
            //we need to add this retaliated pile to already retaliated set
            if (fieldPile.getCardsInPile().size() == 2) {
                final Iterator<Card> iterator = fieldPile.getCardsInPile().iterator();
                retSet.setCoveredCard(iterator.next());
                retSet.setCoveringCard(iterator.next());
                alreadyRetaliatedSets.add(retSet);
                continue;
            }

            final Card pendingCard = fieldPile
                    .findCardByRankAndSuit(cardDatas.get(0).getRank(), cardDatas.get(0).getSuit());

            //add the card as a covered that waiting retaliation
            retSet.setCoveredCard(pendingCard);

            //add set to pending retaliation sets
            pendingRetaliationSets.add(retSet);
        }

        //raise take button
        ServiceLocator.locateService(HudManagementService.class).showTakeButton();

    }
}