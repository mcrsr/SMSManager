package com.example.smsmanager;

import static android.provider.CallLog.Calls.LIMIT_PARAM_KEY;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MainActivity extends AppCompatActivity {

    DateFormat obj = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");
    Date res;
    String msgData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getData(View view) {
        ContentResolver cr = getContentResolver();
        msgData = "";
        Cursor c = cr.query(Telephony.Sms.Inbox.CONTENT_URI,
                new String[] { Telephony.Sms.Inbox.BODY,
                        Telephony.Sms.Inbox.ADDRESS,
                        Telephony.Sms.Inbox.DATE,
                        Telephony.Sms.Inbox.DATE_SENT,}, // Select body text
                null, null, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {

            do {
                msgData += "*********************************************************\n";
                for(int idx=0;idx<c.getColumnCount();idx++)
                {
                    if (idx == 2 || idx == 3){
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

    public void sendEmail(View view){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Recipient's email ID needs to be mentioned.
        String to = "mcrsr0208@gmail.com";

        // Sender's email ID needs to be mentioned
        String from = "mcrsr0209@gmail.com";

        // Assuming you are sending email from through gmails smtp
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication("mcrsr0209@gmail.com", "dihqvujvkatvbiam");

            }

        });

        // Used to debug SMTP issues
        session.setDebug(true);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("Messages In Inbox Back-upped");

            // Now set the actual message
            message.setText(((TextView)findViewById(R.id.displayData)).getText().toString());

            System.out.println("sending...");
            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
            Toast.makeText(this,"Email Sent Successfully!",Toast.LENGTH_LONG).show();
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public void getOutboxData(View view) {
        ContentResolver cr = getContentResolver();
        msgData = "";
        Cursor c = cr.query(Telephony.Sms.Sent.CONTENT_URI,
                new String[] { Telephony.Sms.Sent.BODY,
                        Telephony.Sms.Sent.ADDRESS,
                        Telephony.Sms.Sent.DATE,
                        Telephony.Sms.Sent.DATE_SENT,}, // Select body text
                null, null, Telephony.Sms.Sent.DEFAULT_SORT_ORDER);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {

            do {
                msgData += "*********************************************************\n";
                for(int idx=0;idx<c.getColumnCount();idx++)
                {
                    if (idx == 2 || idx == 3){
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
    }

    @SuppressLint("Range")
    public void getContacts(View view) {
        String msgData = null;
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                @SuppressLint("Range") String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                @SuppressLint("Range") String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.i("TAG", "Name: " + name);
                        Log.i("TAG", "Phone Number: " + phoneNo);
                        msgData += "Name: "+name+" Number: "+phoneNo+"\n";
                    }
                    pCur.close();
                }
            }
        }
        ((TextView)findViewById(R.id.displayData)).setText(msgData);
        if(cur!=null){
            cur.close();
        }
    }

    @SuppressLint("Range")
    public void getCallLogs(View view) {
        String msgData=null;
        Cursor callLogs = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        while (callLogs.moveToNext()){
            String number = callLogs.getString(callLogs.getColumnIndex(CallLog.Calls.NUMBER));
            String type = callLogs.getString(callLogs.getColumnIndex(CallLog.Calls.TYPE));
            String date = callLogs.getString(callLogs.getColumnIndex(CallLog.Calls.DATE));
            Date date1 = new Date(Long.valueOf(date));
            String duration = callLogs.getString(callLogs.getColumnIndex(CallLog.Calls.DURATION));
            String callType=null;
            switch (Integer.parseInt(type)){
                case CallLog.Calls.OUTGOING_TYPE:
                    callType = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    callType = "INCOMING";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callType = "MISSED";
                    break;
            }
            msgData += number+" "+callType+" "+date1+" "+duration+"\n";
            ((TextView)findViewById(R.id.displayData)).setText(msgData);
        }
        callLogs.close();
    }
}