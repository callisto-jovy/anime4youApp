/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 20.05.20, 09:38
 */

package net.bplaced.abzzezz.animeapp.input;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatDialogFragment;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.util.BackgroundHolder;

public class DownloadSpecificInput extends AppCompatDialogFragment {

    private EditText download_in;
    private SpecificDownloadListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_download_layout, null);
        builder.setView(view).setTitle("Download specific").setNegativeButton("Cancel", (dialogInterface, i) -> {
        })
                .setPositiveButton("Enter", (dialogInterface, i) -> {
                    String start = download_in.getText().toString();
                    listener.applyTexts(start);
                });

        download_in = view.findViewById(R.id.download_specific);
        download_in.setHighlightColor(BackgroundHolder.color);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (SpecificDownloadListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement Listener");
        }
    }

    public interface SpecificDownloadListener {
        void applyTexts(String start);
    }

}
