package com.example.mycontact.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycontact.MainActivity;
import com.example.mycontact.R;
import com.example.mycontact.Contact;
import com.example.mycontact.databinding.ContactListItemBinding;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    ArrayList<Contact> contactArrayList = new ArrayList<>();
    private Context context;
    private MainActivity mainActivity;

    public void setContactArrayList(ArrayList<Contact> contactArrayList) {
        this.contactArrayList = contactArrayList;
        notifyDataSetChanged();
    }
    public ContactAdapter(ArrayList<Contact> contacts, MainActivity mainActivity){
        this.contactArrayList = contacts;
        this.mainActivity = mainActivity;
    }
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);
        ContactListItemBinding contactListItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.contact_list_item,
                parent,
                false
        );
        return new ContactViewHolder(contactListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {


        Contact contact = contactArrayList.get(position);

//        holder.firstName.setText(contact.getFirstName());
//        holder.lastName.setText(contact.getLastName());
//        holder.email.setText(contact.getEmail());
//        holder.phone.setText(contact.getPhone());
        holder.contactListItemBinding.setContact(contact);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.addAndEditContact(true, contact, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactArrayList.size();
    }


    class ContactViewHolder extends RecyclerView.ViewHolder {

        private ContactListItemBinding contactListItemBinding;
       // private TextView firstName, lastName, email, phone;
        public ContactViewHolder(@NonNull ContactListItemBinding itemView) {
            super(itemView.getRoot());
            this.contactListItemBinding = itemView;
//            firstName = itemView.findViewById(R.id.firstName);
//            lastName = itemView.findViewById(R.id.lastName);
//            email = itemView.findViewById(R.id.email);
//            phone = itemView.findViewById(R.id.phone);

        }
    }
}
