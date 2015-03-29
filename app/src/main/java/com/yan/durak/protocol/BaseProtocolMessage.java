package com.yan.durak.protocol;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Yan-Home on 12/24/2014.
 */
public class BaseProtocolMessage<T> {

    @SerializedName("name")
    private String mMessageName;
    @SerializedName("data")
    private T mMessageData;

    public BaseProtocolMessage() {
    }

    public void setMessageName(String messageName) {
        mMessageName = messageName;
    }

    public void setMessageData(T messageData) {
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
