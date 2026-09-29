package com.example.contactos;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

final class GroupStorage {

    private static final String PREFERENCES = "contact_groups";
    private static final String IDS_KEY = "ids";
    private static final String SEPARATOR = "\u001f";

    private GroupStorage() {
    }

    @NonNull
    static List<Group> getAll(@NonNull Context context) {
        List<Group> groups = new ArrayList<>();
        for (StaticDatabase.Group group : StaticDatabase.GROUPS) {
            groups.add(new Group(
                    group.id,
                    group.name,
                    group.contactCount,
                    group.description
            ));
        }

        SharedPreferences preferences = getPreferences(context);
        String ids = preferences.getString(IDS_KEY, "");
        if (!ids.isEmpty()) {
            for (String id : ids.split(SEPARATOR)) {
                String record = preferences.getString(id, null);
                if (record == null) {
                    continue;
                }
                String[] fields = record.split(SEPARATOR, -1);
                if (fields.length == 3) {
                    groups.add(new Group(
                            id,
                            fields[0],
                            0,
                            fields[1]
                    ));
                }
            }
        }
        return groups;
    }

    static void create(
            @NonNull Context context,
            @NonNull String name,
            @NonNull String description
    ) {
        SharedPreferences preferences = getPreferences(context);
        String id = "group_" + System.currentTimeMillis();
        String ids = preferences.getString(IDS_KEY, "");
        String record = name + SEPARATOR + description + SEPARATOR + "0";
        preferences.edit()
                .putString(id, record)
                .putString(IDS_KEY, ids.isEmpty() ? id : ids + SEPARATOR + id)
                .apply();
    }

    private static SharedPreferences getPreferences(@NonNull Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    static final class Group {
        final String id;
        final String name;
        final int contactCount;
        final String description;

        Group(String id, String name, int contactCount, String description) {
            this.id = id;
            this.name = name;
            this.contactCount = contactCount;
            this.description = description;
        }
    }
}
