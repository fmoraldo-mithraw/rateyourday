package com.mithraw.howwasyourday.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.mithraw.howwasyourday.R;

public class DiagramActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagram);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.vertical_progressbar);
        progressBar.setProgress(25);
    }
}
