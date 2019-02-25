package com.oleg.givevoice.answersforme;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.oleg.givevoice.R;
import com.oleg.givevoice.db.GVPrivateAzureServiceAdapter;
import com.oleg.givevoice.db.gvanswers.GVAnswer;
import com.oleg.givevoice.db.gvimage.ImageManager;
import com.oleg.givevoice.db.gvquestionsanswers.GVQuestionAnswer;

import java.io.ByteArrayOutputStream;

public class AnswerForMeActivity extends AppCompatActivity {

    private ImageView imageView;
    private ImageView forAnswerImageView;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<GVQuestionAnswer> mQuestionAnswerTable;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<GVAnswer> mAnswerTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_for_me);

        final GVQuestionAnswer itemQA = (GVQuestionAnswer) getIntent().getSerializableExtra(GVQuestionAnswer.class.getSimpleName());

        final TextView textView = findViewById(R.id.question_text);
        final TextView answerTextView = findViewById(R.id.answer_text);
        final TextView answerFromTextView = findViewById(R.id.answer_from);
        textView.setText(itemQA.getQuestion());
        answerTextView.setText(itemQA.getText());
        answerFromTextView.setText(itemQA.getToPhone());

        GVPrivateAzureServiceAdapter servicemAdapter = GVPrivateAzureServiceAdapter.getInstance();
        MobileServiceClient mClient = servicemAdapter.getClient();
        mQuestionAnswerTable = mClient.getTable(GVQuestionAnswer.class);
        mAnswerTable = mClient.getTable(GVAnswer.class);

        this.imageView = findViewById(R.id.question_for_me_image_view);
        this.forAnswerImageView = findViewById(R.id.question_for_answer_image_view);
        UploadQuestionImageTask mTask;

        if (itemQA.getQuestionImage() != null && !itemQA.getQuestionImage().isEmpty()) {
            mTask = new UploadQuestionImageTask(itemQA.getQuestionImage(), imageView);
            mTask.execute();
        }
        if (itemQA.getImage() != null && !itemQA.getImage().isEmpty()) {
            mTask = new UploadQuestionImageTask(itemQA.getImage(), forAnswerImageView);
            mTask.execute();
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

        ImageView mImageView ;
        String mImgName;
        Bitmap mBitmap;

        UploadQuestionImageTask(String imgName, ImageView imageView) {
            this.mImageView = imageView;
            this.mImgName = imgName;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Simulate network access.
            try {
                mBitmap = uploadImage(mImgName);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            mImageView.setImageBitmap(mBitmap);
        }
    }
}
