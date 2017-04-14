package com.seniordesign.wolfpack.quizinator.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.seniordesign.wolfpack.quizinator.Util;
import com.seniordesign.wolfpack.quizinator.adapters.CardAdapter;
import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.Constants.CARD_TYPES;
import com.seniordesign.wolfpack.quizinator.database.Card;
import com.seniordesign.wolfpack.quizinator.database.QuizDataSource;
import com.seniordesign.wolfpack.quizinator.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.apptik.widget.multiselectspinner.BaseMultiSelectSpinner;
import io.apptik.widget.multiselectspinner.MultiSelectSpinner;

import static com.seniordesign.wolfpack.quizinator.Constants.CARD_TYPES.*;

public class CardsActivity extends AppCompatActivity {

    private MultiSelectSpinner cardTypeSpinner;
    private List<CARD_TYPES> selectedCardTypes;
    private List<CARD_TYPES> cardTypes = new ArrayList<>(
            Arrays.asList(
                    TRUE_FALSE,
                    MULTIPLE_CHOICE,
                    FREE_RESPONSE,
                    VERBAL_RESPONSE
            ));
    private String sortBy;

    private QuizDataSource dataSource;
    private FilePickerDialog dialog;

    private static final String TAG = "ACT_CA";
    private static final String VIEW_ACTION = "android.intent.action.VIEW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
        setTitle(Constants.CARDS);
        initializeDB();

        if (getIntent() != null &&
                getIntent().getAction() != null &&
                getIntent().getAction().equals(VIEW_ACTION))
            importCard(getIntent().getData().getPath());

        cardTypeSpinner = (MultiSelectSpinner) findViewById(R.id.card_type_spinner);
        selectedCardTypes = new ArrayList<>();

