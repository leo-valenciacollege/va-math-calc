package com.nof.vamathcalculator;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

/**
 * This is currently incomplete. The database code will execute during the loading screen but
 * right now there is no code that stores data from the user. That will be worked on along with
 * the interface to prompt the user for disability and dependency data.
 *
 * Once that is in place the loading screen will load that data on start, and pass that data to
 * the MainActivity using intents. The MainActivity will populate its view with that data.
 *
 * As a caveat, the MainActivity will require its own db code in case the loading process is
 * not ran or interrupted for any reason. If the App is suspended and resumed the loading screen
 * may not be available to load the data in that case.
 */
public class LoadingFullscreenActivity extends Activity {

    private class QueryDB extends AsyncTask<String, Void, Double> {
        protected Double doInBackground(String... status) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                SQLiteOpenHelper db_helper = new VAMathDBHelper(getApplicationContext());
                SQLiteDatabase db = db_helper.getReadableDatabase();
                Cursor cursor = db.query(
                        VAMathDBHelper.BASIC_TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );
                if (cursor.moveToFirst()) {
                    // skip the first two columns (ID and dependency status)
                    double query_result = cursor.getDouble(2);
                    cursor.close();
                    db.close();
                    return query_result;
                } else {
                    cursor.close();
                    db.close();
                    return 0.0;
                }
            } catch (SQLiteException e) {
                return 0.0;
            }
        }

        protected void onPostExecute(Double response) {
            Intent intent_start_main_fragment
                    = new Intent(getApplicationContext(), MainActivity.class);
            intent_start_main_fragment.putExtra(TestFragment.TEST_VIEW_DATA, response);

            startActivity(intent_start_main_fragment);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_fullscreen);

        if (savedInstanceState == null) {
            new QueryDB().execute(VARates.Basic.Dependent_Status.Alone_No_Depends.getStatus());
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}