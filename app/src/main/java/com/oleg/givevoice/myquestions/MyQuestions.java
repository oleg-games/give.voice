package com.oleg.givevoice.myquestions;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import com.oleg.givevoice.exceptions.GVException;
import com.oleg.givevoice.questionsforme.RecyclerItemClickListener;
import com.oleg.givevoice.tasks.GVTask;

import java.util.Arrays;
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
    private View mQuestionsFormView;
    private View mFab;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private MyQuestionsTask mTask;

    public MyQuestions() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layouts file
        //change R.layouts.yourlayoutfilename for each of your fragments
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

        mQuestionsFormView = getView().findViewById(R.id.questions_form);
        mProgressView = getView().findViewById(R.id.progress_bar);
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
                        GVQuestion item = mAdapter.get(position);
                        Intent intent = new Intent(mActivity, AddNewQuestionActivity.class);
                        intent.putExtra(GVQuestion.class.getSimpleName(), item);
                        startActivity(intent);
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
        mTask = new MyQuestionsTask(Arrays.asList(mQuestionsFormView, mFab), Arrays.asList(mProgressView), getResources());
        // Check the SDK version and whether the permission is already granted or not.
        mTask.execute();
    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */

    private List<GVQuestion> refreshItemsFromMobileServiceTable(String phoneNumber) throws ExecutionException, InterruptedException {
        return mQuestionTable.where().field("UserId").eq(phoneNumber).execute().get();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class MyQuestionsTask extends GVTask {

        MyQuestionsTask(List<View> mainViews, List<View> progressViews, Resources resources) {
            super(mActivity, mainViews, progressViews, resources);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            if (super.doInBackground(params)) {
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

                        return true;
                    } catch (final Exception e){
                        createAndShowDialog(e, "Error");
                    }
                } else {
                    createAndShowDialog(new GVException("Empty phone number"), "GVError");
                }
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