package org.team7.notificationlog.filtering;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.team7.notificationlog.R;
import org.team7.notificationlog.db.StringFilter;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class StringFilterDialog extends DialogFragment {

    public StringFilterDialogCallback listener;
    private String editOldTarget;
    private String editOldType;
    private String editOldFilterText;
    private DeleteTask editDeleteOldTask;
    private boolean isEdit = false;

    public static StringFilterDialog newInstance(StringFilterDialogCallback listener) {
        StringFilterDialog instance = new StringFilterDialog();
        instance.listener = listener;
        return instance;
    }

    public static StringFilterDialog newEditInstance(StringFilterDialogCallback listener, DeleteTask deleteTheOld, StringFilter oldFilter) {
        StringFilterDialog instance = new StringFilterDialog();
        instance.editOldTarget = oldFilter.target;
        instance.editOldType = oldFilter.type;
        instance.editOldFilterText = oldFilter.filterText;
        instance.isEdit = true;
        instance.editDeleteOldTask = deleteTheOld;

        instance.listener = listener;
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View v = getActivity().getLayoutInflater().inflate(R.layout.add_filter_dialog, null);
        builder.setView(v);

        final Spinner targetSpinner = v.findViewById(R.id.targetSpinner);
        final Spinner typeSpinner = v.findViewById(R.id.typeSpinner);
        final EditText editText = v.findViewById(R.id.filterEditText);

        List<String> targetArray = Arrays.asList(getResources().getStringArray(R.array.content_options));
        ArrayAdapter targetAdapter = new ArrayAdapter(getContext(),
                android.R.layout.simple_spinner_dropdown_item, targetArray);
        targetSpinner.setAdapter(targetAdapter);

        List<String> typeArray = Arrays.asList(getResources().getStringArray(R.array.match_options));
        ArrayAdapter typeAdapter = new ArrayAdapter(getContext(),
                android.R.layout.simple_spinner_dropdown_item, typeArray);
        typeSpinner.setAdapter(typeAdapter);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String target = (String) targetSpinner.getSelectedItem();
                String type = (String) typeSpinner.getSelectedItem();
                String filter = editText.getText().toString();

                listener.onAdd(target, type, filter);
                if (isEdit)
                    editDeleteOldTask.execute();
            }
        });

        // If edit mode, set values
        if (isEdit) {
            switch (editOldTarget) {
                case "Title":
                    targetSpinner.setSelection(0);
                    break;
                case "Text":
                    targetSpinner.setSelection(1);
                    break;
            }
            switch (editOldType) {
                case "Match":
                    typeSpinner.setSelection(0);
                    break;
                case "Contains":
                    typeSpinner.setSelection(1);
                    break;
            }
            editText.setText(editOldFilterText);
        }

        return builder.create();
    }

    public interface StringFilterDialogCallback {
        void onAdd(String target, String type, String filter);
    }

}
