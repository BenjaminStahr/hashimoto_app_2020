package com.example.hashimoto_app.ui.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.hashimoto_app.MainActivity;
import com.example.hashimoto_app.R;

public class AddSupplementDialog extends AppCompatDialogFragment
{
    private AddSupplementDialogListener listener;
    private EditText supplementEditText;
    private Spinner supplementUnitSpinner;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view  = inflater.inflate(R.layout.additional_intake_dialog, null);
        builder.setView(view)
                .setTitle("Supplement hinzufügen")
                .setNegativeButton("abbrechen", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                })
                .setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(!supplementEditText.getText().toString().equals(""))
                        {
                            MainActivity.getDataHolder().addSupplement(supplementEditText.getText().toString(), supplementUnitSpinner.getSelectedItem().toString());
                            listener.refreshSupplementList();
                        }
                    }
                });
        supplementEditText = view.findViewById(R.id.supplement_name_edit_text);
        supplementUnitSpinner = view.findViewById(R.id.supplement_unit_spinner);
        return builder.create();
    }
    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        try
        {
            listener = (AddSupplementDialogListener) context;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public interface AddSupplementDialogListener
    {
        void refreshSupplementList();
    }
}