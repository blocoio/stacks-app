package io.bloco.circles.ui.auth.signup

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.bloco.circles.data.AuthRequestsStore
import io.bloco.circles.data.AuthResponseModel
import io.bloco.circles.domain.*
import io.bloco.circles.domain.CheckUsernameStatus.UsernameStatus.Available
import io.bloco.circles.shared.foldOnEach
import io.bloco.circles.ui.BaseViewModel
import io.bloco.circles.ui.auth.signup.ChooseUsernameViewModel.Error.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChooseUsernameViewModel
@Inject constructor(
    generateAuthResponse: GenerateAuthResponse,
    checkUsernameStatus: CheckUsernameStatus,
    authRequestsStore: AuthRequestsStore,
    getAppDetails: GetAppDetails,
    newIdentity: NewIdentity,
    signUp: SignUp,
) : BaseViewModel() {

    // Variables
    private var isAuthRequest: Boolean = authRequestsStore.get() != null
    private var username: String? = null

    // Inputs
    private val usernameSubmitted = MutableStateFlow("")
    private val newAccount = BroadcastChannel<Unit>(1)
    var signUp: Boolean = false

    // Outputs
    private val sendAuthResponse = BroadcastChannel<AuthResponseModel>(1)
    private val openNewAccountScreen = BroadcastChannel<Unit>(1)
    private val loading = MutableStateFlow(false)
    private val errors = BroadcastChannel<Error>(1)

    init {

        usernameSubmitted
            .filter { it.isEmpty() || it.contains(" ") }
            .onEach { errors.send(InvalidUsername) }
            .launchIn(viewModelScope)

        usernameSubmitted
            .filter { it.isNotEmpty() && !it.contains(" ") && !loading.value }
            .map {
                loading.emit(true)
                checkUsernameStatus.isAvailable(it)
            }
            .onEach {
                if (it != Available) {
                    username = null
                    loading.emit(false)
                    errors.send(UnavailableUsername)
                } else {
                    username = usernameSubmitted.value
                    newAccount.send(Unit)
                }
            }
            .launchIn(ioScope)

        newAccount
            .asFlow()
            .map {
                loading.emit(true)
                if (this.signUp) {
                    signUp.newAccount(username)
                } else {
                    newIdentity.create(username)
                }
            }
            .foldOnEach(
                {
                    if (isAuthRequest) {
                        val authRequest = authRequestsStore.get()!!

                        sendAuthResponse.send(
                            AuthResponseModel(
                                getAppDetails.get(authRequest)!!.name,
                                authRequest.redirectUri,
                                generateAuthResponse.generate(it)
                            )
                        )
                    } else {
                        openNewAccountScreen.send(Unit)
                    }
                }, //Success
                { e ->
                    Timber.e(e)
                    loading.emit(false)
                    errors.send(SignUpError)
                } // Failure
            )
            .launchIn(ioScope)

    }

    // Inputs
    suspend fun usernamePicked(username: String) = usernameSubmitted.emit(username)
    fun skipUsername() = newAccount.sendBlocking(Unit)

    // Outputs
    fun sendAuthResponse(): Flow<AuthResponseModel> = sendAuthResponse.asFlow()
    fun openNewAccountScreen() = openNewAccountScreen.asFlow()
    fun loading() = loading.asStateFlow()
    fun errors() = errors.asFlow()

    enum class Error {
        UnavailableUsername, SignUpError, InvalidUsername
    }

}
