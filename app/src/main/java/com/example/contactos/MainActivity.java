package com.example.contactos;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewPager2 viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new ViewPagerAdapter(this));
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        addTabs(tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), true);
            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tab = tabLayout.getTabAt(position);
                if (tab != null && !tab.isSelected()) {
                    tab.select();
                }
            }
        });

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