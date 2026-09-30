package com.example.contactos;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

final class StaticDatabase {

    static final Contact[] CONTACTS = {
            new Contact("alejandro_ramos", "Alejandro Ramos", "DevOps", "Mercado Libre", "+56 9 7321 0092", R.drawable.bg_avatar_blue),
            new Contact("ana_lucia_prado", "Ana Lucía Prado", "Product Lead", "NotCo", "+56 9 9124 5531", R.drawable.bg_avatar_purple),
            new Contact("beatriz_silva", "Beatriz Silva", "Finanzas", "Banco Santander", "+56 9 6554 2189", R.drawable.bg_avatar_green),
            new Contact("carlos_mendoza", "Carlos Mendoza", "Diseñador UX", "TechStudio", "+56 9 5543 8812", R.drawable.bg_avatar_blue),
            new Contact("diego_herrera", "Diego Herrera", "Fotografía", "Comercial", "+56 9 4432 9988", R.drawable.bg_avatar_blue),
            new Contact("elena_castro", "Elena Castro", "Arquitecta", "Estudio Norte", "+56 9 6312 4470", R.drawable.bg_avatar_purple),
            new Contact("fabian_rojas", "Fabián Rojas", "Médico", "Clínica Central", "+56 9 7421 8801", R.drawable.bg_avatar_green),
            new Contact("gabriela_torres", "Gabriela Torres", "Abogada", "Torres Asociados", "+56 9 8123 6654", R.drawable.bg_avatar_blue),
            new Contact("hugo_vargas", "Hugo Vargas", "Chef", "Sabor Urbano", "+56 9 5542 1900", R.drawable.bg_avatar_purple),
            new Contact("isabel_fuentes", "Isabel Fuentes", "Periodista", "Radio Uno", "+56 9 6234 7788", R.drawable.bg_avatar_green),
            new Contact("javier_navarro", "Javier Navarro", "Ingeniero", "Andes Tech", "+56 9 7012 3344", R.drawable.bg_avatar_blue),
            new Contact("karina_molina", "Karina Molina", "Diseñadora", "Molina Studio", "+56 9 8877 1200", R.drawable.bg_avatar_purple),
            new Contact("luis_paredes", "Luis Paredes", "Contador", "Paredes Consultores", "+56 9 6455 9012", R.drawable.bg_avatar_green),
            new Contact("mariana_soto", "Mariana Soto", "Psicóloga", "Consulta Vida", "+56 9 7788 2410", R.drawable.bg_avatar_blue),
            new Contact("nicolas_ortiz", "Nicolás Ortiz", "Profesor", "Universidad del Sur", "+56 9 6112 4300", R.drawable.bg_avatar_purple),
            new Contact("olivia_mendez", "Olivia Méndez", "Productora", "Media Lab", "+56 9 8300 5566", R.drawable.bg_avatar_green),
            new Contact("pablo_rios", "Pablo Ríos", "Fotógrafo", "Ríos Visual", "+56 9 7123 8899", R.drawable.bg_avatar_blue),
            new Contact("quique_salas", "Quique Salas", "Músico", "Sala Sonora", "+56 9 6444 2233", R.drawable.bg_avatar_purple),
            new Contact("rocio_vega", "Rocío Vega", "Veterinaria", "Mascotas Feliz", "+56 9 7555 1177", R.drawable.bg_avatar_green),
            new Contact("sergio_leon", "Sergio León", "Analista", "Data Chile", "+56 9 6888 3322", R.drawable.bg_avatar_blue),
            new Contact("tamara_pinto", "Tamara Pinto", "Ilustradora", "Pinto Arte", "+56 9 7999 4411", R.drawable.bg_avatar_purple),
            new Contact("ulises_carrasco", "Ulises Carrasco", "Abogado", "Carrasco Legal", "+56 9 6555 7788", R.drawable.bg_avatar_green),
            new Contact("valentina_reyes", "Valentina Reyes", "Enfermera", "Salud Norte", "+56 9 8222 6633", R.drawable.bg_avatar_blue),
            new Contact("walter_silva", "Walter Silva", "Técnico", "Soluciones WS", "+56 9 7333 2200", R.drawable.bg_avatar_purple),
            new Contact("ximena_araya", "Ximena Araya", "Bióloga", "EcoLab", "+56 9 8666 5544", R.drawable.bg_avatar_green),
            new Contact("yolanda_paz", "Yolanda Paz", "Emprendedora", "Paz Natural", "+56 9 6777 9900", R.drawable.bg_avatar_blue),
            new Contact("zulema_mora", "Zulema Mora", "Consultora", "Mora & Cía.", "+56 9 8444 7711", R.drawable.bg_avatar_purple)
    };

