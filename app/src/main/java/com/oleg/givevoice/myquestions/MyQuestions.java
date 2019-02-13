package com.oleg.givevoice.myquestions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.oleg.givevoice.R;
import com.oleg.givevoice.db.GVPrivateAzureServiceAdapter;
import com.oleg.givevoice.db.gvquestions.GVQuestion;
import com.oleg.givevoice.questionsforme.RecyclerItemClickListener;
import com.oleg.givevoice.utils.ActivityUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Belal on 18/09/16.
 */


public class MyQuestions extends Fragment {

    private Activity mActivity;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<GVQuestion> mQuestionTable;
    private QuestionAdapter mAdapter;

    private View mProgressView;
    private View mMyQuestionsFormView;
    private View mFab;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private MyQuestionsTask mAuthTask = null;

    public MyQuestions() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_my_questions_layout, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = getActivity();
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(R.string.action_my_questions);

        GVPrivateAzureServiceAdapter servicemAdapter = GVPrivateAzureServiceAdapter.getInstance();
        MobileServiceClient mClient = servicemAdapter.getClient();
        mQuestionTable = mClient.getTable(GVQuestion.class);

        mMyQuestionsFormView = getView().findViewById(R.id.question_form);
        mProgressView = getView().findViewById(R.id.my_questions_progress);
        mFab = getView().findViewById(R.id.fab);
        // устанавливаем для списка адаптер
        RecyclerView recyclerView = getView().findViewById(R.id.questions_list);
        // создаем адаптер
        mAdapter = new QuestionAdapter(getView().getContext());
        refreshItemsFromTable();

        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        GVQuestion item = mAdapter.get(position);
                        Intent intent = new Intent(mActivity, AddNewQuestionActivity.class);
                        intent.putExtra(GVQuestion.class.getSimpleName(), item);
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                        System.out.println("teat2");
                    }
                })
        );

        FloatingActionButton fab = getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddNewQuestionActivity.class);
                startActivity(intent);
            }
        });


    }

    /**
     * Refresh the list with the items in the Table
     */
    private void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the
        // mAdapter
        mAuthTask = new MyQuestionsTask();
        // Check the SDK version and whether the permission is already granted or not.
        mAuthTask.execute((Void) null);

//        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
//            @Override
//            protected Void doInBackground(Void... params) {
//
//                SharedPreferences settings = PreferenceManager
//                        .getDefaultSharedPreferences(getContext());
//                final String fromPhone = settings.getString("phone", "");
//                if (fromPhone != null && !fromPhone.isEmpty()) {
//                    try {
//                        final List<GVQuestion> results = refreshItemsFromMobileServiceTable(fromPhone);
//
//                        mActivity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                mAdapter.clear();
//
//                                for (GVQuestion item : results) {
//                                    mAdapter.add(item);
//                                }
//                                mAdapter.notifyDataSetChanged();
//                            }
//                        });
//                    } catch (final Exception e){
//                        createAndShowDialogFromTask(e, "Error");
//                    }
//                } else {
//                    // TODO
//                }
//
//                return null;
//            }
//        };
//
//        runAsyncTask(task);
    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */

    private List<GVQuestion> refreshItemsFromMobileServiceTable(String phoneNumber) throws ExecutionException, InterruptedException {
        return mQuestionTable.where().field("UserId").eq(phoneNumber).execute().get();
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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class MyQuestionsTask extends AsyncTask<Void, Void, Boolean> {

        MyQuestionsTask() {}

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            SharedPreferences settings = PreferenceManager
                    .getDefaultSharedPreferences(getContext());
            final String fromPhone = settings.getString("phone", "");
            if (fromPhone != null && !fromPhone.isEmpty()) {
                try {
                    final List<GVQuestion> results = refreshItemsFromMobileServiceTable(fromPhone);

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();

                            for (GVQuestion item : results) {
                                mAdapter.add(item);
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (final Exception e){
                    createAndShowDialogFromTask(e, "Error");
                    throw new Error(e);
                }
            } else {
                // TODO
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            mFab.setVisibility(View.VISIBLE);
            ActivityUtils.showProgress(false, mMyQuestionsFormView, mProgressView, getResources());
            if (success) {
//                getActivity().finish();
            } else {
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
//                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mFab.setVisibility(View.GONE);
            ActivityUtils.showProgress(true, mMyQuestionsFormView, mProgressView, getResources());
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            mFab.setVisibility(View.VISIBLE);
            ActivityUtils.showProgress(false, mMyQuestionsFormView, mProgressView, getResources());
        }
    }
}