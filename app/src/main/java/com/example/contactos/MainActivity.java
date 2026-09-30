package com.example.contactos;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.widget.SearchView;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private static final int TAB_CONTACTOS = 0;
    private static final int TAB_FAVORITOS = 1;
    private static final int TAB_NUEVO = 2;
    private static final int TAB_RECIENTES = 3;
    private static final int TAB_GRUPOS = 4;

    private ViewPager2 viewPager;
    private View formContainer;
    private View topBar;
    private TabLayout tabLayout;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewpager);
        formContainer = findViewById(R.id.form_container);
        topBar = findViewById(R.id.top_bar);
        tabLayout = findViewById(R.id.tabLayout);
        searchView = findViewById(R.id.searchView);

        viewPager.setAdapter(new ViewPagerAdapter(this));
        addTabs(tabLayout);
        configureNavigation();
        configureSearch();
        configureInsets();
    }

    private void configureNavigation() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                selectTab(tab.getPosition());
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {

                if (formContainer.getVisibility() == View.VISIBLE) {
                    return;
                }

                TabLayout.Tab tab = tabLayout.getTabAt(positionToTab(position));

                if (tab != null && !tab.isSelected()) {
                    tab.select();
                }

                updateSearchHint(position);

                viewPager.post(() ->
                        applySearch(searchView.getQuery().toString())
                );
            }
        });
    }

    private void selectTab(int tabPosition) {
        if (tabPosition == TAB_NUEVO) {
            showNewContactForm();
            return;
        }

        showPage(tabToPosition(tabPosition));
    }

    private void showPage(int pagePosition) {
        topBar.setVisibility(View.VISIBLE);
        formContainer.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        if (viewPager.getCurrentItem() != pagePosition) {
            viewPager.setCurrentItem(pagePosition, true);
        }
    }

    private void showNewContactForm() {
        topBar.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        formContainer.setVisibility(View.VISIBLE);

        Fragment current = getSupportFragmentManager()
                .findFragmentById(R.id.form_container);
        if (!(current instanceof fragment_nuevo_contacto)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.form_container, new fragment_nuevo_contacto())
                    .commit();
        }
    }

    public void showContacts() {
        TabLayout.Tab contactsTab = tabLayout.getTabAt(TAB_CONTACTOS);
        if (contactsTab != null) {
            contactsTab.select();
        }
    }

    private int tabToPosition(int tabPosition) {
        switch (tabPosition) {
            case TAB_CONTACTOS:
                return 0;
            case TAB_FAVORITOS:
                return 1;
            case TAB_RECIENTES:
                return 2;
            case TAB_GRUPOS:
                return 3;
            default:
                return 0;
        }
    }

    private int positionToTab(int pagePosition) {
        switch (pagePosition) {
            case 0:
                return TAB_CONTACTOS;
            case 1:
                return TAB_FAVORITOS;
            case 2:
                return TAB_RECIENTES;
            case 3:
                return TAB_GRUPOS;
            default:
                return TAB_CONTACTOS;
        }
    }

    @Override
    public void onBackPressed() {
        if (formContainer.getVisibility() == View.VISIBLE) {
            tabLayout.getTabAt(TAB_CONTACTOS).select();
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

            case 2:
                searchView.setQueryHint("Buscar llamadas...");
                break;

            case 3:
                searchView.setQueryHint("Buscar grupos...");
                break;
        }
    }

    private void addTabs(TabLayout tabLayout) {
        tabLayout.addTab(
                tabLayout.newTab()
                        .setIcon(R.drawable.ic_tab_contacts_filled)
                        .setText("Contatos")
        );
        tabLayout.addTab(
                tabLayout.newTab()
                        .setIcon(R.drawable.ic_tab_favorite_filled)
                        .setText("Favoritos")
        );
        tabLayout.addTab(
                tabLayout.newTab()
                        .setIcon(R.drawable.ic_tab_add_filled)
                        .setText("Nuevo")
        );
        tabLayout.addTab(
                tabLayout.newTab()
                        .setIcon(R.drawable.ic_tab_history_filled)
                        .setText("Recientes")
        );
        tabLayout.addTab(
                tabLayout.newTab()
                        .setIcon(R.drawable.ic_tab_groups_filled)
                        .setText("Grupos")
        );
    }
}
