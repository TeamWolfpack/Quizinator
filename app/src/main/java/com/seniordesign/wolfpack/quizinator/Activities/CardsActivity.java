package com.seniordesign.wolfpack.quizinator.Activities;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Adapters.CardAdapter;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Deck.Deck;
import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;
import com.seniordesign.wolfpack.quizinator.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.apptik.widget.multiselectspinner.BaseMultiSelectSpinner;
import io.apptik.widget.multiselectspinner.MultiSelectSpinner;

public class CardsActivity extends AppCompatActivity {

    private MultiSelectSpinner cardTypeSpinner;
    private List<String> selectedCardTypes;
    private List<String> cardTypes = new ArrayList<>(Arrays.asList("True/False", "Multiple Choice"));
//    private List<String> cardTypes = new ArrayList<>(Arrays.asList("True/False", "Multiple Choice", "Free Response", "Verbal Response"));

    private static final String TAG = "ACT_CA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        cardTypeSpinner = (MultiSelectSpinner) findViewById(R.id.card_type_spinner);
        selectedCardTypes = new ArrayList<>();
        ArrayAdapter<String> cardTypeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, cardTypes);
        cardTypeSpinner
                .setListAdapter(cardTypeAdapter)
                .setAllCheckedText("All Types")
                .setAllUncheckedText("None Selected")
                .setSelectAll(true)
                .setMinSelectedItems(1)
                .setListener(new BaseMultiSelectSpinner.MultiSpinnerListener() {
                    @Override
                    public void onItemsSelected(boolean[] selected) {
                        selectedCardTypes.clear();
                        for (int i = 0; i < selected.length; i++) {
                            if (selected[i]) {
                                try{
                                    selectedCardTypes.add(shortFormCardType(cardTypes.get(i)));
                                }catch (Exception e){
                                    System.out.println(e.getMessage());
                                }
                            }
                        }
//                        Deck filteredDeck = deck.filter(tempRules);

//                        if (!isInputEmpty(cardCountInput) &&
//                                Integer.valueOf(cardCountInput.getText().toString()) > filteredDeck.getCards().size())
//                            cardCountInput.setText("" + filteredDeck.getCards().size());
//
//                        filterCardCount(filteredDeck);
                        ArrayList<String> chosenTypes = new ArrayList<>();
                        for(int i = 0; i < selectedCardTypes.size(); i++){
                            chosenTypes.add(selectedCardTypes.get(i));
                        }
                    }
                });
    }

    private void fillListOfCards(List<Card> values){
        final ListView listView = (ListView)findViewById(R.id.list_of_cards);
        final CardAdapter adapter = new CardAdapter(this,
                R.layout.array_adapter_list_of_cards, values);
//        final CardAdapter adapter = new CardAdapter(this,
//                android.R.layout.simple_list_item_1, values);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(parent.getContext(), EditCardActivity.class);
                intent.putExtra("_id", ((Card)listView.getItemAtPosition(position)).getId()); //
                startActivity(intent);
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
    public String shortFormCardType(String type) throws Exception {
        String shortForm = null;

        if (type.equals("True/False"))
            shortForm = "TF";
        else if (type.equals("Multiple Choice"))
            shortForm = "MC";
        else if (type.equals("Free Response"))
            shortForm = "FR";
        else if (type.equals("Verbal Response"))
            shortForm = "VR";
        else
            throw new Exception("Invalid Card Type");
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
