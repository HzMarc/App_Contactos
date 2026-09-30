package com.example.contactos;

import android.os.Bundle;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DetalleContactoActivity extends AppCompatActivity {

    public static final String EXTRA_CONTACT_ID = "contact_id";
    public static final String EXTRA_PROFILE = "profile";

    private String phone = "";
    private String email = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_contacto);

        boolean isProfile = getIntent().getBooleanExtra(EXTRA_PROFILE, false);
        String contactId = getIntent().getStringExtra(EXTRA_CONTACT_ID);

        TextView avatar = findViewById(R.id.detail_avatar);
        TextView name = findViewById(R.id.detail_name);
        TextView subtitle = findViewById(R.id.detail_subtitle);
        TextView phoneView = findViewById(R.id.detail_phone);
        TextView emailView = findViewById(R.id.detail_email);
        TextView companyView = findViewById(R.id.detail_company);
        TextView statusView = findViewById(R.id.detail_status);
        View editButton = findViewById(R.id.btn_detalle_editar);
        View blockButton = findViewById(R.id.btn_detalle_bloquear);
        View deleteButton = findViewById(R.id.btn_detalle_eliminar);

        String fullName;
        String detail;
        int avatarBackground;
        ContactStorage.Contact savedContactForEdit = null;

        if (isProfile) {
            fullName = getString(R.string.contacto_usuario_nombre);
            detail = "Mi perfil";
            phone = "";
            email = "";
            avatarBackground = R.drawable.bg_avatar_purple;
            statusView.setText("● Mi perfil");
        } else {
            StaticDatabase.Contact staticContact =
                    contactId == null ? null : StaticDatabase.findContact(contactId);
            ContactStorage.Contact savedContact =
                    contactId == null ? null : ContactStorage.getById(this, contactId);

            if (staticContact == null && savedContact == null) {
                finish();
                return;
            }

            if (staticContact != null) {
                staticContact = StaticDatabase.resolve(this, staticContact);
                fullName = staticContact.name;
                detail = staticContact.jobTitle + " • " + staticContact.company;
                phone = staticContact.phone;
                avatarBackground = staticContact.avatarBackground;
            } else {
                savedContactForEdit = savedContact;
                fullName = savedContact.getFullName();
                detail = savedContact.getDetail();
                phone = savedContact.phone;
                email = savedContact.email;
                avatarBackground = R.drawable.bg_avatar_purple;
            }
        }

        avatar.setText(initials(fullName));
        avatar.setBackgroundResource(avatarBackground);
        name.setText(fullName);
        subtitle.setText(detail);
        phoneView.setText(phone.isEmpty() ? "No registrado" : phone);
        emailView.setText(email.isEmpty() ? "No registrado" : email);
        companyView.setText(detail.isEmpty() ? "No registrada" : detail);
        if (!isProfile) {
            boolean blocked = getPreferences(Context.MODE_PRIVATE)
                    .getBoolean("blocked_" + contactId, false)
                    || StaticDatabase.isBlocked(this, contactId);
            statusView.setText(blocked ? "● Bloqueado" : "● Disponible");
            statusView.setTextColor(blocked ? Color.rgb(179, 38, 30) : Color.rgb(53, 37, 205));

            String finalContactId = contactId;
            ContactStorage.Contact finalSavedContact = savedContactForEdit;
            blockButton.setOnClickListener(v -> {
                boolean nextBlocked = !getPreferences(Context.MODE_PRIVATE)
                        .getBoolean("blocked_" + finalContactId, false);
                getPreferences(Context.MODE_PRIVATE).edit()
                        .putBoolean("blocked_" + finalContactId, nextBlocked)
                        .apply();
                StaticDatabase.setBlocked(this, finalContactId, nextBlocked);
                statusView.setText(nextBlocked ? "● Bloqueado" : "● Disponible");
                statusView.setTextColor(nextBlocked
                        ? Color.rgb(179, 38, 30)
                        : Color.rgb(53, 37, 205));
                Toast.makeText(
                        this,
                        nextBlocked ? "Contacto bloqueado" : "Contacto desbloqueado",
                        Toast.LENGTH_SHORT
                ).show();
            });
            deleteButton.setOnClickListener(v -> {
                if (finalSavedContact != null) {
                    ContactStorage.delete(this, finalContactId);
                } else {
                    StaticDatabase.setDeleted(this, finalContactId);
                }
                Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                finish();
            });
            editButton.setOnClickListener(v -> {
                if (finalSavedContact != null) {
                    showEditDialog(finalSavedContact);
                } else {
                    StaticDatabase.Contact staticData =
                            StaticDatabase.findContact(finalContactId);
                    if (staticData != null) {
                        showStaticEditDialog(StaticDatabase.resolve(this, staticData));
                    }
                }
            });
        }

        findViewById(R.id.btn_detalle_atras).setOnClickListener(v -> finish());
    }

    private void showEditDialog(@NonNull ContactStorage.Contact contact) {
        LinearLayout fields = new LinearLayout(this);
        fields.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (20 * getResources().getDisplayMetrics().density + 0.5f);
        fields.setPadding(padding, 0, padding, 0);

        EditText firstName = editField(
                "Nombre",
                contact.firstName,
                android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS
        );
        EditText lastName = editField(
                "Apellido",
                contact.lastName,
                android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS
        );
        EditText phoneField = editField(
                "Número de teléfono",
                contact.phone,
                android.text.InputType.TYPE_CLASS_PHONE
        );
        EditText emailField = editField(
                "Correo electrónico",
                contact.email,
                android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );
        EditText companyField = editField(
                "Empresa",
                contact.company,
                android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        );
        EditText jobTitleField = editField(
                "Cargo",
                contact.jobTitle,
                android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        );
        fields.addView(firstName);
        fields.addView(lastName);
        fields.addView(phoneField);
        fields.addView(emailField);
        fields.addView(companyField);
        fields.addView(jobTitleField);

        new AlertDialog.Builder(this)
                .setTitle("Editar contacto")
                .setView(fields)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    ContactStorage.update(
                            this,
                            contact.id,
                            firstName.getText().toString().trim(),
                            lastName.getText().toString().trim(),
                            phoneField.getText().toString().trim(),
                            emailField.getText().toString().trim(),
                            companyField.getText().toString().trim(),
                            jobTitleField.getText().toString().trim()
                    );
                    Toast.makeText(this, "Contacto actualizado", Toast.LENGTH_SHORT).show();
                    recreate();
                })
                .show();
    }

    private void showStaticEditDialog(@NonNull StaticDatabase.Contact contact) {
        LinearLayout fields = new LinearLayout(this);
        fields.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (20 * getResources().getDisplayMetrics().density + 0.5f);
        fields.setPadding(padding, 0, padding, 0);
        EditText name = editField(
                "Nombre",
                contact.name,
                android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS
        );
        EditText job = editField(
                "Cargo",
                contact.jobTitle,
                android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        );
        EditText company = editField(
                "Empresa",
                contact.company,
                android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        );
        EditText phoneField = editField(
                "Número de teléfono",
                contact.phone,
                android.text.InputType.TYPE_CLASS_PHONE
        );
        fields.addView(name);
        fields.addView(job);
        fields.addView(company);
        fields.addView(phoneField);
        new AlertDialog.Builder(this)
                .setTitle("Editar contacto")
                .setView(fields)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    StaticDatabase.update(
                            this,
                            contact.id,
                            name.getText().toString().trim(),
                            job.getText().toString().trim(),
                            company.getText().toString().trim(),
                            phoneField.getText().toString().trim()
                    );
                    Toast.makeText(this, "Contacto actualizado", Toast.LENGTH_SHORT).show();
                    recreate();
                })
                .show();
    }

    @NonNull
    private EditText editField(String hint, String value, int inputType) {
        EditText field = new EditText(this);
        field.setHint(hint);
        field.setText(value);
        field.setInputType(inputType);
        field.setSingleLine(true);
        field.setPadding(15, 0, 15, 0);
        field.setTextColor(Color.BLACK);
        field.setHintTextColor(Color.rgb(111, 105, 139));
        field.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(Color.rgb(53, 37, 205))
        );
        field.setGravity(Gravity.CENTER_VERTICAL);
        return field;
    }

    private String initials(String name) {
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1))
                .toUpperCase();
    }
}
