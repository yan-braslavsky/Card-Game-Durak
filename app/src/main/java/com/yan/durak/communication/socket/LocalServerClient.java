package com.yan.durak.communication.socket;
import com.yan.durak.gamelogic.communication.connection.SocketClient;

/**
 * Created by Yan-Home on 12/24/2014.
 *
 * Used to serve as a SocketClient on a local server side to represent
 * Local player.
 */
public class LocalServerClient implements SocketClient {

    @Override
    public void sendMessage(String msg) {
        SharedLocalMessageQueue.getInstance().insertMessageForClientQueue(msg);
    }

    @Override
    public String readMessage() {
        return SharedLocalMessageQueue.getInstance().getMessageForServerQueue();
    }

    @Override
    public void disconnect() {
        SharedLocalMessageQueue.getInstance().clearForServerQueue();
    }
}
