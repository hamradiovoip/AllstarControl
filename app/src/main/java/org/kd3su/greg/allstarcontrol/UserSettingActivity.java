package org.kd3su.greg.allstarcontrol;

/**
 * Created by greg on 6/2/2016.
 */

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class UserSettingActivity extends PreferenceActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO fix deprecated method
        addPreferencesFromResource(R.xml.settings);

        // Display the fragment as the main content.
      //  getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();


    }

}//class
