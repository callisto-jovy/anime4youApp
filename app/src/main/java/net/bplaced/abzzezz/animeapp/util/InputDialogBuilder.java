/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 28.06.20, 15:08
 */

package net.bplaced.abzzezz.animeapp.util;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.EditText;

public class InputDialogBuilder {

    private final InputDialogListener dialogListener;
    private EditText editText;

    public InputDialogBuilder(final InputDialogListener listener) {
        this.dialogListener = listener;
    }

    public void showInput(final String title, final String text, final Context context) {
        this.editText = new EditText(context);
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton("Enter", (dialogInterface, i) -> dialogListener.onDialogInput(editText.getText().toString())).setNegativeButton("Cancel", (dialogInterface, i) -> dialogListener.onDialogDenied()).setView(editText).show();
    }

    public interface InputDialogListener {
        void onDialogInput(String text);

        void onDialogDenied();
    }
}
