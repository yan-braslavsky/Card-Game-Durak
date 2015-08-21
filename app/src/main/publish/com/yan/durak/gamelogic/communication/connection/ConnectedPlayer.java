package com.yan.durak.gamelogic.communication.connection;

import com.yan.durak.gamelogic.communication.protocol.data.PlayerMetaData;

/**
 * Created by Yan-Home on 15/8/2015.
 * <p/>
 * This is a representation of player that is connected to the game
 */
public class ConnectedPlayer {

    private final IRemoteClient mIRemoteClient;
    private final PlayerMetaData mPlayerMetaData;

    public ConnectedPlayer(final IRemoteClient IRemoteClient, final PlayerMetaData playerMetaData) {
        mIRemoteClient = IRemoteClient;
        mPlayerMetaData = playerMetaData;
    }

    public IRemoteClient getRemoteClient() {
        return mIRemoteClient;
    }

    public PlayerMetaData getPlayerMetaData() {
        return mPlayerMetaData;
    }
}
