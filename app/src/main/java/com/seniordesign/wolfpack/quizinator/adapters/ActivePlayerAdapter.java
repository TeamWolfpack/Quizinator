package com.seniordesign.wolfpack.quizinator.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.messages.Answer;
import com.seniordesign.wolfpack.quizinator.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ActivePlayerAdapter extends BaseAdapter {

    private Context context;
    private List<Answer> players;
    private boolean mustSelectWinner;

    public ActivePlayerAdapter(Context context, List<Answer> players, boolean mustSelectWinner) {
        this.context = context;
        this.players = players;
        this.mustSelectWinner = mustSelectWinner;
    }

    public void addItem(Answer answer) {
        players.add(answer);
        sort();
    }

    private void sort() {
        Collections.sort(players, new Comparator<Answer>() {
            @Override
            public int compare(Answer answer, Answer t1) {
                return (int)(answer.getTimeTaken() - t1.getTimeTaken());
            }
        });
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public Object getItem(int i) {
        return players.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater vi = LayoutInflater.from(context);
            view = vi.inflate(R.layout.base_adapter_active_player_item, null);
        }

        Answer player = players.get(position);

        TextView playerName = (TextView) view.findViewById(R.id.active_player_name);
        playerName.setText(player.getDeviceName());

        TextView playerAnswer = (TextView) view.findViewById(R.id.active_player_answer);
        playerAnswer.setText(player.getAnswer());

        if (!mustSelectWinner) {
            view.findViewById(R.id.active_player_response_area_time).setVisibility(View.GONE);
            TextView answerLabel = (TextView) view.findViewById(R.id.active_player_response_area_answer_label);
            answerLabel.setText(R.string.points);
            return view;
        }

        TextView playerTime = (TextView) view.findViewById(R.id.active_player_time);
        playerTime.setText(String.format(Locale.US, "%.3f sec", player.getTimeTaken() / 1000000000.0));

        return view;
    }
}
