package com.example.contactos;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

final class ContactStorage {

    private static final String PREFERENCES = "saved_contacts";
    private static final String IDS_KEY = "ids";
    private static final String SEPARATOR = "\u001f";

    private ContactStorage() {
    }

    static void save(
            @NonNull Context context,
            @NonNull String firstName,
            @NonNull String lastName,
            @NonNull String phone,
            @NonNull String email,
            @NonNull String company,
            @NonNull String jobTitle
    ) {
        SharedPreferences preferences = getPreferences(context);
        String id = String.valueOf(System.currentTimeMillis());
        String record = join(firstName, lastName, phone, email, company, jobTitle);
        String ids = preferences.getString(IDS_KEY, "");
        preferences.edit()
                .putString(id, record)
                .putString(IDS_KEY, ids.isEmpty() ? id : ids + SEPARATOR + id)
                .apply();
    }

    @NonNull
    static List<Contact> getAll(@NonNull Context context) {
        SharedPreferences preferences = getPreferences(context);
        String ids = preferences.getString(IDS_KEY, "");
        List<Contact> contacts = new ArrayList<>();
        if (ids.isEmpty()) {
            return contacts;
        }

        for (String id : ids.split(SEPARATOR)) {
            String record = preferences.getString(id, null);
            if (record == null) {
                continue;
            }
            String[] fields = record.split(SEPARATOR, -1);
            if (fields.length == 6) {
                contacts.add(new Contact(
                        id,
                        fields[0],
                        fields[1],
                        fields[2],
                        fields[3],
                        fields[4],
                        fields[5]
                ));
            }
        }
        return contacts;
    }

    static Contact getById(@NonNull Context context, @NonNull String contactId) {
        for (Contact contact : getAll(context)) {
            if (contact.id.equals(contactId)) {
                return contact;
            }
        }
        return null;
    }

    static void update(
            @NonNull Context context,
            @NonNull String contactId,
            @NonNull String firstName,
            @NonNull String lastName,
            @NonNull String phone,
            @NonNull String email,
            @NonNull String company,
            @NonNull String jobTitle
    ) {
        getPreferences(context).edit()
                .putString(contactId, join(
                        firstName, lastName, phone, email, company, jobTitle
                ))
                .apply();
    }

    static void delete(@NonNull Context context, @NonNull String contactId) {
        SharedPreferences preferences = getPreferences(context);
        String ids = preferences.getString(IDS_KEY, "");
        List<String> remainingIds = new ArrayList<>();
        if (!ids.isEmpty()) {
            for (String id : ids.split(SEPARATOR)) {
                if (!id.equals(contactId)) {
                    remainingIds.add(id);
                }
            }
        }
        preferences.edit()
                .remove(contactId)
                .putString(IDS_KEY, join(remainingIds.toArray(new String[0])))
                .apply();
    }

    private static String join(String... fields) {
        StringBuilder value = new StringBuilder();
        for (String field : fields) {
            if (value.length() > 0) {
                value.append(SEPARATOR);
            }
            value.append(field);
        }
        return value.toString();
    }

    private static SharedPreferences getPreferences(@NonNull Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    static final class Contact {
        final String id;
        final String firstName;
        final String lastName;
        final String phone;
        final String email;
        final String company;
        final String jobTitle;

        Contact(
                String id,
                String firstName,
                String lastName,
                String phone,
                String email,
                String company,
                String jobTitle
        ) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.email = email;
            this.company = company;
            this.jobTitle = jobTitle;
        }

        String getFullName() {
            return (firstName + " " + lastName).trim();
        }

        String getDetail() {
            if (!jobTitle.isEmpty() && !company.isEmpty()) {
                return jobTitle + " • " + company;
            }
            return !company.isEmpty() ? company : jobTitle;
        }
    }
}
