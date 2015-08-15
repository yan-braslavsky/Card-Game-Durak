package com.yan.durak.gamelogic.communication.connection;

/**
 * Created by Yan-Home on 12/24/2014.
 * <p/>
 * Defines an interface of remote client connected via Socket or other kind of bidirectional connection.
 * Concrete implementation can vary.
 */
public interface IRemoteClient {

    /**
     * Sending message to remote client
     * @param msg message to send
     */
    void sendMessage(String msg);

    /**
     * Reads message from remote client.
     * This is a blocking operation.
     * Thread will block until remote client will supply with a message
     *
     * @return a message from remote client or null if client is disconnected.
     */
    String readMessage();

    /**
     * Disconnects from remote client.
     */
    void disconnect();
}