package com.yan.durak.communication.client.local;

import com.yan.durak.gamelogic.communication.connection.IRemoteClient;

/**
 * Created by Yan-Home on 12/24/2014.
 * <p/>
 * Used to serve as a {@link IRemoteClient} on a local server side to represent
 * a Local player.
 */
public class LocalLsClient implements IRemoteClient {

    private final SharedLocalMessageQueue mSharedMessageQueue;

    public LocalLsClient(final SharedLocalMessageQueue sharedMessageQueue) {
        mSharedMessageQueue = sharedMessageQueue;
    }

    @Override
    public void sendMessage(final String msg) {
        mSharedMessageQueue.insertMessageForClientQueue(msg);
    }

    @Override
    public String readMessage() {
        return mSharedMessageQueue.getMessageForServerQueue();
    }

    @Override
    public void disconnect() {
        mSharedMessageQueue.clearForServerQueue();
    }
}
