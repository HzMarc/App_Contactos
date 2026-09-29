package com.example.contactos;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ContactosFragment();
            case 1:
                return new FavoritosFragment();
            case 2:
                return new RecientesFragment();
            case 3:
                return new GruposFragment();

            default:
                return new ContactosFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}