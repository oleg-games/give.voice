package com.oleg.givevoice.questionsforme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import com.oleg.givevoice.exceptions.GVException;
import com.oleg.givevoice.tasks.GVTask;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Belal on 18/09/16.
 */


public class QuestionsForMe extends Fragment {


    private Activity mActivity;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<GVQuestionAnswer> mQuestionAnswerTable;
    private QuestionsForMeAdapter mAdapter;

    private View mProgressView;
    private View mQuestionsFormView;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private QuestionsForMeTask mTask;

    public QuestionsForMe() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layouts file
        //change R.layouts.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_questions_for_me_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = getActivity();
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(R.string.action_questions_for_me);

        mQuestionsFormView = getView().findViewById(R.id.questions_form);
        mProgressView = getView().findViewById(R.id.progress_bar);

        GVPrivateAzureServiceAdapter serviceAdapter = GVPrivateAzureServiceAdapter.getInstance();
        MobileServiceClient mClient = serviceAdapter.getClient();
        mQuestionAnswerTable = mClient.getTable(GVQuestionAnswer.class);

        // создаем адаптер
        mAdapter = new QuestionsForMeAdapter(getView().getContext());
        refreshItemsFromTable();

        // устанавливаем для списка адаптер
        RecyclerView recyclerView = getView().findViewById(R.id.questions_for_me_list);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        GVQuestionAnswer item = mAdapter.get(position);
                        Intent intent = new Intent(mActivity, GetQuestionsForMeActivity.class);
                        intent.putExtra(GVQuestionAnswer.class.getSimpleName(), item);
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                        GVQuestionAnswer item = mAdapter.get(position);
                        Intent intent = new Intent(mActivity, GetQuestionsForMeActivity.class);
                        intent.putExtra(GVQuestionAnswer.class.getSimpleName(), item);
                        startActivity(intent);
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
        mTask = new QuestionsForMeTask(Arrays.asList(mQuestionsFormView), Arrays.asList(mProgressView), getResources());
        // Check the SDK version and whether the permission is already granted or not.
        mTask.execute();
    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */

    private List<GVQuestionAnswer> refreshItemsFromMobileServiceTable(String phoneNumber) throws ExecutionException, InterruptedException {
        return mQuestionAnswerTable.where().field("toPhone").eq(phoneNumber).and().field("text").ne("").execute().get();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class QuestionsForMeTask extends GVTask {

        QuestionsForMeTask(List<View> mainViews, List<View> progressViews, Resources resources) {
            super(mActivity, mainViews, progressViews, resources);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            if (super.doInBackground(params)) {
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