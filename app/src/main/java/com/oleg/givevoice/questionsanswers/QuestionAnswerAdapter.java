package com.oleg.givevoice.questionsanswers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oleg.givevoice.R;
import com.oleg.givevoice.db.gvanswers.GVAnswer;

import java.util.ArrayList;
import java.util.List;

public class QuestionAnswerAdapter extends RecyclerView.Adapter<QuestionAnswerAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<GVAnswer> answers;

    public QuestionAnswerAdapter(Context context) {
        this.answers = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    public QuestionAnswerAdapter(Context context, List<GVAnswer> answers) {
        this.answers= answers;
        this.inflater = LayoutInflater.from(context);
    }

    public void add(GVAnswer question) {
        answers.add(question);
    }

    public void clear() {
        answers.clear();
    }

    @Override
    public QuestionAnswerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.answers_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuestionAnswerAdapter.ViewHolder holder, int position) {
        GVAnswer question = answers.get(position);
        holder.answerTextView.setText(question.getText());
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView answerTextView ;
        ViewHolder(View view){
            super(view);
            answerTextView = (TextView) view.findViewById(R.id.question_text);
        }
    }
}