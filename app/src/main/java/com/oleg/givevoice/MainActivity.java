package com.oleg.givevoice;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Context mContext;

    private Button btn;
    private TextView tvname, tvphone,tvmail;
    private ArrayList<String> mItems;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    RelativeLayout mRelativeLayout;
    Cursor cursor ;
    ArrayList<String> storeContacts ;
    String name, phonenumber ;
    public  static final int RequestPermissionCode  = 1 ;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    RecyclerView recyclerview;
    View ChildView ;
    int RecyclerViewItemPosition ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerview = (RecyclerView)findViewById(R.id.recycler_view);

        btn = (Button) findViewById(R.id.btn);

        // Get the widgets reference from XML layout
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());

        mRecyclerView.setLayoutManager(RecyclerViewLayoutManager);

        storeContacts = new ArrayList<String>();
        EnableRuntimePermission();

        recyclerview.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {

                @Override public boolean onSingleTapUp(MotionEvent motionEvent) {

                    return true;
                }

            });
            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

                ChildView = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if(ChildView != null && gestureDetector.onTouchEvent(motionEvent)) {

                    RecyclerViewItemPosition = Recyclerview.getChildAdapterPosition(ChildView);

                    Toast.makeText(MainActivity.this, storeContacts.get(RecyclerViewItemPosition), Toast.LENGTH_LONG).show();
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetContactsIntoArrayList();
                RecyclerView.Adapter adapter = new RecyclerViewAdapter(storeContacts);

                recyclerview.setAdapter(adapter);


//                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//                startActivityForResult(intent, 1);
            }
        });

    }



    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                MainActivity.this,
                Manifest.permission.READ_CONTACTS))
        {

            Toast.makeText(MainActivity.this,"CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }
    }

    public void GetContactsIntoArrayList(){

        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);

        while (cursor.moveToNext()) {

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            storeContacts.add(name + " "  + ":" + " " + phonenumber);
        }

        cursor.close();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ContentResolver cr = getContentResolver(); //Activity/Application android.content.Context
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if(cursor.moveToFirst())
        {
            ArrayList<String> alContacts = new ArrayList<String>();
            do
            {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{ id }, null);
                    while (pCur.moveToNext())
                    {
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        alContacts.add(contactNumber);
                        break;
                    }
                    pCur.close();
                }

            } while (cursor.moveToNext());

            System.out.println(alContacts);
        }
    }
//        if (resultCode == Activity.RESULT_OK) {
//            Uri contactData = data.getData();
//            System.out.println("aaaaaa");
//            Cursor c =  getContentResolver().query(contactData, null, null, null, null);
//            System.out.println("aaaaaa2");
//            if (c.moveToFirst()) {
//                System.out.println("aaaaaa3");
//
//                String phoneNumber="",emailAddress="";
//                String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
//                //http://stackoverflow.com/questions/866769/how-to-call-android-contacts-list   our upvoted answer
//
//                System.out.println("aaaaaa4");
//                String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
//
//                System.out.println("aaaaaa5");
//                if ( hasPhone.equalsIgnoreCase("1"))
//                    hasPhone = "true";
//                else
//                    hasPhone = "false" ;
//
//                System.out.println("aaaaaa6");
//                if (Boolean.parseBoolean(hasPhone))
//                {
//                    System.out.println("aaaaaa7");
//                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
//                    while (phones.moveToNext())
//                    {
//                        System.out.println("aaaaaa87");
//                        phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                    }
//                    System.out.println("aaaaaa9");
//
//                    phones.close();
//                }
//                System.out.println("aaaaaa10");
//
//                // Find Email Addresses
//                Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId,null, null);
//                System.out.println("aaaaaa11");
//                while (emails.moveToNext())
//                {
//                    emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//                }
//                emails.close();
//
//                //mainActivity.onBackPressed();
//                // Toast.makeText(mainactivity, "go go go", Toast.LENGTH_SHORT).show();
//
//                tvname.setText("Name: "+name);
//                tvphone.setText("Phone: "+phoneNumber);
//                tvmail.setText("Email: "+emailAddress);
//                Log.d("curs", name + " num" + phoneNumber + " " + "mail" + emailAddress);
//            }
//            c.close();
//        }
//    }
}
