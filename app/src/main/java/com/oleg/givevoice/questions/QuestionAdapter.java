package com.oleg.givevoice.questions;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oleg.givevoice.R;
import com.oleg.givevoice.db.gvquestions.GVQuestion;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<GVQuestion> questions;

    public QuestionAdapter(Context context) {
        this.questions = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    public QuestionAdapter(Context context, List<GVQuestion> questions) {
        this.questions= questions;
        this.inflater = LayoutInflater.from(context);
    }

    public void add(GVQuestion question) {
        questions.add(question);
    }

    public void clear() {
        questions.clear();
    }

    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.questions_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuestionAdapter.ViewHolder holder, int position) {
        GVQuestion question = questions.get(position);
//        holder.imageView.setImageResource(questions.getImage());
        holder.questionTextView.setText(question.getText());
//        holder.companyView.setText(questions.getCompany());
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        final ImageView imageView;
//        final TextView companyView;
        final TextView questionTextView ;
        ViewHolder(View view){
            super(view);
//            imageView = (ImageView)view.findViewById(R.id.image);
            questionTextView = (TextView) view.findViewById(R.id.question_text);
//            companyView = (TextView) view.findViewById(R.id.company);
        }
    }
}