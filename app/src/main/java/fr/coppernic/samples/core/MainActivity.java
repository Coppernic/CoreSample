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
import fr.coppernic.samples.core.ui.HdkCiziFragment;
import fr.coppernic.samples.core.ui.HdkConeFragment;
import fr.coppernic.sdk.utils.helpers.CpcOs;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

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
    protected void onStart() {
        super.onStart();
        if (CpcOs.isCone()) {
            displayFragment(getHdkConeFragment(), HdkConeFragment.TAG);
        } else if (CpcOs.isCizi()) {
            displayFragment(getHdkCiziFragment(), HdkCiziFragment.TAG);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_hdk_cone) {
            displayFragment(getHdkConeFragment(), HdkConeFragment.TAG);
        } else if (id == R.id.nav_hdk_cizi) {
            displayFragment(getHdkCiziFragment(), HdkCiziFragment.TAG);
        } else if (id == R.id.nav_power) {

        } else if (id == R.id.nav_powermgmt) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Fragment getHdkConeFragment() {
        HdkConeFragment f = (HdkConeFragment) getSupportFragmentManager()
            .findFragmentByTag(HdkConeFragment.TAG);
        if (f == null) {
            f = new HdkConeFragment();
        }
        return f;
    }

    private Fragment getHdkCiziFragment() {
        HdkCiziFragment f = (HdkCiziFragment) getSupportFragmentManager()
            .findFragmentByTag(HdkCiziFragment.TAG);
        if (f == null) {
            f = new HdkCiziFragment();
        }
        return f;
    }

    private void displayFragment(Fragment f, String tag) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.container, f, tag)
            .commit();
    }
}
