package com.yan.durak.gamelogic.communication.protocol;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Yan-Home on 12/24/2014.
 */
public abstract class BaseProtocolMessage<T> {

    @SerializedName("name")
    private String mMessageName;
    @SerializedName("data")
    private T mMessageData;

    public void setMessageName(final String messageName) {
        mMessageName = messageName;
    }

    public void setMessageData(final T messageData) {
        mMessageData = messageData;
    }

    public String toJsonString() {
        return (new Gson()).toJson(this);
    }

    public String getMessageName() {
        return mMessageName;
    }

    public T getMessageData() {
        return mMessageData;
    }
}
