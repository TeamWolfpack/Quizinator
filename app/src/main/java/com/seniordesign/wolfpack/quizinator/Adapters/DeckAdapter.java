package com.seniordesign.wolfpack.quizinator.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Deck.Deck;
import com.seniordesign.wolfpack.quizinator.R;

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

        Deck deck = getItem(position);

        if (deck != null) {
            int mcCount = 0;
            int tfCount = 0;
            int frCount = 0;
            int vrCount = 0;

            for(Card card : deck.getCards()){
                switch(card.getCardType()) {
                    case "TF":
                        tfCount++;
                        break;
                    case "MC":
                        mcCount++;
                        break;
                    case "FR":
                        frCount++;
                        break;
                    case "VR":
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
            mcCountView.setText(""+mcCount);
            tfCountView.setText(""+tfCount);
            frCountView.setText(""+frCount);
            vrCountView.setText(""+vrCount);
            System.out.println(deck.getDeckName());
        }

        return v;
    }
}
