package com.yan.durak.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.yan.durak.R;
import com.yan.durak.communication.socket.LocalServerClient;
import com.yan.durak.gamelogic.GameStarter;

public class MainMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

//        getWindow().setBackgroundDrawable(null);
//        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void buttonClicked(View buttonClicked) {

        //TODO : open server on different thread
        (new Thread(new Runnable() {
            @Override
            public void run() {
                (new GameStarter(new LocalServerClient(), null, null)).start();
            }
        })).start();

        Intent myIntent = new Intent(MainMenuActivity.this, GameActivity.class);
        //Optional parameters
//                myIntent.putExtra("key", value);
        startActivity(myIntent);

        //TODO : custom transition animation ?
//                overridePendingTransition();

        //we no longer need this activity
        finish();

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
