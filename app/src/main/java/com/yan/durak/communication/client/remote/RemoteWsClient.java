package com.yan.durak.communication.client.remote;

import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.WebSocket.StringCallback;
import com.yan.durak.gamelogic.communication.connection.IRemoteClient;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Yan-Home on 5/29/2015.
 * Used to serve as a {@link IRemoteClient} on a client side that connected
 * to a local server using websockets.
 */
public class RemoteWsClient implements IRemoteClient {

    public static final String WS_NEW_CONNECTION_KEY = "new connection: /";
    private final WebSocket mWebSocket;
    private final Queue<String> mMessageQueue;

    public RemoteWsClient(final WebSocket webSocket) {
        this.mWebSocket = webSocket;
        mMessageQueue = new LinkedBlockingDeque<>();

        //set socket callbacks
        mWebSocket.setStringCallback(new StringCallback() {
            @Override
            public void onStringAvailable(final String msg) {
                mMessageQueue.add(msg);
            }
        });
    }

    @Override
    public void sendMessage(final String msg) {
        mWebSocket.send(msg);
    }

    @Override
    public String readMessage() {
        final String msg = mMessageQueue.poll();
        //we ignore socket specific messages
        return (WS_NEW_CONNECTION_KEY.equals(msg)) ? null : msg;
    }

    @Override
    public void disconnect() {
        mWebSocket.close();
    }
}
