package com.example.quizapp.studentFragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quizapp.classes.Question;
import com.example.quizapp.databinding.FragmentQuestionBinding;
import com.example.quizapp.databinding.FragmentStudentQuestionBinding;
import com.example.quizapp.listeners.AnswerStateListener;

import java.util.HashSet;
import java.util.Set;

public class StudentQuestionFragment extends Fragment {//Checked

    private static final String ARG_QUESTION = "question";
    private Question question;
    private Set<String> selectedOptions = new HashSet<>();//can be removed (kept for debugging only)
    public FragmentStudentQuestionBinding binding;
    AnswerStateListener answerStateListener;

    public static StudentQuestionFragment newInstance(Question question) {
        StudentQuestionFragment fragment = new StudentQuestionFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUESTION, question);
        fragment.setArguments(args);
        return fragment;
    }

    public void registerAnswerStateListener(AnswerStateListener answerStateListener){

        this.answerStateListener = answerStateListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        Log.d("LIFECYCLE", "StudentQuestionFragment(" + getTag()+ ") onCreate" +
                " before super call:" +
                " selected options: " + selectedOptions);

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable(ARG_QUESTION);
        }
        Log.d("LIFECYCLE", "StudentQuestionFragment(" + getTag()+ ") onCrete" +
                " after super call:" +
                " selected options: " + selectedOptions);
//        Integer.parseInt("2");
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_question, container, false);
//
//        TextView tvQuestion = view.findViewById(R.id.tv_question_text);
//        LinearLayout optionsLayout = view.findViewById(R.id.options_container);

        binding = FragmentStudentQuestionBinding.inflate(getLayoutInflater());
        Log.d("LIFECYCLE", "StudentQuestionFragment(" + getTag()+ ") onCreateView:" +
                " selected options: " + selectedOptions);
        return binding.getRoot();



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle){


        Log.d("LIFECYCLE", "StudentQuestionFragment(" + getTag()+ ") onViewCreated" +
                " before super call:" +
                " selected options: " + selectedOptions);

        super.onViewCreated(view,bundle);

        binding.tvQuestionText.setText(question.getQuestionText());
        String tag = getTag();

        if(tag == null){

            Log.d("TAG","NULL TAG");
            return;
        }

        Set<String> answers = answerStateListener.getOptionsByQuestionTag(tag);

        for (String key : question.getOptions().keySet()) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(question.getOptions().get(key));
            if(answers.contains(key)) checkBox.setChecked(true);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

//                Set<String> answers = answerStateListener.getOptionsByQuestionTag(getTag());
//                if (isChecked) selectedOptions.add(key);
//                else selectedOptions.remove(key);
                if (isChecked) answers.add(key);
                else answers.remove(key);
                //update the answers
                answerStateListener.setAnswers(tag,answers);
            });
            binding.layoutOptions.addView(checkBox);
        }

        Log.d("LIFECYCLE", "StudentQuestionFragment(" + getTag()+ ") onViewCreated" +
                " after super call:" +
                " selected options: " + selectedOptions);

       // return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        Log.d("LIFECYCLE", "StudentQuestionFragment(" + getTag()+ ") onViewStateRestored" +
                " before super call:" +
                " selected options: " + selectedOptions);
        super.onViewStateRestored(savedInstanceState);

        Log.d("LIFECYCLE", "StudentQuestionFragment(" + getTag()+ ") onViewStateRestored" +
                " after super call:" +
                " selected options: " + selectedOptions);
    }

    @Override
    public void onStart() {
        Log.d("LIFECYCLE", "StudentQuestionFragment(" + getTag()+ ") onStart" +
                " before super call:" +
                " selected options: " + selectedOptions);
        super.onStart();

        Log.d("LIFECYCLE", "StudentQuestionFragment(" + getTag()+ ") onStart" +
                " after super call:" +
                " selected options: " + selectedOptions);
    }

    @Override
    public void onResume() {
        Log.d("LIFECYCLE", "StudentQuestionFragment(" + getTag()+ ") onResume" +
                " before super call:" +
                " selected options: " + selectedOptions);
        super.onResume();
        Log.d("LIFECYCLE", "StudentQuestionFragment(" + getTag()+ ") onResume" +
                " after super call:" +
                " selected options: " + selectedOptions);

    }

    @Override
    public void onPause() {
        Log.d("LIFECYCLE", "StudentQuestionFragment(" + getTag()+ ") onPause" +
                " before super call:" +
                " selected options: " + selectedOptions);
        super.onPause();
        Log.d("LIFECYCLE", "StudentQuestionFragment(" + getTag()+ ") onPause" +
                " after super call:" +
                " selected options: " + selectedOptions);

    }

    @Override
    public void onStop() {
        Log.d("LIFECYCLE", "StudentQuestionFragment(" + getTag()+ ") onStop" +
                " before super call:" +
                " selected options: " + selectedOptions);
        super.onStop();

        Log.d("LIFECYCLE", "StudentQuestionFragment(" + getTag()+ ") onStop" +
                " after super call:" +
                " selected options: " + selectedOptions);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        Log.d("LIFECYCLE", "StudentQuestionFragment(" + getTag()+ ") onSaveInstanceState" +
                " before super call:" +
                " selected options: " + selectedOptions);
        super.onSaveInstanceState(outState);

        Log.d("LIFECYCLE", "StudentQuestionFragment(" + getTag()+ ") onSaveInstanceState" +
                " after super call:" +
                " selected options: " + selectedOptions);
    }

    @Override
    public void onDestroyView() {

        Log.d("LIFECYCLE", "StudentQuestionFragment(" + getTag()+ ") onDestroyView" +
                " before super call:" +
                " selected options: " + selectedOptions);
        super.onDestroyView();
        Log.d("LIFECYCLE", "StudentQuestionFragment(" + getTag()+ ") onDestroyView" +
                " after super call:" +
                " selected options: " + selectedOptions);
    }

    @Override
    public void onDestroy() {

        Log.d("LIFECYCLE", "StudentQuestionFragment(" + getTag()+ ") onDestroy" +
                " before super call:" +
                " selected options: " + selectedOptions);

        super.onDestroy();

        Log.d("LIFECYCLE", "StudentQuestionFragment(" + getTag()+ ") onDestroy" +
                " after super call:" +
                " selected options: " + selectedOptions);
    }

    public Set<String> getSelectedOptions() {

        return selectedOptions;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AnswerStateListener) {
            answerStateListener = (AnswerStateListener) context;
        } else {
            throw new RuntimeException("Host must implement AnswerStateListener");
        }
    }

}
