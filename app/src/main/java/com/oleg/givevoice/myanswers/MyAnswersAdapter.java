package com.oleg.givevoice.myanswers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import java.util.concurrent.ExecutionException;

public class MyAnswersAdapter extends RecyclerView.Adapter<MyAnswersAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<GVQuestionAnswer> questionsForMe;

    public MyAnswersAdapter(Context context) {
        this.questionsForMe = new ArrayList<>();
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
    public MyAnswersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.my_answers_item, parent, false);
        return new MyAnswersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyAnswersAdapter.ViewHolder holder, int position) {
        final GVQuestionAnswer questionForMe = questionsForMe.get(position);
        holder.questionTextView.setText(questionForMe.getQuestion());
        holder.questionFromTextView.setText(questionForMe.getFromPhone());
        holder.answerTextView.setText(questionForMe.getText());

        UploadQuestionImageTask mQTask = new UploadQuestionImageTask(holder, questionForMe.getQuestionImage());
        mQTask.execute();
        UploadAnswerImageTask mATask = new UploadAnswerImageTask(holder, questionForMe.getImage());
        mATask.execute();
    }

    @Override
    public int getItemCount() {
        return questionsForMe.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView questionTextView;
        final TextView questionFromTextView;
        final ImageView questionImageView;
        final ImageView answerImageView;
        final TextView answerTextView;


        ViewHolder(View view){
            super(view);
            questionTextView = view.findViewById(R.id.question_text_view);
            questionFromTextView = view.findViewById(R.id.question_from_text_view);
            questionImageView = view.findViewById(R.id.question_image_view);
            answerImageView = view.findViewById(R.id.answer_image_view);
            answerTextView = view.findViewById(R.id.my_answer_text_view);
        }
    }

    private Bitmap uploadImage(String imgName) throws Exception {
        final ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
        long imageLength = 0;
        ImageManager.GetImage(imgName, imageStream, imageLength);

        byte[] buffer = imageStream.toByteArray();

        return BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UploadQuestionImageTask extends AsyncTask<Void, Void, Boolean> {

        MyAnswersAdapter.ViewHolder mHolder;
        String mImgName;
        Bitmap bitmap;

        UploadQuestionImageTask(final MyAnswersAdapter.ViewHolder holder, String imgName) {
            this.mHolder = holder;
            this.mImgName = imgName;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Simulate network access.
                bitmap = uploadImage(mImgName);
                return true;
            } catch (InterruptedException | ExecutionException e) {
//                createAndShowDialog(e, "Error");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            mHolder.questionImageView.setImageBitmap(bitmap);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UploadAnswerImageTask extends AsyncTask<Void, Void, Boolean> {

        MyAnswersAdapter.ViewHolder mHolder;
        String mImgName;
        Bitmap bitmap;

        UploadAnswerImageTask(final MyAnswersAdapter.ViewHolder holder, String imgName) {
            this.mHolder = holder;
            this.mImgName = imgName;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Simulate network access.
                bitmap = uploadImage(mImgName);
                return true;
            } catch (InterruptedException | ExecutionException e) {
//                createAndShowDialog(e, "Error");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            mHolder.answerImageView.setImageBitmap(bitmap);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}