package com.seniordesign.wolfpack.quizinator.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.messages.Answer;
import com.seniordesign.wolfpack.quizinator.messages.Wager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class WagerPlayerAdapter extends BaseAdapter {

    private Context context;
    private List<Wager> players;

    public WagerPlayerAdapter(Context context, List<Wager> players) {
        this.context = context;
        this.players = players;
    }

    public void addItem(Wager wager) {
        players.add(wager);
        sort();
    }

    private void sort() {
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

        Wager player = players.get(position);

        TextView playerName = (TextView) view.findViewById(R.id.active_player_name);
        playerName.setText(player.getDeviceName());

        LinearLayout responseArea = (LinearLayout) view.findViewById(R.id.active_player_response_area);

        TextView playerWager = (TextView) view.findViewById(R.id.active_player_answer);
        playerWager.setText("" + player.getWager());

        return view;
    }
}
