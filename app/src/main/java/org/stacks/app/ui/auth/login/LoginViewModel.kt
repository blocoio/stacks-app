package org.stacks.app.ui.auth.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import org.stacks.app.data.AuthRequestsStore
import org.stacks.app.domain.Login
import org.stacks.app.shared.foldOnEach
import org.stacks.app.ui.BaseViewModel

class LoginViewModel
@ViewModelInject constructor(
    authRequestsStore: AuthRequestsStore,
    login: Login
) : BaseViewModel() {

    // Inputs
    private val secretKeyValues = MutableStateFlow("")
    private val submittedSecretKey = BroadcastChannel<String>(1)

    // Outputs
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
            .map { login.login(it) }
            .foldOnEach({
                if (authRequestsStore.isEmpty()) {
                    openWelcomeScreen.send(Unit)
                } else {
                    openIdentitiesScreen.send(Unit)
                }
            }, {
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

    companion object {
        const val SECRET_KEY_WORDS = 12
    }

}
