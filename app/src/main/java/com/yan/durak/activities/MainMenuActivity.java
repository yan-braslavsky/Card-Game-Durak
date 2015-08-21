package com.yan.durak.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.yan.durak.R;
import com.yan.durak.communication.game_server.LocalGameServer;
import com.yan.durak.communication.game_server.connector.IGameServerConnector;
import com.yan.durak.communication.game_server.connector.LocalGameServerConnector;
import com.yan.durak.communication.game_server.connector.RemoteGameServerConnector;

public class MainMenuActivity extends Activity {

    public static String EXTRA_CONNECTOR_CLASS_KEY = "EXTRA_CONNECTOR_CLASS_KEY";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mode_chooser);
    }

    /**
     * Called by reflection from layout xml
     */
    public void onStartButtonClicked(final View view) {

        final boolean isOnline = ((RadioGroup) (findViewById(R.id.onlineRadioGroup))).getCheckedRadioButtonId()
                == R.id.radioButtonOnline;
        final int numPlayers = ((RadioGroup) (findViewById(R.id.playerCountRadioGroup))).getCheckedRadioButtonId()
                == R.id.radioButtonTwoPlayers ? 2 : 3;
        final String nickname = ((EditText) findViewById(R.id.nickname_edit_text)).getText().toString();
        final String avatar = ((Spinner) findViewById(R.id.spinner)).getSelectedItem().toString();

        final GameActivity.GameInitConfig gameInitConf = new GameActivity.GameInitConfig(nickname, avatar, numPlayers);

        if (isOnline) {
            startOnlineGame(gameInitConf);
        } else {
            startLocalGame(gameInitConf);
        }
    }


    private void startOnlineGame(final GameActivity.GameInitConfig gameInitConf) {
        final Class<? extends IGameServerConnector> connectorClass = RemoteGameServerConnector.class;
        startGameActivity(connectorClass);
    }

    private void startLocalGame(final GameActivity.GameInitConfig gameInitConf) {
        //start local server
        LocalGameServer.start(gameInitConf.playersAmount, gameInitConf.avatarResource, gameInitConf.nickname);

        final Class<? extends IGameServerConnector> connectorClass = LocalGameServerConnector.class;
        startGameActivity(connectorClass);
    }

    private void startGameActivity(final Class<? extends IGameServerConnector> connectorClass) {
        final Intent myIntent = new Intent(MainMenuActivity.this, GameActivity.class);
        myIntent.putExtra(EXTRA_CONNECTOR_CLASS_KEY, connectorClass);
        startActivity(myIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}