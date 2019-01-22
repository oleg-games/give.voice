package com.oleg.givevoice.main;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.oleg.givevoice.R;
import com.oleg.givevoice.db.GVPublicAzureServiceAdapter;
import com.oleg.givevoice.db.gvverificationuser.GVVerificationUser;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class VerifyCodeLoginActivity extends AppCompatActivity {

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<GVVerificationUser> mVerificationUserTable;

    private GVVerificationUser currentUser;
    private View mProgressView;
//    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_code_login);

        GVPublicAzureServiceAdapter
                servicemAdapter = GVPublicAzureServiceAdapter.getInstance();
        MobileServiceClient mClient = servicemAdapter.getClient();
        mVerificationUserTable = mClient.getTable(GVVerificationUser.class);

        Button button = (Button) findViewById(R.id.verifyButton);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(v.getContext());
                final String phone = settings.getString("phone", "");
                final EditText edit = (EditText) findViewById(R.id.editText);
                final Context context = v.getContext();
                AsyncTask<Void, Void, Void> task1 = new AsyncTask<Void, Void, Void>(){
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            showProgress(true);
//                    final List<GVVerificationUser> users = getItemsInTable(phone);
                            final List<GVVerificationUser> users = mVerificationUserTable.where().field("userPhone").eq(phone).execute().get();
                            currentUser = users.get(0);
                            showProgress(false);
                            if (currentUser != null) {
                                currentUser.setCode(edit.getText().toString());
                                try {
                                    updateItemInTable(currentUser);
                                    Intent intent = new Intent(context, MainActivity.class);
                                    startActivity(intent);
                                } catch (final Exception e) {
                                    createAndShowDialogFromTask(e, "Error");
                                }
                            }
                        } catch (final Exception e) {
                            createAndShowDialogFromTask(e, "Error");
                        }
                        return null;
                    }
                };

                runAsyncTask(task1);

//                if (currentUser != null) {
//                    currentUser.setCode(edit.getText().toString());
//
//                    AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
//                        @Override
//                        protected Void doInBackground(Void... params) {
//                            try {
//                                updateItemInTable(currentUser);
//                            } catch (final Exception e) {
//                                createAndShowDialogFromTask(e, "Error");
//                            }
//                            return null;
//                        }
//                    };
//
//                    runAsyncTask(task);
//                }
            }
        });
//        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });

//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mProgressView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//                }
//            });
//        } else {
//            // The ViewPropertyAnimator APIs are not available, so simply show
//            // and hide the relevant UI components.
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
////            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//        }
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
        runOnUiThread(new Runnable() {
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
    public GVVerificationUser updateItemInTable(GVVerificationUser item) throws ExecutionException, InterruptedException {
        GVVerificationUser entity = mVerificationUserTable.update(item).get();
        return entity;
    }

    public List<GVVerificationUser> getItemsInTable(String phone) throws ExecutionException, InterruptedException {
        return mVerificationUserTable.where().field("userPhone").eq(phone).execute().get();
    }
}
