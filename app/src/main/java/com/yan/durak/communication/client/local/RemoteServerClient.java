package com.yan.durak.communication.client.local;

import com.yan.durak.gamelogic.communication.connection.IRemoteClient;

/**
 * Created by Yan-Home on 12/24/2014.
 * <p/>
 * Used to serve as a {@link IRemoteClient} on a client side that connected
 * to a local server.
 */
public class RemoteServerClient implements IRemoteClient {

    @Override
    public void sendMessage(String msg) {
        SharedLocalMessageQueue.getInstance().insertMessageForServerQueue(msg);
    }

    @Override
    public String readMessage() {
        return SharedLocalMessageQueue.getInstance().getMessageForClientQueue();
    }

    @Override
    public void disconnect() {
        SharedLocalMessageQueue.getInstance().clearForServerQueue();
    }
}
