package io.bloco.circles.ui.account

import dagger.hilt.android.lifecycle.HiltViewModel
import io.bloco.circles.data.IdentityModel
import io.bloco.circles.domain.GetUserAuthState
import io.bloco.circles.domain.GetUserAuthState.UserAuthState.Authenticated
import io.bloco.circles.domain.GetUserAuthState.UserAuthState.Unauthenticated
import io.bloco.circles.domain.Logout
import io.bloco.circles.shared.foldOnEach
import io.bloco.circles.ui.BaseViewModel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class AccountViewModel
@Inject constructor(
    getUserAuthState: GetUserAuthState,
    logout: Logout
) : BaseViewModel() {

    // Inputs
    private val logoutPressed = BroadcastChannel<Unit>(1)

    // Outputs
    private val identities = BroadcastChannel<List<IdentityModel>>(1)
    private val loggedOut = BroadcastChannel<Unit>(1)
    private val errors = BroadcastChannel<Unit>(1)

    init {
        getUserAuthState
            .state()
            .onEach {
                when (it) {
                    is Authenticated -> identities.send(it.identities)
                    Unauthenticated -> errors.send(Unit)
                }
            }
            .launchIn(ioScope)

        logoutPressed
            .asFlow()
            .flatMapConcat { logout.logout() }
            .foldOnEach(
                {
                    loggedOut.send(Unit)
                },
                {
                    errors.send(Unit)
                }
            )
            .launchIn(ioScope)
    }

    // Inputs
    suspend fun logoutPressed() = logoutPressed.send(Unit)

    // Outputs
    fun identities(): Flow<List<IdentityModel>> = identities.asFlow()
    fun loggedOut(): Flow<Unit> = loggedOut.asFlow()
    fun finish(): Flow<Unit> = errors.asFlow()

}
