package com.oleg.givevoice.myquestions;

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
import com.oleg.givevoice.db.gvquestions.GVQuestion;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<GVQuestion> questions;

    public QuestionAdapter(Context context) {
        this.questions = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    public void add(GVQuestion question) {
        questions.add(question);
    }

    public GVQuestion get(int position) {
        return questions.get(position);
    }

    public void clear() {
        questions.clear();
    }

    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.my_questions_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final QuestionAdapter.ViewHolder holder, int position) {
        final GVQuestion question = questions.get(position);
        holder.questionTextView.setText(question.getText());

        if (question.getImage() != null && !question.getImage().isEmpty()) {
            final ByteArrayOutputStream imageStream = new ByteArrayOutputStream();

            final Handler handler = new Handler();

            Thread th = new Thread(new Runnable() {
                public void run() {

                    try {

                        long imageLength = 0;

                        ImageManager.GetImage(question.getImage(), imageStream, imageLength);

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
                        throw new Error(ex);
                    }
                }});
            th.start();
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView questionTextView;
        final ImageView questionImageView;
        ViewHolder(View view){
            super(view);
            questionTextView = view.findViewById(R.id.question_text_view);
            questionImageView = view.findViewById(R.id.question_image_view);
        }
    }
}