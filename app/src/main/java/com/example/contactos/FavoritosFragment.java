package com.example.contactos;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import java.util.Locale;

public class FavoritosFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_favoritos, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_ir_a_contactos).setOnClickListener(clickedView ->
                ((ViewPager2) requireActivity().findViewById(R.id.viewpager))
                        .setCurrentItem(0, true)
        );
        renderFavorites(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            renderFavorites(getView());
        }
    }
    public void filterByQuery(String query) {

        currentQuery = query == null
                ? ""
                : query.trim().toLowerCase(Locale.ROOT);

        if (getView() != null) {
            renderFavorites(getView());
        }
    }

    private void renderFavorites(@NonNull View root) {

        LinearLayout quickAccess =
                root.findViewById(R.id.layout_acceso_rapido);

        LinearLayout favoritesLayout =
                root.findViewById(R.id.layout_favoritos);

        quickAccess.removeAllViews();
        favoritesLayout.removeAllViews();


        SharedPreferences preferences = getFavorites();


        for (StaticDatabase.Contact contact : StaticDatabase.CONTACTS) {

            boolean isFavorite = preferences.getBoolean(
                    FAVORITE_PREFIX + contact.id,
                    isDefaultFavorite(contact.id)
            );

            if (isFavorite && matchesContact(contact)) {

                quickAccess.addView(
                        createQuickAccessCard(contact)
                );

                favoritesLayout.addView(
                        createFavoriteCard(
                                contact,
                                favoritesLayout
                        )
                );
            }
        }


        for (ContactStorage.Contact savedContact :
                ContactStorage.getAll(requireContext())) {

            boolean isFavorite = preferences.getBoolean(
                    FAVORITE_PREFIX + savedContact.id,
                    false
            );

            if (isFavorite) {

                StaticDatabase.Contact contact =
                        new StaticDatabase.Contact(
                                savedContact.id,
                                savedContact.getFullName(),
                                savedContact.jobTitle,
                                savedContact.company,
                                savedContact.phone,
                                R.drawable.bg_avatar_purple
                        );

                if (!matchesContact(contact)) {
                    continue;
                }

                favoritesLayout.addView(
                        createFavoriteCard(
                                contact,
                                favoritesLayout
                        )
                );
            }
        }


        if (favoritesLayout.getChildCount() == 0) {

            TextView emptyView =
                    createDetailView("No se encontraron favoritos");

            emptyView.setGravity(Gravity.CENTER);

            emptyView.setPadding(
                    0,
                    dp(40),
                    0,
                    dp(40)
            );

            favoritesLayout.addView(emptyView);
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

    private View createQuickAccessCard(@NonNull StaticDatabase.Contact contact) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER_HORIZONTAL);
        card.setPadding(dp(12), dp(12), dp(12), dp(4));
        card.setBackgroundResource(R.drawable.bg_contact_card);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                dp(126),
                dp(148)
        );
        cardParams.setMargins(0, 0, dp(8), 0);
        card.setLayoutParams(cardParams);

        card.addView(createInitialsView(contact, 56));
        TextView quickAccessName = createNameView(contact.name, 13);
        quickAccessName.setGravity(Gravity.CENTER);
        card.addView(quickAccessName);
        card.addView(createIconButton(
                R.drawable.ic_phone,
                "Llamar a " + contact.name
        ));
        return card;
    }

    private View createFavoriteCard(
            @NonNull StaticDatabase.Contact contact,
            @NonNull ViewGroup parent
    ) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(10), dp(10), dp(10), dp(10));
        card.setBackgroundResource(R.drawable.bg_contact_card);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(82)
        );
        cardParams.setMargins(0, 0, 0, dp(8));
        card.setLayoutParams(cardParams);

        card.addView(createInitialsView(contact, 48));

        LinearLayout details = new LinearLayout(requireContext());
        details.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams detailsParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );
        detailsParams.setMargins(dp(10), 0, 0, 0);
        details.setLayoutParams(detailsParams);
        details.addView(createNameView(contact.name, 16));
        details.addView(createDetailView(contact.jobTitle + " • " + contact.company));
        details.addView(createDetailView(contact.phone));
        card.addView(details);

        ImageButton favoriteButton = createIconButton(
                R.drawable.ic_tab_favorite_filled,
                "Quitar a " + contact.name + " de favoritos"
        );
        favoriteButton.setOnClickListener(clickedView -> {
            getFavorites().edit()
                    .putBoolean(FAVORITE_PREFIX + contact.id, false)
                    .apply();
            renderFavorites(requireView());
        });
        card.addView(favoriteButton);
        card.addView(createIconButton(
                R.drawable.ic_phone,
                "Llamar a " + contact.name
        ));
        return card;
    }

    private TextView createInitialsView(
            @NonNull StaticDatabase.Contact contact,
            int size
    ) {
        TextView initials = new TextView(requireContext());
        initials.setLayoutParams(new LinearLayout.LayoutParams(dp(size), dp(size)));
        initials.setGravity(Gravity.CENTER);
        initials.setText(getInitials(contact.name));
        initials.setTextColor(Color.WHITE);
        initials.setTextSize(14);
        initials.setTypeface(null, android.graphics.Typeface.BOLD);
        initials.setBackgroundResource(contact.avatarBackground);
        return initials;
    }

    private TextView createNameView(@NonNull String name, int textSize) {
        TextView nameView = new TextView(requireContext());
        nameView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        nameView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        nameView.setText(name);
        nameView.setTextColor(Color.rgb(32, 33, 36));
        nameView.setTextSize(textSize);
        nameView.setTypeface(null, android.graphics.Typeface.BOLD);
        return nameView;
    }

    private TextView createDetailView(@NonNull String detail) {
        TextView detailView = new TextView(requireContext());
        detailView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        detailView.setText(detail);
        detailView.setTextColor(Color.rgb(119, 119, 119));
        detailView.setTextSize(12);
        return detailView;
    }

    private ImageButton createIconButton(int icon, @NonNull String description) {
        ImageButton button = new ImageButton(requireContext());
        button.setLayoutParams(new LinearLayout.LayoutParams(dp(48), dp(48)));
        button.setBackgroundResource(R.drawable.bg_action_circle_small);
        button.setImageResource(icon);
        button.setPadding(dp(12), dp(12), dp(12), dp(12));
        button.setContentDescription(description);
        return button;
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

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private String getInitials(@NonNull String name) {
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1))
                .toUpperCase();
    }
}
