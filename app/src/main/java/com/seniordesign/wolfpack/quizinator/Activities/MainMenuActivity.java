package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.seniordesign.wolfpack.quizinator.R;

/**
 * The main menu activity is...
 * @creation 09/28/2016
 */
public class MainMenuActivity extends AppCompatActivity {

    /*
     * @author kuczynskij (09/28/2016)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Button newGameButton = (Button) findViewById(R.id.newGameButton);

        final Intent intent = new Intent(this, NewGameSettingsActivity.class);

        newGameButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(intent);
            }
        });
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar
        // if it is present.
        //need to add menu object in the layout (activity_main_menu)
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            //this thing let's the user press the menu button to
//            //open a lil menu option at the bottom
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    public String onButtonClick(View view){
        Intent intent;  //used to start a new activity
        switch (view.getId()){
//            case R.id.newGameBtn:
//                intent = new Intent(this.NewGameSettingsActivity.class);
//                startActivity(intent);
//                return "NewGame";
//                break;
        }
        return null;
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    @Override
    protected void onResume(){
        super.onResume();
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    @Override
    protected void onPause(){
        super.onPause();
    }
}
