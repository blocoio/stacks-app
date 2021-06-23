package io.bloco.circles.ui.secret

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.bloco.circles.data.interfaces.SecretKeyRepository
import io.bloco.circles.domain.GenerateSecretKey
import io.bloco.circles.ui.BaseViewModel
import io.bloco.circles.ui.shared.ClipboardUtils
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SecretKeyViewModel
@Inject constructor(
    secretKeyRepository: SecretKeyRepository,
    generateSecretKey: GenerateSecretKey,
    clipboardUtils: ClipboardUtils
) : BaseViewModel() {

    // Inputs
    private val copyPressed = BroadcastChannel<Unit>(1)
    private val signUp = BroadcastChannel<Unit>(1)

    // Outputs
    private val secretKey = MutableStateFlow("")

    init {
        secretKeyRepository
            .observe()
            .onEach {
                secretKey.emit(it)
            }
            .launchIn(ioScope)

        copyPressed
            .asFlow()
            .onEach {
                clipboardUtils.setText(secretKey.value)
            }
            .launchIn(viewModelScope)

        signUp
            .asFlow()
            .map {
                generateSecretKey.generate()
            }
            .onEach {
                secretKey.emit(it)
            }
            .launchIn(ioScope)
    }

    // Inputs
    suspend fun copyPressed() = copyPressed.send(Unit)
    suspend fun signUp() = signUp.send(Unit)

    // Output
    fun secretKey() = secretKey.asStateFlow()

}
