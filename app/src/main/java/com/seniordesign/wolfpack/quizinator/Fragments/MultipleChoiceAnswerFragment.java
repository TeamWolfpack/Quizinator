package com.seniordesign.wolfpack.quizinator.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.seniordesign.wolfpack.quizinator.R;

public class MultipleChoiceAnswerFragment extends Fragment {

    private String[] choiceButtons = new String[4];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multiple_choice_answer,
                container, false);
        ((Button) view.findViewById(R.id.choiceAButton)).setText(choiceButtons[0]);
        ((Button) view.findViewById(R.id.choiceBButton)).setText(choiceButtons[1]);
        ((Button) view.findViewById(R.id.choiceCButton)).setText(choiceButtons[2]);
        ((Button) view.findViewById(R.id.choiceDButton)).setText(choiceButtons[3]);
        return view;
    }

    public boolean setChoiceA(String choice){
        choiceButtons[0] = choice;
        return true;
    }
    public boolean setChoiceB(String choice){
        choiceButtons[1] = choice;
        return true;
    }
    public boolean setChoiceC(String choice){
        choiceButtons[2] = choice;
        return true;
    }
    public boolean setChoiceD(String choice){
        choiceButtons[3] = choice;
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
