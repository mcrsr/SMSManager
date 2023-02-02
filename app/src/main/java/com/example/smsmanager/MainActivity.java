package com.example.smsmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DateFormat obj = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");
    Date res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getData(View view) {
        ContentResolver cr = getContentResolver();
        String msgData = "";
        Cursor c = cr.query(Telephony.Sms.Inbox.CONTENT_URI,
                new String[] { Telephony.Sms.Inbox.BODY,
                        Telephony.Sms.Inbox.ADDRESS,
                        Telephony.Sms.Inbox.CREATOR,
                        Telephony.Sms.Inbox.DATE,
                        Telephony.Sms.Inbox.DATE_SENT,
                        Telephony.Sms.Inbox.PERSON,
                        Telephony.Sms.Inbox.ERROR_CODE,
                        Telephony.Sms.Inbox.LOCKED,
                        Telephony.Sms.Inbox.PROTOCOL,
                        Telephony.Sms.Inbox.READ,
                        Telephony.Sms.Inbox.REPLY_PATH_PRESENT,
                        Telephony.Sms.Inbox.SEEN,
                        Telephony.Sms.Inbox.SERVICE_CENTER,
                        Telephony.Sms.Inbox.STATUS,
                        Telephony.Sms.Inbox.SUBJECT,
                        Telephony.Sms.Inbox.SUBSCRIPTION_ID,
                        Telephony.Sms.Inbox.THREAD_ID,
                        Telephony.Sms.Inbox.TYPE}, // Select body text
                null, null, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {

            do {
                msgData += "*********************************************************\n";
                for(int idx=0;idx<c.getColumnCount();idx++)
                {
                    if (idx == 3 || idx == 4){
                        res = new Date(Long.valueOf(c.getString(idx)));
                        msgData += " " + c.getColumnName(idx) + ":" + obj.format(res)+"\n";
                        continue;
                    }
                    msgData += " " + c.getColumnName(idx) + ":" + c.getString(idx)+"\n";

                }
                // use msgData
            } while (c.moveToNext());

        } else {
            throw new RuntimeException("You have no SMS in Inbox");
        }
        c.close();

        ((TextView)findViewById(R.id.displayData)).setText(msgData);
    }

    public void sendEmail(View view) {
        Log.i("Send email", "");
        String[] TO = {"mcrsr0208@gmail.com"};
        String[] CC = {"mcrsr0209@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Messages Backup");
        emailIntent.putExtra(Intent.EXTRA_TEXT, ((TextView)findViewById(R.id.displayData)).getText().toString());

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getOutboxData(View view) {
        ContentResolver cr = getContentResolver();
        String msgData = "";
        Cursor c = cr.query(Telephony.Sms.Sent.CONTENT_URI,
                new String[] { Telephony.Sms.Sent.BODY,
                        Telephony.Sms.Sent.ADDRESS,
                        Telephony.Sms.Sent.CREATOR,
                        Telephony.Sms.Sent.DATE,
                        Telephony.Sms.Sent.DATE_SENT,
                        Telephony.Sms.Sent.PERSON,
                        Telephony.Sms.Sent.ERROR_CODE,
                        Telephony.Sms.Sent.LOCKED,
                        Telephony.Sms.Sent.PROTOCOL,
                        Telephony.Sms.Sent.READ,
                        Telephony.Sms.Sent.REPLY_PATH_PRESENT,
                        Telephony.Sms.Sent.SEEN,
                        Telephony.Sms.Sent.SERVICE_CENTER,
                        Telephony.Sms.Sent.STATUS,
                        Telephony.Sms.Sent.SUBJECT,
                        Telephony.Sms.Sent.SUBSCRIPTION_ID,
                        Telephony.Sms.Sent.THREAD_ID,
                        Telephony.Sms.Sent.TYPE}, // Select body text
                null, null, Telephony.Sms.Sent.DEFAULT_SORT_ORDER);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {

            do {
                msgData += "*********************************************************\n";
                for(int idx=0;idx<c.getColumnCount();idx++)
                {
                    if (idx == 3 || idx == 4){
                        res = new Date(Long.valueOf(c.getString(idx)));
                        msgData += " " + c.getColumnName(idx) + ":" + obj.format(res)+"\n";
                        continue;
                    }
                    msgData += " " + c.getColumnName(idx) + ":" + c.getString(idx)+"\n";

                }
                // use msgData
            } while (c.moveToNext());

        } else {
            throw new RuntimeException("You have no SMS in Inbox");
        }
        c.close();

        ((TextView)findViewById(R.id.displayData)).setText(msgData);
    }

    public void downloadAsExcel(View view) {

        //Text of the Document
        String textToWrite = "bla bla bla";

        //Checking the availability state of the External Storage.
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {

            //If it isn't mounted - we can't write into it.
            return;
        }

        //Create a new file that points to the root directory, with the given name:
        String filenameExternal = "testdata.txt";
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filenameExternal);
        Log.d("file name",file.getAbsolutePath());
        //This point and below is responsible for the write operation
        FileOutputStream outputStream = null;
        try {
            file.createNewFile();
            //second argument of FileOutputStream constructor indicates whether
            //to append or create new file if one exists
            outputStream = new FileOutputStream(file, true);

            outputStream.write(textToWrite.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        XSSFWorkbook workbook = new XSSFWorkbook();
//        ContentResolver cr = getContentResolver();
//        String msgData = "";
//        Cursor c = cr.query(Telephony.Sms.Inbox.CONTENT_URI,
//                new String[] { Telephony.Sms.Inbox.ADDRESS,
//                        Telephony.Sms.Inbox.BODY,
//                        Telephony.Sms.Inbox.DATE,}, // Select body text
//                null, null, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
//        int totalSMS = c.getCount();
//
//        if (c.moveToFirst()) {
//
//            do {
//
//                for(int idx=0;idx<c.getColumnCount();idx++)
//                {
//                    if (idx == 2){
//                        res = new Date(Long.valueOf(c.getString(idx)));
//                        msgData += " " + c.getColumnName(idx) + ":" + obj.format(res)+"\n";
//                        continue;
//                    }
//                    msgData += " " + c.getColumnName(idx) + ":" + c.getString(idx)+"\n";
//
//                }
//            } while (c.moveToNext());
//
//        } else {
//            throw new RuntimeException("You have no SMS in Inbox");
//        }
//        c.close();
//
//        ((TextView)findViewById(R.id.displayData)).setText(msgData);
    }
}