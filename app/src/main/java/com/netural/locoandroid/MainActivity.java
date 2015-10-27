package com.netural.locoandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.netural.loco.library.LocoManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateView();

        findViewById(R.id.button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LocoManager.getInstance().reload(
                                new LocoManager.OnLanguageLoadedListener() {
                                    @Override
                                    public void onLanguageLoaded() {
                                        updateView();
                                    }
                                });
                    }
                });
    }

    private void updateView() {
        final TextView testText = (TextView) findViewById(R.id.text);
        testText.invalidate();

        TextView infoText = (TextView) findViewById(R.id.info);
        infoText.setText(LocoManager.getInstance().getInfo().toString());
    }
}
