package fr.coppernic.samples.core.ui.screen

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.askey.mapping.model.IntentType
import fr.coppernic.samples.core.R
import fr.coppernic.samples.core.databinding.ActivityShortcutBinding
import fr.coppernic.samples.core.ui.ApiMappingFragment
import fr.coppernic.samples.core.ui.shortcut.ShortcutAdapter
import fr.coppernic.samples.core.ui.shortcut.ShortcutContent
import fr.coppernic.samples.core.ui.shortcut.ShortcutItem
import fr.coppernic.sdk.mapping.Mapper

class ShortcutActivity : AppCompatActivity() {

    private val TAG = "ShortcutActivity"

    private lateinit var viewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
    lateinit var mapper: Mapper

    private lateinit var binding: ActivityShortcutBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShortcutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initializeRecyclerView()

        val d = Mapper.Factory
            .getKeyMapperSingle(applicationContext)
            .subscribe(
                {
                    mapper = it
                },
                {
                    it.message?.let { it1 -> Log.e(TAG, it1) }
                }
            )
    }

    private fun initializeRecyclerView() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = ShortcutAdapter(
            ShortcutContent(applicationContext).items,
            object : ShortcutAdapter.OnShortcutAdapterListener {
                override fun onShortcutChosen(item: ShortcutItem) {
                    when (intent.getStringExtra(ApiMappingFragment.SHORTCUT)) {
                        ApiMappingFragment.P1 -> {
                            item.let {
                                mapper.mapIntent(
                                    Mapper.ProgKey.P1,
                                    it.launchIntent, KeyEvent.ACTION_DOWN, IntentType.ACTIVITY,
                                    it
                                        .label
                                        .toString()
                                )
                            }
                            onBackPressed()
                        }
                        ApiMappingFragment.P2 -> {
                            item.let {
                                mapper.mapIntent(
                                    Mapper.ProgKey.P2,
                                    it.launchIntent, KeyEvent.ACTION_DOWN, IntentType.ACTIVITY,
                                    it
                                        .label
                                        .toString()
                                )
                            }
                            onBackPressed()
                        }
                        ApiMappingFragment.P3 -> {
                            item.let {
                                mapper.mapIntent(
                                    Mapper.ProgKey.P3,
                                    it.launchIntent, KeyEvent.ACTION_DOWN, IntentType.ACTIVITY,
                                    it
                                        .label
                                        .toString()
                                )
                            }
                            onBackPressed()
                        }
                    }
                }
            }
        )
       with(binding.rvShortcut) {
            setHasFixedSize(true)
            layoutManager = viewManager
            addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            adapter = viewAdapter
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
