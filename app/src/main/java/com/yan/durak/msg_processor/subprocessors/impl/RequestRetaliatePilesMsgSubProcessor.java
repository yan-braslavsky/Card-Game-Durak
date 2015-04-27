package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.communication.sender.GameServerMessageSender;
import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestRetaliatePilesMessage;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.session.GameInfo;
import com.yan.durak.session.states.impl.RetaliationState;

import java.util.ArrayList;
import java.util.List;

import glengine.yan.glengine.util.object_pool.YANObjectPool;

/**
 * Created by ybra on 17/04/15.
 */
public class RequestRetaliatePilesMsgSubProcessor extends BaseMsgSubProcessor<RequestRetaliatePilesMessage> {

    private final GameServerMessageSender mMessageSender;
    private final List<List<Card>> mRetaliationList;
    private final GameInfo mGameInfo;

    public RequestRetaliatePilesMsgSubProcessor(final GameInfo gameInfo, final GameServerMessageSender messageSender) {
        super();
        this.mMessageSender = messageSender;
        this.mRetaliationList = new ArrayList<>();
        this.mGameInfo = gameInfo;
    }

    @Override
    public void processMessage(RequestRetaliatePilesMessage serverMessage) {

        if (!(mGameInfo.getActivePlayerState() instanceof RetaliationState))
            throw new IllegalStateException("Currently game must be at Retaliation state , but was at " + mGameInfo.getActivePlayerState());

        RetaliationState retaliationState = (RetaliationState) mGameInfo.getActivePlayerState();

        //retaliation set should be clean at this point
        List<RetaliationState.RetaliationSet> pendingRetaliationSets = retaliationState.getPendingRetaliationCardSets();

        //each list/pile should contain only one card that is pending retaliation
        for (List<CardData> cardDatas : serverMessage.getMessageData().getPilesBeforeRetaliation()) {

            //TODO : Pool , not allocate
            Card pendingCard = new Card(cardDatas.get(0).getRank(), cardDatas.get(0).getSuit());

            //add the card as a covered that waiting retaliation
            RetaliationState.RetaliationSet retSet = YANObjectPool.getInstance().obtain(RetaliationState.RetaliationSet.class);
            retSet.setCoveredCard(pendingCard);

            //add set to pending retaliation sets
            pendingRetaliationSets.add(retSet);
        }

    }
}