package com.yan.durak.communication.game_server;

import com.yan.durak.communication.client.local.LocalLsClient;
import com.yan.durak.gamelogic.GameStarter;

/**
 * Created by Yan-Home on 5/31/2015.
 */
public class LocalGameServer {

    private static Thread gameThread;

    /**
     * Starts a local game server on another thread.
     */
    public static void start() {
        //open local server on different thread
        gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                (new GameStarter(new LocalLsClient(), null, null)).start();
            }
        });
        gameThread.start();
    }

    /**
     * Gracefully stops the local game server
     */
    public static void shutDown() {
        //TODO : Shut down the server
    }
}
