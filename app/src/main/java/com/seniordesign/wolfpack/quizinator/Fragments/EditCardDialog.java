package com.seniordesign.wolfpack.quizinator.Fragments;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.seniordesign.wolfpack.quizinator.R;

public class EditCardDialog {

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

    private boolean updateViewOnCardTypeChanged(){
        //take card reference and populate given fields

        return true;
    }
}
