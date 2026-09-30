package com.example.contactos;

import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.widget.SearchView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private static final int TAB_CONTACTOS = 0;
    private static final int TAB_FAVORITOS = 1;
    private static final int TAB_NUEVO = 2;
    private static final int TAB_RECIENTES = 3;
    private static final int TAB_GRUPOS = 4;

    private ViewPager2 viewPager;
    private View topBar;
    private TabLayout tabLayout;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewpager);
        topBar = findViewById(R.id.top_bar);
        tabLayout = findViewById(R.id.tabLayout);
        searchView = findViewById(R.id.searchView);
        findViewById(R.id.imageView2).setOnClickListener(view -> {
            Intent intent = new Intent(this, DetalleContactoActivity.class);
            intent.putExtra(DetalleContactoActivity.EXTRA_PROFILE, true);
            startActivity(intent);
        });

        viewPager.setAdapter(new ViewPagerAdapter(this));
        configureTabs();
        configureNavigation();
        configureSearch();
        configureInsets();
    }

    private void configureNavigation() {
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                boolean isNewContactPage = position == TAB_NUEVO;
                topBar.setVisibility(isNewContactPage ? View.GONE : View.VISIBLE);

                if (!isNewContactPage) {
                    updateSearchHint(position);
                    viewPager.post(() ->
                            applySearch(searchView.getQuery().toString())
                    );
                }
            }
        });
    }

    private void configureTabs() {
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case TAB_CONTACTOS:
                    tab.setIcon(R.drawable.ic_tab_contacts_filled);
                    tab.setText("Contactos");
                    break;
                case TAB_FAVORITOS:
                    tab.setIcon(R.drawable.ic_tab_favorite_filled);
                    tab.setText("Favoritos");
                    break;
                case TAB_NUEVO:
                    tab.setIcon(R.drawable.ic_tab_add_filled);
                    tab.setText("Nuevo");
                    break;
                case TAB_RECIENTES:
                    tab.setIcon(R.drawable.ic_tab_history_filled);
                    tab.setText("Recientes");
                    break;
                case TAB_GRUPOS:
                    tab.setIcon(R.drawable.ic_tab_groups_filled);
                    tab.setText("Grupos");
                    break;
                default:
                    break;
            }
        }).attach();
    }

    public void showContacts() {
        viewPager.setCurrentItem(TAB_CONTACTOS, true);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == TAB_NUEVO) {
            viewPager.setCurrentItem(TAB_CONTACTOS, true);
            return;
        }
        super.onBackPressed();
    }

    private void configureInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (view, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );
    }

    private void configureSearch() {

        int searchTextId = getResources().getIdentifier(
                "search_src_text",
                "id",
                "android"
        );
        EditText searchText = searchView.findViewById(searchTextId);
        if (searchText != null) {
            searchText.setTextColor(Color.BLACK);
            searchText.setHintTextColor(Color.rgb(111, 105, 139));
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                applySearch(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applySearch(newText);
                return true;
            }
        });
    }

    private void applySearch(String query) {

        int pagePosition = viewPager.getCurrentItem();

        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag("f" + pagePosition);

        if (fragment instanceof ContactosFragment) {
            ((ContactosFragment) fragment).filterByQuery(query);
        }

        if (fragment instanceof FavoritosFragment) {
            ((FavoritosFragment) fragment).filterByQuery(query);
        }

        if (fragment instanceof RecientesFragment) {
            ((RecientesFragment) fragment).filterByQuery(query);
        }

        if (fragment instanceof GruposFragment) {
            ((GruposFragment) fragment).filterByQuery(query);
        }
    }

    private void updateSearchHint(int position) {

        switch (position) {

            case 0:
                searchView.setQueryHint("Buscar contactos...");
                break;

            case 1:
                searchView.setQueryHint("Buscar favoritos...");
                break;

            case TAB_RECIENTES:
                searchView.setQueryHint("Buscar llamadas...");
                break;

            case TAB_GRUPOS:
                searchView.setQueryHint("Buscar grupos...");
                break;
        }
    }

}
