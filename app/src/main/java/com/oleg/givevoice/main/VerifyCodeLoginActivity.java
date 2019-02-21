package com.oleg.givevoice.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import com.oleg.givevoice.exceptions.GVException;
import com.oleg.givevoice.tasks.GVTask;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class VerifyCodeLoginActivity extends AppCompatActivity {

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<GVVerificationUser> mVerificationUserTable;

    private GVVerificationUser currentUser;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private VerifyTask mTask;
    private Activity mActivity;

    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_code_login);

        GVPublicAzureServiceAdapter
                servicemAdapter = GVPublicAzureServiceAdapter.getInstance();
        MobileServiceClient mClient = servicemAdapter.getClient();
        mVerificationUserTable = mClient.getTable(GVVerificationUser.class);

        mLoginFormView= findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.progress_bar);
        mActivity = this;
        Button button = findViewById(R.id.verifyButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText edit = findViewById(R.id.editText);
                final Context context = v.getContext();

                mTask = new VerifyTask(Arrays.asList(mLoginFormView), Arrays.asList(mProgressView), getResources(), context, edit.getText().toString());
                // Check the SDK version and whether the permission is already granted or not.
                mTask.execute();
            }
        });
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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class VerifyTask extends GVTask {

        Context mContext;
        String mCode;

        VerifyTask(List<View> mainViews, List<View> progressViews, Resources resources, Context context, String code) {
            super(mActivity, mainViews, progressViews, resources);
            this.mContext = context;
            this.mCode = code;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            if (super.doInBackground(params)) {
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(mContext);
                final String phoneNumber = settings.getString("phone", "");

                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    try {
                        final List<GVVerificationUser> users = mVerificationUserTable.where().field("userPhone").eq(phoneNumber).execute().get();
                        currentUser = users.get(0);
                        if (currentUser != null) {
                            currentUser.setCode(mCode);
                                updateItemInTable(currentUser);
                                Intent intent = new Intent(mContext, MainActivity.class);
                                startActivity(intent);
                        }

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
