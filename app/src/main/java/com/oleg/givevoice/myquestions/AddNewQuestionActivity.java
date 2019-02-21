package com.oleg.givevoice.myquestions;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.oleg.givevoice.exceptions.GVException;
import com.oleg.givevoice.main.MainActivity;
import com.oleg.givevoice.tasks.GVTask;
import com.oleg.givevoice.utils.PhoneUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.oleg.givevoice.utils.ActivityUtils.createAndShowDialogOnUI;

public class AddNewQuestionActivity extends AppCompatActivity {

    private static final int SELECT_IMAGE = 100;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private Uri imageUri;

    private ImageView imageView;
    private Activity mActivity;

    private View mProgressView;
    private View mQuestionFormView;
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
    private AddQuestionTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_question);

        this.imageView = findViewById(R.id.question_image_view);
        EditText edit = findViewById(R.id.question_text);
        mQuestionFormView = findViewById(R.id.question_form);
        mProgressView = findViewById(R.id.progress_bar);

        Button button = findViewById(R.id.add_button);
        Button selectImageButton = findViewById(R.id.select_image);

        final GVQuestion itemQ = (GVQuestion) getIntent().getSerializableExtra(GVQuestion.class.getSimpleName());

        System.out.println(itemQ);
        if (itemQ == null) {
            GVPrivateAzureServiceAdapter serviceAdapter = GVPrivateAzureServiceAdapter.getInstance();
            MobileServiceClient mClient = serviceAdapter.getClient();
            mQuestionTable = mClient.getTable(GVQuestion.class);
            mAnswerTable = mClient.getTable(GVAnswer.class);
            mActivity = this;

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    addQuestion();
                }
            });

            selectImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectImageFromGallery();
                }
            });
        } else {
            edit.setText(itemQ.getText());
            edit.setEnabled(false);
            edit.setFocusable(false);
            button.setVisibility(View.GONE);
            selectImageButton.setVisibility(View.GONE);

            final ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
            final Handler handler = new Handler();
            Thread th = new Thread(new Runnable() {
                public void run() {

                    try {
                        long imageLength = 0;
                        ImageManager.GetImage(itemQ.getImage(), imageStream, imageLength);

                        handler.post(new Runnable() {

                            public void run() {
                                byte[] buffer = imageStream.toByteArray();
                                Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                    catch(Exception ex) {
                        createAndShowDialogOnUI(mActivity, ex, ex.getMessage());
                    }
                }});
            th.start();
        }
    }

    private void addQuestion() {
        final EditText edit = findViewById(R.id.question_text);

        item.setText(edit.getText().toString());
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(this);
        final String fromPhone = settings.getString("phone", "");

        if (fromPhone != null && !fromPhone.isEmpty()) {
            BigInteger fromPhoneInteger = new BigInteger(fromPhone);
            item.setUserId(fromPhoneInteger);

            mTask = new AddQuestionTask(Arrays.asList(mQuestionFormView), Arrays.asList(mProgressView), getResources());
            // Check the SDK version and whether the permission is already granted or not.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
            } else {
                mTask.execute();
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
                mTask.execute();
            } else {
                createAndShowDialogOnUI(mActivity, new GVException("Until you grant the permission, we cannot display the names"));
            }
        }
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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class AddQuestionTask extends GVTask {

        AddQuestionTask(List<View> mainViews, List<View> progressViews, Resources resources) {
            super(mActivity, mainViews, progressViews, resources);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Simulate network access.
                String imageName = uploadImage();
                item.setImage(imageName);
                GVQuestion qv = mQuestionTable.insert(item).get();
                PhoneUtils.setPhoneContacts(qv.getId(), getContentResolver());
                Intent intent = new Intent(mActivity, MainActivity.class);
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
