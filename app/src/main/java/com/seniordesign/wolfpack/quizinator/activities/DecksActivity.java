package com.seniordesign.wolfpack.quizinator.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Util;
import com.seniordesign.wolfpack.quizinator.adapters.DeckAdapter;
import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.database.Card;
import com.seniordesign.wolfpack.quizinator.database.Deck;
import com.seniordesign.wolfpack.quizinator.database.QuizDataSource;
import com.seniordesign.wolfpack.quizinator.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DecksActivity extends AppCompatActivity
        implements
            AdapterView.OnItemClickListener,
            AdapterView.OnItemLongClickListener {

    QuizDataSource dataSource;

    private static final String TAG = "ACT_DA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decks);
        setTitle(Constants.DECKS);
        initializeDB();
        fillListOfDecks(dataSource.getAllDecks());
    }

    private boolean initializeDB(){
        dataSource = new QuizDataSource(this);
        return dataSource.open();
    }

    private void fillListOfDecks(List<Deck> values){
        final ListView listView = (ListView)findViewById(R.id.list_of_decks);
        DeckAdapter adapter = new DeckAdapter(this,
                android.R.layout.simple_list_item_1, values);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        Intent intent = new Intent(this, EditDeckActivity.class);
        Gson gson = new Gson();
        String jsonDeck = gson.toJson(dataSource.getAllDecks().get(position));
        intent.putExtra("Deck", jsonDeck);
        startActivity(intent);
    }

    /**
     * @return true to prevent onItemClick from being called
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        createSharingDialog(position).show();
        return true;
    }

    private AlertDialog createSharingDialog(final int position){
        View innerDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_sharing, null);
        final EditText fileNameTextView = (EditText) innerDialogView.findViewById(R.id.dialog_file_name);
        TextView fileExtensionTextView = (TextView) innerDialogView.findViewById(R.id.dialog_file_extension);

        final Deck selectedDeck =  dataSource.getAllDecks().get(position);
        final String selectedDeckFileName = selectedDeck.getDeckName();
        final String fileExtension = ".json.deck.quizinator";

        fileNameTextView.setText(selectedDeckFileName);
        fileExtensionTextView.setText(fileExtension);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                    if(fileNameTextView.getText().toString().isEmpty())
                        selectedDeck.toJsonFile(Util.defaultDirectory(), selectedDeckFileName + fileExtension);
                    else
                        selectedDeck.toJsonFile(Util.defaultDirectory(), fileNameTextView.getText().toString() + fileExtension);
                }
            });
        return builder.create();
    }

    public void newDeckClick(View view){
        Intent intent = new Intent(this, EditDeckActivity.class);
        Gson gson = new Gson();
        Deck deck = new Deck();
            deck.setDeckName("New Deck");
            deck.setDuplicateCards(true);
            deck.setCards(new ArrayList<Card>());
        String jsonDeck = gson.toJson(deck);
        intent.putExtra("Deck",jsonDeck);
        startActivity(intent);
    }

    public void importDeckClick(View view){
        DialogProperties properties = new DialogProperties();
            properties.selection_mode = DialogConfigs.SINGLE_MODE;
            properties.selection_type = DialogConfigs.FILE_SELECT;
            properties.root = Util.defaultDirectory();
            properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
            properties.offset = new File(DialogConfigs.DEFAULT_DIR);
            properties.extensions = new String[]{"deck.quizinator"};
        FilePickerDialog dialog = new FilePickerDialog(DecksActivity.this, properties);
            dialog.setTitle("Select a Deck");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                //files is the array of the paths of files selected by the Application User.
                Deck newDeck = (new Deck()).fromJsonFile(files[0]);
                dataSource.createDeck(newDeck);
                fillListOfDecks(dataSource.getAllDecks());
//                ListView listView = (ListView)findViewById(R.id.list_of_decks);
//                ((DeckAdapter)listView.getAdapter()).notifyDataSetChanged();
            }
        });
        dialog.show();
    }

    protected void onResume() {
        super.onResume();
        dataSource.open();
        fillListOfDecks(dataSource.getAllDecks());
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }
}
