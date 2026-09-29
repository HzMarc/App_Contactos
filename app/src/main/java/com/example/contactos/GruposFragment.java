package com.example.contactos;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class GruposFragment extends Fragment {

    private static final int[] GROUP_COLORS = {
            Color.rgb(37, 18, 201),
            Color.rgb(7, 153, 138),
            Color.rgb(91, 75, 180),
            Color.rgb(216, 119, 47)
    };

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_grupos, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_crear_grupo)
                .setOnClickListener(clickedView -> showCreateGroupDialog(view));
        renderGroups(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            renderGroups(getView());
        }
    }

    private void renderGroups(@NonNull View root) {
        LinearLayout groupsLayout = root.findViewById(R.id.layout_grupos);
        groupsLayout.removeAllViews();

        List<GroupStorage.Group> groups = GroupStorage.getAll(requireContext());
        for (int index = 0; index < groups.size(); index++) {
            groupsLayout.addView(createGroupCard(groups.get(index), index));
        }
    }

    @NonNull
    private View createGroupCard(
            @NonNull GroupStorage.Group group,
            int index
    ) {
        LinearLayout card = new LinearLayout(requireContext());
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(120)
        );
        cardParams.setMargins(0, 0, 0, dp(14));
        card.setLayoutParams(cardParams);
        card.setBackgroundResource(R.drawable.bg_contact_card);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setPadding(dp(17), dp(17), dp(10), dp(17));

        TextView avatar = new TextView(requireContext());
        avatar.setLayoutParams(new LinearLayout.LayoutParams(dp(72), dp(72)));
        GradientDrawable avatarBackground = new GradientDrawable();
        avatarBackground.setColor(GROUP_COLORS[index % GROUP_COLORS.length]);
        avatarBackground.setShape(GradientDrawable.OVAL);
        avatar.setBackground(avatarBackground);
        avatar.setGravity(Gravity.CENTER);
        avatar.setText(group.name.substring(0, 1).toUpperCase());
        avatar.setTextColor(Color.WHITE);
        avatar.setTextSize(27);
        avatar.setTypeface(null, Typeface.BOLD);
        card.addView(avatar);

        LinearLayout details = new LinearLayout(requireContext());
        LinearLayout.LayoutParams detailsParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );
        detailsParams.setMargins(dp(17), 0, 0, 0);
        details.setLayoutParams(detailsParams);
        details.setOrientation(LinearLayout.VERTICAL);
        details.addView(createText(group.name, 20, true, Color.rgb(32, 32, 39)));

        String count = group.contactCount + " contactos";
        details.addView(createText(count, 14, false, Color.rgb(119, 119, 128)));
        details.addView(createText(group.description, 12, false, Color.rgb(119, 119, 128)));
        card.addView(details);

        TextView arrow = createText("›", 35, false, Color.rgb(85, 85, 94));
        arrow.setLayoutParams(new LinearLayout.LayoutParams(dp(40), dp(86)));
        arrow.setGravity(Gravity.CENTER);
        card.addView(arrow);

        card.setOnClickListener(clickedView -> showGroupDetails(group));
        return card;
    }

    @NonNull
    private TextView createText(
            @NonNull String text,
            int size,
            boolean bold,
            int color
    ) {
        TextView textView = new TextView(requireContext());
        textView.setText(text);
        textView.setTextColor(color);
        textView.setTextSize(size);
        textView.setTypeface(null, bold ? Typeface.BOLD : Typeface.NORMAL);
        return textView;
    }

    private void showCreateGroupDialog(@NonNull View root) {
        LinearLayout content = new LinearLayout(requireContext());
        content.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(20);
        content.setPadding(padding, 0, padding, 0);

        EditText nameInput = new EditText(requireContext());
        nameInput.setHint("Nombre del grupo");
        nameInput.setSingleLine(true);
        content.addView(nameInput);

        EditText descriptionInput = new EditText(requireContext());
        descriptionInput.setHint("Descripción (opcional)");
        descriptionInput.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        content.addView(descriptionInput);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Crear nuevo grupo")
                .setView(content)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Crear", null)
                .create();

        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(clickedView -> {
                    String name = nameInput.getText().toString().trim();
                    String description = descriptionInput.getText().toString().trim();
                    if (name.isEmpty()) {
                        nameInput.setError("Escribe un nombre");
                        return;
                    }

                    GroupStorage.create(requireContext(), name, description);
                    dialog.dismiss();
                    renderGroups(root);
                    Toast.makeText(
                            requireContext(),
                            "Grupo creado",
                            Toast.LENGTH_SHORT
                    ).show();
                }));
        dialog.show();
    }

    private void showGroupDetails(@NonNull GroupStorage.Group group) {
        String message = group.contactCount > 0
                ? group.description
                : "Este grupo todavía no tiene contactos asignados.";
        new AlertDialog.Builder(requireContext())
                .setTitle(group.name)
                .setMessage(message)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
