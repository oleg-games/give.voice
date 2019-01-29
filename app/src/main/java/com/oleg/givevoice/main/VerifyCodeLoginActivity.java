package com.oleg.givevoice.main;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_code_login);

        GVPublicAzureServiceAdapter
                servicemAdapter = GVPublicAzureServiceAdapter.getInstance();
        MobileServiceClient mClient = servicemAdapter.getClient();
        mVerificationUserTable = mClient.getTable(GVVerificationUser.class);

        Button button = findViewById(R.id.verifyButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText edit = findViewById(R.id.editText);
                final Context context = v.getContext();
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
                    @Override
                    protected Void doInBackground(Void... params) {
                        SharedPreferences settings = PreferenceManager
                                .getDefaultSharedPreferences(context);
                        final String phoneNumber = settings.getString("phone", "");

                        if (phoneNumber != null && !phoneNumber.isEmpty()) {
                            try {
                                final List<GVVerificationUser> users = mVerificationUserTable.where().field("userPhone").eq(phoneNumber).execute().get();
                                currentUser = users.get(0);
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
                        } else {
                            // TODO
                        }

                        return null;
                    }
                };

                runAsyncTask(task);
            }
        });
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
}
