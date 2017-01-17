package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.seniordesign.wolfpack.quizinator.Adapters.CardAdapter;
import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Card.CardDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Deck.Deck;
import com.seniordesign.wolfpack.quizinator.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.apptik.widget.multiselectspinner.BaseMultiSelectSpinner;
import io.apptik.widget.multiselectspinner.MultiSelectSpinner;

public class CardsActivity extends AppCompatActivity {

    private MultiSelectSpinner cardTypeSpinner;
    private List<String> selectedCardTypes;
    private List<String> cardTypes = new ArrayList<>(Arrays.asList(Constants.LONG_TRUE_FALSE, Constants.LONG_MULTIPLE_CHOICE));
//    private List<String> cardTypes = new ArrayList<>(Arrays.asList(Constants.LONG_TRUE_FALSE, Constants.LONG_MULTIPLE_CHOICE, Constants.LONG_FREE_RESPONSE, Constants.LONG_VERBAL_RESPONSE));

    private CardDataSource cardDataSource;

    private static final String TAG = "ACT_CA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
        setTitle(Constants.CARDS);
        initializeDB();

        cardTypeSpinner = (MultiSelectSpinner) findViewById(R.id.card_type_spinner);
        selectedCardTypes = new ArrayList<>();
        ArrayAdapter<String> cardTypeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, cardTypes);
        cardTypeSpinner
                .setListAdapter(cardTypeAdapter)
                .setAllCheckedText(Constants.ALL_CARD_TYPES)
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
                        ArrayList<String> chosenTypes = new ArrayList<>();
                        for(int i = 0; i < selectedCardTypes.size(); i++){
                            chosenTypes.add(selectedCardTypes.get(i));
                        }
                        fillListOfCards(cardDataSource.filterCards(chosenTypes));
                    }
                });
        fillListOfCards(cardDataSource.filterCards(cardTypes));
    }

    private boolean initializeDB(){
        cardDataSource = new CardDataSource(this);
        return cardDataSource.open();
    }

    private void initializeList(){
        fillListOfCards(cardDataSource.filterCards(cardTypes));
    }

    private void fillListOfCards(List<Card> values){
        final ListView listView = (ListView)findViewById(R.id.list_of_cards);
        final CardAdapter adapter = new CardAdapter(this,
                R.layout.array_adapter_list_of_cards, values);
//        final CardAdapter adapter = new CardAdapter(this,
//                android.R.layout.simple_list_item_1, values);
        listView.setAdapter(adapter);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                Intent intent = new Intent(parent.getContext(), EditCardActivity.class);
//                intent.putExtra("_id", ((Card)listView.getItemAtPosition(position)).getId()); //
//                startActivity(intent);
//            }
//        });
    }

    /*
     * @author leonardj (12/16/16)
     */
    private String shortFormCardType(String type) throws Exception {
        if (type.equals(Constants.LONG_TRUE_FALSE))
            return Constants.SHORT_TRUE_FALSE;
        else if (type.equals(Constants.LONG_MULTIPLE_CHOICE))
            return Constants.SHORT_MULTIPLE_CHOICE;
        else if (type.equals(Constants.LONG_FREE_RESPONSE))
            return Constants.SHORT_FREE_RESPONSE;
        else if (type.equals(Constants.LONG_VERBAL_RESPONSE))
            return Constants.SHORT_VERBAL_RESPONSE;
        else
            throw new Exception("Invalid Card Type");
    }

    /*
  * @author leonardj (12/16/16)
  */

    private String longFormCardType(String type) throws Exception {
        if (type.equals(Constants.SHORT_TRUE_FALSE))
            return Constants.LONG_TRUE_FALSE;
        else if (type.equals(Constants.SHORT_MULTIPLE_CHOICE))
            return Constants.LONG_MULTIPLE_CHOICE;
        else if (type.equals(Constants.SHORT_FREE_RESPONSE))
            return Constants.LONG_FREE_RESPONSE;
        else if (type.equals(Constants.SHORT_VERBAL_RESPONSE))
            return Constants.LONG_VERBAL_RESPONSE;
        else
            throw new Exception("Invalid Card Type");
    }
}
