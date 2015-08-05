package com.yan.durak.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.yan.durak.R;
import com.yan.durak.communication.game_server.connector.IGameServerConnector;
import com.yan.durak.communication.game_server.connector.LocalGameServerConnector;
import com.yan.durak.communication.game_server.connector.RemoteGameServerConnector;

public class MainMenuActivity extends Activity {

    public static String EXTRA_CONNECTOR_CLASS_KEY = "EXTRA_CONNECTOR_CLASS_KEY";
    public static String EXTRA_GAME_CONFIG_KEY = "EXTRA_NUM_PLAYERS_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mode_chooser);
    }

    /**
     * Called by reflection from layout xml
     */
    public void onStartButtonClicked(View view) {

        boolean isOnline = ((RadioGroup) (findViewById(R.id.onlineRadioGroup))).getCheckedRadioButtonId()
                == R.id.radioButtonOnline;
        int numPlayers = ((RadioGroup) (findViewById(R.id.playerCountRadioGroup))).getCheckedRadioButtonId()
                == R.id.radioButtonTwoPlayers ? 2 : 3;
        String nickname = ((EditText) findViewById(R.id.nickname_edit_text)).getText().toString();
        String avatar = ((Spinner) findViewById(R.id.spinner)).getSelectedItem().toString();

        GameActivity.GameInitConfig gameInitConf = new GameActivity.GameInitConfig(nickname, avatar, numPlayers);

        if (isOnline) {
            startOnlineGame(gameInitConf);
        } else {
            startLocalGame(gameInitConf);
        }
    }



    private void startOnlineGame(GameActivity.GameInitConfig gameInitConf) {
        Class<? extends IGameServerConnector> connectorClass = RemoteGameServerConnector.class;
        startGameActivity(connectorClass, gameInitConf);
    }

    private void startLocalGame(GameActivity.GameInitConfig gameInitConf) {
        Class<? extends IGameServerConnector> connectorClass = LocalGameServerConnector.class;
        startGameActivity(connectorClass, gameInitConf);
    }

    private void startGameActivity(Class<? extends IGameServerConnector> connectorClass,
                                   GameActivity.GameInitConfig gameInitConf) {
        Intent myIntent = new Intent(MainMenuActivity.this, GameActivity.class);
        myIntent.putExtra(EXTRA_CONNECTOR_CLASS_KEY, connectorClass);
        myIntent.putExtra(EXTRA_GAME_CONFIG_KEY, gameInitConf);
        startActivity(myIntent);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }
}