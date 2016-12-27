package com.seniordesign.wolfpack.quizinator.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import com.seniordesign.wolfpack.quizinator.Database.Deck.Deck;
import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;
import com.seniordesign.wolfpack.quizinator.R;

import java.util.ArrayList;
import java.util.List;

import io.apptik.widget.multiselectspinner.BaseMultiSelectSpinner;
import io.apptik.widget.multiselectspinner.MultiSelectSpinner;

public class CardsActivity extends AppCompatActivity {

    private MultiSelectSpinner cardTypeSpinner;
    private List<String> selectedCardTypes;
    private List<String> cardTypeOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        cardTypeSpinner = (MultiSelectSpinner) findViewById(R.id.card_type_spinner);
        selectedCardTypes = new ArrayList<>();
        cardTypeOptions = formatCardTypes(deck);
        ArrayAdapter<String> cardTypeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, cardTypeOptions);
        cardTypeSpinner
                .setListAdapter(cardTypeAdapter)
                .setAllCheckedText("All Types")
                .setAllUncheckedText("None Selected")
                .setMinSelectedItems(1)
                .setListener(new BaseMultiSelectSpinner.MultiSpinnerListener() {
                    @Override
                    public void onItemsSelected(boolean[] selected) {
                        selectedCardTypes.clear();
                        for (int i = 0; i < selected.length; i++) {
                            if (selected[i])
                                selectedCardTypes.add(shortFormCardType(cardTypeOptions.get(i)));
                        }
                        Rules tempRules = new Rules();
                        tempRules.setCardTypes(gson.toJson(selectedCardTypes));
                        Deck filteredDeck = deck.filter(tempRules);

                        if (!isInputEmpty(cardCountInput) &&
                                Integer.valueOf(cardCountInput.getText().toString()) > filteredDeck.getCards().size())
                            cardCountInput.setText("" + filteredDeck.getCards().size());

                        filterCardCount(filteredDeck);
                    }
                });
    }

    /*
     * @author leonardj (12/23/16)
     */
    private boolean isInputEmpty(EditText input) {
        return input.getText().toString().equals("");
    }

    /*
     * @author leonardj (12/16/16)
     */
    public List<String> formatCardTypes(Deck deck) {
        ArrayList<String> types = new ArrayList<>();
        if (deck == null) {
            return types;
        }
        for (String type : deck.getCardTypes()) {
            if (type.equals("TF"))
                types.add("True/False");
            else if (type.equals("MC"))
                types.add("Multi-Choice");
            else if (type.equals("FR"))
                types.add("Free Response");
            else if (type.equals("VR"))
                types.add("Verbal Response");
        }
        return types;
    }

    /*
     * @author leonardj (12/16/16)
     */
    public String shortFormCardType(String type) {
        String shortForm = null;

        if (type.equals("True/False"))
            shortForm = "TF";
        else if (type.equals("Multi-Choice"))
            shortForm = "MC";
        else if (type.equals("Free Response"))
            shortForm = "FR";
        else if (type.equals("Verbal Response"))
            shortForm = "VR";
        return shortForm;
    }

    /*
  * @author leonardj (12/16/16)
  */


    public String longFormCardType(String type) {
        String longForm = null;

        if (type.equals("TF"))
            longForm = "True/False";
        else if (type.equals("MC"))
            longForm = "Multi-Choice";
        else if (type.equals("FR"))
            longForm = "Free Response";
        else if (type.equals("VR"))
            longForm = "Verbal Response";
        return longForm;

    }
}
