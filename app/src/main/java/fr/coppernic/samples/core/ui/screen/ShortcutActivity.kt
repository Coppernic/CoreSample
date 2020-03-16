package fr.coppernic.samples.core.ui.screen

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import fr.coppernic.samples.core.R
import fr.coppernic.samples.core.ui.ApiMappingFragment
import fr.coppernic.samples.core.ui.shortcut.ShortcutAdapter
import fr.coppernic.samples.core.ui.shortcut.ShortcutContent
import fr.coppernic.samples.core.ui.shortcut.ShortcutItem
import fr.coppernic.sdk.mapping.Mapper
import fr.coppernic.sdk.mapping.cone2.MapperImpl
import kotlinx.android.synthetic.main.activity_shortcut.*

class ShortcutActivity : AppCompatActivity() {

    private val TAG = "ShortcutActivity"

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    lateinit var mapper: Mapper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shortcut)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initializeRecyclerView()
        MapperImpl.Manager.get().getConnector(applicationContext).subscribe({
            mapper = it
        }, {
            Log.e(TAG, it.message)
        })
    }

    private fun initializeRecyclerView() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = ShortcutAdapter(ShortcutContent(applicationContext).items, object : ShortcutAdapter.OnShortcutAdapterListener {
            override fun onShortcutChosen(item: ShortcutItem) {
                when (intent.getStringExtra(ApiMappingFragment.SHORTCUT)) {
                    ApiMappingFragment.P1 -> {
                        item.launchIntent.`package`.let { mapper.mapShortcut(Mapper.ProgKey.P1, applicationContext, it) }
                        onBackPressed()
                    }
                    ApiMappingFragment.P2 -> {
                        item.launchIntent.`package`.let { mapper.mapShortcut(Mapper.ProgKey.P2, applicationContext, it) }
                        onBackPressed()
                    }
                    ApiMappingFragment.P3 -> {
                        item.launchIntent.`package`.let { mapper.mapShortcut(Mapper.ProgKey.P3, applicationContext, it) }
                        onBackPressed()
                    }
                }
            }
        })
        rv_shortcut.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            adapter = viewAdapter
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
