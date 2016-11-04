package com.seniordesign.wolfpack.quizinator.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.seniordesign.wolfpack.quizinator.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MultipleChoiceAnswerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MultipleChoiceAnswerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultipleChoiceAnswerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String[] choiceButtons = new String[4];

    private OnFragmentInteractionListener mListener;

    public MultipleChoiceAnswerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MultipleChoiceAnswerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MultipleChoiceAnswerFragment newInstance(String param1, String param2) {
        MultipleChoiceAnswerFragment fragment = new MultipleChoiceAnswerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_multiple_choice_answer, container, false);
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String answer);
    }
}