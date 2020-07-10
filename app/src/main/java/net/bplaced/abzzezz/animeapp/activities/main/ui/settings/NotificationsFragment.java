/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 02.07.20, 23:40
 */

package net.bplaced.abzzezz.animeapp.activities.main.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import id.ionbit.ionalert.IonAlert;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;

public class NotificationsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.simplelist_layout, container, false);

        ListView listView = root.findViewById(R.id.simple_list_view);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        arrayAdapter.addAll(AnimeAppMain.getInstance().getAnimeNotifications().getPreferences().getAll().keySet());

        listView.setAdapter(arrayAdapter);
        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            new IonAlert(getContext(), IonAlert.WARNING_TYPE)
                    .setTitleText("Remove from notifications?")
                    .setContentText("")
                    .setConfirmText("Yes, remove!")
                    .setConfirmClickListener(ionAlert -> {
                        String item = arrayAdapter.getItem(i);
                        AnimeAppMain.getInstance().getAnimeNotifications().remove(item);
                        arrayAdapter.remove(item);
                        arrayAdapter.notifyDataSetChanged();
                        ionAlert.dismissWithAnimation();
                    }).setCancelText("Abort").setCancelClickListener(IonAlert::dismissWithAnimation)
                    .show();
            return true;
        });
        return root;
    }


}