        initializeCardTypeSpinner();
        initializeSortBySpinner();
        initializeListOfCards();
    }

    //Add this method to show Dialog when the required permission has been granted to the app.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(dialog != null)
                    {   //Show dialog if the read permission has been granted.
                        dialog.show();
                    }
                }
                else {
                    //Permission has not been granted. Notify the user.
                    Toast.makeText(CardsActivity.this, "Permission is required for getting list of Decks", Toast.LENGTH_SHORT).show();
                }
            }
        }
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
                        initializeListOfCards();
                    }
                });
        return true;
    }

    private void initializeSortBySpinner() {
        Spinner sortBySpinner = (Spinner) findViewById(R.id.card_sort_spinner);
        sortBySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortBy = parent.getSelectedItem().toString();
                initializeListOfCards();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do Nothing
            }
        });
    }

    private boolean initializeDB() {
        dataSource = new QuizDataSource(this);
        return dataSource.open();
    }

    private void initializeListOfCards() {
        final List<Card> values = dataSource.filterCards(selectedCardTypes, sortBy);
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
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            /**
             * @return true to prevent onItemClick from being called
             */
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                createSharingDialog(position).show();
                return true;
            }
        });
    }

    private android.support.v7.app.AlertDialog createSharingDialog(final int position){
        View innerDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_sharing, null);
        final EditText fileNameTextView = (EditText) innerDialogView.findViewById(R.id.dialog_file_name);
        TextView fileExtensionTextView = (TextView) innerDialogView.findViewById(R.id.dialog_file_extension);

        final Card selectedDeck =  dataSource.getAllCards().get(position);
        final String selectedDeckFileName = String.valueOf(selectedDeck.getId());
        final String fileExtension = ".json.card.quizinator";

        fileNameTextView.setText(selectedDeckFileName);
        fileExtensionTextView.setText(fileExtension);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setView(innerDialogView);
        builder.setTitle("Share");
        builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO -> send file using Android sharing services
            }
        });
        builder.setNeutralButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!Util.isExternalStorageWritable() && !Util.isExternalStorageReadable())
                    return;
                File dir = new File(Environment.getExternalStorageDirectory().getPath(), "Quizinator"); //stores to /storage/emulated/0
                if(!dir.exists())
                    dir.mkdirs();
                File file;
                if(fileNameTextView.getText().toString().isEmpty())
                    file = selectedDeck.toJsonFile(dir, selectedDeckFileName + fileExtension);
                else
                    file = selectedDeck.toJsonFile(dir, fileNameTextView.getText().toString() + fileExtension);
                if (file != null) {
                    Toast.makeText(CardsActivity.this, "File Saved!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return builder.create();
    }

    private void createEditCardDialog(final Card card, final boolean isNew){
        LayoutInflater li = LayoutInflater.from(this);
        final View promptsView = li.inflate(R.layout.fragment_edit_card, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder
            .setCancelable(false)
            .setTitle("Edit Card")
            .setPositiveButton(Constants.SAVE, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    editCard(card,promptsView,true);
                    dialog.cancel();
                }
            })
            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    initializeListOfCards();
                }
            });
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialogBuilder
                .setNegativeButton(Constants.CANCEL, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        if(isNew){
                            dataSource.deleteCard(card);
                        }
                        dialog.cancel();
                    }
                })
                .setNeutralButton(Constants.DELETE, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        createDeleteCardConfirmation(card);
                    }
                });
        }else{
            alertDialogBuilder
                .setNeutralButton(Constants.CANCEL, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        if(isNew){
                            dataSource.deleteCard(card);
                        }
                        dialog.cancel();
                    }
                })
                .setNegativeButton(Constants.DELETE, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        createDeleteCardConfirmation(card);
                    }
                });
        }
        AlertDialog alertDialog = alertDialogBuilder.create();
        populateEditCardValues(card, promptsView, true);
        alertDialog.show();
    }

    private void populateEditCardValues(final Card card, View promptsView, boolean initializeSpinner){
        ((EditText)promptsView.findViewById(R.id.edit_card_points_value)).setText(String.valueOf(card.getPoints()));
        ((EditText)promptsView.findViewById(R.id.edit_card_question_value)).setText(card.getQuestion());

        TableRow tableRow1FR = (TableRow)promptsView.findViewById(R.id.answer_row_1);
        TableRow tableRowMC1 = (TableRow)promptsView.findViewById(R.id.answer_row_2_1);
        TableRow tableRowMC2 = (TableRow)promptsView.findViewById(R.id.answer_row_2_2);
        TableRow tableRowMC3 = (TableRow)promptsView.findViewById(R.id.answer_row_2_3);
        TableRow tableRowTF = (TableRow)promptsView.findViewById(R.id.answer_row_3);

        switch (Constants.CARD_TYPES.values()[card.getCardType()]){
            case TRUE_FALSE:
                tableRow1FR.setVisibility(View.GONE);
                tableRowMC1.setVisibility(View.GONE);
                tableRowMC2.setVisibility(View.GONE);
                tableRowMC3.setVisibility(View.GONE);
                tableRowTF.setVisibility(View.VISIBLE);
                //grab group
                RadioGroup radioGroupForTrueFalse = (RadioGroup) promptsView.findViewById(R.id.edit_card_true_or_false);
                radioGroupForTrueFalse.setVisibility(View.VISIBLE);

                if(card.getCorrectAnswer() != null && Boolean.valueOf(card.getCorrectAnswer())){
                    RadioButton radioButton = (RadioButton) promptsView.findViewById(R.id.edit_card_true);
                    radioButton.setChecked(true);
                }
                else{
                    RadioButton radioButton = (RadioButton) promptsView.findViewById(R.id.edit_card_false);
                    radioButton.setChecked(true);
                }
                break;
            case MULTIPLE_CHOICE:
                tableRow1FR.setVisibility(View.VISIBLE);
                tableRowMC1.setVisibility(View.VISIBLE);
                tableRowMC2.setVisibility(View.VISIBLE);
                tableRowMC3.setVisibility(View.VISIBLE);
                tableRowTF.setVisibility(View.GONE);
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
                tableRow1FR.setVisibility(View.VISIBLE);
                tableRowMC1.setVisibility(View.GONE);
                tableRowMC2.setVisibility(View.GONE);
                tableRowMC3.setVisibility(View.GONE);
                tableRowTF.setVisibility(View.GONE);
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
                        initializeListOfCards();
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
        cardSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
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
                    card.setCorrectAnswer(String.valueOf(true));
                }
                else{
                    card.setCorrectAnswer(String.valueOf(false));
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
                card.setModeratorNeeded(Boolean.TRUE.toString());
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

    private void importCard(String cardPath){
        Card newCard = (new Card()).fromJsonFilePath(cardPath);
        if(newCard != null)
            dataSource.createCard(newCard);
        initializeListOfCards();
    }

    public void importCardClick(View view){
        File defaultPath = Util.defaultDirectory();
        DialogProperties properties = new DialogProperties();
            properties.selection_mode = DialogConfigs.SINGLE_MODE;
            properties.selection_type = DialogConfigs.FILE_SELECT;
            properties.root = defaultPath;
            properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
            properties.offset = new File(DialogConfigs.DEFAULT_DIR);
            properties.extensions = new String[]{"card.quizinator"};
        dialog = new FilePickerDialog(CardsActivity.this, properties);
            dialog.setTitle("Select a Card");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                //files is the array of the paths of files selected by the Application User.
                importCard(files[0]);
            }
        });
        dialog.show();
    }
}
