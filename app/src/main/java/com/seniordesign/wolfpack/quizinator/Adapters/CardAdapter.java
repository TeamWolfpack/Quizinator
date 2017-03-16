package com.seniordesign.wolfpack.quizinator.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.database.Card;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.Util;

import java.util.List;

public class CardAdapter extends ArrayAdapter<Card> {

    public CardAdapter(Context context, int resource) {
        super(context, resource);
    }

    public CardAdapter(Context context, int resource, List<Card> values) {
        super(context, resource, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.array_adapter_list_of_cards, null);
        }

        Card card = getItem(position);

        if (card != null) {
            ImageView icon = (ImageView) v.findViewById(R.id.card_type_icon);
            Util.updateCardTypeIcon(card, icon);
            TextView question = (TextView) v.findViewById(R.id.array_adapter_card_question);
            question.setText(card.getQuestion());
        }

        return v;
    }
}
