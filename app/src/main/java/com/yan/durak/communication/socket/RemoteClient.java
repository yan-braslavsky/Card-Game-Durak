package com.yan.durak.communication.socket;

import com.yan.durak.gamelogic.communication.connection.SocketClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/**
 * Created by Yan-Home on 12/24/2014.
 */
public class RemoteClient implements SocketClient {

    private final Socket mSocket;
    private PrintWriter mOutputWriter;
    private BufferedReader mInputReader;
    private boolean mDisconnected;

    public RemoteClient(Socket socket) {
        mSocket = socket;
        try {
            mOutputWriter = new PrintWriter(socket.getOutputStream(), true);
            mInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String msg) {
        mOutputWriter.println(msg);
    }

    @Override
    public String readMessage() {
        try {
            return mInputReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void disconnect() {
        mDisconnected = true;
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isDisconnected() {
        return mDisconnected;
    }
}
