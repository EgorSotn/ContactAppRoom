package com.example.mycontact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycontact.adapters.ContactAdapter;
import com.example.mycontact.db.Model.Contact;
import com.example.mycontact.db.data.ContactDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ContactDatabase contactDatabase;
    private ArrayList<Contact> contactsArrayList = new ArrayList<>();
    private ContactAdapter contactAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = findViewById(R.id.fab);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contactDatabase = Room.databaseBuilder(getApplicationContext(),ContactDatabase.class, "ContactsDB")
                .allowMainThreadQueries()
                .build();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        contactAdapter = new ContactAdapter(contactsArrayList,MainActivity.this);
        recyclerView.setAdapter(contactAdapter);

        loadContact();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Contact contact = contactsArrayList.get(viewHolder.getAdapterPosition());
                deleteContact(contact);
            }
        }).attachToRecyclerView(recyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAndEditContact(false, null, -1);
            }
        });
    }
    public void addAndEditContact(boolean isUpdate, Contact contact, int position){
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.add_contact, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);

        TextView contactTitleText = view.findViewById(R.id.contactTitle);
        EditText firstName= view.findViewById(R.id.firstNameEt);
        EditText lastName = view.findViewById(R.id.lastNameEt);
        EditText email = view.findViewById(R.id.emailEt);
        EditText phone = view.findViewById(R.id.phoneEt);

        contactTitleText.setText(!isUpdate ? "Add contact" : "Edit contact");
        if(isUpdate && contact !=null){
            firstName.setText(contact.getFirstName());
            lastName.setText(contact.getLastName());
            email.setText(contact.getEmail());
            phone.setText(contact.getPhone());
        }
        builder.setCancelable(false)
                .setPositiveButton(isUpdate ? "Update" : "Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(TextUtils.isEmpty(firstName.getText().toString())){
                            Toast.makeText(MainActivity.this, "Enter first name", Toast.LENGTH_SHORT).show();
                        }
                        else if(TextUtils.isEmpty(lastName.getText().toString())){
                            Toast.makeText(MainActivity.this, "Enter last name", Toast.LENGTH_SHORT).show();
                        }
                        else if(TextUtils.isEmpty(email.getText().toString())){
                            Toast.makeText(MainActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                        }
                        else if(TextUtils.isEmpty(phone.getText().toString())){
                            Toast.makeText(MainActivity.this, "Enter phone", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if(isUpdate && contact!=null){
                                updateContact(lastName.getText().toString(),
                                        firstName.getText().toString(),
                                        email.getText().toString(),
                                        phone.getText().toString(),
                                        position);
                            }
                            else{
                                addContact(lastName.getText().toString(),
                                        firstName.getText().toString(),
                                        email.getText().toString(),
                                        phone.getText().toString());
                            }
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void loadContact(){
        new GetAllContactsAsyncTask().execute();
    }
    private void deleteContact(Contact contact){
        new DeleteContactsAsyncTask().execute(contact);
    }
    private void addContact(String firstName,String lastName, String email, String phone){
        Contact contact = new Contact(0,firstName, lastName,email,phone);
        new AddContactsAsyncTask().execute(contact);
    }
    private void updateContact(String firstName,String lastName, String email, String phone, int position){
        Contact contact = contactsArrayList.get(position);
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setEmail(email);
        contact.setPhone(phone);
        new UpdateContactsAsyncTask().execute(contact);

        contactsArrayList.set(position,contact);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetAllContactsAsyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            contactsArrayList = (ArrayList<Contact>) contactDatabase.contactDao().getAllContact();
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            contactAdapter.setContactArrayList(contactsArrayList);
        }
    }

    private class DeleteContactsAsyncTask extends AsyncTask<Contact, Void, Void>{

        @Override
        protected Void doInBackground(Contact... contacts) {
            contactDatabase.contactDao().deleteContact(contacts[0]);
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            loadContact();
        }
    }

    private class AddContactsAsyncTask extends AsyncTask<Contact, Void, Void>{

        @Override
        protected Void doInBackground(Contact... contacts) {
            contactDatabase.contactDao().addContact(contacts[0]);
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            loadContact();
        }
    }
    private class UpdateContactsAsyncTask extends AsyncTask<Contact, Void, Void>{

        @Override
        protected Void doInBackground(Contact... contacts) {
            contactDatabase.contactDao().Update(contacts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            loadContact();
        }
    }
}