package com.seniordesign.wolfpack.quizinator.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.seniordesign.wolfpack.quizinator.Adapters.CardAdapter;
import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.Constants.CARD_TYPES;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.QuizDataSource;
import com.seniordesign.wolfpack.quizinator.Fragments.EditCardDialog;
import com.seniordesign.wolfpack.quizinator.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.apptik.widget.multiselectspinner.BaseMultiSelectSpinner;
import io.apptik.widget.multiselectspinner.MultiSelectSpinner;

public class CardsActivity extends AppCompatActivity {

    private MultiSelectSpinner cardTypeSpinner;
    private List<String> selectedCardTypes;
    private List<String> cardTypes = new ArrayList<>(
            Arrays.asList(
                    CARD_TYPES.TRUE_FALSE.toString(),
                    CARD_TYPES.MULTIPLE_CHOICE.toString(),
                    CARD_TYPES.FREE_RESPONSE.toString(),
                    CARD_TYPES.VERBAL_RESPONSE.toString()
            ));
//    private List<String> cardTypes = new ArrayList<>(Arrays.asList(Constants.LONG_TRUE_FALSE, Constants.LONG_MULTIPLE_CHOICE, Constants.LONG_FREE_RESPONSE, Constants.LONG_VERBAL_RESPONSE));

    private QuizDataSource dataSource;

    private static final String TAG = "ACT_CA";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
        setTitle(Constants.CARDS);
        //initializeDB();

        cardTypeSpinner = (MultiSelectSpinner) findViewById(R.id.card_type_spinner);
        selectedCardTypes = new ArrayList<>();

        initializeCardTypeSpinner();

        fillListOfCards(new ArrayList<Card>(){{add(new Card() {{setId(1);setCardType(CARD_TYPES.MULTIPLE_CHOICE);setQuestion("test question");setCorrectAnswer("correct");setPossibleAnswers(new String[]{"correct","wrong1","wrong2","wrong3",});setModeratorNeeded(String.valueOf(false));setPoints(10);}});}});

        //fillListOfCards(dataSource.filterCards(cardTypes));TODO -> make me work with the DB

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private Boolean initializeCardTypeSpinner(){
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
                                try {
                                    selectedCardTypes.add(cardTypes.get(i));
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                        }
                        ArrayList<String> chosenTypes = new ArrayList<>();
                        for (int i = 0; i < selectedCardTypes.size(); i++) {
                            chosenTypes.add(selectedCardTypes.get(i));
                        }
                        fillListOfCards(dataSource.filterCards(chosenTypes));
                    }
                });
        return true;
    }

    private boolean initializeDB() {
        dataSource = new QuizDataSource(this);
        return dataSource.open();
    }

    private void initializeList() {
        fillListOfCards(dataSource.filterCards(cardTypes));
    }

    private void fillListOfCards(List<Card> values) {
        final ListView listView = (ListView) findViewById(R.id.list_of_cards);
        final CardAdapter adapter = new CardAdapter(this,
                R.layout.array_adapter_list_of_cards, values);
//        final CardAdapter adapter = new CardAdapter(this,
//                android.R.layout.simple_list_item_1, values);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                createDialog();
            }
        });
    }

    public void createDialog(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.fragment_edit_card, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("Edit Card")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        //save card
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void onSaveButtonClicked(View v) {
        EditCardDialog.handleSavedButtonClick(v);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Cards Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
