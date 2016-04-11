package at.qurps.noefinderlein.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class Activity_About extends AppCompatActivity {

    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        rootView = getWindow().getDecorView().getRootView();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.hide();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(R.string.title_activity_about);

        ((TextView) rootView.findViewById(R.id.about_version)).setText(BuildConfig.VERSION_NAME);
        ((LinearLayout) rootView.findViewById(R.id.about_googleplus)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = "https://plus.google.com/communities/102566277075059968592";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        ((LinearLayout) rootView.findViewById(R.id.about_github)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = "https://github.com/derqurps/noefinderlein";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });
    }
}