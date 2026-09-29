package com.example.contactos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class ContactosFragment extends Fragment {

    private static final String FAVORITES_PREFERENCES = "contact_favorites";
    private static final String FAVORITE_PREFIX = "favorite_";

    public ContactosFragment() {
        // Constructor vacío requerido por Fragment.
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.fragment_contactos,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        List<ImageButton> favoriteButtons = new ArrayList<>();
        collectFavoriteButtons((ViewGroup) view, favoriteButtons);

        for (ImageButton button : favoriteButtons) {
            String contactId = (String) button.getTag();
            boolean defaultFavorite = isDefaultFavorite(contactId);
            updateFavoriteButton(
                    button,
                    getFavorites().getBoolean(FAVORITE_PREFIX + contactId, defaultFavorite)
            );

            button.setOnClickListener(clickedView -> {
                ImageButton favoriteButton = (ImageButton) clickedView;
                String clickedContactId = (String) favoriteButton.getTag();
                boolean isFavorite = !getFavorites().getBoolean(
                        FAVORITE_PREFIX + clickedContactId,
                        isDefaultFavorite(clickedContactId)
                );

                getFavorites()
                        .edit()
                        .putBoolean(FAVORITE_PREFIX + clickedContactId, isFavorite)
                        .apply();
                updateFavoriteButton(favoriteButton, isFavorite);
            });
        }
    }

    private void collectFavoriteButtons(
            @NonNull ViewGroup parent,
            @NonNull List<ImageButton> buttons
    ) {
        for (int index = 0; index < parent.getChildCount(); index++) {
            View child = parent.getChildAt(index);
            if (child instanceof ImageButton && child.getTag() instanceof String) {
                buttons.add((ImageButton) child);
            } else if (child instanceof ViewGroup) {
                collectFavoriteButtons((ViewGroup) child, buttons);
            }
        }
    }

    private android.content.SharedPreferences getFavorites() {
        return requireContext().getSharedPreferences(
                FAVORITES_PREFERENCES,
                android.content.Context.MODE_PRIVATE
        );
    }

    private boolean isDefaultFavorite(@NonNull String contactId) {
        return "beatriz_silva".equals(contactId)
                || "carlos_mendoza".equals(contactId);
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
}