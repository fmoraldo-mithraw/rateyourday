package com.mithraw.howwasyourday.Activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.MenuItem;

import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.Helpers.NotificationHelper;
import com.mithraw.howwasyourday.Helpers.PreferenceHelper;
import com.mithraw.howwasyourday.Helpers.ThreadSyncDatas;
import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.Tools.Hour;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            if(preference.getKey().equals("sync_frequency")){
                if(!value.equals(PreferenceManager.getDefaultSharedPreferences(App.getContext()).getString("sync_frequency","180")))
                    ThreadSyncDatas.reSchedule(PreferenceHelper.toTime((String)value));
            }

            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    private static String getRingtoneSummary(String value) {
        String ret;
        if (TextUtils.isEmpty(value)) {
            Resources res = App.getApplication().getResources();
            ret = res.getString(R.string.pref_ringtone_silent);

        } else {
            Ringtone ringtone = RingtoneManager.getRingtone(
                    App.getApplication().getApplicationContext(), Uri.parse(value));

            if (ringtone == null) {
                // Clear the summary if there was a lookup error.
                ret = null;
            } else {
                // Set the summary to reflect the new ringtone display
                // name.
                String name = ringtone.getTitle(App.getApplication().getApplicationContext());
                ret = name;
            }
        }
        return ret;
    }

    private static String formatLocaleTime(String stringValue) {
        //Format the date depending on the time type (24h/am/pm)
        String summaryStringValue = stringValue;
        if (!DateFormat.is24HourFormat(App.getContext())) {
            Hour hour = new Hour(stringValue);
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, hour.getIntHour());
            cal.set(Calendar.MINUTE, hour.getIntMinute());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aaa");
            summaryStringValue = simpleDateFormat.format(new java.util.Date(cal.getTimeInMillis()));
        }
        return summaryStringValue;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);
            findPreference("notify_time").setSummary(formatLocaleTime(PreferenceManager.getDefaultSharedPreferences(findPreference("notify_time").getContext()).getString("notify_time", "21:00")));
            findPreference("notifications_new_message_ringtone").setSummary(getRingtoneSummary(PreferenceManager.getDefaultSharedPreferences(findPreference("notifications_new_message_ringtone").getContext()).getString("notifications_new_message_ringtone", "None")));
            findPreference("use_notifications").setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object value) {
                            NotificationHelper.updateNotificationStatus((boolean) value,
                                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString("notify_time", "21:00"),
                                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString("notifications_new_message_ringtone", "None"),
                                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getBoolean("notifications_new_message_vibrate", false));
                            return true;
                        }
                    }
            );
            findPreference("notifications_new_message_ringtone").setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object value) {
                            String stringValue = value.toString();
                            preference.setSummary(getRingtoneSummary(stringValue));
                            NotificationHelper.updateNotificationStatus(
                                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getBoolean("use_notifications", true),
                                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString("notify_time", "21:00"),
                                    stringValue,
                                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getBoolean("notifications_new_message_vibrate", false));
                            return true;
                        }
                    }
            );
            findPreference("notifications_new_message_vibrate").setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object value) {
                            NotificationHelper.updateNotificationStatus(
                                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getBoolean("use_notifications", true),
                                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString("notify_time", "21:00"),
                                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString("notifications_new_message_ringtone", "None"),
                                    (boolean) value);
                            return true;
                        }
                    }
            );
            findPreference("notify_time").setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object value) {
                            String stringValue = value.toString();
                            // Validation
                            if (preference.getKey().equals("notify_time")) {
                                if (!Hour.isHour(stringValue)) {
                                    return false;
                                }
                            }
                            //Format the date depending on the time type (24h/am/pm)
                            String summaryStringValue = formatLocaleTime(stringValue);


                            preference.setSummary(summaryStringValue);
                            NotificationHelper.updateNotificationStatus(
                                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getBoolean("use_notifications", true),
                                    stringValue,
                                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString("notifications_new_message_ringtone", "None"),
                                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getBoolean("notifications_new_message_vibrate", false));
                            return true;
                        }
                    }
            );
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
