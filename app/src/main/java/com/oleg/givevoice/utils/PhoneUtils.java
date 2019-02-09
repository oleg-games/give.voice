package com.oleg.givevoice.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.http.HttpConstants;
import com.oleg.givevoice.db.GVPrivateAzureServiceAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PhoneUtils {
    public static String getPhoneNumber(String phone) {
        return phone.replaceAll("[^0-9]", "");
    }

    public static void setPhoneContacts(String questionId, ContentResolver contentResolver) throws ExecutionException, InterruptedException {
        // Check the SDK version and whether the permission is already granted or not.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
//            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
//        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            String phoneNumber;
            List<String> phones = new ArrayList<String>();

            //Связываемся с контактными данными и берем с них значения id контакта, имени контакта и его номера:
            Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
            String _ID = ContactsContract.Contacts._ID;
            String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

            Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
            String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;


            StringBuffer output = new StringBuffer();
            Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);
            //Запускаем цикл обработчик для каждого контакта:
            if (cursor.getCount() > 0) {
                //Если значение имени и номера контакта больше 0 (то есть они существуют) выбираем
                //их значения в приложение привязываем с соответствующие поля "Имя" и "Номер":
                while (cursor.moveToNext()) {
                    String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                    //Получаем имя:
                    if (hasPhoneNumber > 0) {
                        Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null,
                                Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);

                        //и соответствующий ему номер:
                        while (phoneCursor.moveToNext()) {
                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                            phones.add(PhoneUtils.getPhoneNumber(phoneNumber));
                        }

                        phoneCursor.close();
                    }
                    output.append("\n");
                }
            }

            cursor.close();

            // foreach
            // Basic loop
            JsonObject content = new JsonObject();
            content.add("phones", new Gson().toJsonTree(phones));
            content.add("questionId", new Gson().toJsonTree(questionId));
            testInvokeNullResponseObject(content);
//        }
    }

    public static void testInvokeNullResponseObject(JsonObject content) {

        try {

            GVPrivateAzureServiceAdapter servicemAdapter = GVPrivateAzureServiceAdapter.getInstance();
            MobileServiceClient mClient = servicemAdapter.getClient();


//            MobileServiceClient client = new MobileServiceClient(appUrl, getInstrumentation().getTargetContext());
//            client = client.withFilter(new NullResponseFilter());

            JsonElement response = mClient.invokeApi("answers", content, HttpConstants.PutMethod, null).get();
            System.out.print("response" + response);
        } catch (Exception exception) {
            if (!(exception.getCause() instanceof MobileServiceException)) {
                System.out.println("test");
//                fail(exception.getMessage());
            }

            return;
        }

//        fail("Exception expected");

    }
}
