package com.example.remindme.ui.fragments.dialogFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.remindme.R;

import java.util.List;
import java.util.Locale;

public class NoteDialog extends DialogFragment {
    public static final String TAG = "NoteDialog";

    private INoteInputDialogListener listener;
    private String note;
    private static final int SPEECH_REQUEST_CODE = 117;
    private AppCompatEditText txt_reminder_note;
    private AppCompatTextView tv_reminder_note_limit_msg;
    private static final int NOTE_MAX_LENGTH = 500;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (INoteInputDialogListener) context;
            note = listener.getNoteDialogModel();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + " : " + context.toString() + " must implement IReminderNoteListener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            final List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            final String spokenText = results.get(0);
            // Do something with spokenText.
            txt_reminder_note.setText(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null)
            return builder.create();
        final LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.input_note_dialog, null);

        txt_reminder_note = view.findViewById(R.id.tv_reminder_note);
        tv_reminder_note_limit_msg = view.findViewById(R.id.tv_reminder_note_limit_msg);

        txt_reminder_note.setText(note);
        txt_reminder_note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int len = s.length();
                if (len > NOTE_MAX_LENGTH) {
                    s.delete(NOTE_MAX_LENGTH, len);
                }
                if (NOTE_MAX_LENGTH - len > 0) {
                    tv_reminder_note_limit_msg.setText(String.format(Locale.getDefault(), "%d characters available", NOTE_MAX_LENGTH - len));
                } else {
                    tv_reminder_note_limit_msg.setText("0 character available");
                }
            }
        });

        final AppCompatImageView img_reminder_note_voice_input = view.findViewById(R.id.img_reminder_note_voice_input);
        img_reminder_note_voice_input.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            // This starts the activity and populates the intent with the speech text.
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        });

        builder.setView(view).setTitle("Reminder Note").setPositiveButton("OK", (dialog, which) ->
                listener.setNoteDialogModel(
                        txt_reminder_note.getText() == null ? null : txt_reminder_note.getText().toString()))
                .setNegativeButton("CANCEL", (dialog, which) -> {
                });

        return builder.create();
    }

    public interface INoteInputDialogListener {

        void setNoteDialogModel(String note);

        String getNoteDialogModel();

    }
}
