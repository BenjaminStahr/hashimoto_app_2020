package com.example.hashimoto_app.backend;

import java.util.ArrayList;

public class DataHolder
{
    private ArrayList<ThyroidElement> thyroidData;
    private ArrayList<SymptomElement> symptomData;
    private ArrayList<IntakeElement> intakeData;

    public DataHolder()
    {
        thyroidData = new ArrayList<>();
        symptomData = new ArrayList<>();
        intakeData = new ArrayList<>();
    }
    public ArrayList<String> getSymptoms()
    {
        ArrayList<String> symptoms = new ArrayList<>();
        for(int i = 0; i < symptomData.size(); i++)
        {
            symptoms.add(symptomData.get(i).getSymptomName());
        }
        return symptoms;
    }
    public ArrayList<String> getSupplements()
    {
        ArrayList<String> supplements = new ArrayList<>();
        for(int i = 0; i < intakeData.size(); i++)
        {
            supplements.add(intakeData.get(i).getNameOfSubstance());
        }
        return supplements;
    }
    public String getUnitOfSupplement(String substance)
    {
        for (int i = 0; i < intakeData.size(); i++)
        {
            if(intakeData.get(i).getNameOfSubstance().equals(substance))
            {
                return intakeData.get(i).getUnit();
            }
        }
        return "mg";
    }
    public void addSymptom(String symptomName)
    {
        SymptomElement element = new SymptomElement(symptomName, new ArrayList<Measurement>());
        symptomData.add(element);
    }
    public void addSupplement(String supplementName, String unit)
    {
        IntakeElement element = new IntakeElement(supplementName, unit, new ArrayList<Measurement>());
        intakeData.add(element);
    }


    public ArrayList<ThyroidElement> getThyroidData()
    {
        return thyroidData;
    }
    public ArrayList<SymptomElement> getSymptomData()
    {
        return symptomData;
    }
    public ArrayList<IntakeElement> getIntakeData()
    {
        return intakeData;
    }
}
