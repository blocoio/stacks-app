package io.bloco.circles.ui.secret.bottomsheet

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.bloco.circles.data.interfaces.SecretKeyRepository
import io.bloco.circles.ui.BaseViewModel
import io.bloco.circles.ui.shared.ClipboardUtils
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ShareKeyBottomSheetViewModel
@Inject constructor(
    secretKeyRepository: SecretKeyRepository,
    clipboardUtils: ClipboardUtils
) : BaseViewModel() {

    // Inputs
    private val copyPressed = BroadcastChannel<Unit>(1)

    // Outputs
    private val secretKey = MutableStateFlow("")
    private val copiedSuccessful = BroadcastChannel<Unit>(1)

    init {

        secretKeyRepository
            .observe()
            .take(1)
            .onEach { secretKey.emit(it) }
            .launchIn(ioScope)

        copyPressed
            .asFlow()
            .onEach {
                clipboardUtils.setText(secretKey.value)
                copiedSuccessful.send(Unit)
            }
            .launchIn(viewModelScope)

    }

    // Inputs
    suspend fun copyPressed() =
        copyPressed.send(Unit)

    // Outputs
    fun secretKey() = secretKey.asStateFlow()
    fun copiedSuccessful() = copiedSuccessful.asFlow()

}
