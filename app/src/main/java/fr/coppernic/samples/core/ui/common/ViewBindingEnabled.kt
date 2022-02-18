package fr.coppernic.samples.core.ui.common

import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding

interface ViewBindingEnabled<T : ViewBinding> {
    val viewBinding: T

    fun inflate(inflater: LayoutInflater): View

    fun deflate()
}
