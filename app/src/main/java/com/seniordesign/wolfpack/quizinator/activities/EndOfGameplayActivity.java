package com.seniordesign.wolfpack.quizinator.activities;

import android.content.Intent;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.adapters.OtherPlayersAdapter;
import com.seniordesign.wolfpack.quizinator.database.GamePlayStats;
import com.seniordesign.wolfpack.quizinator.database.HighScores;
import com.seniordesign.wolfpack.quizinator.database.QuizDataSource;
import com.seniordesign.wolfpack.quizinator.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndOfGameplayActivity extends AppCompatActivity {

    private QuizDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_gameplay);
        setTitle(Constants.END_OF_GAMEPLAY);
        initializeDB();

        boolean isMultiplayerGame = false;
        GamePlayStats gamePlayStats;
        Map<String, Pair<String, Integer>> playerScores = new HashMap<>();
        if (getIntent().getExtras() != null) {
            gamePlayStats = getIntent().getExtras().getParcelable("gameStats");
            isMultiplayerGame = getIntent().getExtras().getBoolean("multiplayerGame");

            String json = getIntent().getExtras().getString("playerScores");
            Type typeOfHashMap = new TypeToken<Map<String, Pair<String, Integer>>>() { }.getType();
            playerScores = new Gson().fromJson(json, typeOfHashMap);
        } else {
            gamePlayStats = new GamePlayStats();
            gamePlayStats.setScore(0);
            gamePlayStats.setTimeElapsed(0);
            gamePlayStats.setTotalCardsCompleted(0);
            gamePlayStats.setDeckID(-1);
        }

        ((TextView)findViewById(R.id.endOfGameScoreText)).setText(
                String.valueOf(gamePlayStats != null ? gamePlayStats.getScore() : 0)
        );
        long endOfGameSeconds = (gamePlayStats.getTimeElapsed()/1000)%60;
        String formattedGameSeconds = endOfGameSeconds < 10 ? "0" + endOfGameSeconds : "" + endOfGameSeconds;
        ((TextView)findViewById(R.id.endOfGameTimeText)).setText(
                (gamePlayStats.getTimeElapsed()/60000) + ":" + formattedGameSeconds
        );
        ((TextView)findViewById(R.id.endOfGameTotalCardsText)).setText(
                String.valueOf(gamePlayStats.getTotalCardsCompleted())
        );

        HighScores highScores = new HighScores();
        if (dataSource.getAllHighScores().size() > 0) {
            for(HighScores hs : dataSource.getAllHighScores()){
                if(hs.getDeckID() == gamePlayStats.getDeckID()){
                    highScores = hs;
                    break;
                }
            }
        } else {
            highScores.setBestScore(0);
            highScores.setBestTime(0);
            highScores.setDeckID(-1);
        }
        if(isMultiplayerGame || highScores.getDeckID()==-1){
            findViewById(R.id.end_of_gameplay_row_hs_hs).setVisibility(View.GONE);
            findViewById(R.id.end_of_gameplay_row_hs_time).setVisibility(View.GONE);

            TableRow bottomRow = (TableRow) findViewById(R.id.end_of_gameplay_row_hs_cards);
            TableRow.LayoutParams params = new TableRow.LayoutParams(bottomRow.getLayoutParams());
            params.setMargins(0, 0, 0, 4);
            bottomRow.setLayoutParams(params);
        }
        else{
            findViewById(R.id.end_of_gameplay_row_hs_hs).setVisibility(View.VISIBLE);
            findViewById(R.id.end_of_gameplay_row_hs_time).setVisibility(View.VISIBLE);
        }
        long highScoreSeconds = (highScores.getBestTime()/1000)%60;
        String formattedHighScoreSeconds = highScoreSeconds < 10 ? "0" + highScoreSeconds : "" + highScoreSeconds;
        ((TextView)findViewById(R.id.endOfGameHighScoreText)).setText(
                String.valueOf(highScores.getBestScore()));
        ((TextView)findViewById(R.id.endOfGameHighScoreTimeText)).setText(
                (highScores.getBestTime()/60000) + ":" + formattedHighScoreSeconds
        );

        if (isMultiplayerGame && (playerScores != null && playerScores.size() > 0))
            populateOtherPlayersScores(playerScores.values());
    }

    private void populateOtherPlayersScores(Collection<Pair<String, Integer>> playerScores) {
        findViewById(R.id.end_of_multi_game_title).setVisibility(View.VISIBLE);
        ListView otherPlayers = (ListView) findViewById(R.id.end_of_gameplay_list_of_other_players);
        otherPlayers.setVisibility(View.VISIBLE);

        List<Pair<String, Integer>> scores = new ArrayList<>(playerScores);
        OtherPlayersAdapter adapter =
                new OtherPlayersAdapter(this, R.layout.array_adapter_list_of_other_players, scores);
        otherPlayers.setAdapter(adapter);
    }

    public void showMainMenu(View v){
        final Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean initializeDB(){
        dataSource = new QuizDataSource(this);
        return dataSource.open();
    }

    @Override
    protected void onResume(){
        super.onResume();
        dataSource.open();
    }

    @Override
    protected void onPause(){
        super.onPause();
        dataSource.close();
    }

    @Override
    public void onBackPressed() {
        showMainMenu(this.getCurrentFocus());
    }
}
