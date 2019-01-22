package com.oleg.givevoice.questionsanswers;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.oleg.givevoice.R;
import com.oleg.givevoice.db.GVPrivateAzureServiceAdapter;
import com.oleg.givevoice.db.gvanswers.GVAnswer;
import com.oleg.givevoice.db.gvquestions.GVQuestion;
import com.oleg.givevoice.db.gvquestionsanswers.GVQuestionAnswer;
import com.oleg.givevoice.main.MainActivity;
import com.oleg.givevoice.questions.QuestionAdapter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GetQuestionAnswerActivity extends AppCompatActivity {

    String questionId;
    String fromPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_get_question_answer);

        GVQuestionAnswer place = (GVQuestionAnswer) getIntent().getSerializableExtra("question_answer");

        GVPrivateAzureServiceAdapter servicemAdapter = GVPrivateAzureServiceAdapter.getInstance();
        MobileServiceClient mClient = servicemAdapter.getClient();
        mQuestionTable = mClient.getTable(GVQuestion.class);
        mAnswerTable = mClient.getTable(GVAnswer.class);
        final Activity activity = this;
        mActivity = this;
//        Button button = (Button) findViewById(R.id.button);
        Button button = (Button) findViewById(R.id.add_question_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final EditText edit = (EditText) findViewById(R.id.question_text);
                final GVQuestion item = new GVQuestion();

                item.setText(edit.getText().toString());
                fromPhone = "89507355808";

                BigInteger fromPhoneInteger = new BigInteger(fromPhone);
                item.setUserId(fromPhoneInteger);

                // Insert the new item
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
                    @Override
                    protected Void doInBackground(Void... params) {
                    try {
                        GVQuestion question = addItemInTable(item);
                        questionId = question.getId();
                        setPhoneContacts();
//                        Intent intent = new Intent(activity, MainActivity.class);
//                        intent.putExtra("name_of_extra", myParcelableObject);
//                        startActivity(intent);
                    } catch (final Exception e) {
                        createAndShowDialogFromTask(e, "Error");
                    }
                    return null;
                    }
                };

                runAsyncTask(task);
            }
        });
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
    public List<String> setPhoneContacts() throws ExecutionException, InterruptedException {
        List<String> phones = new ArrayList<>();

        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
//            List<String> contacts = getContactNames();
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contacts);
//            lstNames.setAdapter(adapter);


            String phoneNumber = null;

            //Связываемся с контактными данными и берем с них значения id контакта, имени контакта и его номера:
            Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
            String _ID = ContactsContract.Contacts._ID;
            String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
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
                    String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                    //Получаем имя:
                    if (hasPhoneNumber > 0) {
//                        output.append("\n Имя: " + name);
                        Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null,
                                Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);

                        //и соответствующий ему номер:
                        while (phoneCursor.moveToNext()) {
                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                            phones.add(phoneNumber);
                            GVAnswer item = new GVAnswer();
                            item.setQuestionId(questionId);
                            item.setToPhone(fromPhone);
                            addItemInTableAnswer(item);
//                            output.append("\n Телефон: " + phoneNumber);
                        }
                    }
                    output.append("\n");
                }
                System.out.println("test");
                //Полученные данные отображаем с созданном элементе TextView:
//            contacts.setText(output);
            }
        }
        return phones;
    }


//    public void onAddQuestionClick(View view)
//    {
//        final EditText edit = (EditText) findViewById(R.id.questionText);
//        // выводим сообщение
//        Toast.makeText(this, edit.getText().toString(), Toast.LENGTH_SHORT).show();
//    }

    //    List<GVQuestion> questions = new ArrayList<>();

    private Activity mActivity;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<GVQuestion> mQuestionTable;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<GVAnswer> mAnswerTable;
//    /**
//     * mAdapter to sync the items list with the view
//     */
//    private GVQuestionAdapter mAdapter;

    MobileServiceClient mClient;

    private QuestionAdapter mAdapter;

//    public Questions() {
//    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        //returning our layout file
//        //change R.layout.yourlayoutfilename for each of your fragments
//        return inflater.inflate(R.layout.fragment_questions_layout, container, false);
//    }


//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mActivity = getActivity();
//        //you can set the title for your toolbar here for different fragments different titles
//        getActivity().setTitle("Questions");



        // Offline Sync
//        mQuestionTable = mClient.getSyncTable("GVQuestion", GVQuestion.class);

        // Load the items from the Mobile Service


//        setInitialData();
//        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.questions_list);
//        // создаем адаптер
//        mAdapter = new QuestionAdapter(getView().getContext());
//        refreshItemsFromTable();
//
//        // устанавливаем для списка адаптер
//        recyclerView.setAdapter(mAdapter);
//
//        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), AddNewQuestionActivity.class);
//                startActivity(intent);

//                Snackbar.make(view, view + "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


//    }

//    private void setInitialData(){
//
//        questions.add(new GVQuestion("Huawei P10", "Huawei"));
//        questions.add(new GVQuestion("Elite z3", "HP"));
//        questions.add(new GVQuestion("Galaxy S8", "Samsung"));
//        questions.add(new GVQuestion("LG G 5", "LG"));
//    }

//    /**
//     * Refresh the list with the items in the Table
//     */
//    private void refreshItemsFromTable() {
//
//        // Get the items that weren't marked as completed and add them in the
//        // mAdapter
//
//        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
//            @Override
//            protected Void doInBackground(Void... params) {
//
//                try {
//                    final List<GVQuestion> results = refreshItemsFromMobileServiceTable();
//
//                    //Offline Sync
////                    final List<GVQuestion> results = refreshItemsFromMobileServiceTableSyncTable();
//
//                    mActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mAdapter.clear();
//
//                            for (GVQuestion item : results) {
//                                mAdapter.add(item);
//                            }
//                            mAdapter.notifyDataSetChanged();
//                        }
//                    });
//                } catch (final Exception e){
//                    createAndShowDialogFromTask(e, "Error");
//                }
//
//                return null;
//            }
//        };
//
//        runAsyncTask(task);
//    }

//    /**
//     * Refresh the list with the items in the Mobile Service Table
//     */
//
//    private List<GVQuestion> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException {
//        return mQuestionTable.where().field("UserId").eq("89507355809").execute().get();
//    }

    //Offline Sync
//    /**
//     * Refresh the list with the items in the Mobile Service Sync Table
//     */
//    private List<GVQuestion> refreshItemsFromMobileServiceTableSyncTable() throws ExecutionException, InterruptedException {
//        //sync the data
//        sync().get();
//        Query query = QueryOperations.field("complete").
//                eq(val(false));
//        return mQuestionTable.read(query).get();
//    }

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

//    /**
//     * Add a new item
//     *
//     * @param view
//     *            The view that originated the call
//     */
//    public void addItem(View view, String questionText) {
//        if (mClient == null) {
//            return;
//        }
//
//        // Create a new item
//        final GVQuestion item = new GVQuestion();
//
//        item.setQuestionText(questionText);
////        item.setComplete(false);
//
//        // Insert the new item
//        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
//            @Override
//            protected Void doInBackground(Void... params) {
//                try {
//                    final GVQuestion entity = addItemInTable(item);
//
//                    mActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
////                            if(!entity.isComplete()){
//                            mAdapter.add(entity);
////                            }
//                        }
//                    });
//                } catch (final Exception e) {
//                    createAndShowDialogFromTask(e, "Error");
//                }
//                return null;
//            }
//        };
//
//        runAsyncTask(task);
//
////        mTextNewToDo.setText("");
//    }

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

}
