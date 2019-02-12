package com.oleg.givevoice.myquestions;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.oleg.givevoice.R;
import com.oleg.givevoice.db.GVPrivateAzureServiceAdapter;
import com.oleg.givevoice.db.gvanswers.GVAnswer;
import com.oleg.givevoice.db.gvimage.ImageManager;
import com.oleg.givevoice.db.gvquestions.GVQuestion;
import com.oleg.givevoice.main.MainActivity;
import com.oleg.givevoice.utils.ActivityUtils;
import com.oleg.givevoice.utils.PhoneUtils;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class AddNewQuestionActivity extends AppCompatActivity {

    private Uri imageUri;
    private static final int SELECT_IMAGE = 100;
    private ImageView imageView;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private Activity mActivity;

    private View mProgressView;
    private View mAddQuestionFormView;
    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<GVQuestion> mQuestionTable;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<GVAnswer> mAnswerTable;

    final GVQuestion item = new GVQuestion();

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private AddQuestionTask mAuthTask = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_question);

        GVPrivateAzureServiceAdapter servicemAdapter = GVPrivateAzureServiceAdapter.getInstance();
        MobileServiceClient mClient = servicemAdapter.getClient();
        mQuestionTable = mClient.getTable(GVQuestion.class);
        mAnswerTable = mClient.getTable(GVAnswer.class);
        mActivity = this;

        Button button = (Button) findViewById(R.id.add_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                addQuestion();
            }
        });

        Button selectImageButton = findViewById(R.id.select_image);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImageFromGallery();
            }
        });

        this.imageView = findViewById(R.id.question_image_view);
        mAddQuestionFormView = findViewById(R.id.add_question_form);
        mProgressView = findViewById(R.id.add_progress);
    }

    private void addQuestion() {
        final EditText edit = (EditText) findViewById(R.id.question_text);

        item.setText(edit.getText().toString());
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(this);
        final String fromPhone = settings.getString("phone", "");

        if (fromPhone != null && !fromPhone.isEmpty()) {
            BigInteger fromPhoneInteger = new BigInteger(fromPhone);
            item.setUserId(fromPhoneInteger);

            mAuthTask = new AddQuestionTask();
            // Check the SDK version and whether the permission is already granted or not.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
            } else {
                mAuthTask.execute((Void) null);
            }
        }
    }

    private void SelectImageFromGallery()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                mAuthTask.execute((Void) null);
            } else {
                System.out.println("test");
//                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialogFromTask(final Exception exception, String title) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }


    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    /**
     * Add an item to the Mobile Service Table
     *
     * @param item
     *            The item to Add
     */
    public GVQuestion addItemInTable(GVQuestion item) throws ExecutionException, InterruptedException {
        GVQuestion entity = mQuestionTable.insert(item).get();
        return entity;
    }

    /**
     * Add an item to the Mobile Service Table
     *
     * @param item
     *            The item to Add
     */
    public GVAnswer addItemInTableAnswer(GVAnswer item) throws ExecutionException, InterruptedException {
        GVAnswer entity = mAnswerTable.insert(item).get();
        return entity;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    this.imageUri = imageReturnedIntent.getData();
                    this.imageView.setImageURI(this.imageUri);
                }
        }
    }

    private String uploadImage()
    {
        String imageName = null;
        if (this.imageUri != null) {
            try {

                final InputStream imageStream = getContentResolver().openInputStream(this.imageUri);
                final int imageLength = imageStream.available();

                imageName = ImageManager.UploadImage(imageStream, imageLength);
//                Toast.makeText(this, "Text", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {

//            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return imageName;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class AddQuestionTask extends AsyncTask<Void, Void, Boolean> {

        AddQuestionTask() {}

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                String imageName = uploadImage();
                item.setImage(imageName);
                GVQuestion qv = mQuestionTable.insert(item).get();
                PhoneUtils.setPhoneContacts(qv.getId(), getContentResolver());
                Intent intent = new Intent(mActivity, MainActivity.class);
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
            ActivityUtils.showProgress(false, mAddQuestionFormView, mProgressView, getResources());

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
            ActivityUtils.showProgress(true, mAddQuestionFormView, mProgressView, getResources());
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            ActivityUtils.showProgress(false, mAddQuestionFormView, mProgressView, getResources());
        }
    }

}
