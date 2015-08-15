package com.yan.durak.communication.game_server;

import com.yan.durak.communication.client.local.LocalLsClient;
import com.yan.durak.communication.client.local.SharedLocalMessageQueue;
import com.yan.durak.gamelogic.GameStarter;
import com.yan.durak.gamelogic.communication.connection.IRemoteClient;
import com.yan.durak.gamelogic.game.IGameRules;

/**
 * Created by Yan-Home on 5/31/2015.
 */
public class LocalGameServer {

    private static Thread gameThread;

    /**
     * Starts a local game server on another thread.
     */
    public static void start(final int playersAmount) {

        //we must recreate the message queue to make sure we are using a fresh queue
        SharedLocalMessageQueue.recreateInstance();

        //create remote clients array
        final IRemoteClient[] clients = new IRemoteClient[playersAmount];

        clients[0] = new LocalLsClient(SharedLocalMessageQueue.getInstance());

        //open local server on different thread
        gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                (new GameStarter(new IGameRules() {
                    @Override
                    public int getTotalPlayersInGameAmount() {
                        return clients.length;
                    }
                }, clients)).start();
            }
        });
        gameThread.start();
    }

    /**
     * Gracefully stops the local game server
     */
    public static void shutDown() {
        //TODO : Shut down the server
        gameThread.interrupt();
        gameThread = null;
    }
}
