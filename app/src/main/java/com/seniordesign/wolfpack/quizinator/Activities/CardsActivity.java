package com.seniordesign.wolfpack.quizinator.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.Adapters.CardAdapter;
import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.Constants.CARD_TYPES;
import com.seniordesign.wolfpack.quizinator.Database.Card;
import com.seniordesign.wolfpack.quizinator.Database.QuizDataSource;
import com.seniordesign.wolfpack.quizinator.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.apptik.widget.multiselectspinner.BaseMultiSelectSpinner;
import io.apptik.widget.multiselectspinner.MultiSelectSpinner;

import static com.seniordesign.wolfpack.quizinator.Constants.CARD_TYPES.TRUE_FALSE;

public class CardsActivity extends AppCompatActivity {

    private MultiSelectSpinner cardTypeSpinner;
    private List<CARD_TYPES> selectedCardTypes;
    private List<CARD_TYPES> cardTypes = new ArrayList<>(
            Arrays.asList(
                    TRUE_FALSE,
                    CARD_TYPES.MULTIPLE_CHOICE,
                    CARD_TYPES.FREE_RESPONSE,
                    CARD_TYPES.VERBAL_RESPONSE
            ));

    private QuizDataSource dataSource;

    private static final String TAG = "ACT_CA";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
        setTitle(Constants.CARDS);
        initializeDB();

        cardTypeSpinner = (MultiSelectSpinner) findViewById(R.id.card_type_spinner);
        selectedCardTypes = new ArrayList<>();

        initializeCardTypeSpinner();

