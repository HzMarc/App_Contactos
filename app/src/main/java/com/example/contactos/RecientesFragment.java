package com.example.contactos;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.Locale;

public class RecientesFragment extends Fragment {

    private String currentQuery = "";

    private boolean showingMissedOnly = false;

    public void filterByQuery(String query) {

        currentQuery = query == null
                ? ""
                : query.trim().toLowerCase(Locale.ROOT);

        if (getView() != null) {
            renderCalls(
                    getView(),
                    showingMissedOnly
            );
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_recientes, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        TextView allButton = view.findViewById(R.id.btn_todas);
        TextView missedButton = view.findViewById(R.id.btn_perdidas);

        allButton.setOnClickListener(clickedView -> {

            showingMissedOnly = false;

            updateFilterButtons(
                    allButton,
                    missedButton,
                    true
            );

            renderCalls(
                    view,
                    showingMissedOnly
            );
        });
        missedButton.setOnClickListener(clickedView -> {

            showingMissedOnly = true;

            updateFilterButtons(
                    allButton,
                    missedButton,
                    false
            );

            renderCalls(
                    view,
                    showingMissedOnly
            );
        });

        renderCalls(view, showingMissedOnly);    }

    private void renderCalls(
            @NonNull View root,
            boolean missedOnly
    ) {

        LinearLayout callsLayout =
                root.findViewById(R.id.layout_recientes);

        callsLayout.removeAllViews();

        String currentSection = "";

        boolean hasResults = false;


        for (
                int index = 0;
                index < StaticDatabase.RECENT_CALLS.length;
                index++
        ) {

            StaticDatabase.RecentCall call =
                    StaticDatabase.RECENT_CALLS[index];


            if (missedOnly && !call.missed) {
                continue;
            }


            if (!matchesCall(call)) {
                continue;
            }


            String section =
                    index < 3
                            ? "Hoy"
                            : "Ayer";


            if (!section.equals(currentSection)) {

                callsLayout.addView(
                        createSectionTitle(section)
                );

                currentSection = section;
            }


            callsLayout.addView(
                    createCallCard(call)
            );

            hasResults = true;
        }


        if (!hasResults) {

            TextView emptyState = createText(
                    "No se encontraron llamadas",
                    15,
                    false
            );

            emptyState.setGravity(Gravity.CENTER);

            emptyState.setPadding(
                    0,
                    dp(40),
                    0,
                    dp(40)
            );

            callsLayout.addView(emptyState);
        }
    }

    private boolean matchesCall(
            @NonNull StaticDatabase.RecentCall call
    ) {

        if (currentQuery.isEmpty()) {
            return true;
        }

        String searchableText =
                call.name + " "
                        + call.type + " "
                        + call.time + " "
                        + call.duration;

        return searchableText
                .toLowerCase(Locale.ROOT)
                .contains(currentQuery);
    }

    @NonNull
    private TextView createSectionTitle(@NonNull String title) {
        TextView sectionTitle = createText(title, 17, true);
        sectionTitle.setTextColor(Color.rgb(37, 18, 201));
        sectionTitle.setPadding(0, dp(4), 0, dp(10));
        return sectionTitle;
    }

    @NonNull
    private View createCallCard(@NonNull StaticDatabase.RecentCall call) {
        LinearLayout card = new LinearLayout(requireContext());
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(92)
        );
        cardParams.setMargins(0, 0, 0, dp(12));
        card.setLayoutParams(cardParams);
        card.setBackgroundResource(R.drawable.bg_contact_card);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setPadding(dp(12), dp(12), dp(12), dp(12));

        TextView initials = new TextView(requireContext());
        initials.setLayoutParams(new LinearLayout.LayoutParams(dp(62), dp(62)));
        initials.setBackgroundResource(call.avatarBackground);
        initials.setGravity(Gravity.CENTER);
        initials.setText(call.initials);
        initials.setTextColor(Color.WHITE);
        initials.setTextSize(18);
        initials.setTypeface(null, Typeface.BOLD);
        card.addView(initials);

        LinearLayout details = new LinearLayout(requireContext());
        LinearLayout.LayoutParams detailsParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );
        detailsParams.setMargins(dp(14), 0, 0, 0);
        details.setLayoutParams(detailsParams);
        details.setOrientation(LinearLayout.VERTICAL);
        details.addView(createText(call.name, 17, true));

        TextView type = createText(
                (call.missed ? "↙ " : "↗ ") + call.type,
                13,
                false
        );
        type.setTextColor(call.missed
                ? Color.rgb(216, 50, 50)
                : Color.rgb(20, 163, 90));
        details.addView(type);

        String time = call.time + (call.duration.isEmpty() ? "" : "  ·  " + call.duration);
        TextView timeView = createText(time, 12, false);
        timeView.setTextColor(Color.rgb(124, 124, 133));
        details.addView(timeView);
        card.addView(details);

        return card;
    }

    @NonNull
    private TextView createText(
            @NonNull String text,
            int textSize,
            boolean bold
    ) {
        TextView textView = new TextView(requireContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        textView.setText(text);
        textView.setTextColor(Color.rgb(32, 32, 39));
        textView.setTextSize(textSize);
        textView.setTypeface(null, bold ? Typeface.BOLD : Typeface.NORMAL);
        return textView;
    }

    private void updateFilterButtons(
            @NonNull TextView allButton,
            @NonNull TextView missedButton,
            boolean allSelected
    ) {
        allButton.setBackgroundResource(
                allSelected
                        ? R.drawable.bg_recent_filter_selected
                        : R.drawable.bg_recent_filter_unselected
        );
        allButton.setTextColor(allSelected ? Color.WHITE : Color.rgb(119, 119, 128));
        missedButton.setBackgroundResource(
                allSelected
                        ? R.drawable.bg_recent_filter_unselected
                        : R.drawable.bg_recent_filter_selected
        );
        missedButton.setTextColor(allSelected ? Color.rgb(119, 119, 128) : Color.WHITE);
    }

    private boolean hasMissedCalls() {
        for (StaticDatabase.RecentCall call : StaticDatabase.RECENT_CALLS) {
            if (call.missed) {
                return true;
            }
        }
        return false;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

}
