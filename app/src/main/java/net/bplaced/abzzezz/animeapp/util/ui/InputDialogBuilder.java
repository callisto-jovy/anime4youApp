/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 06.04.21, 18:42
 */

package net.bplaced.abzzezz.animeapp.util.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.EditText;

public class InputDialogBuilder {

    private final InputDialogListener dialogListener;
    private EditText inputText;

    public InputDialogBuilder(final InputDialogListener listener) {
        this.dialogListener = listener;
    }

    public void showInput(final String title, final String text, final Context context) {
        this.inputText = new EditText(context);
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton("Enter", (dialogInterface, i) -> dialogListener.onDialogInput(inputText.getText().toString()))
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogListener.onDialogDenied()).setView(inputText).show();
    }

    public interface InputDialogListener {
        void onDialogInput(String text);

        void onDialogDenied();
    }
}
