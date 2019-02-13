package com.oleg.givevoice.questionsforme;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.oleg.givevoice.main.MainActivity;
import com.oleg.givevoice.utils.ActivityUtils;
import com.oleg.givevoice.utils.PhoneUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class GetQuestionsForMeActivity extends AppCompatActivity {

    String questionId;
    private Uri imageUri;
    private static final int SELECT_IMAGE = 100;
    private ImageView imageView;
    private ImageView forAnswerImageView;
    private View mProgressView;
    private View mGetAnswerFormView;
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
    private GetAnswerTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_for_me);

        final GVQuestionAnswer itemQA = (GVQuestionAnswer) getIntent().getSerializableExtra(GVQuestionAnswer.class.getSimpleName());

        final TextView textView = (TextView) findViewById(R.id.question_text);
        textView.setText(itemQA.getQuestion());

        GVPrivateAzureServiceAdapter servicemAdapter = GVPrivateAzureServiceAdapter.getInstance();
        MobileServiceClient mClient = servicemAdapter.getClient();
        mQuestionAnswerTable = mClient.getTable(GVQuestionAnswer.class);
        mAnswerTable = mClient.getTable(GVAnswer.class);
//        final Activity activity = this;
        mActivity = this;
////        Button button = (Button) findViewById(R.id.button);

        mGetAnswerFormView = findViewById(R.id.get_answer_form);
        mProgressView = findViewById(R.id.add_progress);

        Button button = findViewById(R.id.get_answer_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//                qaId = itemQA.getQuestionId()
                getAnswer(itemQA);
            }
        });

        this.imageView = (ImageView)findViewById(R.id.question_for_me_image_view);
        this.forAnswerImageView = (ImageView)findViewById(R.id.question_for_answer_image_view);

        Button selectImageButton = findViewById(R.id.select_image_for_answer);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImageFromGallery();
            }
        });

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

                            imageView.setImageBitmap(bitmap);
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
        final EditText answerEdit = (EditText) findViewById(R.id.getAnswerEditText);
        final GVAnswer answer = itemQA.getAnswer();
        answer.setText(answerEdit.getText().toString());
        String imageName = uploadImage();
        answer.setImage(imageName);

        // Insert the new item

        mAuthTask = new GetAnswerTask(answer);
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            mAuthTask.execute((Void) null);
        }
    }

    private String uploadImage()
    {
        String imageName = null;
        if (this.imageUri != null) {
            try {

                final InputStream imageStream = getContentResolver().openInputStream(this.imageUri);
                final int imageLength = imageStream.available();

//            final Handler handler = new Handler();

//            Thread th = new Thread(new Runnable() {
//                public void run() {

//                    try {

                imageName = ImageManager.UploadImage(imageStream, imageLength);
//                        handler.post(new Runnable() {
//
//                            public void run() {
//                                Toast.makeText(AddNewQuestionActivity.this, "Image Uploaded Successfully. Name = " + imageName, Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                    catch(Exception ex) {
//                        final String exceptionMessage = ex.getMessage();
//                        handler.post(new Runnable() {
//                            public void run() {
//                                Toast.makeText(AddNewQuestionActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                }});
//            th.start();
            } catch (Exception ex) {

//            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return imageName;
    }

    private void SelectImageFromGallery()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
    }

//    public void setPhoneContactsIntoArrayList(){
//
//        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);
//        while (cursor.moveToNext()) {
////            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//            String phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
////            storeContacts.add(name + " "  + ":" + " " + phonenumber);
//            System.out.println(phonenumber);
//        }
//
//        cursor.close();
//
//    }

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                //                    setPhoneContacts();
                mAuthTask.execute((Void) null);
            } else {
//                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
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
    public class GetAnswerTask extends AsyncTask<Void, Void, Boolean> {

        private GVAnswer mAnswer;
        GetAnswerTask(GVAnswer answer) {
            mAnswer = answer;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                GVAnswer anwerToChange = updateItemInTable(mAnswer);
                PhoneUtils.setPhoneContacts(anwerToChange.getId(), getContentResolver());
                Intent intent = new Intent(mActivity, MainActivity.class);
                intent.putExtra("fragment", R.id.questions_fo_me);
                startActivity(intent);
            } catch (InterruptedException e) {
                return false;
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            ActivityUtils.showProgress(false, mGetAnswerFormView, mProgressView, getResources());

            if (success) {
                finish();
            } else {
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
//                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ActivityUtils.showProgress(true, mGetAnswerFormView, mProgressView, getResources());
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            ActivityUtils.showProgress(false, mGetAnswerFormView, mProgressView, getResources());
        }
    }
}
