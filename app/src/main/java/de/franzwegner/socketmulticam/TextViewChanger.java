package de.franzwegner.socketmulticam;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

public class TextViewChanger extends Activity {

    Activity activity;

    public TextViewChanger(Context context) {

        activity = (Activity) context;


    }

    public void changeText(final String viewName, final String newValue) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textToChange = (TextView) activity.findViewById(getResources().getIdentifier(viewName, "id", getPackageName()));
                textToChange.setText(newValue);
            }
        });
    }

}
