package com.scheduler;

import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PrefsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		
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
	
	public static void updateLanguage(Context context, String idioma) {
	    if (idioma != null && !"".equals(idioma)) {
	        Locale locale = new Locale(idioma);
	        Locale.setDefault(locale);
	        Configuration config = new Configuration();
	        config.locale = locale;
	        context.getResources().updateConfiguration(config, null);
	    }
	}
	
	@Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		
        if (key.equals("language")) {
        	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    		String language = sharedPrefs.getString("language", "en_EN");
        	updateLanguage(this, language);
            restartActivity();
        }
    }
	
	@Override
	protected void onPause() {
	    super.onPause();
	    // Unregister the listener whenever a key changes
	    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
		
	private void restartActivity() {
	    Intent intent = getIntent();
	    finish();
	    startActivity(intent);
	}
}