package com.yan.durak.communication.client.remote;

import com.yan.durak.gamelogic.communication.connection.IRemoteClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/**
 * Created by Yan-Home on 12/24/2014.
 * Used to serve as a {@link IRemoteClient} on a client side that connected
 * to a remote server using plain sockets.
 */
public class RemoteSocketClient implements IRemoteClient {

    private final Socket mSocket;
    private PrintWriter mOutputWriter;
    private BufferedReader mInputReader;
    private boolean mDisconnected;

    public RemoteSocketClient(final Socket socket) {
        mSocket = socket;
        try {
            mOutputWriter = new PrintWriter(socket.getOutputStream(), true);
            mInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(final String msg) {
        mOutputWriter.println(msg);
    }

    @Override
    public String readMessage() {
        try {
            return mInputReader.readLine();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void disconnect() {
        mDisconnected = true;
        try {
            mSocket.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isDisconnected() {
        return mDisconnected;
    }
}
