package com.oleg.givevoice.myquestions;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.http.HttpConstants;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.oleg.givevoice.R;
import com.oleg.givevoice.db.GVPrivateAzureServiceAdapter;
import com.oleg.givevoice.db.gvanswers.GVAnswer;
import com.oleg.givevoice.db.gvimage.ImageManager;
import com.oleg.givevoice.db.gvquestions.GVQuestion;
import com.oleg.givevoice.main.MainActivity;
import com.oleg.givevoice.utils.PhoneUtils;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AddNewQuestionActivity extends AppCompatActivity {

    String questionId;
    String fromPhone;
    private Uri imageUri;
    private static final int SELECT_IMAGE = 100;
    private ImageView imageView;
    private Button uploadImageButton;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private Activity mActivity;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<GVQuestion> mQuestionTable;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<GVAnswer> mAnswerTable;

    final GVQuestion item = new GVQuestion();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_question);

        GVPrivateAzureServiceAdapter servicemAdapter = GVPrivateAzureServiceAdapter.getInstance();
        MobileServiceClient mClient = servicemAdapter.getClient();
        mQuestionTable = mClient.getTable(GVQuestion.class);
        mAnswerTable = mClient.getTable(GVAnswer.class);
        final Activity activity = this;
        mActivity = this;

        Button button = (Button) findViewById(R.id.add_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final EditText edit = (EditText) findViewById(R.id.question_text);

                item.setText(edit.getText().toString());
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(v.getContext());
                final String fromPhone = settings.getString("phone", "");

                if (fromPhone != null && !fromPhone.isEmpty()) {
                    BigInteger fromPhoneInteger = new BigInteger(fromPhone);
                    item.setUserId(fromPhoneInteger);

                    // Insert the new item
                    AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                String imageName = uploadImage();
                                item.setImage(imageName);
                                setPhoneContacts();
                            } catch (final Exception e) {
                                createAndShowDialogFromTask(e, "Error");
                            }
                            return null;
                        }
                    };

                    runAsyncTask(task);
                }
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
                try {
                    setPhoneContacts();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Описываем метод:
    public void setPhoneContacts() throws ExecutionException, InterruptedException {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            String phoneNumber;
            List<String> phones = new ArrayList<String>();

            //Связываемся с контактными данными и берем с них значения id контакта, имени контакта и его номера:
            Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
            String _ID = ContactsContract.Contacts._ID;
            String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

            Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
            String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;


            StringBuffer output = new StringBuffer();
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);
            //Запускаем цикл обработчик для каждого контакта:
            if (cursor.getCount() > 0) {
                //Если значение имени и номера контакта больше 0 (то есть они существуют) выбираем
                //их значения в приложение привязываем с соответствующие поля "Имя" и "Номер":
                while (cursor.moveToNext()) {
                    String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                    //Получаем имя:
                    if (hasPhoneNumber > 0) {
                        Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null,
                                Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);

                        //и соответствующий ему номер:
                        while (phoneCursor.moveToNext()) {
                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                            phones.add(PhoneUtils.getPhoneNumber(phoneNumber));
                        }
                    }
                    output.append("\n");
                }
            }

            // foreach
            // Basic loop
            JsonObject content = new JsonObject();
            GVQuestion qv = mQuestionTable.insert(item).get();
            content.add("phones", new Gson().toJsonTree(phones));
            content.add("questionId", new Gson().toJsonTree(qv.getId()));
            testInvokeNullResponseObject(content);
            Intent intent = new Intent(mActivity, MainActivity.class);
            startActivity(intent);
        }
    }

    public void testInvokeNullResponseObject(JsonObject content) {

        try {

            GVPrivateAzureServiceAdapter servicemAdapter = GVPrivateAzureServiceAdapter.getInstance();
            MobileServiceClient mClient = servicemAdapter.getClient();


//            MobileServiceClient client = new MobileServiceClient(appUrl, getInstrumentation().getTargetContext());
//            client = client.withFilter(new NullResponseFilter());

            JsonElement response = mClient.invokeApi("answers", content, HttpConstants.PutMethod, null).get();
            System.out.print("response" + response);
        } catch (Exception exception) {
            if (!(exception.getCause() instanceof MobileServiceException)) {
                System.out.println("test");
//                fail(exception.getMessage());
            }

            return;
        }

//        fail("Exception expected");

    }

    /**
     * Run an ASync task on the corresponding executor
     * @param task
     * @return
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
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
                Toast.makeText(this, "Text", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {

//            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return imageName;
    }
}
