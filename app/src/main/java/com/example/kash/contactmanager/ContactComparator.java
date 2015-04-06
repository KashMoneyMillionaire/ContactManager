package com.example.kash.contactmanager;

import java.util.Comparator;

/**
 * Created by Kash on 3/10/2015.
 * Used to order the Contacts in the list by first then last name
 */
public class ContactComparator implements Comparator<Contact> {
    @Override
    public int compare(Contact o1, Contact o2) {
        return o1.getFullName().compareTo(o2.getFullName());
    }
}
