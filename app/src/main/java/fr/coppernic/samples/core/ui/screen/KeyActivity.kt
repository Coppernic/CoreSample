package fr.coppernic.samples.core.ui.screen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import fr.coppernic.samples.core.R
import fr.coppernic.samples.core.ui.ApiMappingFragment
import fr.coppernic.samples.core.ui.key.KeyAdapter
import fr.coppernic.samples.core.ui.key.KeyContent
import fr.coppernic.samples.core.ui.key.KeyItem
import fr.coppernic.sdk.mapping.Mapper
import kotlinx.android.synthetic.main.activity_key.*

class KeyActivity : AppCompatActivity() {

    private val TAG = "KeyActivity"

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    lateinit var mapper: Mapper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_key)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initializeRecyclerView()

        Mapper.Factory
                .getKeyMapperSingle(applicationContext)
                .subscribe({
                    mapper = it
                }, {
                    Log.e(TAG, it.message)
                })
    }

    private fun initializeRecyclerView() {
        viewManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        viewAdapter = KeyAdapter(KeyContent(applicationContext).items.sortedBy { it.name }, object : KeyAdapter
        .OnKeyAdapterListener {
            override fun onKeyChosen(item: KeyItem) {
                when (intent.getStringExtra(ApiMappingFragment.KEY)) {
                    "P1" -> {
                        mapper.mapKey(Mapper.ProgKey.P1, item.code)
                        onBackPressed()
                    }
                    "P2" -> {
                        mapper.mapKey(Mapper.ProgKey.P2, item.code)
                        onBackPressed()
                    }
                    "P3" -> {
                        mapper.mapKey(Mapper.ProgKey.P3, item.code)
                        onBackPressed()
                    }
                }
            }
        })
        rv_key.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL))
            adapter = viewAdapter
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
