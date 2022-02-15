package fr.coppernic.samples.core.ui.screen

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import fr.coppernic.samples.core.R
import fr.coppernic.samples.core.databinding.ActivityKeyBinding
import fr.coppernic.samples.core.ui.ApiMappingFragment
import fr.coppernic.samples.core.ui.key.KeyAdapter
import fr.coppernic.samples.core.ui.key.KeyContent
import fr.coppernic.samples.core.ui.key.KeyItem
import fr.coppernic.sdk.mapping.Mapper

class KeyActivity : AppCompatActivity() {

    private val TAG = "KeyActivity"

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    lateinit var mapper: Mapper

    private lateinit var binding: ActivityKeyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKeyBinding.inflate(layoutInflater)
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
        viewManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        viewAdapter = KeyAdapter(
            KeyContent(applicationContext).items.sortedBy { it.name },
            object : KeyAdapter
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
            }
        )
        binding.rvKey.apply {
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
