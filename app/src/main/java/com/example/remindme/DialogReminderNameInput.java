package com.example.remindme;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.remindme.ui.main.IReminderNameListener;

import java.util.List;

public class DialogReminderNameInput extends DialogFragment {
    private IReminderNameListener listener;
    private String name;
    private static final int SPEECH_REQUEST_CODE = 117;
    private EditText txt_reminder_name;
    private TextView tv_reminder_name_limit_msg;
    private static final int NOTE_MAX_LENGTH = 50;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (IReminderNameListener) context;
            name = listener.getReminderName();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + " : " + context.toString() + " must implement IReminderNameListener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            // Do something with spokenText.
            txt_reminder_name.setText(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null) return builder.create();
        LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_reminder_input_name, null);

        txt_reminder_name = view.findViewById(R.id.txt_reminder_name);
        tv_reminder_name_limit_msg = view.findViewById(R.id.tv_reminder_name_limit_msg);
        //tv_reminder_name_limit_msg.setMax

        txt_reminder_name.setText(name);
        txt_reminder_name.addTextChangedListener(new TextWatcher() {
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
                    tv_reminder_name_limit_msg.setText(NOTE_MAX_LENGTH - len + " characters available");
                } else {
                    tv_reminder_name_limit_msg.setText("0 character available");
                }
            }
        });

        final ImageView img_reminder_name_voice_input = view.findViewById(R.id.img_reminder_name_voice_input);
        img_reminder_name_voice_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                // This starts the activity and populates the intent with the speech text.
                startActivityForResult(intent, SPEECH_REQUEST_CODE);
            }
        });

        builder.setView(view).setTitle("Reminder Name").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.setName(txt_reminder_name.getText().toString(), true);
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }
}
