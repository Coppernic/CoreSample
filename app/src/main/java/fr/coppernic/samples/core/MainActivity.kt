package fr.coppernic.samples.core

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import fr.coppernic.samples.core.databinding.ActivityMainBinding
import fr.coppernic.samples.core.databinding.AppBarMainBinding
import fr.coppernic.samples.core.ui.*
import fr.coppernic.sdk.utils.helpers.OsHelper
import timber.log.Timber

private const val KEY_LAST_TAG = "last_tag"

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var lastTag = ""

    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingToolBar: AppBarMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        bindingToolBar = AppBarMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        Timber.v("onCreate")

        setSupportActionBar(bindingToolBar.toolbar)

        val toggle = ActionBarDrawerToggle(
            this, binding.drawer, bindingToolBar.toolbar, R.string.navigation_drawer_open,
            R.string
                .navigation_drawer_close
        )
        binding.drawer.addDrawerListener(toggle)
        toggle.syncState()

        binding.nav.setNavigationItemSelectedListener(this)
        binding.nav.getHeaderView(0).findViewById<TextView>(R.id.tvVersion).text = getString(
            R.string.version,
            BuildConfig.VERSION_NAME + if (BuildConfig.DEBUG) " debug" else ""
        )

        restoreState(savedInstanceState)

        Timber.v("end onCreate")
    }

    override fun onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START)
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
                OsHelper.isAccess() -> displayFragment(HdkAccessFragment::class.java.name)
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
            R.id.nav_hdk_access -> displayFragment(HdkAccessFragment::class.java.name)
            R.id.nav_hdk_cizi -> displayFragment(HdkCiziFragment::class.java.name)
            R.id.nav_power -> displayFragment(ApiPowerFragment::class.java.name)
            R.id.nav_powermgmt -> displayFragment(ApiPowerMgmtFragment::class.java.name)
            R.id.nav_net -> displayFragment(NetFragment::class.java.name)
            R.id.nav_mapping -> displayFragment(ApiMappingFragment::class.java.name)
        }

        binding.drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            lastTag = savedInstanceState.getString(KEY_LAST_TAG, "")
        }
    }

    private fun getFragmentFromTag(tag: String): androidx.fragment.app.Fragment {
        var f: androidx.fragment.app.Fragment? = supportFragmentManager.findFragmentByTag(tag)
        if (f == null) {
            f = createFragmentFromTag(tag)
        }
        return f
    }

    private fun createFragmentFromTag(tag: String): androidx.fragment.app.Fragment {
        return when (tag) {
            HdkConeFragment::class.java.name -> HdkConeFragment()
            HdkCiziFragment::class.java.name -> HdkCiziFragment()
            ApiPowerFragment::class.java.name -> ApiPowerFragment()
            ApiPowerMgmtFragment::class.java.name -> ApiPowerMgmtFragment()
            HdkCone2Fragment::class.java.name -> HdkCone2Fragment()
            HdkAccessFragment::class.java.name -> HdkAccessFragment()
            NetFragment::class.java.name -> NetFragment()
            ApiMappingFragment::class.java.name -> ApiMappingFragment()
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
