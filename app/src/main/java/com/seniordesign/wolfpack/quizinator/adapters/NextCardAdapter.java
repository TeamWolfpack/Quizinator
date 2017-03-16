package com.seniordesign.wolfpack.quizinator.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.database.Card;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.Util;

import java.util.Collections;
import java.util.List;

public class NextCardAdapter extends BaseAdapter {

    private Context context;
    private List<Card> cards;

    public NextCardAdapter(Context context, List<Card> values) {
        this.context = context;
        cards = values;
    }

    public void removeItem(int position) {
        cards.remove(position);
        notifyDataSetChanged();
    }

    public void shuffle() {
        Collections.shuffle(cards);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Object getItem(int position) {
        return cards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return cards.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.array_adapter_list_of_cards, null);
        }
        Card card = cards.get(position);

        if (card != null) {
            ImageView icon = (ImageView) v.findViewById(R.id.card_type_icon);
            Util.updateCardTypeIcon(card, icon);
            TextView question = (TextView) v.findViewById(R.id.array_adapter_card_question);
            question.setText(card.getQuestion());
        }

        return v;
    }
}