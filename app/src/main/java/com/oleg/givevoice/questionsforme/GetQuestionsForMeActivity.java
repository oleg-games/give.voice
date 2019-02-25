package com.oleg.givevoice.questionsforme;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.oleg.givevoice.R;
import com.oleg.givevoice.db.GVPrivateAzureServiceAdapter;
import com.oleg.givevoice.db.gvanswers.GVAnswer;
import com.oleg.givevoice.db.gvimage.ImageManager;
import com.oleg.givevoice.db.gvquestionsanswers.GVQuestionAnswer;
import com.oleg.givevoice.exceptions.GVException;
import com.oleg.givevoice.main.MainActivity;
import com.oleg.givevoice.tasks.GVTask;
import com.oleg.givevoice.utils.PhoneUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.oleg.givevoice.utils.ActivityUtils.createAndShowDialogOnUI;

public class GetQuestionsForMeActivity extends AppCompatActivity {

    private Uri imageUri;
    private static final int SELECT_IMAGE = 100;
    private ImageView questionImageView;
    private ImageView forAnswerImageView;
    private View mProgressView;
    private View mAnswerFormView;
    private Activity mActivity;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<GVQuestionAnswer> mQuestionAnswerTable;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<GVAnswer> mAnswerTable;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private GetAnswerTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_for_me);

        final GVQuestionAnswer itemQA = (GVQuestionAnswer) getIntent().getSerializableExtra(GVQuestionAnswer.class.getSimpleName());

        final TextView textView = findViewById(R.id.question_text);
        textView.setText(itemQA.getQuestion());

        GVPrivateAzureServiceAdapter servicemAdapter = GVPrivateAzureServiceAdapter.getInstance();
        MobileServiceClient mClient = servicemAdapter.getClient();
        mQuestionAnswerTable = mClient.getTable(GVQuestionAnswer.class);
        mAnswerTable = mClient.getTable(GVAnswer.class);
        mActivity = this;
        mAnswerFormView = findViewById(R.id.answer_form);
        mProgressView = findViewById(R.id.progress_bar);

        Button button = findViewById(R.id.get_answer_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                getAnswer(itemQA);
            }
        });

        this.questionImageView = findViewById(R.id.question_image_view);
        this.forAnswerImageView = findViewById(R.id.answer_image_view);

        Button selectImageButton = findViewById(R.id.select_image_for_answer);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImageFromGallery();
            }
        });

        if (itemQA.getQuestionImage() != null && !itemQA.getQuestionImage().isEmpty()) {
            final ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
            final Handler handler = new Handler();
            Thread th = new Thread(new Runnable() {
                public void run() {

                    try {

                        long imageLength = 0;

                        ImageManager.GetImage(itemQA.getQuestionImage(), imageStream, imageLength);

                        handler.post(new Runnable() {

                            public void run() {
                                byte[] buffer = imageStream.toByteArray();

                                Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);

                                questionImageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                    catch(Exception ex) {
//                    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! проверить
                        createAndShowDialogOnUI(mActivity, ex, ex.getMessage());
                    }
                }});
            th.start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    this.imageUri = imageReturnedIntent.getData();
                    this.forAnswerImageView.setImageURI(this.imageUri);
//                    this.uploadImageButton.setEnabled(true);
                }
        }
    }

    private void getAnswer(GVQuestionAnswer itemQA) {
//        final EditText answerEdit = findViewById(R.id.getAnswerEditText);
//        final GVAnswer answer = itemQA.getAnswer();
//        answer.setText(answerEdit.getText().toString());
//        String imageName = uploadImage();
//        answer.setImage(imageName);

        // Insert the new item

        mTask = new GetAnswerTask(Arrays.asList(mAnswerFormView), Arrays.asList(mProgressView), getResources(), itemQA);
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            mTask.execute((Void) null);
        }
    }

    private String uploadImage()
    {
        if (this.imageUri != null) {
            try {
                final InputStream imageStream = getContentResolver().openInputStream(this.imageUri);
                final int imageLength = imageStream.available();
                return ImageManager.UploadImage(imageStream, imageLength);
            } catch (Exception ex) {
                createAndShowDialogOnUI(mActivity, ex, ex.getMessage());
            }
        }

        return null;
    }

    private void SelectImageFromGallery()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
    }

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                mTask.execute();
            } else {
                createAndShowDialogOnUI(mActivity, new GVException("Until you grant the permission, we cannot display the names"));
            }
        }
    }

    /**
     * Add an item to the Mobile Service Table
     *
     * @param item
     *            The item to Add
     */
    public GVAnswer updateItemInTable(GVAnswer item) throws ExecutionException, InterruptedException {
        GVAnswer entity = mAnswerTable.update(item).get();
        return entity;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class GetAnswerTask extends GVTask {
        private GVQuestionAnswer mItemQA;

        GetAnswerTask(List<View> mainViews, List<View> progressViews, Resources resources, GVQuestionAnswer itemQA) {
            super(mActivity, mainViews, progressViews, resources);
            mItemQA = itemQA;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Simulate network access.
                final EditText answerEdit = findViewById(R.id.answer_edit_text);
                final GVAnswer answer = mItemQA.getAnswer();
                answer.setText(answerEdit.getText().toString());
                String imageName = uploadImage();
                answer.setImage(imageName);

                GVAnswer anwerToChange = updateItemInTable(answer);
                PhoneUtils.setPhoneContacts(anwerToChange.getQuestionId(), getContentResolver());
                Intent intent = new Intent(mActivity, MainActivity.class);
                intent.putExtra("fragment", R.id.questions_fo_me);
                startActivity(intent);

                return true;
            } catch (InterruptedException | ExecutionException e) {
                createAndShowDialog(e, "Error");
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            mTask = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mTask = null;
        }
    }
}
