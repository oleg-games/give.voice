package com.oleg.givevoice.questionsanswers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.oleg.givevoice.R;
import com.oleg.givevoice.db.GVPrivateAzureServiceAdapter;
import com.oleg.givevoice.db.gvquestionsanswers.GVQuestionAnswer;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Belal on 18/09/16.
 */


public class QuestionsAnswers extends Fragment {


    private Activity mActivity;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<GVQuestionAnswer> mQuestionAnswerTable;
    private QuestionAnswerAdapter mAdapter;

    public QuestionsAnswers() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_questions_answers_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = getActivity();
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Questions Answers");

        GVPrivateAzureServiceAdapter serviceAdapter = GVPrivateAzureServiceAdapter.getInstance();
        MobileServiceClient mClient = serviceAdapter.getClient();
        mQuestionAnswerTable = mClient.getTable(GVQuestionAnswer.class);

        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.questions_answers_list);
        // создаем адаптер
        mAdapter = new QuestionAnswerAdapter(getView().getContext());
        refreshItemsFromTable();

        // устанавливаем для списка адаптер
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        GVQuestionAnswer item = mAdapter.get(position);
                        Intent intent = new Intent(mActivity, GetQuestionAnswerActivity.class);
                        intent.putExtra(GVQuestionAnswer.class.getSimpleName(), item);
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                        System.out.println("teat2");
                    }
                })
        );
    }

    /**
     * Refresh the list with the items in the Table
     */
    private void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the
        // mAdapter

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getContext());
                final String phoneNumber = settings.getString("phone", "");
                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    try {
                        final List<GVQuestionAnswer> results = refreshItemsFromMobileServiceTable(phoneNumber);

                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.clear();

                                for (GVQuestionAnswer item : results) {
                                    mAdapter.add(item);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (final Exception e){
                        createAndShowDialogFromTask(e, "Error");
                    }
                } else {
                    // TODO
                }

                return null;
            }
        };

        runAsyncTask(task);
    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */

    private List<GVQuestionAnswer> refreshItemsFromMobileServiceTable(String phoneNumber) throws ExecutionException, InterruptedException {
        return mQuestionAnswerTable.where().field("toPhone").eq(phoneNumber).execute().get();
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }
}