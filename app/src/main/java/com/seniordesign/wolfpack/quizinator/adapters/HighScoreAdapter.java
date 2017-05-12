package com.seniordesign.wolfpack.quizinator.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.Constants.CARD_TYPES;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.Util;
import com.seniordesign.wolfpack.quizinator.database.Card;
import com.seniordesign.wolfpack.quizinator.database.Deck;
import com.seniordesign.wolfpack.quizinator.database.HighScores;
import com.seniordesign.wolfpack.quizinator.database.QuizDataSource;
import com.seniordesign.wolfpack.quizinator.views.CardIcon;

import java.util.ArrayList;
import java.util.List;


public class HighScoreAdapter extends ArrayAdapter<HighScores> {

    List<Deck> decks;

    public HighScoreAdapter(Context context, int resource) {
        super(context, resource);
    }

    public HighScoreAdapter(Context context, int resource, List<HighScores> values) {
        super(context, resource, values);

        QuizDataSource dataSource = new QuizDataSource(context);
        dataSource.open();
        decks = dataSource.getAllDecks();
        dataSource.close();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.array_adapter_list_of_highscores, null);
        }

        HighScores scores = getItem(position);
        TextView score = (TextView) v.findViewById(R.id.array_adapter_hs_score);
        score.setText(Constants.HIGH_SCORE_SCORE + " "+ scores.getBestScore());
        long highScoreSeconds = (scores.getBestTime()/1000)%60;
        String formattedHighScoreSeconds = highScoreSeconds < 10 ? "0" + highScoreSeconds : "" + highScoreSeconds;
        ((TextView)v.findViewById(R.id.array_adapter_hs_time)).setText(
                Constants.HIGH_SCORE_TIME + (scores.getBestTime()/60000) + ":" + formattedHighScoreSeconds
        );
        for(Deck deck : decks){
            if(deck.getId() == scores.getDeckID()){
                ((TextView)v.findViewById(R.id.array_adapter_hs_deck)).setText(deck.getDeckName());
                break;
            }
        }

        return v;
    }
}
