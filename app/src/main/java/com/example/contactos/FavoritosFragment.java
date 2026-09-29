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

public class FavoritosFragment extends Fragment {

    private static final String FAVORITES_PREFERENCES = "contact_favorites";
    private static final String FAVORITE_PREFIX = "favorite_";

    private final ContactData[] contacts = {
            new ContactData(
                    "alejandro_ramos", "AR", "Alejandro Ramos",
                    "DevOps • Mercado Libre", "+56 9 7321 0092",
                    R.drawable.bg_avatar_blue
            ),
            new ContactData(
                    "ana_lucia_prado", "AL", "Ana Lucía Prado",
                    "Product Lead • NotCo", "+56 9 9124 5531",
                    R.drawable.bg_avatar_purple
            ),
            new ContactData(
                    "beatriz_silva", "BS", "Beatriz Silva",
                    "Finanzas • Banco Santander", "+56 9 6554 2189",
                    R.drawable.bg_avatar_green
            ),
            new ContactData(
                    "carlos_mendoza", "CM", "Carlos Mendoza",
                    "Diseñador UX • TechStudio", "+56 9 5543 8812",
                    R.drawable.bg_avatar_blue
            ),
            new ContactData(
                    "diego_herrera", "DH", "Diego Herrera",
                    "Fotografía Comercial", "+56 9 4432 9988",
                    R.drawable.bg_avatar_blue
            )
    };

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

    private void renderFavorites(@NonNull View root) {
        LinearLayout quickAccess = root.findViewById(R.id.layout_acceso_rapido);
        LinearLayout favoritesLayout = root.findViewById(R.id.layout_favoritos);
        quickAccess.removeAllViews();
        favoritesLayout.removeAllViews();

        SharedPreferences preferences = getFavorites();
        for (ContactData contact : contacts) {
            boolean isFavorite = preferences.getBoolean(
                    FAVORITE_PREFIX + contact.id,
                    isDefaultFavorite(contact.id)
            );
            if (isFavorite) {
                quickAccess.addView(createQuickAccessCard(contact));
                favoritesLayout.addView(createFavoriteCard(contact, favoritesLayout));
            }
        }
    }

    private View createQuickAccessCard(@NonNull ContactData contact) {
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
            @NonNull ContactData contact,
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
        details.addView(createDetailView(contact.detail));
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
            @NonNull ContactData contact,
            int size
    ) {
        TextView initials = new TextView(requireContext());
        initials.setLayoutParams(new LinearLayout.LayoutParams(dp(size), dp(size)));
        initials.setGravity(Gravity.CENTER);
        initials.setText(contact.initials);
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
        return "beatriz_silva".equals(contactId)
                || "carlos_mendoza".equals(contactId);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private static class ContactData {
        private final String id;
        private final String initials;
        private final String name;
        private final String detail;
        private final String phone;
        private final int avatarBackground;

        private ContactData(
                String id,
                String initials,
                String name,
                String detail,
                String phone,
                int avatarBackground
        ) {
            this.id = id;
            this.initials = initials;
            this.name = name;
            this.detail = detail;
            this.phone = phone;
            this.avatarBackground = avatarBackground;
        }
    }
}
