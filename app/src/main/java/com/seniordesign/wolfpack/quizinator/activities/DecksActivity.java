package com.seniordesign.wolfpack.quizinator.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
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
import android.widget.Toast;

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

    private QuizDataSource dataSource;
    private FilePickerDialog dialog;

    private static final String TAG = "ACT_DA";
    private static final String VIEW_ACTION = "android.intent.action.VIEW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decks);
        setTitle(Constants.DECKS);
        initializeDB();
        if (getIntent() != null &&
                getIntent().getAction() != null &&
                getIntent().getAction().equals(VIEW_ACTION))
            importDeck(getIntent().getData().getPath());
        fillListOfDecks(dataSource.getAllDecks());
    }

    //Add this method to show Dialog when the required permission has been granted to the app.
    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String permissions[],@NonNull int[] grantResults) {
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
                    Toast.makeText(DecksActivity.this, "Permission is required for getting list of Decks", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean initializeDB(){
        dataSource = new QuizDataSource(this);
        return dataSource.open();
    }

    private void fillListOfDecks(List<Deck> values){
        DeckAdapter adapter = new DeckAdapter(this,
                android.R.layout.simple_list_item_1, values);
        final ListView listView = (ListView)findViewById(R.id.list_of_decks);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
            listView.setOnItemLongClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        Intent intent = new Intent(this, EditDeckActivity.class);
        String jsonDeck = (new Gson()).toJson(dataSource.getAllDecks().get(position));
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
                    File deckFile = createDeckFile(fileNameTextView, selectedDeck, selectedDeckFileName, fileExtension);
                    if(deckFile != null){
                        shareDeckFile(deckFile);
                    }
                    //TODO -> set up receiving file with custom mime type
                        //url -> http://stackoverflow.com/questions/1733195/android-intent-filter-for-a-particular-file-extension/2062112#2062112
                }
            });
            builder.setNeutralButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    File file = createDeckFile(fileNameTextView, selectedDeck, selectedDeckFileName, fileExtension);
                    if (file != null) {
                        Toast.makeText(DecksActivity.this, "File Saved!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        return builder.create();
    }

    private File createDeckFile(EditText fileNameTextView, Deck selectedDeck, String selectedDeckFileName, String fileExtension){
        File file;
        if(!Util.isExternalStorageWritable() && !Util.isExternalStorageReadable())
            return null;
        if(fileNameTextView.getText().toString().isEmpty())
            file = selectedDeck.toJsonFile(Util.defaultDirectory(), selectedDeckFileName + fileExtension);
        else
            file = selectedDeck.toJsonFile(Util.defaultDirectory(), fileNameTextView.getText().toString() + fileExtension);
        return file;
    }

    private void shareDeckFile(File deckFile){
        Uri fileUri = null;
        try {
            fileUri = FileProvider.getUriForFile(DecksActivity.this, "com.seniordesign.wolfpack.quizinator", deckFile);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "The selected file can't be shared: " + deckFile.getName());
        }
        if(fileUri != null){
            Intent shareIntent = new Intent();
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                shareIntent.setType("*/*");
            startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.sharing_send_to)));
        }
//        if(deckFile.exists()){
//            deckFile.delete();
//        }
    }

    public void newDeckClick(View view){
        Intent intent = new Intent(this, EditDeckActivity.class);
        Deck deck = new Deck();
            deck.setDeckName("New Deck");
            deck.setDuplicateCards(true);
            deck.setCards(new ArrayList<Card>());
        intent.putExtra("Deck", (new Gson()).toJson(deck));
        startActivity(intent);
    }

    public void importDeckClick(View view){
        File defaultPath = Util.defaultDirectory();
        DialogProperties properties = new DialogProperties();
            properties.selection_mode = DialogConfigs.SINGLE_MODE;
            properties.selection_type = DialogConfigs.FILE_SELECT;
            properties.root = defaultPath;
            properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
            properties.offset = new File(DialogConfigs.DEFAULT_DIR);
            properties.extensions = new String[]{"deck.quizinator"};
        dialog = new FilePickerDialog(DecksActivity.this, properties);
            dialog.setTitle("Select a Deck");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                //files is the array of the paths of files selected by the Application User.
                importDeck(files[0]);
            }
        });
        dialog.show();
    }

    private void importDeck(String deckPath){
        Deck newDeck = (new Deck()).fromJsonFilePath(deckPath);
        if(newDeck != null)
            dataSource.importDeck(newDeck);
        fillListOfDecks(dataSource.getAllDecks());
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
