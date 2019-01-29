package com.oleg.givevoice.questionsanswers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oleg.givevoice.R;
import com.oleg.givevoice.db.gvquestionsanswers.GVQuestionAnswer;

import java.util.ArrayList;
import java.util.List;

public class QuestionAnswerAdapter extends RecyclerView.Adapter<QuestionAnswerAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<GVQuestionAnswer> questionAnswers;

    public QuestionAnswerAdapter(Context context) {
        this.questionAnswers = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    public QuestionAnswerAdapter(Context context, List<GVQuestionAnswer> questionAnswers) {
        this.questionAnswers= questionAnswers;
        this.inflater = LayoutInflater.from(context);
    }

    public void add(GVQuestionAnswer qa) {
        questionAnswers.add(qa);
    }

    public GVQuestionAnswer get(int position) {
        return questionAnswers.get(position);
    }

    public void clear() {
        questionAnswers.clear();
    }

    @Override
    public QuestionAnswerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.questions_answers_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuestionAnswerAdapter.ViewHolder holder, int position) {
        GVQuestionAnswer question = questionAnswers.get(position);
        holder.answerTextView.setText(question.getQuestion());
    }

    @Override
    public int getItemCount() {
        return questionAnswers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView answerTextView ;
        ViewHolder(View view){
            super(view);
            answerTextView = (TextView) view.findViewById(R.id.question_answer_text);
        }
    }
}