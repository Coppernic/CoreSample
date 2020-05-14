package fr.coppernic.samples.core.ui.screen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.KeyEvent
import com.askey.mapping.model.IntentType
import fr.coppernic.samples.core.R
import fr.coppernic.samples.core.ui.ApiMappingFragment
import fr.coppernic.samples.core.ui.trigger.TriggerAdapter
import fr.coppernic.samples.core.ui.trigger.TriggerContent
import fr.coppernic.samples.core.ui.trigger.TriggerItem
import fr.coppernic.sdk.mapping.Mapper
import kotlinx.android.synthetic.main.activity_trigger.*

class TriggerActivity : AppCompatActivity() {

    private val TAG = "TriggerActivity"

    private lateinit var viewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
    lateinit var mapper: Mapper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trigger)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initializeRecyclerView()

        val d = Mapper.Factory
                .getKeyMapperSingle(applicationContext)
                .subscribe({
                    mapper = it
                }, {
                    Log.e(TAG, it.message)
                })
    }

    private fun initializeRecyclerView() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = TriggerAdapter(TriggerContent(applicationContext).items, object :
                TriggerAdapter
                .OnTriggerAdapterListener {
            override fun onTriggerChosen(item: TriggerItem) {
                when (intent.getStringExtra(ApiMappingFragment.TRIGGER)) {
                    ApiMappingFragment.P1 -> {
                        item.let {
                            mapper.mapIntent(Mapper.ProgKey.P1,
                                    it.intentDown, KeyEvent.ACTION_DOWN, IntentType.BROADCAST, it.label
                                    .toString())
                            mapper.mapIntent(Mapper.ProgKey.P1,
                                    it.intentUp, KeyEvent.ACTION_UP, IntentType.BROADCAST, it.label
                                    .toString())
                        }
                        onBackPressed()
                    }
                    ApiMappingFragment.P2 -> {
                        item.let {
                            mapper.mapIntent(Mapper.ProgKey.P2,
                                    it.intentDown, KeyEvent.ACTION_DOWN, IntentType.BROADCAST, it.label
                                    .toString())
                            mapper.mapIntent(Mapper.ProgKey.P2,
                                    it.intentUp, KeyEvent.ACTION_UP, IntentType.BROADCAST, it.label
                                    .toString())
                        }
                        onBackPressed()
                    }
                    ApiMappingFragment.P3 -> {
                        item.let {
                            mapper.mapIntent(Mapper.ProgKey.P3,
                                    it.intentDown, KeyEvent.ACTION_DOWN, IntentType.BROADCAST, it.label
                                    .toString())
                            mapper.mapIntent(Mapper.ProgKey.P3,
                                    it.intentUp, KeyEvent.ACTION_UP, IntentType.BROADCAST, it.label
                                    .toString())
                        }
                        onBackPressed()
                    }
                }
            }
        })
        rv_trigger.apply {
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
