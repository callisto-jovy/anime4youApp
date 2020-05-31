/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 21.05.20, 13:01
 */

package net.bplaced.abzzezz.animeapp.activities.input;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatDialogFragment;
import net.bplaced.abzzezz.animeapp.R;

public class InputDialog extends AppCompatDialogFragment {

    private EditText download_in;
    private InputDialogListener listener;
    private String hint;

    public InputDialog(String hint) {
        this.hint = hint;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout, null);
        builder.setView(view).setTitle(getTag()).setNegativeButton("Cancel", (dialogInterface, i) -> {
        }).setPositiveButton("Enter", (dialogInterface, i) -> {
            String start = download_in.getText().toString();
            listener.applyTexts(start);
        });
        download_in = view.findViewById(R.id.input_dialog);
        download_in.setHint(hint);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (InputDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement Listener");
        }
    }

    public interface InputDialogListener {
        void applyTexts(String start);
    }
}
