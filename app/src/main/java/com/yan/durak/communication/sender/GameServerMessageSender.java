package com.yan.durak.communication.sender;

import com.yan.durak.communication.game_server.connector.IGameServerConnector;
import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.ResponseCardForAttackMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.ResponseRetaliatePilesMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.ResponseThrowInsMessage;
import com.yan.durak.service.IService;

import java.util.Collections;
import java.util.List;

/**
 * Created by ybra on 20/04/15.
 * <p/>
 * PURPOSE :
 * Responsible for sending messages to game server
 */
public class GameServerMessageSender implements IService {

    private IGameServerConnector mConnector;

    public GameServerMessageSender(IGameServerConnector gameServerConnector) {
        mConnector = gameServerConnector;
    }

    /**
     * Sends the throw in response to the server
     *
     * @param cards list of cards that player selected to throw in
     */
    public void sendThrowInResponse(List<Card> cards) {
        sendMessage(new ResponseThrowInsMessage(cards));
    }

    /**
     * Sends retaliate piles response to the server
     * If empty list or null is sent  , that means that user takes all the cards in the field.
     *
     * @param retaliatedPilesList list of piles after retaliation.
     */
    public void sendResponseRetaliatePiles(List<List<Card>> retaliatedPilesList) {
        if ((retaliatedPilesList == null)) {
            retaliatedPilesList = Collections.emptyList();
        }
        sendMessage(new ResponseRetaliatePilesMessage(retaliatedPilesList));
    }

    /**
     * Sends card for attack response to the server
     *
     * @param attackCard card that player have choosen to start the attack with
     */
    public void sendCardForAttackResponse(Card attackCard) {
        sendMessage(new ResponseCardForAttackMessage(attackCard));
    }


    private void sendMessage(BaseProtocolMessage message) {
        mConnector.sentMessageToServer(message);
    }
}
