package com.yan.durak.communication.game_server.connector;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;
import com.yan.durak.communication.client.local.RemoteLsClient;
import com.yan.durak.communication.client.remote.RemoteSocketClient;
import com.yan.durak.communication.client.remote.RemoteWsClient;
import com.yan.durak.gamelogic.communication.connection.IRemoteClient;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

import glengine.yan.glengine.service.IService;
import glengine.yan.glengine.util.loggers.YANLogger;

/**
 * Created by Yan-Home on 12/24/2014.
 * <p/>
 * Implemented as a singleton.
 * Manages connection to remote socket server
 * @deprecated requires redefinition of responsibilities
 */
@Deprecated
public class SocketConnectionManager implements IService{

    private IRemoteClient mSocketClient;
    private volatile boolean mConnected;
    private Queue<String> mMessageQueue;
    private Thread mListeningThread;

    public SocketConnectionManager() {
        mMessageQueue = new LinkedList<>();
    }

    /**
     * Connects to remote web socket server.
     *
     * @return true if connection was established , false otherwise
     */
    public boolean connectToRemoteServerViaWebSocket(final String serverDomain, final int serverPort) {

        if (isConnected())
            return false;

        //TODO : this might be not an appropriate way to maintain connection
        mListeningThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Future<WebSocket> future = AsyncHttpClient.getDefaultInstance().websocket("ws://" + serverDomain, null, null);
                    WebSocket websocket = future.get();
                    mSocketClient = new RemoteWsClient(websocket);
                    mConnected = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                while (isConnected()) {
                    String msg = mSocketClient.readMessage();
                    if (msg != null) {
                        synchronized (mMessageQueue) {
                            YANLogger.log("[RECEIVED] " + msg);
                            mMessageQueue.add(msg);
                        }
                    }
                }
            }
        });

        mListeningThread.start();
        return true;
    }

    /**
     * Connects to remote socket server using plain sockets.
     *
     * @return true if connection was established , false otherwise
     */
    public boolean connectToRemoteServerViaSocket(final String serverAddress, final int serverPort) {

        if (isConnected())
            return false;

        //TODO : this might be not an appropriate way to maintain connection
        (new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocketClient = new RemoteSocketClient(new Socket(serverAddress, serverPort));
                    mConnected = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                while (isConnected()) {
                    String msg = mSocketClient.readMessage();
                    if (msg != null) {
                        synchronized (mMessageQueue) {
                            YANLogger.log("[RECEIVED] " + msg);
                            mMessageQueue.add(msg);
                        }
                    }
                }

            }
        })).start();

        return true;
    }

    /**
     * Connects to remote socket server
     *
     * @return true if connection was established , false otherwise
     */
    public boolean connectToLocalServer() {

        if (isConnected())
            return false;

        //TODO : this might be not an appropriate way to maintain connection
        mListeningThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mSocketClient = new RemoteLsClient();
                mConnected = true;

                while (isConnected()) {
                    String msg = mSocketClient.readMessage();
                    if (msg != null) {
                        synchronized (mMessageQueue) {
                            YANLogger.log("[RECEIVED] " + msg);
                            mMessageQueue.add(msg);
                        }
                    }
                }
            }
        });
        mListeningThread.start();

        return true;
    }

    public void disconnectFromLocalServer() {
        mSocketClient.disconnect();
        mConnected = false;
    }

    public void disconnectFromRemoteServer() {
        mSocketClient.disconnect();
        mConnected = false;
    }

    public void sendMessageToRemoteServer(String msg) {

        YANLogger.log("[SENT] " + msg);
        mSocketClient.sendMessage(msg);
    }

    public String readMessageFromRemoteServer() {
        synchronized (mMessageQueue) {
            return mMessageQueue.poll();
        }
    }

    public boolean isConnected() {
        return mConnected;
    }

    @Override
    public void clearServiceData() {
        mConnected = false;
        mListeningThread = null;
        mMessageQueue.clear();
    }
}