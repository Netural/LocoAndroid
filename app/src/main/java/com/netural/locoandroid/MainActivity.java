package com.netural.locoandroid;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netural.loco.base.LocoInfo;
import com.netural.loco.library.LocoContextWrapper;
import com.netural.loco.library.LocoManager;
import com.netural.loco.library.LocoUtils;

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
                                        LocoUtils.reloadTextViews((ViewGroup) findViewById(android.R.id.content));
                                    }
                                });
                    }
                });
    }

    private void updateView() {
        TextView infoText = (TextView) findViewById(R.id.info);
        LocoInfo info = LocoManager.getInstance().getInfo();
        if (info != null) {
            infoText.setText(info.toString());
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocoContextWrapper.wrap(newBase));
    }
}
