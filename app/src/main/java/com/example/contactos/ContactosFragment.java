package com.example.contactos;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContactosFragment extends Fragment {

    private static final String FAVORITES_PREFERENCES = "contact_favorites";
    private static final String FAVORITE_PREFIX = "favorite_";
    private String currentQuery = "";

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_contactos, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        renderContacts(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            renderContacts(getView());
        }
    }

    private void renderContacts(@NonNull View root) {

        LinearLayout contactsLayout =
                root.findViewById(R.id.layout_lista_contactos);

        contactsLayout.removeAllViews();

        List<ContactStorage.Contact> savedContacts =
                ContactStorage.getAll(requireContext());


        for (char letter = 'A'; letter <= 'Z'; letter++) {

            String initial = String.valueOf(letter);

            boolean hasResults = false;


            for (StaticDatabase.Contact contact : StaticDatabase.CONTACTS) {

                if (contact.name.substring(0, 1)
                        .equalsIgnoreCase(initial)
                        && matchesContact(contact)) {

                    hasResults = true;
                    break;
                }
            }


            if (!hasResults) {

                for (ContactStorage.Contact contact : savedContacts) {

                    if (contact.getFullName()
                            .substring(0, 1)
                            .equalsIgnoreCase(initial)
                            && matchesSavedContact(contact)) {

                        hasResults = true;
                        break;
                    }
                }
            }


            if (!hasResults) {
                continue;
            }


            contactsLayout.addView(
                    createSectionTitle(initial)
            );


            for (StaticDatabase.Contact contact : StaticDatabase.CONTACTS) {

                if (contact.name
                        .substring(0, 1)
                        .equalsIgnoreCase(initial)
                        && matchesContact(contact)) {

                    contactsLayout.addView(
                            createContactCard(contact)
                    );
                }
            }


            for (ContactStorage.Contact contact : savedContacts) {

                if (contact.getFullName()
                        .substring(0, 1)
                        .equalsIgnoreCase(initial)
                        && matchesSavedContact(contact)) {

                    contactsLayout.addView(
                            createSavedContactCard(contact)
                    );
                }
            }
        }


        if (contactsLayout.getChildCount() == 0) {

            TextView empty = createContactText(
                    "No se encontraron contactos",
                    15,
                    true
            );

            empty.setGravity(Gravity.CENTER);
            empty.setPadding(
                    0,
                    dp(50),
                    0,
                    dp(50)
            );

            contactsLayout.addView(empty);
        }
    }

    @NonNull
    private TextView createSectionTitle(@NonNull String title) {
        TextView sectionTitle = new TextView(requireContext());
        sectionTitle.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(30)
        ));
        sectionTitle.setGravity(Gravity.CENTER_VERTICAL);
        sectionTitle.setText(title);
        sectionTitle.setTextColor(Color.rgb(75, 67, 168));
        sectionTitle.setTextSize(15);
        sectionTitle.setTypeface(null, Typeface.BOLD);
        return sectionTitle;
    }

    @NonNull
    private View createContactCard(@NonNull StaticDatabase.Contact contact) {
        LinearLayout card = new LinearLayout(requireContext());
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(82)
        );
        cardParams.setMargins(0, 0, 0, dp(6));
        card.setLayoutParams(cardParams);
        card.setBackgroundResource(R.drawable.bg_contact_card);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setPadding(dp(10), dp(10), dp(6), dp(10));

        TextView initials = new TextView(requireContext());
        initials.setLayoutParams(new LinearLayout.LayoutParams(dp(48), dp(48)));
        initials.setBackgroundResource(contact.avatarBackground);
        initials.setGravity(Gravity.CENTER);
        initials.setText(getInitials(contact.name));
        initials.setTextColor(Color.WHITE);
        initials.setTextSize(14);
        initials.setTypeface(null, Typeface.BOLD);
        card.addView(initials);

        LinearLayout details = new LinearLayout(requireContext());
        LinearLayout.LayoutParams detailsParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );
        detailsParams.setMargins(dp(10), 0, 0, 0);
        details.setLayoutParams(detailsParams);
        details.setOrientation(LinearLayout.VERTICAL);
        details.addView(createContactText(contact.name, 16, true));
        details.addView(createContactText(
                contact.jobTitle + " • " + contact.company,
                12,
                false
        ));
        details.addView(createContactText(contact.phone, 11, false));
        card.addView(details);

        card.addView(createFavoriteButton(contact.id, isDefaultFavorite(contact.id)));
        card.addView(createActionIcon(R.drawable.ic_phone, "Llamar a " + contact.name));
        card.addView(createActionIcon(R.drawable.ic_message, "Enviar mensaje a " + contact.name));
        return card;
    }

    @NonNull
    private ImageButton createFavoriteButton(
            @NonNull String contactId,
            boolean defaultFavorite
    ) {
        ImageButton button = new ImageButton(requireContext());
        button.setLayoutParams(new LinearLayout.LayoutParams(dp(44), dp(48)));
        button.setBackgroundResource(R.drawable.bg_action_circle_small);
        button.setPadding(dp(9), dp(9), dp(9), dp(9));
        boolean isFavorite = getFavorites().getBoolean(
                FAVORITE_PREFIX + contactId,
                defaultFavorite
        );
        updateFavoriteButton(button, isFavorite);
        button.setOnClickListener(clickedView -> {
            boolean nextValue = !getFavorites().getBoolean(
                    FAVORITE_PREFIX + contactId,
                    defaultFavorite
            );
            getFavorites().edit()
                    .putBoolean(FAVORITE_PREFIX + contactId, nextValue)
                    .apply();
            updateFavoriteButton(button, nextValue);
        });
        return button;
    }

    @NonNull
    private ImageView createActionIcon(int icon, @NonNull String description) {
        ImageView image = new ImageView(requireContext());
        image.setLayoutParams(new LinearLayout.LayoutParams(dp(38), dp(38)));
        image.setBackgroundResource(R.drawable.bg_action_circle);
        image.setContentDescription(description);
        image.setPadding(dp(9), dp(9), dp(9), dp(9));
        image.setImageResource(icon);
        return image;
    }

    @NonNull
    private View createSavedContactCard(@NonNull ContactStorage.Contact contact) {
        StaticDatabase.Contact data = new StaticDatabase.Contact(
                contact.id,
                contact.getFullName(),
                contact.jobTitle,
                contact.company,
                contact.phone,
                R.drawable.bg_avatar_purple
        );
        View card = createContactCard(data);
        card.setTag("saved_contact");
        return card;
    }

    @NonNull
    private TextView createContactText(
            @NonNull String text,
            int size,
            boolean bold
    ) {
        TextView textView = new TextView(requireContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        textView.setText(text);
        textView.setTextColor(Color.rgb(32, 33, 36));
        textView.setTextSize(size);
        textView.setTypeface(null, bold ? Typeface.BOLD : Typeface.NORMAL);
        return textView;
    }

    @NonNull
    private String getInitials(@NonNull String name) {
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase(Locale.ROOT);
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1))
                .toUpperCase(Locale.ROOT);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private SharedPreferences getFavorites() {
        return requireContext().getSharedPreferences(
                FAVORITES_PREFERENCES,
                Context.MODE_PRIVATE
        );
    }

    private boolean isDefaultFavorite(@NonNull String contactId) {
        for (String favoriteId : StaticDatabase.DEFAULT_FAVORITE_IDS) {
            if (favoriteId.equals(contactId)) {
                return true;
            }
        }
        return false;
    }

    private void updateFavoriteButton(
            @NonNull ImageButton button,
            boolean isFavorite
    ) {
        button.setImageResource(
                isFavorite
                        ? R.drawable.ic_tab_favorite_filled
                        : R.drawable.ic_tab_favorite_outline
        );
        button.setContentDescription(
                isFavorite
                        ? "Quitar de favoritos"
                        : "Agregar a favoritos"
        );
    }

    public void filterByQuery(String query) {

        currentQuery = query == null
                ? ""
                : query.trim().toLowerCase(Locale.ROOT);

        if (getView() != null) {
            renderContacts(getView());
        }
    }

    private boolean matchesContact(
            @NonNull StaticDatabase.Contact contact
    ) {

        if (currentQuery.isEmpty()) {
            return true;
        }

        String searchableText =
                contact.name + " "
                        + contact.jobTitle + " "
                        + contact.company + " "
                        + contact.phone;

        return searchableText
                .toLowerCase(Locale.ROOT)
                .contains(currentQuery);
    }

    private boolean matchesSavedContact(
            @NonNull ContactStorage.Contact contact
    ) {

        if (currentQuery.isEmpty()) {
            return true;
        }

        String searchableText =
                contact.getFullName() + " "
                        + contact.jobTitle + " "
                        + contact.company + " "
                        + contact.phone + " "
                        + contact.email;

        return searchableText
                .toLowerCase(Locale.ROOT)
                .contains(currentQuery);
    }

}
