package com.seniordesign.wolfpack.quizinator.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.seniordesign.wolfpack.quizinator.R;

public class EditCardFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_card,
                container, false);
        return view; // Inflate the layout for this fragment
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
            .setTitle("Edit Card")
            .setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        // do something...
                        handleSavedButtonClick(getView());
                    }
                }
            )
            .setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        dialog.dismiss();
                    }
                }
            )
            .create();
    }

    public static void createPopUp(){

    }

    public static void handleSavedButtonClick(View v){
        EditText pointsValue = (EditText) v.findViewById(R.id.edit_card_points_value);
        EditText cardQuestionValue = (EditText) v.findViewById(R.id.edit_card_question_value);
        if(pointsValue == null || cardQuestionValue == null)
            return;
        Spinner cardType = (Spinner) v.findViewById(R.id.edit_card_card_type_spinner);
        switch (cardType.toString()){
            case "T/F":
                //grab group
                RadioGroup radioGroupForTrueFalse = (RadioGroup) v.findViewById(R.id.edit_card_true_or_false);
                break;
            case "Multiple Choice":
                EditText correctAnswer1 = (EditText) v.findViewById(R.id.edit_card_answer_field_1);
                EditText wrongAnswer1 = (EditText) v.findViewById(R.id.edit_card_answer_field_2);
                EditText wrongAnswer2 = (EditText) v.findViewById(R.id.edit_card_answer_field_3);
                EditText wrongAnswer3 = (EditText) v.findViewById(R.id.edit_card_answer_field_4);
                break;
            default:
                EditText correctAnswer2 = (EditText) v.findViewById(R.id.edit_card_answer_field_1);
                break;
        }
        //save the database
        //close fragment and return to card list
    }

    private Boolean updateViewOnCardTypeChanged(){
        //take card reference and populate given fields

        return true;
    }
}
