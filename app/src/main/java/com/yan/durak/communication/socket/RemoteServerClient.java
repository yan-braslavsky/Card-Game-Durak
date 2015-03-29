package com.yan.durak.communication.socket;

import com.yan.durak.gamelogic.communication.connection.SocketClient;

/**
 * Created by Yan-Home on 12/24/2014.
 * <p/>
 * Used to serve as a SocketClient on a client side that connected
 * to a local server.
 */
public class RemoteServerClient implements SocketClient {

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
