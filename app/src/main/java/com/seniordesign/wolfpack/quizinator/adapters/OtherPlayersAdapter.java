package com.seniordesign.wolfpack.quizinator.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.Util;
import com.seniordesign.wolfpack.quizinator.database.Card;
import com.seniordesign.wolfpack.quizinator.views.CardIcon;
import com.seniordesign.wolfpack.quizinator.wifiDirect.ConnectionService;

import java.util.Collection;
import java.util.List;

public class OtherPlayersAdapter extends ArrayAdapter<Pair<String, Integer>> {

    public OtherPlayersAdapter(Context context, int resource, List<Pair<String, Integer>> scores) {
        super(context, resource, scores);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.array_adapter_list_of_other_players, null);
        }

        Pair<String, Integer> playerScore = getItem(position);

        if (playerScore != null) {
            TextView name = (TextView) v.findViewById(R.id.other_player_name);
            TextView score = (TextView) v.findViewById(R.id.other_player_score);

            name.setText(playerScore.first);
            score.setText(playerScore.second.toString());
        }

        return v;
    }
}
