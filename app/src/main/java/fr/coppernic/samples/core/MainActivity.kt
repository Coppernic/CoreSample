package fr.coppernic.samples.core

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import fr.coppernic.samples.core.ui.*
import fr.coppernic.sdk.utils.helpers.OsHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

private const val KEY_LAST_TAG = "last_tag"

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private var lastTag = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        nav.setNavigationItemSelectedListener(this)
        nav.getHeaderView(0).tvVersion.text = getString(R.string.version,
                BuildConfig.VERSION_NAME + if (BuildConfig.DEBUG) " debug" else "")

        restoreState(savedInstanceState)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_LAST_TAG, lastTag)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        restoreState(savedInstanceState)
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        if (lastTag.isEmpty()) {
            when {
                OsHelper.isConeV2() -> displayFragment(HdkCone2Fragment::class.java.name)
                OsHelper.isCone() -> displayFragment(HdkConeFragment::class.java.name)
                OsHelper.isCizi() -> displayFragment(HdkCiziFragment::class.java.name)
            }
        } else {
            displayFragment(lastTag)
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        when (item.itemId) {
            R.id.nav_hdk_cone -> displayFragment(HdkConeFragment::class.java.name)
            R.id.nav_hdk_cone_2 -> displayFragment(HdkCone2Fragment::class.java.name)
            R.id.nav_hdk_cizi -> displayFragment(HdkCiziFragment::class.java.name)
            R.id.nav_power -> displayFragment(ApiPowerFragment::class.java.name)
            R.id.nav_powermgmt -> displayFragment(ApiPowerMgmtFragment::class.java.name)
            R.id.nav_net -> displayFragment(NetFragment::class.java.name)
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            lastTag = savedInstanceState.getString(KEY_LAST_TAG, "")
        }
    }

    private fun getFragmentFromTag(tag: String): Fragment {
        var f: Fragment? = supportFragmentManager.findFragmentByTag(tag)
        if (f == null) {
            f = createFragmentFromTag(tag)
        }
        return f
    }

    private fun createFragmentFromTag(tag: String): Fragment {
        return when (tag) {
            HdkConeFragment::class.java.name -> HdkConeFragment()
            HdkCiziFragment::class.java.name -> HdkCiziFragment()
            ApiPowerFragment::class.java.name -> ApiPowerFragment()
            ApiPowerMgmtFragment::class.java.name -> ApiPowerMgmtFragment()
            HdkCone2Fragment::class.java.name -> HdkCone2Fragment()
            NetFragment::class.java.name -> NetFragment()
            else -> HdkConeFragment()
        }
    }

    private fun displayFragment(tag: String) {
        lastTag = tag
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, getFragmentFromTag(lastTag), lastTag)
                .commit()
    }
}
