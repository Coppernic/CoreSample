package fr.coppernic.samples.core.ui.common

import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass

class ViewBindingHolder<T : ViewBinding>(
    private val clazz: KClass<T>
) : ViewBindingEnabled<T> {

    private var binding: T? = null

    override val viewBinding: T
        get() {
            return binding ?: throw Exception("Inflate should be called first")
        }

    override fun inflate(inflater: LayoutInflater): View {
        val m = clazz.members.first {
            it.name == "inflate" && it.parameters.size == 1
        }
        binding = m.call(inflater) as T
        return binding!!.root
    }

    override fun deflate() {
        binding = null
    }
}