        initializeListOfCards(dataSource.filterCards(cardTypes));

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private Boolean initializeCardTypeSpinner(){
        ArrayAdapter<CARD_TYPES> cardTypeAdapter = new ArrayAdapter<>(this,
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
                        ArrayList<CARD_TYPES> chosenTypes = new ArrayList<>();
                        for (int i = 0; i < selectedCardTypes.size(); i++) {
                            chosenTypes.add(selectedCardTypes.get(i));
                        }
                        initializeListOfCards(dataSource.filterCards(chosenTypes));
                    }
                });
        return true;
    }

    private boolean initializeDB() {
        dataSource = new QuizDataSource(this);
        return dataSource.open();
    }

    private void initializeList() {
        initializeListOfCards(dataSource.filterCards(cardTypes));
    }

    private void initializeListOfCards(final List<Card> values) {
        final ListView listView = (ListView) findViewById(R.id.list_of_cards);
        final CardAdapter adapter = new CardAdapter(this,
                R.layout.array_adapter_list_of_cards, values);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                createEditCardDialog(values.get(position), false);
            }
        });
    }

    private void createEditCardDialog(final Card card, final boolean isNew){
        LayoutInflater li = LayoutInflater.from(this);
        final View promptsView = li.inflate(R.layout.fragment_edit_card, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("Edit Card")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        editCard(card,promptsView,true);
                        dialog.cancel();
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        if(isNew){
                            dataSource.deleteCard(card);
                        }
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        createDeleteCardConfirmation(card);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        initializeListOfCards(dataSource.filterCards(cardTypes));
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        populateEditCardValues(card, promptsView, true);
        alertDialog.show();
    }

    private void populateEditCardValues(final Card card, View promptsView, boolean initializeSpinner){
        ((EditText)promptsView.findViewById(R.id.edit_card_points_value)).setText(String.valueOf(card.getPoints()));
        ((EditText)promptsView.findViewById(R.id.edit_card_question_value)).setText(card.getQuestion());

        LinearLayout answerArea1 = ((LinearLayout)promptsView.findViewById(R.id.edit_card_answer_layout_1));
        int answerArea1CC = answerArea1.getChildCount();
        for(int i = 0; i < answerArea1CC; i++){
            answerArea1.getChildAt(i).setVisibility(View.INVISIBLE);
        }

        LinearLayout answerArea2 = ((LinearLayout)promptsView.findViewById(R.id.edit_card_answer_layout_2));
        int answerArea2CC = answerArea2.getChildCount();
        for(int i = 0; i < answerArea2CC; i++){
            answerArea2.getChildAt(i).setVisibility(View.INVISIBLE);
        }

        switch (Constants.CARD_TYPES.values()[card.getCardType()]){
            case TRUE_FALSE:
                answerArea1.setVisibility(View.INVISIBLE);
                answerArea2.setVisibility(View.VISIBLE);
                //grab group
                RadioGroup radioGroupForTrueFalse = (RadioGroup) promptsView.findViewById(R.id.edit_card_true_or_false);
                radioGroupForTrueFalse.setVisibility(View.VISIBLE);

                if(card.getCorrectAnswer() != null && card.getCorrectAnswer().equals("True")){
                    RadioButton radioButton = (RadioButton) promptsView.findViewById(R.id.edit_card_true);
                    radioButton.setChecked(true);
                }
                else{
                    RadioButton radioButton = (RadioButton) promptsView.findViewById(R.id.edit_card_false);
                    radioButton.setChecked(true);
                }

                break;
            case MULTIPLE_CHOICE:
                answerArea1.setVisibility(View.VISIBLE);
                answerArea2.setVisibility(View.INVISIBLE);
                EditText correctAnswer1 = (EditText) promptsView.findViewById(R.id.edit_card_answer_field_1);
                correctAnswer1.setText(card.getCorrectAnswer());
                correctAnswer1.setVisibility(View.VISIBLE);

                String[] answers = card.getPossibleAnswers();
                if (answers == null) {
                    answers = new String[] {"", "", "", ""};
                }
                EditText wrongAnswer1 = (EditText) promptsView.findViewById(R.id.edit_card_answer_field_2);
                wrongAnswer1.setText(answers.length > 1 ? answers[1] : "");
                wrongAnswer1.setVisibility(View.VISIBLE);
                EditText wrongAnswer2 = (EditText) promptsView.findViewById(R.id.edit_card_answer_field_3);
                wrongAnswer2.setText(answers.length > 2 ? answers[2] : "");
                wrongAnswer2.setVisibility(View.VISIBLE);
                EditText wrongAnswer3 = (EditText) promptsView.findViewById(R.id.edit_card_answer_field_4);
                wrongAnswer3.setText(answers.length > 3 ? answers[3] : "");
                wrongAnswer3.setVisibility(View.VISIBLE);
                break;
            default:
                answerArea1.setVisibility(View.VISIBLE);
                answerArea2.setVisibility(View.INVISIBLE);
                EditText correctAnswer2 = (EditText) promptsView.findViewById(R.id.edit_card_answer_field_1);
                correctAnswer2.setText(card.getCorrectAnswer());
                correctAnswer2.setVisibility(View.VISIBLE);
                break;
        }
        if (initializeSpinner)
            initializeCardTypeSpinnerSingleSelection(card, promptsView);
    }

    private void createDeleteCardConfirmation(final Card card){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("Delete Card?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dataSource.deleteCard(card);
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        initializeListOfCards(dataSource.filterCards(cardTypes));
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void initializeCardTypeSpinnerSingleSelection(final Card card, final View promptsView){
        Spinner cardSpinner = (Spinner)promptsView.findViewById(R.id.edit_card_card_type_spinner);
        List<String> cardTypesAdapter = new ArrayList<>();
        for(Constants.CARD_TYPES cardType : Constants.CARD_TYPES.values()){
            cardTypesAdapter.add(cardType.toString());
        }
        final ArrayAdapter<String> cardAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, cardTypesAdapter);
        cardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cardSpinner.setAdapter(cardAdapter);
        cardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                card.setCardType(CARD_TYPES.values()[position]);
                populateEditCardValues(card,promptsView,false);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { /* Do nothing */ }
        });
        cardSpinner.setSelection(card.getCardType());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void editCard(final Card card,View promptsView, boolean saveCard){
        Spinner cardSpinner = (Spinner)promptsView.findViewById(R.id.edit_card_card_type_spinner);
        card.setCardType(CARD_TYPES.values()[cardSpinner.getSelectedItemPosition()]);

        String pointsStr = ""+((TextView)promptsView.findViewById(R.id.edit_card_points_value)).getText();
        if(pointsStr.equals("")){
            card.setPoints(0);
        }
        else {
            card.setPoints(Integer.parseInt(pointsStr));
        }

        card.setQuestion(""+((TextView)promptsView.findViewById(R.id.edit_card_question_value)).getText());

        switch (Constants.CARD_TYPES.values()[card.getCardType()]) {
            case TRUE_FALSE:
                RadioGroup radioGroupForTrueFalse = (RadioGroup) promptsView.findViewById(R.id.edit_card_true_or_false);
                int checkedID = radioGroupForTrueFalse.getCheckedRadioButtonId();

                if((checkedID == R.id.edit_card_true)){
                    card.setCorrectAnswer("True");
                }
                else{
                    card.setCorrectAnswer("False");
                }
                break;
            case MULTIPLE_CHOICE:
                String[] possibleAnswers = new String[4];
                EditText correctAnswer1 = (EditText) promptsView.findViewById(R.id.edit_card_answer_field_1);
                card.setCorrectAnswer("" + correctAnswer1.getText());
                possibleAnswers[0] = "" + correctAnswer1.getText();
                EditText wrongAnswer1 = (EditText) promptsView.findViewById(R.id.edit_card_answer_field_2);
                possibleAnswers[1] = "" + wrongAnswer1.getText();
                EditText wrongAnswer2 = (EditText) promptsView.findViewById(R.id.edit_card_answer_field_3);
                possibleAnswers[2] = "" + wrongAnswer2.getText();
                EditText wrongAnswer3 = (EditText) promptsView.findViewById(R.id.edit_card_answer_field_4);
                possibleAnswers[3] = "" + wrongAnswer3.getText();
                card.setPossibleAnswers(possibleAnswers);

                break;
            default:
                EditText correctAnswer2 = (EditText) promptsView.findViewById(R.id.edit_card_answer_field_1);
                card.setCorrectAnswer("" + correctAnswer2.getText());
                break;
        }
        if(saveCard) {
            dataSource.updateCard(card);
        }
        else{
            populateEditCardValues(card,promptsView, false);
        }
    }

    public void newCardClick(View view){
        Card card = dataSource.createCard(new Card());
        createEditCardDialog(card, true);
    }
}
