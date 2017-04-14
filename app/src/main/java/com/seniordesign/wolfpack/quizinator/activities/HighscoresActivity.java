package com.seniordesign.wolfpack.quizinator.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.adapters.CardAdapter;
import com.seniordesign.wolfpack.quizinator.adapters.HighScoreAdapter;
import com.seniordesign.wolfpack.quizinator.database.HighScores;
import com.seniordesign.wolfpack.quizinator.database.QuizDataSource;

import java.util.List;

public class HighscoresActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);

        QuizDataSource dataSource = new QuizDataSource(this);
        dataSource.open();
        List<HighScores> highScores = dataSource.getAllHighScores();
        dataSource.close();
        highScores = orderScoresByScoreAndTime(highScores);
        final HighScoreAdapter adapter = new HighScoreAdapter(this,
                R.layout.array_adapter_list_of_highscores, highScores);

        final ListView listView = (ListView) findViewById(R.id.list_of_highscores);
        listView.setAdapter(adapter);

    }

    private List<HighScores> orderScoresByID(List<HighScores> scores){
        for(int i = 1; i<scores.size();i++){
            if(scores.get(i).getId()<scores.get(i-1).getId()){
                HighScores tempScore = scores.get(i-1);
                scores.set(i-1,scores.get(i));
                scores.set(i,tempScore);
            }
        }
        return scores;
    }

    private List<HighScores> orderScoresByScoreAndTime(List<HighScores> scores){
        for(int i = 1; i<scores.size();i++){
            if(scores.get(i).getBestScore()>scores.get(i-1).getBestScore()){
                HighScores tempScore = scores.get(i-1);
                scores.set(i-1,scores.get(i));
                scores.set(i,tempScore);
            }
            else if(scores.get(i).getBestScore() == scores.get(i-1).getBestScore() &&
                    scores.get(i).getBestTime() < scores.get(i-1).getBestTime()){
                HighScores tempScore = scores.get(i-1);
                scores.set(i-1,scores.get(i));
                scores.set(i,tempScore);
            }
        }
        return scores;
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }
}
