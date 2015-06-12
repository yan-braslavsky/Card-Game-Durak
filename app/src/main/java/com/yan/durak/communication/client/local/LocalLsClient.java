package com.yan.durak.communication.client.local;
import com.yan.durak.gamelogic.communication.connection.IRemoteClient;

/**
 * Created by Yan-Home on 12/24/2014.
 *
 * Used to serve as a {@link IRemoteClient} on a local server side to represent
 * a Local player.
 */
public class LocalLsClient implements IRemoteClient {

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
