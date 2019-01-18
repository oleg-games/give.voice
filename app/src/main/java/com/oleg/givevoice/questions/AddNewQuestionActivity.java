package com.oleg.givevoice.questions;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.oleg.givevoice.R;
import com.oleg.givevoice.db.GVPrivateAzureServiceAdapter;
import com.oleg.givevoice.db.gvquestions.GVQuestion;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class AddNewQuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_question);

        GVPrivateAzureServiceAdapter servicemAdapter = GVPrivateAzureServiceAdapter.getInstance();
        MobileServiceClient mClient = servicemAdapter.getClient();
        mQuestionTable = mClient.getTable(GVQuestion.class);

//        Button button = (Button) findViewById(R.id.button);
        Button button = (Button) findViewById(R.id.add_question_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText edit = (EditText) findViewById(R.id.question_text);
                final GVQuestion item = new GVQuestion();

                item.setText(edit.getText().toString());
                BigInteger a = new BigInteger("89507355808");
                item.setUserId(a);
//                item.setId("3");
//                item.set(true);
                // Insert the new item
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
                    @Override
                    protected Void doInBackground(Void... params) {
                    try {
                        addItemInTable(item);
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
}