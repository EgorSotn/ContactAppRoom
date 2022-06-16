package com.example.mycontact.db.data;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.mycontact.db.Model.Contact;

@Database(entities = {Contact.class}, version = 1)
public abstract class ContactDatabase extends RoomDatabase {
    public abstract ContactDao contactDao();
}
