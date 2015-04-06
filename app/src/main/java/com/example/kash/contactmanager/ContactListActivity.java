package com.example.kash.contactmanager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.Dialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("ALL")
/**
 * Created by Kash on 3/10/2015.
 * This activity is our list of contacts.
 */
public class ContactListActivity extends ActionBarActivity {

    private RecyclerView mRecyclerView;
    private ContactListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Context context;

    private final int NEW_CONTACT_CODE = 1, EDIT_CONTACT_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        //the recycler view is our list. we set the lines between each item
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        //set context
        context = this;

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //fill list of contacts asynchronously
        new AsyncReadContactsTask().execute();
    }

    //gets called when returning from activities with results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //make sure everything came back okay
        if (resultCode == RESULT_OK) {

            //adding new contact
            if (requestCode == NEW_CONTACT_CODE) {
                // get the contact from the intent, then add it through the adapter. Save changes
                Contact contact = (Contact) data.getSerializableExtra("result");
                mAdapter.addItem(contact);
                new AsyncSaveContactsTask().execute(new Pair<List<Contact>, Context>(mAdapter.mDataset, this));
            }
            //editing old contact
            else if (requestCode == EDIT_CONTACT_CODE) {
                // get the contact from the intent and position, then pass to adapter's editItem. Save changes
                Contact contact = (Contact) data.getSerializableExtra("result");
                int itemPosition = data.getIntExtra("itemPosition", -1);
                mAdapter.editItem(contact, itemPosition);
                new AsyncSaveContactsTask().execute(new Pair<List<Contact>, Context>(mAdapter.mDataset, this));
            }
        }
    }

    //launches a new activity to create a new contact
    public void addNewContact(View view) {
        Intent intent = new Intent(this, EditAddContactActivity.class);
        startActivityForResult(intent, NEW_CONTACT_CODE);
    }

    //launches a dialog to confirm deleting contact
    public void deleteContactConfirm(View v) {
        //get position
        TextView itemPositionView = (TextView) ((View) v.getParent().getParent()).findViewById(R.id.item_position);
        CharSequence text = itemPositionView.getText();
        final int itemPosition = Integer.parseInt(text.toString());

        //launch dialog
        Dialog dialog = new Dialog(this, "Confirm Delete", "Are you sure you wish to delete this contact?");
        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete item and save contacts
                mAdapter.deleteItem(itemPosition);
                new AsyncSaveContactsTask().execute(new Pair<>(mAdapter.mDataset, context));
            }
        });
        dialog.addCancelButton("Cancel");
        dialog.show();

        //edit accept button
        ButtonFlat buttonAccept = dialog.getButtonAccept();
        buttonAccept.setText("Delete");
    }

    //edit contact button click
    public void editContact(View v) {
        //get item position
        TextView itemPositionView = (TextView) ((View) v.getParent().getParent()).findViewById(R.id.item_position);
        int itemPosition = Integer.parseInt(itemPositionView.getText().toString());

        //edit the contact
        editContact(mAdapter.mDataset.get(itemPosition), itemPosition);
    }

    //launches a new activity to edit an existing contact
    private void editContact(Contact c, int itemPosition) {
        Intent intent = new Intent(this, EditAddContactActivity.class);
        intent.putExtra("contact", c);
        intent.putExtra("itemPosition", itemPosition);
        startActivityForResult(intent, EDIT_CONTACT_CODE);
    }

    /**
     * Created by Kash on 3/10/2015.
     * Used to read the list of contacts asynchronously
     */
    public class AsyncReadContactsTask extends AsyncTask<Void, Void, List<Contact>> {

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected List<Contact> doInBackground(Void... params) {

            //open file
            File file = new File(context.getFilesDir(), "Contacts.txt");
            List<Contact> contacts = new ArrayList<>();

            //for each line in file, create a new contact and add it to the list
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    contacts.add(Contact.Read(line.split("\\t", -1)));
                }
                br.close();
            } catch (IOException e) {
                //You'll need to add proper error handling here
            }
            return contacts;
        }

        @Override
        protected void onPostExecute(List<Contact> myDataset) {
            /* Download complete. Lets update UI */
            setProgressBarIndeterminateVisibility(false);

            //set values in the adapter
            mAdapter = new ContactListAdapter(myDataset);
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}
