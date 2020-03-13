package fr.coppernic.samples.core.ui.screen

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import fr.coppernic.samples.core.R
import fr.coppernic.samples.core.ui.ApiMappingFragment
import fr.coppernic.samples.core.ui.key.KeyAdapter
import fr.coppernic.samples.core.ui.key.KeyContent
import fr.coppernic.samples.core.ui.key.KeyItem
import fr.coppernic.sdk.mapping.Mapper
import fr.coppernic.sdk.mapping.cone2.MapperImpl
import kotlinx.android.synthetic.main.activity_key.*

class KeyActivity : AppCompatActivity() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    lateinit var mapper: Mapper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_key)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initializeRecyclerView()
        MapperImpl.Manager.get().getConnector(applicationContext).subscribe({
            mapper = it
        }, {
            throw it
        })
    }

    private fun initializeRecyclerView() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = KeyAdapter(KeyContent().items.sortedBy { it.name }, object : KeyAdapter.OnKeyAdapterListener {
            override fun onKeyChosen(item: KeyItem) {
                when (intent.getStringExtra(ApiMappingFragment.KEY)) {
                    ApiMappingFragment.P1 -> {
                        mapper.mapKey(Mapper.ProgKey.P1, item.code)
                        onBackPressed()
                    }
                    ApiMappingFragment.P2 -> {
                        mapper.mapKey(Mapper.ProgKey.P2, item.code)
                        onBackPressed()
                    }
                    ApiMappingFragment.P3 -> {
                        mapper.mapKey(Mapper.ProgKey.P3, item.code)
                        onBackPressed()
                    }
                }
            }
        })
        rv_key.apply {
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
