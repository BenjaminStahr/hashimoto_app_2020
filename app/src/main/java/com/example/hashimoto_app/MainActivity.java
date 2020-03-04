package com.example.hashimoto_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import com.example.hashimoto_app.backend.DataHolder;
import com.example.hashimoto_app.backend.FileManager;
import com.example.hashimoto_app.backend.IntakeElement;
import com.example.hashimoto_app.backend.Measurement;
import com.example.hashimoto_app.backend.SymptomElement;
import com.example.hashimoto_app.backend.ThyroidElement;
import com.example.hashimoto_app.backend.ThyroidMeasurement;
import com.example.hashimoto_app.ui.main.intake.AddSupplementDialog;
import com.example.hashimoto_app.ui.main.symtoms.AddSymptomDialog;
import com.example.hashimoto_app.ui.main.intake.DeleteIntakeDataPointDialog;
import com.example.hashimoto_app.ui.main.symtoms.DeleteSymptomDataPointDialog;
import com.example.hashimoto_app.ui.main.thyroid.DeleteThyroidDataPointDialog;
import com.example.hashimoto_app.ui.main.intake.IntakeDialog;
import com.example.hashimoto_app.ui.main.symtoms.SymptomDialog;
import com.example.hashimoto_app.ui.main.thyroid.ThyroidDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.hashimoto_app.ui.main.SectionsPagerAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements ThyroidDialog.ThyroidDialogListener,
        SymptomDialog.SymptomDialogListener, AddSymptomDialog.AddSymptomDialogListener, IntakeDialog.IntakeDialogListener,
        AddSupplementDialog.AddSupplementDialogListener, DeleteThyroidDataPointDialog.DeleteThyroidDataPointDialogListener,
        DeleteSymptomDataPointDialog.DeleteSymptomDataPointDialogListener, DeleteIntakeDataPointDialog.DeleteIntakeDataPointDialogListener
{
    // central data management of the applications data
    private static DataHolder dataHolder;
    // holds the different fragments
    SectionsPagerAdapter sectionsPagerAdapter;
    // lets user change the shown time period
    private Spinner periodSpinner;
    // dialogs of which the main activity needs the reference, because they can init some action
    SymptomDialog symptomDialog;
    IntakeDialog intakeDialog;
    public static String actualPeriod = "Woche";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        final ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        final FloatingActionButton fab = findViewById(R.id.fab);

        try
        {
            dataHolder = new Gson().fromJson(FileManager.getFileAsString("userData", getApplicationContext()), DataHolder.class);
            for (int i = 0; i < dataHolder.getSymptomData().size(); i++)
            {
                if (dataHolder.getSymptomData().get(i).getMeasurements() == null)
                {
                    dataHolder.getSymptomData().get(i).setMeasurements(new ArrayList<Measurement>());
                }
            }
        }
        catch (Exception ex)
        {
            Random rand = new Random(System.currentTimeMillis());
            int id = rand.nextInt();
            if (id < 0)
            {
                id *= -1;
            }
            dataHolder = new DataHolder(id);
            // register user in the server
            sendFirstTimeDataToServer(getUserDataAsJson());
            // add some sample data to the holder
            Calendar calendar = Calendar.getInstance();
            calendar.set(2020, 0, 9, 0, 0, 0);
            /*Date date = calendar.getTime();
            calendar.set(2020, 0, 13, 0, 0, 0);
            Date date2 = calendar.getTime();
            calendar.set(2020, 0, 14, 6, 0, 0);
            Date date3 = calendar.getTime();
            // sample data for thyroid measurements
            ThyroidMeasurement thyroidMeasurement1 = new ThyroidMeasurement(date, 3, 1, 3);
            ThyroidMeasurement thyroidMeasurement2 = new ThyroidMeasurement(date2, 4, 1, 3);
            ThyroidMeasurement thyroidMeasurement5 = new ThyroidMeasurement(date3, 2, 1, 3);
            ArrayList<ThyroidMeasurement> thyroidMeasurements1 = new ArrayList<>();
            thyroidMeasurements1.add(thyroidMeasurement1);
            thyroidMeasurements1.add(thyroidMeasurement2);
            thyroidMeasurements1.add(thyroidMeasurement5);
            ArrayList<ThyroidMeasurement> thyroidMeasurements2 = new ArrayList<>();
            ThyroidMeasurement thyroidMeasurement3 = new ThyroidMeasurement(date, 1.6f, 1, 3);
            ThyroidMeasurement thyroidMeasurement4 = new ThyroidMeasurement(date2, 1.4f, 1, 3);
            thyroidMeasurements2.add(thyroidMeasurement3);
            thyroidMeasurements2.add(thyroidMeasurement4);
            ArrayList<ThyroidMeasurement> thyroidMeasurements3 = new ArrayList<>();
            thyroidMeasurements3.add(thyroidMeasurement1);
            thyroidMeasurements3.add(thyroidMeasurement2);
            thyroidMeasurements3.add(thyroidMeasurement5);
            dataHolder.getThyroidData().add(new ThyroidElement("TSH", "µU/ml", thyroidMeasurements1));
            dataHolder.getThyroidData().add(new ThyroidElement("fT3", "pg/ml", thyroidMeasurements2));
            dataHolder.getThyroidData().add(new ThyroidElement("fT4", "ng/dl", thyroidMeasurements3));

            // sample data for symptoms
            Measurement symptomMeasurement1 = new Measurement(date, 2);
            Measurement symptomMeasurement2 = new Measurement(date2, 3);
            Measurement symptomMeasurement3 = new Measurement(date3, 3);
            Measurement symptomMeasurement4 = new Measurement(date, 5);
            Measurement symptomMeasurement5 = new Measurement(date2, 3);
            Measurement symptomMeasurement6 = new Measurement(date3, 1);
            ArrayList<Measurement> symptomMeasurements1 = new ArrayList<>();
            symptomMeasurements1.add(symptomMeasurement1);
            symptomMeasurements1.add(symptomMeasurement2);
            symptomMeasurements1.add(symptomMeasurement3);
            ArrayList<Measurement> symptomMeasurements2 = new ArrayList<>();
            symptomMeasurements2.add(symptomMeasurement4);
            symptomMeasurements2.add(symptomMeasurement5);
            symptomMeasurements2.add(symptomMeasurement6);
            ArrayList<Measurement> symptomMeasurements3 = new ArrayList<>();
            symptomMeasurements3.add(symptomMeasurement4);
            symptomMeasurements3.add(symptomMeasurement5);
            symptomMeasurements3.add(symptomMeasurement6);
            ArrayList<Measurement> symptomMeasurements4 = new ArrayList<>();
            symptomMeasurements4.add(symptomMeasurement1);
            symptomMeasurements4.add(symptomMeasurement2);
            symptomMeasurements4.add(symptomMeasurement3);*/

            dataHolder.getSymptomData().add(new SymptomElement("Depression", new ArrayList<Measurement>()));
            dataHolder.getSymptomData().add(new SymptomElement("Erschöpfung", new ArrayList<Measurement>()));
            dataHolder.getSymptomData().add(new SymptomElement("Gelenkschmerzen", new ArrayList<Measurement>()));
            dataHolder.getSymptomData().add(new SymptomElement("Haarausfall", new ArrayList<Measurement>()));
            dataHolder.getSymptomData().add(new SymptomElement("Heiserkeit", new ArrayList<Measurement>()));
            dataHolder.getSymptomData().add(new SymptomElement("Kälteempfindlichkeit", new ArrayList<Measurement>()));
            dataHolder.getSymptomData().add(new SymptomElement("Kopfschmerzen", new ArrayList<Measurement>()));
            dataHolder.getSymptomData().add(new SymptomElement("Kurzatmigkeit", new ArrayList<Measurement>()));
            dataHolder.getSymptomData().add(new SymptomElement("Muskelkrämpfe", new ArrayList<Measurement>()));
            dataHolder.getSymptomData().add(new SymptomElement("schuppige Haut", new ArrayList<Measurement>()));
            dataHolder.getSymptomData().add(new SymptomElement("Schwächegefühl", new ArrayList<Measurement>()));
            dataHolder.getSymptomData().add(new SymptomElement("sprödes/trockenes Haar", new ArrayList<Measurement>()));
            dataHolder.getSymptomData().add(new SymptomElement("Taubheitsgefühl", new ArrayList<Measurement>()));
            dataHolder.getSymptomData().add(new SymptomElement("trockene Haut", new ArrayList<Measurement>()));
            dataHolder.getSymptomData().add(new SymptomElement("unregelmäßige Menstruation", new ArrayList<Measurement>()));
            dataHolder.getSymptomData().add(new SymptomElement("verringertes Schwitzen", new ArrayList<Measurement>()));
            dataHolder.getSymptomData().add(new SymptomElement("Verstopfung", new ArrayList<Measurement>()));
            dataHolder.getSymptomData().add(new SymptomElement("zu starke Menstruation", new ArrayList<Measurement>()));
            /*Measurement intakeMeasurement1 = new Measurement(date, 6);
            Measurement intakeMeasurement2 = new Measurement(date2, 3);
            Measurement intakeMeasurement3 = new Measurement(date3, 7);
            Measurement intakeMeasurement4 = new Measurement(date, 1);
            Measurement intakeMeasurement5 = new Measurement(date2, 2);
            Measurement intakeMeasurement6 = new Measurement(date3, 10);

            ArrayList<Measurement> intakeMeasurements1 = new ArrayList<>();
            intakeMeasurements1.add(intakeMeasurement1);
            intakeMeasurements1.add(intakeMeasurement2);
            intakeMeasurements1.add(intakeMeasurement3);
            ArrayList<Measurement> intakeMeasurements2 = new ArrayList<>();
            intakeMeasurements2.add(intakeMeasurement4);
            intakeMeasurements2.add(intakeMeasurement5);
            intakeMeasurements2.add(intakeMeasurement6);
            ArrayList<Measurement> intakeMeasurements3 = new ArrayList<>();
            intakeMeasurements3.add(intakeMeasurement1);
            intakeMeasurements3.add(intakeMeasurement2);
            intakeMeasurements3.add(intakeMeasurement3);
            dataHolder.getIntakeData().add(new IntakeElement("Magnesium", "g", intakeMeasurements2));
            dataHolder.getIntakeData().add(new IntakeElement("Selen", "mg", intakeMeasurements1));
            dataHolder.getIntakeData().add(new IntakeElement("Vitamin D", "mg", intakeMeasurements3));*/
            FileManager.saveFile("userData", new Gson().toJson(dataHolder), getApplicationContext());

            // Here we like to get the delay till 6 o'Clock, the time the user should record his symptoms
            Date currentDate = new Date();
            calendar.setTime(currentDate);
            Date targetDate;
            long delay;
            if(calendar.get(Calendar.HOUR_OF_DAY) < 17)
            {
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                        17, 0, 0);
                targetDate = calendar.getTime();
                delay = targetDate.getTime() - currentDate.getTime();
            }
            else
            {
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) +1,
                        17, 0, 0);
                targetDate = calendar.getTime();
                delay = targetDate.getTime() - currentDate.getTime();
            }
            PeriodicWorkRequest notificationRequest =
                    new PeriodicWorkRequest.Builder(NotificationWorker.class, 1, TimeUnit.DAYS)
                            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                            .build();
            WorkManager.getInstance(getApplicationContext())
                    .enqueue(notificationRequest);
        }
        TextView titleView = findViewById(R.id.title);
        titleView.setText("ID : " + dataHolder.getUSER_ID());


        periodSpinner = findViewById(R.id.period_spinner);
        periodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                updateDataAccordingToSelectedTimePeriod();

                //viewPager.getAdapter().notifyDataSetChanged();
                /*OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(NetworkWorker.class)
                        .build();
                WorkManager.getInstance(getApplicationContext())
                        .enqueue(work);*/
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(viewPager.getCurrentItem() == 0)
                {
                    openThyroidDialog();
                }
                else if(viewPager.getCurrentItem() == 1)
                {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    // user can make entries between 6 and 11 o'Clock pm
                    if(calendar.get(Calendar.HOUR_OF_DAY) >= 17)
                    {
                        openSymptomDialog();
                    }
                    else
                    {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder.setTitle("Hinweis");
                        alertDialogBuilder
                                .setMessage("Sie können keine Symptome vor 17 Uhr eintragen")
                                .setCancelable(false)
                                .setPositiveButton("Okay",new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog,int id)
                                    {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
                else
                {
                    openIntakeDialog();
                }
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position)
            {
                if(position == 0)
                {
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.thyroidBlue)));
                }
                else if (position == 1)
                {
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.symptomRed)));
                }
                else
                {
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.intakeYellow)));
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        PeriodicWorkRequest networkRequest =
                new PeriodicWorkRequest.Builder(NetworkWorker.class, 1, TimeUnit.HOURS)
                        .build();
        WorkManager.getInstance(getApplicationContext())
                .enqueue(networkRequest);
    }

    public static void sendFirstTimeDataToServer(final String symptomData)
    {
        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... voids)
            {
                return initServerData(symptomData);
            }
        }.execute();
    }
    public static String initServerData(String symptomData)
    {
        String query_url = "http://srvgvm33.offis.uni-oldenburg.de:8080/1/thyreodata";
        try
        {
            URL url = new URL(query_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();
            os.write(symptomData.getBytes());
            os.close();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String result = in.toString();
            in.close();
            conn.disconnect();
            return result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "unsuccessful initialization of connection to server";
        }
    }

    public static String getUserDataAsJson()
    {
        final JsonObject sendData = new JsonObject();
        String idJson = new Gson().toJson(dataHolder.getUSER_ID());
        String symptomJson = new Gson().toJson(dataHolder.getSymptomData());
        sendData.add("id", new Gson().fromJson(idJson, JsonPrimitive.class));
        sendData.add("symptomData", new Gson().fromJson(symptomJson, JsonArray.class));
        return sendData.toString();
    }
    public void openThyroidDialog()
    {
        ThyroidDialog thyroidDialog = new ThyroidDialog();
        thyroidDialog.show(getSupportFragmentManager(), "thyroid dialog");
    }

    public void openSymptomDialog()
    {
        symptomDialog = new SymptomDialog(this);
        symptomDialog.show(getSupportFragmentManager(), "symptom dialog");
    }

    public void openIntakeDialog()
    {
        intakeDialog = new IntakeDialog(this);
        intakeDialog.show(getSupportFragmentManager(), "intake dialog");
    }
    public void openAddSymptomDialog()
    {
        AddSymptomDialog addSymptomDialog = new AddSymptomDialog();
        addSymptomDialog.show(getSupportFragmentManager(), "add symptom dialog");
    }
    public void openAddSupplementDialog()
    {
        AddSupplementDialog addSupplementDialog = new AddSupplementDialog();
        addSupplementDialog.show(getSupportFragmentManager(), "add supplement dialog");
    }

    public void updateDataAccordingToSelectedTimePeriod()
    {
        if(periodSpinner.getSelectedItemPosition() == 0)
        {
            sectionsPagerAdapter.adjustDataToTimePeriod(getString(R.string.period_week));
            actualPeriod = getString(R.string.period_week);
        }
        else if (periodSpinner.getSelectedItemPosition() == 1)
        {
            sectionsPagerAdapter.adjustDataToTimePeriod(getString(R.string.period_month));
            actualPeriod = getString(R.string.period_month);
        }
        else
        {
            sectionsPagerAdapter.adjustDataToTimePeriod(getString(R.string.period_year));
            actualPeriod = getString(R.string.period_year);
        }
        //sectionsPagerAdapter.updateThyroidFragment(this);
    }

    @Override
    public void applyThyroidTexts(String registeredValue, String substance, String unit, Date selectedDate)
    {
        boolean alreadyRegistered = false;
        boolean registeredInserted = false;
        ThyroidMeasurement thyroidMeasurement = new ThyroidMeasurement(selectedDate, Float.valueOf(registeredValue), 0 ,0);
        for(int i = 0; i < dataHolder.getThyroidData().size(); i++)
        {
            // right substance
            if(dataHolder.getThyroidData().get(i).getNameOfSubstance().equals(substance))
            {
                alreadyRegistered = true;
                for(int j = 0; j < dataHolder.getThyroidData().get(i).getMeasurements().size(); j++)
                {
                    if(dataHolder.getThyroidData().get(i).getMeasurements().get(j).getDate().getTime() > selectedDate.getTime())
                    {
                        if(j != 0)
                        {
                            // the case that its newer than the oldest entry but older than the newest entry
                            dataHolder.getThyroidData().get(i).getMeasurements().add(j, thyroidMeasurement);
                            registeredInserted = true;
                            break;
                        }
                        else
                        {
                            // the case when its older than the oldest entry
                            dataHolder.getThyroidData().get(i).getMeasurements().add(0, thyroidMeasurement);
                            registeredInserted = true;
                            break;
                        }
                    }
                }
                // the case when its newer than the newest entry
                if(!registeredInserted)
                {
                    dataHolder.getThyroidData().get(i).getMeasurements().add(thyroidMeasurement);
                }

            }
        }
        if(!alreadyRegistered)
        {
            ArrayList<ThyroidMeasurement> thyroidMeasurements = new ArrayList<>();
            thyroidMeasurements.add(thyroidMeasurement);
            ThyroidElement thyroidElement = new ThyroidElement(substance, unit, thyroidMeasurements);
            dataHolder.getThyroidData().add(thyroidElement);
        }
        updateDataAccordingToSelectedTimePeriod();
        FileManager.saveFile("userData", new Gson().toJson(dataHolder), getApplicationContext());
    }
    @Override
    public void applySymptomTexts(int registeredValue, String symptom)
    {
        Measurement measurement = new Measurement(new Date(), registeredValue);
        for(int i = 0; i < dataHolder.getSymptomData().size(); i++)
        {
            if(dataHolder.getSymptomData().get(i).getSymptomName().equals(symptom))
            {
                dataHolder.getSymptomData().get(i).getMeasurements().add(measurement);
            }
        }
        updateDataAccordingToSelectedTimePeriod();
        FileManager.saveFile("userData", new Gson().toJson(dataHolder), getApplicationContext());
    }
    @Override
    public void refreshSymptomList()
    {
        symptomDialog.setSpinnerItems();
        updateDataAccordingToSelectedTimePeriod();
        FileManager.saveFile("userData", new Gson().toJson(dataHolder), getApplicationContext());
    }
    @Override
    public void refreshSupplementList()
    {
        intakeDialog.setSpinnerItems();
        updateDataAccordingToSelectedTimePeriod();
        FileManager.saveFile("userData", new Gson().toJson(dataHolder), getApplicationContext());
    }
    @Override
    public void applyRegisteredIntake(String registeredValue, String substance)
    {
        Measurement measurement = new Measurement(new Date(), Float.valueOf(registeredValue));
        for(int i = 0; i < dataHolder.getIntakeData().size(); i++)
        {
            if(dataHolder.getIntakeData().get(i).getNameOfSubstance().equals(substance))
            {
                dataHolder.getIntakeData().get(i).getMeasurements().add(measurement);
            }
        }
        updateDataAccordingToSelectedTimePeriod();
        FileManager.saveFile("userData", new Gson().toJson(dataHolder), getApplicationContext());
    }
    public static DataHolder getDataHolder()
    {
        return dataHolder;
    }

    /*
    This method overwrites the interface from the DeleteThyroidDataPointDialog and processes its data
     */
    @Override
    public void refreshThyroidGraph(Series series, double dataPoint, String substance)
    {
        LineGraphSeries lineGraphSeries = ((LineGraphSeries<DataPoint>)series);
        lineGraphSeries.resetData(getDataHolder().getThyroidDataPointsForSubstance(substance));
        FileManager.saveFile("userData", new Gson().toJson(dataHolder), getApplicationContext());
    }

    @Override
    public void refreshSymptomGraph(Series series, double dataPoint, String symptom)
    {
        LineGraphSeries lineGraphSeries = ((LineGraphSeries<DataPoint>)series);
        lineGraphSeries.resetData(getDataHolder().getDataPointsForSymptom(symptom));
        FileManager.saveFile("userData", new Gson().toJson(dataHolder), getApplicationContext());
    }

    @Override
    public void refreshIntakeGraph(Series series, double dataPoint, String substance)
    {
        LineGraphSeries lineGraphSeries = ((LineGraphSeries<DataPoint>)series);
        lineGraphSeries.resetData(getDataHolder().getIntakeDataPointsForSubstance(substance));
        FileManager.saveFile("userData", new Gson().toJson(dataHolder), getApplicationContext());
    }
}