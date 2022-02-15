package fr.coppernic.samples.core.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.askey.mapping.utils.IconUtils
import fr.coppernic.samples.core.R
import fr.coppernic.samples.core.databinding.FragmentApiMappingBinding
import fr.coppernic.samples.core.ui.key.KeyContent
import fr.coppernic.samples.core.ui.key.KeyItem
import fr.coppernic.samples.core.ui.screen.KeyActivity
import fr.coppernic.samples.core.ui.screen.ShortcutActivity
import fr.coppernic.samples.core.ui.screen.TriggerActivity
import fr.coppernic.sdk.mapping.Mapper
import fr.coppernic.sdk.mapping.utils.MapperUtils

/**
 * A simple [Fragment] subclass.
 */
class ApiMappingFragment : androidx.fragment.app.Fragment() {

    private val TAG = "LoadProgKey"

    lateinit var mapper: Mapper

    private var _binding: FragmentApiMappingBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        loadMapper()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentApiMappingBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun loadMapper() {
        val d = Mapper.Factory
            .getKeyMapperSingle(context)
            .subscribe(
                {
                    mapper = it
                    refresh()
                },
                {
                    it.message?.let { it1 -> Log.e(TAG, it1) }
                }
            )
    }

    private fun loadFromMapper(progKey: Mapper.ProgKey) {
        val type = mapper.getMappingType(progKey)
        var actionName = ""

        when (type) {
            Mapper.MappingType.KEY -> actionName = loadKey(mapper.getKeyMapping(progKey))
            Mapper.MappingType.SHORTCUT -> actionName = mapper.getShortcutMapping(progKey)
            else -> Log.d(TAG, "Not supported yet")
        }

        binding.apply {
            when (progKey) {
                Mapper.ProgKey.P1 -> {
                    tvP1.text = getString(R.string.mapping_display, progKey.name, actionName)
                    loadIcon(binding.img1, actionName)
                }
                Mapper.ProgKey.P2 -> {
                    tvP2.text = getString(R.string.mapping_display, progKey.name, actionName)
                    loadIcon(img2, actionName)
                }
                Mapper.ProgKey.P3 -> {
                    tvP3.text = getString(R.string.mapping_display, progKey.name, actionName)
                    loadIcon(img3, actionName)
                }
            }
        }
    }

    private fun loadIcon(img: ImageView, app: String) {
        context?.let {
            val uri: String?
            val pack: String?
            if (!app.contains('.')) { // not a package name
                pack = MapperUtils.fromAppNameToApplicationId(it, app)
                uri = MapperUtils.fromAppIdToIntent(it, pack)?.toUri(Intent.URI_INTENT_SCHEME)
            } else {
                uri = MapperUtils.fromAppIdToIntent(it, app)?.toUri(Intent.URI_INTENT_SCHEME)
            }
            var icon = context?.resources?.getDrawable(R.drawable.appwidget_item_bg_normal)
            icon = IconUtils.getIconFromStringUri(context, uri.toString(), icon)
            img.setImageDrawable(icon)
        }
    }

    private fun loadKey(key: Int): String {
        val keyContent = KeyContent(context)
        val item = keyContent.itemMap.get(key, KeyItem.NONE)
        return item.name
    }

    private fun loadScreen() {
        var keyPos = 1
        listOf<Button>(binding.btnKey1, binding.btnKey2, binding.btnKey3).forEach {
            val pNameKey = "P${keyPos++}"
            it.setOnClickListener {
                val intent = Intent(context, KeyActivity::class.java)
                intent.putExtra(KEY, pNameKey)
                startActivity(intent)
            }
        }

        var shortPos = 1
        listOf<Button>(binding.btnShortcut1, binding.btnShortcut2, binding.btnShortcut3).forEach {
            val pNameShort = "P${shortPos++}"
            it.setOnClickListener {
                val intent = Intent(context, ShortcutActivity::class.java)
                intent.putExtra(SHORTCUT, pNameShort)
                startActivity(intent)
            }
        }

        var trigPos = 1
        listOf<Button>(binding.btnTrigger1, binding.btnTrigger2, binding.btnTrigger3).forEach {
            val pNameTrig = "P${trigPos++}"
            it.setOnClickListener {
                val intent = Intent(context, TriggerActivity::class.java)
                intent.putExtra(TRIGGER, pNameTrig)
                startActivity(intent)
            }
        }

        var delPos = 1
        listOf<ImageButton>(binding.imgBtnDeleteP1, binding.imgBtnDeleteP2, binding.imgBtnDeleteP3).forEach {
            val pNameDel = "P${delPos++}"
            it.setOnClickListener {
                when (pNameDel) {
                    "P1" -> {
                        mapper.remove(Mapper.ProgKey.P1)
                        refresh()
                    }
                    "P2" -> {
                        mapper.remove(Mapper.ProgKey.P2)
                        refresh()
                    }
                    "P3" -> {
                        mapper.remove(Mapper.ProgKey.P3)
                        refresh()
                    }
                }
            }
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
        const val TRIGGER = "trigger"
        const val P1 = "P1"
        const val P2 = "P2"
        const val P3 = "P3"
    }
}
