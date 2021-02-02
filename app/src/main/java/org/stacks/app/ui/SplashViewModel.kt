package org.stacks.app.ui

import androidx.hilt.lifecycle.ViewModelInject
import com.google.gson.Gson
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import me.uport.sdk.core.decodeBase64
import me.uport.sdk.jwt.InvalidJWTException
import me.uport.sdk.jwt.JWTEncodingException
import me.uport.sdk.jwt.JWTUtils
import org.stacks.app.data.AuthRequestModel
import org.stacks.app.data.AuthRequestsStore
import org.stacks.app.domain.GetUserAuthState
import org.stacks.app.domain.GetUserAuthState.UserAuthState.Authenticated
import org.stacks.app.ui.SplashViewModel.Request.*
import org.stacks.app.ui.SplashViewModel.Request.Nothing

class SplashViewModel
@ViewModelInject constructor(
    private val userAuthState: GetUserAuthState,
    private val gson: Gson,
    store: AuthRequestsStore,
) : BaseViewModel() {

    // Inputs
    private val dataReceived = BroadcastChannel<String?>(1)

    // Outputs
    private val openHomepage = BroadcastChannel<Unit>(1)
    private val openLogin = BroadcastChannel<Unit>(1)
    private val openSignUp = BroadcastChannel<Unit>(1)
    private val openIdentities = BroadcastChannel<Unit>(1)
    private val errors = BroadcastChannel<Unit>(1)

    init {

        dataReceived
            .asFlow()
            .map { getDataFromUri(it) }
            .onEach { (authRequestToken, state) ->
                if (state != Nothing) {
                    store.set(decodeJWT(authRequestToken))
                }

                when (state) {
                    LoggedUser -> openIdentities.send(Unit)
                    Login -> openLogin.send(Unit)
                    SignUp -> openSignUp.send(Unit)
                    Nothing -> openHomepage.send(Unit)
                }
            }
            .catch { errors.send(Unit) }
            .launchIn(ioScope)

    }

    private fun decodeJWT(token: String): AuthRequestModel {
        //Split token by . from jwtUtils
        val (encodedHeader, encodedPayload, _) = JWTUtils.splitToken(token)
        if (encodedHeader.isEmpty())
            throw InvalidJWTException("Header cannot be empty")
        else if (encodedPayload.isEmpty())
            throw InvalidJWTException("Payload cannot be empty")
        //Decode the pieces
        val payloadString = String(encodedPayload.decodeBase64())

        try {
            return gson.fromJson(payloadString, AuthRequestModel::class.java)
        } catch (ex: Throwable) {
            throw JWTEncodingException("cannot parse the JWT($token)", ex)
        }
    }

    private suspend fun getDataFromUri(uri: String?): Pair<String, Request> {
        val authRequest = uri?.substringAfter("authRequest=") ?: return Pair("", Nothing)

        val state = when {
            userAuthState.state().first() is Authenticated -> LoggedUser
            uri.contains("/sign-up?") -> SignUp
            uri.contains("/sign-in?") -> Login
            else -> throw Exception("failed to get data from URI")

        }

        return Pair(authRequest, state)
    }

    // Inputs
    suspend fun dataReceived(data: String?) = dataReceived.send(data)

    // Outputs
    fun openHomepage() = openHomepage.asFlow()
    fun openLogin() = openLogin.asFlow()
    fun openSignUp() = openSignUp.asFlow()
    fun openIdentities() = openIdentities.asFlow()
    fun errors() = errors.asFlow()


    enum class Request {
        LoggedUser, Login, SignUp, Nothing
    }

}
