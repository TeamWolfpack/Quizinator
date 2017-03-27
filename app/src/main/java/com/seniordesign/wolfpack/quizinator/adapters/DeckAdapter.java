package com.seniordesign.wolfpack.quizinator.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.Constants.CARD_TYPES;
import com.seniordesign.wolfpack.quizinator.Util;
import com.seniordesign.wolfpack.quizinator.database.Card;
import com.seniordesign.wolfpack.quizinator.database.Deck;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.views.CardIcon;

import java.util.List;


public class DeckAdapter extends ArrayAdapter<Deck> {

    public DeckAdapter(Context context, int resource) {
        super(context, resource);
    }

    public DeckAdapter(Context context, int resource, List<Deck> values) {
        super(context, resource, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.array_adapter_list_of_decks, null);
        }

        Util.setCardIconToMC((CardIcon)v.findViewById(R.id.array_adapter_deck_MC_icon), 75);
        Util.setCardIconToTF((CardIcon)v.findViewById(R.id.array_adapter_deck_TF_icon), 75);
        Util.setCardIconToFR((CardIcon)v.findViewById(R.id.array_adapter_deck_FR_icon), 75);
        Util.setCardIconToVR((CardIcon)v.findViewById(R.id.array_adapter_deck_VR_icon), 75);

        Deck deck = getItem(position);

        if (deck != null) {
            int mcCount = 0;
            int tfCount = 0;
            int frCount = 0;
            int vrCount = 0;

            for(Card card : deck.getCards()){
                CARD_TYPES cardType = CARD_TYPES.values()[card.getCardType()];
                switch(cardType) {
                    case TRUE_FALSE:
                        tfCount++;
                        break;
                    case MULTIPLE_CHOICE:
                        mcCount++;
                        break;
                    case FREE_RESPONSE:
                        frCount++;
                        break;
                    case VERBAL_RESPONSE:
                        vrCount++;
                        break;
                }
            }

            TextView name = (TextView) v.findViewById(R.id.array_adapter_deck_name);
            TextView size = (TextView) v.findViewById(R.id.array_adapter_deck_size);
            TextView mcCountView = (TextView) v.findViewById(R.id.array_adapter_deck_MC_count);
            TextView tfCountView = (TextView) v.findViewById(R.id.array_adapter_deck_TF_count);
            TextView frCountView = (TextView) v.findViewById(R.id.array_adapter_deck_FR_count);
            TextView vrCountView = (TextView) v.findViewById(R.id.array_adapter_deck_VR_count);

            name.setText(deck.getDeckName());
            size.setText(""+deck.getCards().size()+" Cards");
            mcCountView.setText(String.valueOf(mcCount));
            tfCountView.setText(String.valueOf(tfCount));
            frCountView.setText(String.valueOf(frCount));
            vrCountView.setText(String.valueOf(vrCount));
            System.out.println(deck.getDeckName());
        }

        return v;
    }
}
