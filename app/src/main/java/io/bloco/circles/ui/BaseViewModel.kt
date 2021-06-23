package io.bloco.circles.ui

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.plus

abstract class BaseViewModel : ViewModel(), LifecycleObserver {
    val ioScope = viewModelScope + Dispatchers.IO
}
