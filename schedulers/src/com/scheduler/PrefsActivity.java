package com.scheduler;

import android.app.Dialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PrefsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
		
		Preference dialogPreference = (Preference) getPreferenceScreen().findPreference("dialog_preference_about");
		dialogPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		        public boolean onPreferenceClick(Preference preference) {
		        	
		        	// Create a Dialog component
					final Dialog dialog = new Dialog(preference.getContext());
					dialog.setContentView(R.layout.prefs_dialog);
					dialog.setTitle(getApplicationContext().getResources().getString(R.string.pref_dialog_title));
					Button dialogButton = (Button) dialog.findViewById(R.id.dialogButton);
					dialogButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});

					dialog.show();
		            return true;
		        }
		    });
		
	}
}