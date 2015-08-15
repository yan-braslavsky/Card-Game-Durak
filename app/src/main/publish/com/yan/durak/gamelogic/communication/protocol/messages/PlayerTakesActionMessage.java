package com.yan.durak.gamelogic.communication.protocol.messages;


import com.google.gson.annotations.SerializedName;
import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;

/**
 * Created by Yan-Home on 12/24/2014.
 */
public class PlayerTakesActionMessage extends BaseProtocolMessage<PlayerTakesActionMessage.ProtocolMessageData> {

    public static final String MESSAGE_NAME = "playerTakesAction";

    /**
     * Defines an action that represents what player is currently doing
     */
    public enum PlayerAction {
        /**
         * Player requested to attack with a card
         */
        ATTACK_START,
        /**
         * Player requested to retaliate cards on the field
         */
        RETALIATION_START,
        /**
         * Player requested to pick cards to throw in
         */
        THROW_IN_START,
        /**
         * Player decided do not throw in anything
         */
        THROW_IN_PASS,
        /**
         * Player decided to throw in cards
         */
        THROW_IN_END,

        /**
         * Player decided to take cards
         */
        PLAYER_TAKES_CARDS
    }

    public PlayerTakesActionMessage(int playerIndex, PlayerAction action) {
        super();
        setMessageName(MESSAGE_NAME);
        setMessageData(new ProtocolMessageData(playerIndex, action.name()));
    }

    public static class ProtocolMessageData {

        @SerializedName("playerIndex")
        int mPlayerIndex;
        @SerializedName("action")
        String mAction;

        public ProtocolMessageData(int playerIndex, String action) {
            mPlayerIndex = playerIndex;
            mAction = action;
        }

        public int getPlayerIndex() {
            return mPlayerIndex;
        }

        public String getAction() {
            return mAction;
        }
    }
}
