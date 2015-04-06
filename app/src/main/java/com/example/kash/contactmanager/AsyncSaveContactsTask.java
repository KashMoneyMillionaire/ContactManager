package com.example.kash.contactmanager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by Kash on 3/11/2015.
 * This asynchonously saves a list of contacts to the phone under Contacts.txt
 */
public class AsyncSaveContactsTask extends AsyncTask<Pair<List<Contact>, Context>, Void, Void> {
    @Override
    protected Void doInBackground(Pair<List<Contact>, Context>... params) {
        String filename = "Contacts.txt";
        FileOutputStream outputStream;

        List<Contact> contacts = params[0].first;
        Context context = params[0].second;

        //for each item in our list, save it as tab separated properties on a single line
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            for (Contact contact : contacts) {
                outputStream.write((contact.stringify() + "\n").getBytes());
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
