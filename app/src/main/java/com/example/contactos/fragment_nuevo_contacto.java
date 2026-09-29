package com.example.contactos;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class fragment_nuevo_contacto extends Fragment {

    public fragment_nuevo_contacto() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_nuevo_contacto, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        Button saveButton = view.findViewById(R.id.btnGuardar);
        saveButton.setOnClickListener(clickedView -> saveContact(view));
    }

    private void saveContact(@NonNull View root) {
        EditText firstName = root.findViewById(R.id.edtNombre);
        EditText lastName = root.findViewById(R.id.edtApellido);
        EditText phone = root.findViewById(R.id.edtTelefono);
        EditText email = root.findViewById(R.id.edtCorreo);
        EditText company = root.findViewById(R.id.edtEmpresa);
        EditText jobTitle = root.findViewById(R.id.edtCargo);

        String firstNameValue = firstName.getText().toString().trim();
        String lastNameValue = lastName.getText().toString().trim();
        String phoneValue = phone.getText().toString().trim();

        if (TextUtils.isEmpty(firstNameValue)
                || TextUtils.isEmpty(lastNameValue)
                || TextUtils.isEmpty(phoneValue)) {
            Toast.makeText(
                    requireContext(),
                    "Completa nombre, apellido y teléfono",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        ContactStorage.save(
                requireContext(),
                firstNameValue,
                lastNameValue,
                phoneValue,
                email.getText().toString().trim(),
                company.getText().toString().trim(),
                jobTitle.getText().toString().trim()
        );

        Toast.makeText(
                requireContext(),
                "Contacto guardado",
                Toast.LENGTH_SHORT
        ).show();
        clearFields(firstName, lastName, phone, email, company, jobTitle);

        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).showContacts();
        }
    }

    private void clearFields(EditText... fields) {
        for (EditText field : fields) {
            field.setText("");
        }
    }
}
