package com.example.kash.contactmanager;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.gc.materialdesign.views.Button;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.Serializable;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Kash on 3/10/2015.
 * Activity used to edit/add a contact
 */
public class EditAddContactActivity extends ActionBarActivity {

    @InjectView(R.id.fName) MaterialEditText firstName;
    @InjectView(R.id.lName) EditText lastName;
    @InjectView(R.id.phoneNum) EditText phoneNumber;
    @InjectView(R.id.email) EditText email;

    private int itemPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);
        ButterKnife.inject(this);

        //set submit on done for email
        email.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveContact(null);
                    return true;
                }
                return false;
            }
        });

        //if we are passed back an intent with a contact, set the fields
        Intent intent = getIntent();
        if (intent != null){
            Contact contact = (Contact) intent.getSerializableExtra("contact");
            itemPosition = intent.getIntExtra("itemPosition", -1);
            if (contact != null){
                setTitle(R.string.edit_contact);
//                title.setText(R.string.edit_contact);
                firstName.setText(contact.getFirstName().trim());
                lastName.setText(contact.getLastName().trim());
                phoneNumber.setText(contact.getPhoneNumber().trim());
                email.setText(contact.getEmail().trim());
            }
        }

    }

    //saves the current contact
    public void saveContact(View v){

        //first name is required
        if (firstName.length() == 0) {
            firstName.setError("Field required");
            return;
        }

        //creates contact from fields
        Contact contact = new Contact(firstName.getText().toString(),
                lastName.getText().toString(),
                phoneNumber.getText().toString(),
                email.getText().toString());

        Intent i = new Intent();
        i.putExtra("result", contact);
        i.putExtra("itemPosition", itemPosition);
        setResult(RESULT_OK, i);
        finish();
    }
}
