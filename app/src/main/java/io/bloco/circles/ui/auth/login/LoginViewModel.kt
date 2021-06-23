package io.bloco.circles.ui.auth.login

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.bloco.circles.data.AuthRequestsStore
import io.bloco.circles.domain.Login
import io.bloco.circles.shared.foldOnEach
import io.bloco.circles.ui.BaseViewModel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject constructor(
    authRequestsStore: AuthRequestsStore,
    login: Login
) : BaseViewModel() {

    // Inputs
    private val secretKeyValues = MutableStateFlow("")
    private val submittedSecretKey = BroadcastChannel<String>(1)

    // Outputs
    private val loading = MutableStateFlow(false)
    private val showError = BroadcastChannel<Unit>(1)
    private val openWelcomeScreen = BroadcastChannel<Unit>(1)
    private val openIdentitiesScreen = BroadcastChannel<Unit>(1)

    init {
        secretKeyValues
            .asStateFlow()
            .filter { it.split(" ").count() > SECRET_KEY_WORDS }
            .onEach {
                showError.send(Unit)
            }
            .launchIn(viewModelScope)

        submittedSecretKey
            .asFlow()
            .onEach { loading.emit(true) }
            .map { login.login(it) }
            .foldOnEach({
                if (authRequestsStore.isEmpty()) {
                    openWelcomeScreen.send(Unit)
                } else {
                    openIdentitiesScreen.send(Unit)
                }
            }, {
                loading.emit(false)
                showError.send(Unit)
            })
            .launchIn(ioScope)
    }

    // Inputs
    suspend fun secretKeyUpdated(privateKey: CharSequence) =
        secretKeyValues.emit(privateKey.toString())

    suspend fun submitClicked(text: String) =
        submittedSecretKey.send(text)

    // Outputs
    fun showError() =
        showError.asFlow()

    fun openWelcomeScreen() =
        openWelcomeScreen.asFlow()

    fun openIdentitiesScreen() =
        openIdentitiesScreen.asFlow()

    fun loading() = loading.asStateFlow()

    companion object {
        const val SECRET_KEY_WORDS = 12
    }

}
