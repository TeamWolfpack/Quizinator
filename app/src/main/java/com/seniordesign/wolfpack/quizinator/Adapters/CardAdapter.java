package com.seniordesign.wolfpack.quizinator.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.Database.Card;
import com.seniordesign.wolfpack.quizinator.R;

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
            Constants.CARD_TYPES cardType = Constants.CARD_TYPES.values()[card.getCardType()];
            switch(cardType) {
                case TRUE_FALSE:
                    icon.setImageResource(R.drawable.tf_icon);
                    break;
                case MULTIPLE_CHOICE:
                    icon.setImageResource(R.drawable.mc_icon);
                    break;
                case FREE_RESPONSE:
                    icon.setImageResource(R.drawable.fr_icon);
                    break;
                case VERBAL_RESPONSE:
                    icon.setImageResource(R.drawable.vr_icon);
                    break;
            }
            TextView question = (TextView) v.findViewById(R.id.array_adapter_card_question);
            question.setText(card.getQuestion());
        }

        return v;
    }
}
