package com.example.mycontact.db.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mycontact.Contact;

import java.util.List;


@Dao
public interface ContactDao {
    @Query("SELECT * FROM contacts")
    public List<Contact> getAllContact();

    @Query("SELECT * FROM contacts WHERE id ==:contact_id")
    public Contact getContact(long contact_id);

    @Insert
    public void addContact(Contact contact);

    @Delete
    public void deleteContact(Contact contact);

    @Update
    public void Update(Contact contact);


}
