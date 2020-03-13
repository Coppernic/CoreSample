package fr.coppernic.samples.core.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.coppernic.samples.core.R
import fr.coppernic.samples.core.ui.key.KeyContent
import fr.coppernic.samples.core.ui.key.KeyItem
import fr.coppernic.samples.core.ui.screen.KeyActivity
import fr.coppernic.samples.core.ui.screen.ShortcutActivity
import fr.coppernic.sdk.mapping.Mapper
import fr.coppernic.sdk.mapping.cone2.MapperImpl
import kotlinx.android.synthetic.main.fragment_api_mapping.*

/**
 * A simple [Fragment] subclass.
 */
class ApiMappingFragment : Fragment() {

    private val TAG = "LoadProgKey"

    lateinit var mapper: Mapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        loadMapper()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_api_mapping, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadMapper()
    }

    private fun loadMapper() {
        MapperImpl.Manager.get().getConnector(context).subscribe({
            mapper = it
            refresh()
        }, {
            throw it
        })
    }

    private fun loadFromMapper(progKey: Mapper.ProgKey) {
        val type = mapper.getMappingType(progKey)
        lateinit var actionName: String

        when (type) {
            Mapper.MappingType.KEY -> actionName = loadKey(mapper.getKeyMapping(progKey))
            Mapper.MappingType.SHORTCUT -> actionName = mapper.getShortcutMapping(progKey)
            else -> Log.d(TAG, "Not supported yet")
        }

        if (actionName == null) {
            actionName = ""
        }

        when (progKey) {
            Mapper.ProgKey.P1 -> tv_p1.text = "${progKey.name} - ${type.name} - ${actionName}"
            Mapper.ProgKey.P2 -> tv_p2.text = "${progKey.name} - ${type.name} - ${actionName}"
            Mapper.ProgKey.P3 -> tv_p3.text = "${progKey.name} - ${type.name} - ${actionName}"
        }
    }

    private fun loadKey(key: Int): String {
        val keyContent = KeyContent()
        val item = keyContent.itemMap.get(key, KeyItem.NONE)
        return item.name
    }

    private fun loadScreen() {

        btnKey1.setOnClickListener {
            val intent = Intent(context, KeyActivity::class.java)
            intent.putExtra(KEY, P1)
            startActivity(intent)
        }
        btnShortcut1.setOnClickListener {
            val intent = Intent(context, ShortcutActivity::class.java)
            intent.putExtra(SHORTCUT, P1)
            startActivity(intent)
        }
        img_btn_deleteP1.setOnClickListener {
            mapper.remove(Mapper.ProgKey.P1)
            refresh()
        }


        btnKey2.setOnClickListener {
            val intent = Intent(context, KeyActivity::class.java)
            intent.putExtra(KEY, P2)
            startActivity(intent)
        }
        btnShortcut2.setOnClickListener {
            val intent = Intent(context, ShortcutActivity::class.java)
            intent.putExtra(SHORTCUT, P2)
            startActivity(intent)
        }
        img_btn_deleteP2.setOnClickListener {
            mapper.remove(Mapper.ProgKey.P2)
            refresh()
        }


        btnKey3.setOnClickListener {
            val intent = Intent(context, KeyActivity::class.java)
            intent.putExtra(KEY, P3)
            startActivity(intent)
        }
        btnShortcut3.setOnClickListener {
            val intent = Intent(context, ShortcutActivity::class.java)
            intent.putExtra(SHORTCUT, P3)
            startActivity(intent)
        }
        img_btn_deleteP3.setOnClickListener {
            mapper.remove(Mapper.ProgKey.P3)
            refresh()
        }
    }

    private fun refresh() {
        loadFromMapper(Mapper.ProgKey.P1)
        loadFromMapper(Mapper.ProgKey.P2)
        loadFromMapper(Mapper.ProgKey.P3)
        loadScreen()
    }

    companion object {
        const val KEY = "key"
        const val SHORTCUT = "shortcut"
        const val P1 = "P1"
        const val P2 = "P2"
        const val P3 = "P3"
    }
}
