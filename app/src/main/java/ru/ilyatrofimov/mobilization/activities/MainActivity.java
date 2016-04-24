package ru.ilyatrofimov.mobilization.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import butterknife.Bind;
import butterknife.ButterKnife;
import ru.ilyatrofimov.mobilization.MobilizationApp;
import ru.ilyatrofimov.mobilization.R;


/**
 * @author Ilya Trofimov
 */
public class MainActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initToolbar();
        if (MobilizationApp.IS_LOLLIPOP_OR_HIGHER) {
            initTransitions();
        }
    }

    private void initToolbar() {
        mToolbar.setTitle(R.string.artists);
        setSupportActionBar(mToolbar);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initTransitions() {
        Window window = getWindow();

        window.setExitTransition(null);
        window.setReturnTransition(null);
        window.setEnterTransition(null);
    }
}
