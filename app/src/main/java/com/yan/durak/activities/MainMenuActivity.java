package com.yan.durak.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.yan.durak.R;
import com.yan.durak.communication.game_server.LocalGameServer;
import com.yan.durak.communication.game_server.connector.IGameServerConnector;
import com.yan.durak.communication.game_server.connector.LocalGameServerConnector;
import com.yan.durak.communication.game_server.connector.RemoteGameServerConnector;

public class MainMenuActivity extends Activity {

    public static String EXTRA_CONNECTOR_CLASS_KEY = "EXTRA_CONNECTOR_CLASS_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void buttonClicked(View buttonClicked) {
        switch (buttonClicked.getId()) {
            case R.id.quick_btn:
                startLocalGame();
                break;
            case R.id.online_btn:
                startOnlineGame();
                break;
            default:
                Toast.makeText(this, "This mode not supported yet", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void startOnlineGame() {
        Class<? extends IGameServerConnector> connectorClass = RemoteGameServerConnector.class;
        startGameActivity(connectorClass);
    }

    private void startLocalGame() {
        LocalGameServer.start();
        Class<? extends IGameServerConnector> connectorClass = LocalGameServerConnector.class;
        startGameActivity(connectorClass);
    }

    private void startGameActivity(Class<? extends IGameServerConnector> connectorClass) {
        Intent myIntent = new Intent(MainMenuActivity.this, GameActivity.class);
        myIntent.putExtra(EXTRA_CONNECTOR_CLASS_KEY, connectorClass);
        startActivity(myIntent);
        overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
        finish();
    }
}