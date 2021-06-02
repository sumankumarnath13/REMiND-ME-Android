package com.example.remindme.ui.fragments.dialogFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import androidx.fragment.app.FragmentActivity;

import com.example.remindme.R;
import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.ui.fragments.dialogFragments.common.DialogFragmentBase;

import java.util.List;

public class NoteDialog extends DialogFragmentBase {

    public static final String TAG = "NoteDialog";

    public interface INoteInputDialogListener {

        void setNoteDialogModel(String note);

        String getNoteDialogModel();

    }

    private INoteInputDialogListener listener;

    protected INoteInputDialogListener getListener() {
        if (listener == null) {
            listener = super.getListener(INoteInputDialogListener.class);
        }
        return listener;
    }

    private String model;

    protected String getModel() {
        return model;
    }

    private static final int SPEECH_REQUEST_CODE = 117;
    private AppCompatEditText txt_reminder_note;
    private AppCompatTextView tv_reminder_note_limit_msg;
    private static final int NOTE_MAX_LENGTH = 500;
    private static final String MODEL_KEY = "MODEL_KEY";

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(MODEL_KEY, getModel());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getListener() == null) {
            ToastHelper.showError(getContext(), "Listener incompatible!");
            dismiss();
            return;
        }

        if (savedInstanceState == null) {
            model = getListener().getNoteDialogModel();
        } else {
            model = savedInstanceState.getString(MODEL_KEY);
        }
    }

    @Override
    protected void onUIRefresh() {

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

        final View view = inflater.inflate(R.layout.dialog_fragment_input_note, null);

        txt_reminder_note = view.findViewById(R.id.tv_reminder_note);
        tv_reminder_note_limit_msg = view.findViewById(R.id.tv_reminder_note_limit_msg);

        txt_reminder_note.setText(getModel());
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
                    tv_reminder_note_limit_msg.setText(getString(R.string.format_label_character_limit_warning, NOTE_MAX_LENGTH - len, "s"));
                } else {
                    tv_reminder_note_limit_msg.setText(getString(R.string.format_label_character_limit_warning, 0, ""));
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
                getListener().setNoteDialogModel(
                        txt_reminder_note.getText() == null ? null : txt_reminder_note.getText().toString()))
                .setNegativeButton("CANCEL", (dialog, which) -> {
                });

        return builder.create();
    }


}
