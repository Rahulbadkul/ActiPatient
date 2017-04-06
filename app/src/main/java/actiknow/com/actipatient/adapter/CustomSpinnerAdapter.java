package actiknow.com.actipatient.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import actiknow.com.actipatient.R;
import actiknow.com.actipatient.model.SurveyType;
import actiknow.com.actipatient.utils.Utils;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    SurveyType surveyType = null;
    LayoutInflater inflater;
    TextView tvSurveyTypeName;
    TextView tvSurveyTypeID;
    Activity activity;
    private List data;

    public CustomSpinnerAdapter (Activity activity, int textViewResourceId, List objects) {
        super (activity, textViewResourceId, objects);
        this.activity = activity;
        data = objects;
        inflater = (LayoutInflater) activity.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView (int position, View convertView, ViewGroup parent) {
        return getCustomView (position, convertView, parent);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        return getCustomView (position, convertView, parent);
    }

    public View getCustomView (int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate (R.layout.spinner_item, parent, false);
        surveyType = (SurveyType) data.get (position);
        tvSurveyTypeName = (TextView) row.findViewById (R.id.tvSurveyTypeName);
        tvSurveyTypeID = (TextView) row.findViewById (R.id.tvSurveyTypeID);
        Utils.setTypefaceToAllViews (activity, tvSurveyTypeID);
        tvSurveyTypeName.setText (surveyType.getSurvey_type_text ());
        tvSurveyTypeID.setText ("" + surveyType.getSurvey_type_id ());
        return row;
    }
}