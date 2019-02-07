package com.oleg.givevoice.questionsforme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oleg.givevoice.R;
import com.oleg.givevoice.db.gvimage.ImageManager;
import com.oleg.givevoice.db.gvquestionsanswers.GVQuestionAnswer;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class QuestionsForMeAdapter extends RecyclerView.Adapter<QuestionsForMeAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<GVQuestionAnswer> questionsForMe;

    public QuestionsForMeAdapter(Context context) {
        this.questionsForMe = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    public QuestionsForMeAdapter(Context context, List<GVQuestionAnswer> questionsForMe) {
        this.questionsForMe= questionsForMe;
        this.inflater = LayoutInflater.from(context);
    }

    public void add(GVQuestionAnswer qa) {
        questionsForMe.add(qa);
    }

    public GVQuestionAnswer get(int position) {
        return questionsForMe.get(position);
    }

    public void clear() {
        questionsForMe.clear();
    }

    @Override
    public QuestionsForMeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.questions_for_me_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final QuestionsForMeAdapter.ViewHolder holder, int position) {
        final GVQuestionAnswer questionForMe = questionsForMe.get(position);
        holder.questionTextView.setText(questionForMe .getQuestion());
        holder.questionFromTextView.setText(questionForMe .getFromPhone());

        final ByteArrayOutputStream imageStream = new ByteArrayOutputStream();

        final Handler handler = new Handler();

        Thread th = new Thread(new Runnable() {
            public void run() {

                try {

                    long imageLength = 0;

                    ImageManager.GetImage(questionForMe .getQuestionImage(), imageStream, imageLength);

                    handler.post(new Runnable() {

                        public void run() {
                            byte[] buffer = imageStream.toByteArray();

                            Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);

                            holder.questionImageView.setImageBitmap(bitmap);
                        }
                    });
                }
                catch(Exception ex) {
                    final String exceptionMessage = ex.getMessage();
//                    handler.post(new Runnable() {
//                        public void run() {
//                            Toast.makeText(ImageActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
//                        }
//                    });
                }
            }});
        th.start();
    }

    @Override
    public int getItemCount() {
        return questionsForMe.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView questionTextView;
        final TextView questionFromTextView;
        final ImageView questionImageView;


        ViewHolder(View view){
            super(view);
            questionTextView = view.findViewById(R.id.question_for_me_text_view);
            questionFromTextView = view.findViewById(R.id.question_from_text_view);
            questionImageView = view.findViewById(R.id.question_image_view);
        }
    }
}