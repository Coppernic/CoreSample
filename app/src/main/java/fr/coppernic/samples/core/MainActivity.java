package fr.coppernic.samples.core;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.coppernic.samples.core.ui.ApiPowerFragment;
import fr.coppernic.samples.core.ui.ApiPowerMgmtFragment;
import fr.coppernic.samples.core.ui.HdkCiziFragment;
import fr.coppernic.samples.core.ui.HdkConeFragment;
import fr.coppernic.sdk.utils.helpers.CpcOs;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

    private static final String KEY_LAST_TAG = "last_tag";
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    private String lastTag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        restoreState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_LAST_TAG, lastTag);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        restoreState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (lastTag.isEmpty()) {
            if (CpcOs.isCone()) {
                displayFragment(getFragmentFromTag(HdkConeFragment.TAG), HdkConeFragment.TAG);
            } else if (CpcOs.isCizi()) {
                displayFragment(getFragmentFromTag(HdkCiziFragment.TAG), HdkCiziFragment.TAG);
            }
        } else {
            displayFragment(getFragmentFromTag(lastTag), lastTag);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_hdk_cone) {
            displayFragment(getFragmentFromTag(HdkConeFragment.TAG), HdkConeFragment.TAG);
        } else if (id == R.id.nav_hdk_cizi) {
            displayFragment(getFragmentFromTag(HdkCiziFragment.TAG), HdkCiziFragment.TAG);
        } else if (id == R.id.nav_power) {
            displayFragment(getFragmentFromTag(ApiPowerFragment.TAG), ApiPowerFragment.TAG);
        } else if (id == R.id.nav_powermgmt) {
            displayFragment(getFragmentFromTag(ApiPowerMgmtFragment.TAG), ApiPowerMgmtFragment.TAG);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            lastTag = savedInstanceState.getString(KEY_LAST_TAG, "");
        }
    }

    private Fragment getFragmentFromTag(String tag) {
        Fragment f = getSupportFragmentManager().findFragmentByTag(tag);
        if (f == null) {
            f = createFragmentFromTag(tag);
        }
        return f;
    }

    private Fragment createFragmentFromTag(String tag) {
        Fragment ret;
        switch (tag) {
            case HdkConeFragment.TAG:
                ret = new HdkConeFragment();
                break;
            case HdkCiziFragment.TAG:
                ret = new HdkCiziFragment();
                break;
            case ApiPowerFragment.TAG:
                ret = new ApiPowerFragment();
                break;
            case ApiPowerMgmtFragment.TAG:
                ret = new ApiPowerMgmtFragment();
                break;
            default:
                ret = null;
        }
        return ret;
    }

    private void displayFragment(Fragment f, String tag) {
        lastTag = tag;
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.container, f, tag)
            .commit();
    }
}
