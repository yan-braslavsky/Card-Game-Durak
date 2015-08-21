package com.yan.durak.communication.client.local;

import com.yan.durak.gamelogic.communication.connection.IRemoteClient;

/**
 * Created by Yan-Home on 12/24/2014.
 * <p/>
 * Used to serve as a {@link IRemoteClient} on a client side that connected
 * to a local server.
 */
public class RemoteLsClient implements IRemoteClient {

    private final SharedLocalMessageQueue mSharedMessageQueue;

    public RemoteLsClient(final SharedLocalMessageQueue sharedMessageQueue) {
        mSharedMessageQueue = sharedMessageQueue;
    }

    @Override
    public void sendMessage(final String msg) {
        mSharedMessageQueue.insertMessageForServerQueue(msg);
    }

    @Override
    public String readMessage() {
        return mSharedMessageQueue.getMessageForClientQueue();
    }

    @Override
    public void disconnect() {
        mSharedMessageQueue.clearForClientQueue();
    }
}
