package com.yan.durak.protocol.messages;

import com.yan.durak.protocol.BaseProtocolMessage;
import com.yan.durak.protocol.data.RetaliationSetData;
import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Created by Yan-Home on 12/24/2014.
 * Sent to client to notify that the move he made is not valid.
 */
public class RetaliationInvalidProtocolMessage extends BaseProtocolMessage<RetaliationInvalidProtocolMessage.ProtocolMessageData> {

    public static final String MESSAGE_NAME = "retaliationInvalid";

    public RetaliationInvalidProtocolMessage(List<RetaliationSetData> retaliationSetDataList) {
        super();
        setMessageName(MESSAGE_NAME);
        setMessageData(new ProtocolMessageData(retaliationSetDataList));
    }

    public static class ProtocolMessageData {

        @SerializedName("invalidRetaliationsList")
        List<RetaliationSetData> mInvalidRetaliationsList;

        public ProtocolMessageData(List<RetaliationSetData> invalidRetaliationsList) {
            mInvalidRetaliationsList = invalidRetaliationsList;
        }

        public List<RetaliationSetData> getInvalidRetaliationsList() {
            return mInvalidRetaliationsList;
        }
    }
}
