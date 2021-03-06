package com.example.hashimoto_app.ui.main.intake;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.hashimoto_app.MainActivity;
import com.example.hashimoto_app.R;
import com.jjoe64.graphview.series.Series;

/**
 * This class implements the dialog for deleteing a data point for one supplement
 */
public class DeleteIntakeDataPointDialog extends AppCompatDialogFragment
{
    private DeleteIntakeDataPointDialog.DeleteIntakeDataPointDialogListener listener;
    private String substance;
    private double dateOfDataPoint;
    private Series series;

    public DeleteIntakeDataPointDialog(String substance, double dateOfDataPoint, Series series)
    {
        this.substance = substance;
        this.dateOfDataPoint = dateOfDataPoint;
        this.series = series;
    }

    /**
     * @param savedInstanceState
     * @return returns a instance of this dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view  = inflater.inflate(R.layout.delete_datapoint_dialog, null);
        builder.setView(view)
                .setTitle("Eintragung löschen")
                .setNegativeButton("abbrechen", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                })
                .setPositiveButton("löschen", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        MainActivity.getDataHolder().deleteIntakeDataPoint(substance, dateOfDataPoint);
                        listener.refreshIntakeGraph(series, dateOfDataPoint, substance);
                    }
                });
        return builder.create();
    }

    /**
     * This method attaches the dialog to the application context
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        try
        {
            listener = (DeleteIntakeDataPointDialog.DeleteIntakeDataPointDialogListener) context;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public interface DeleteIntakeDataPointDialogListener
    {
        void refreshIntakeGraph(Series series, double dataPoint, String substance);
    }
}
