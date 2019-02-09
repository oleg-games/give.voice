package com.oleg.givevoice.myanswers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

public class MyAnswerActivity extends AppCompatActivity {

    String questionId;
    String fromPhone;
    private Uri imageUri;
    private static final int SELECT_IMAGE = 100;
    private ImageView imageView;
    private ImageView forAnswerImageView;

    //    List<GVQuestion> questions = new ArrayList<>();

    private Activity mActivity;

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
        setContentView(R.layout.activity_my_answer);

        final GVQuestionAnswer itemQA = (GVQuestionAnswer) getIntent().getSerializableExtra(GVQuestionAnswer.class.getSimpleName());

        final TextView textView = (TextView) findViewById(R.id.question_text);
        final TextView answerTextView = (TextView) findViewById(R.id.answer_text);
        textView.setText(itemQA.getQuestion());
        answerTextView.setText(itemQA.getText());

        GVPrivateAzureServiceAdapter servicemAdapter = GVPrivateAzureServiceAdapter.getInstance();
        MobileServiceClient mClient = servicemAdapter.getClient();
        mQuestionAnswerTable = mClient.getTable(GVQuestionAnswer.class);
        mAnswerTable = mClient.getTable(GVAnswer.class);
//        final Activity activity = this;
        mActivity = this;

        this.imageView = (ImageView)findViewById(R.id.question_for_me_image_view);
        this.forAnswerImageView = (ImageView)findViewById(R.id.question_for_answer_image_view);
        this.imageView = (ImageView)findViewById(R.id.question_for_me_image_view);
        this.forAnswerImageView = (ImageView)findViewById(R.id.question_for_answer_image_view);

        final ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
        final ByteArrayOutputStream imageStream2 = new ByteArrayOutputStream();

        final Handler handler = new Handler();

        Thread th = new Thread(new Runnable() {
            public void run() {

                try {

                    long imageLength = 0;
                    long imageLength2 = 0;

                    ImageManager.GetImage(itemQA.getQuestionImage(), imageStream, imageLength);
                    ImageManager.GetImage(itemQA.getImage(), imageStream2, imageLength2);

                    handler.post(new Runnable() {

                        public void run() {
                            byte[] buffer = imageStream.toByteArray();

                            Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);

                            imageView.setImageBitmap(bitmap);
                            byte[] buffer2 = imageStream2.toByteArray();

                            Bitmap bitmap2 = BitmapFactory.decodeByteArray(buffer2, 0, buffer.length);

                            forAnswerImageView.setImageBitmap(bitmap2);
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

//        final ByteArrayOutputStream imageStreamAnswer = new ByteArrayOutputStream();
//
//        final Handler handlerAnswer = new Handler();
//
//        Thread thAnswer = new Thread(new Runnable() {
//            public void run() {
//
//                try {
//
//                    long imageLength = 0;
//
//                    ImageManager.GetImage(itemQA.getImage(), imageStreamAnswer, imageLength);
//
//                    handlerAnswer.post(new Runnable() {
//
//                        public void run() {
//                            byte[] buffer = imageStreamAnswer.toByteArray();
//
//                            Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
//
//                            forAnswerImageView.setImageBitmap(bitmap);
//                        }
//                    });
//                }
//                catch(Exception ex) {
//                    final String exceptionMessage = ex.getMessage();
////                    handler.post(new Runnable() {
////                        public void run() {
////                            Toast.makeText(ImageActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
////                        }
////                    });
//                }
//            }});
//        thAnswer.start();
    }
}