    static final String[] DEFAULT_FAVORITE_IDS = {
            "alejandro_ramos",
            "ana_lucia_prado",
            "beatriz_silva",
            "carlos_mendoza"
    };

    static final Group[] GROUPS = {
            new Group("family", "Familia", 8, "Mamá, Papá, Carlos y 5 más"),
            new Group("work", "Trabajo", 12, "Beatriz, Alejandro, Ana y 9 más"),
            new Group("university", "Universidad", 18, "Compañeros de clase")
    };



    static final RecentCall[] RECENT_CALLS = {
            new RecentCall("Alejandro Ramos", "AR", "Llamada saliente", "10:42 a. m.", "", false, R.drawable.bg_avatar_blue),
            new RecentCall("Beatriz Silva", "BS", "Llamada recibida", "09:15 a. m.", "4 min", false, R.drawable.bg_avatar_green),
            new RecentCall("Carlos Mendoza", "CM", "Llamada perdida", "08:31 a. m.", "", true, R.drawable.bg_avatar_blue),
            new RecentCall("Ana Lucía Prado", "AL", "Llamada saliente", "06:24 p. m.", "7 min", false, R.drawable.bg_avatar_purple)
    };

    private StaticDatabase() {
    }

    static Contact findContact(@NonNull String contactId) {
        for (Contact contact : CONTACTS) {
            if (contact.id.equals(contactId)) {
                return contact;
            }
        }
        return null;
    }

    static boolean isDeleted(@NonNull Context context, @NonNull String contactId) {
        return getOverrides(context).getBoolean("deleted_" + contactId, false);
    }

    static boolean isBlocked(@NonNull Context context, @NonNull String contactId) {
        return getOverrides(context).getBoolean("blocked_" + contactId, false);
    }

    static void setBlocked(
            @NonNull Context context,
            @NonNull String contactId,
            boolean blocked
    ) {
        getOverrides(context).edit()
                .putBoolean("blocked_" + contactId, blocked)
                .apply();
    }

    static void setDeleted(@NonNull Context context, @NonNull String contactId) {
        getOverrides(context).edit()
                .putBoolean("deleted_" + contactId, true)
                .apply();
    }

    static void update(
            @NonNull Context context,
            @NonNull String contactId,
            @NonNull String name,
            @NonNull String jobTitle,
            @NonNull String company,
            @NonNull String phone
    ) {
        getOverrides(context).edit()
                .putString("name_" + contactId, name)
                .putString("job_" + contactId, jobTitle)
                .putString("company_" + contactId, company)
                .putString("phone_" + contactId, phone)
                .apply();
    }

    @NonNull
    static Contact resolve(@NonNull Context context, @NonNull Contact contact) {
        SharedPreferences preferences = getOverrides(context);
        return new Contact(
                contact.id,
                preferences.getString("name_" + contact.id, contact.name),
                preferences.getString("job_" + contact.id, contact.jobTitle),
                preferences.getString("company_" + contact.id, contact.company),
                preferences.getString("phone_" + contact.id, contact.phone),
                contact.avatarBackground
        );
    }

    private static SharedPreferences getOverrides(@NonNull Context context) {
        return context.getSharedPreferences("contact_overrides", Context.MODE_PRIVATE);
    }

    static final class Contact {
        final String id;
        final String name;
        final String jobTitle;
        final String company;
        final String phone;
        final int avatarBackground;

        Contact(String id, String name, String jobTitle, String company, String phone, int avatarBackground) {
            this.id = id;
            this.name = name;
            this.jobTitle = jobTitle;
            this.company = company;
            this.phone = phone;
            this.avatarBackground = avatarBackground;
        }
    }

    static final class RecentCall {
        final String name;
        final String initials;
        final String type;
        final String time;
        final String duration;
        final boolean missed;
        final int avatarBackground;

        RecentCall(String name, String initials, String type, String time, String duration, boolean missed, int avatarBackground) {
            this.name = name;
            this.initials = initials;
            this.type = type;
            this.time = time;
            this.duration = duration;
            this.missed = missed;
            this.avatarBackground = avatarBackground;
        }
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
