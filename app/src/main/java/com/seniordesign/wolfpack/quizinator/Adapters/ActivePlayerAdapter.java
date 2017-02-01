package com.seniordesign.wolfpack.quizinator.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.Database.Card;
import com.seniordesign.wolfpack.quizinator.Messages.Answer;
import com.seniordesign.wolfpack.quizinator.R;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        View v = view;

        if (v == null) {
            LayoutInflater vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.array_adapter_list_of_cards, null);
        }

        Answer player = players.get(position);

        TextView playerName = (TextView) v.findViewById(R.id.active_player_name);
        playerName.setText(player.getDeviceName());

        LinearLayout responseArea = (LinearLayout) view.findViewById(R.id.active_player_response_area);
        if (!mustSelectWinner) {
            responseArea.setVisibility(View.GONE);
            return v;
        }

        TextView playerAnswer = (TextView) v.findViewById(R.id.active_player_answer);
        playerAnswer.setText(player.getAnswer());

        TextView playerTime = (TextView) v.findViewById(R.id.active_player_time);
        playerTime.setText(player.getTimeTaken() + "");

        return v;
    }
}
