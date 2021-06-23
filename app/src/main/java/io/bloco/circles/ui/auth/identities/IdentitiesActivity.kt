package io.bloco.circles.ui.auth.identities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.bloco.circles.R
import io.bloco.circles.data.IdentityModel
import io.bloco.circles.ui.BaseActivity
import io.bloco.circles.ui.auth.signup.ChooseUsernameActivity
import io.bloco.circles.ui.shared.IdentityRowView
import kotlinx.android.synthetic.main.activity_identities.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import reactivecircus.flowbinding.android.view.clicks

@AndroidEntryPoint
class IdentitiesActivity : BaseActivity() {

    private val viewModel: IdentitiesViewModel by lazy {
        ViewModelProvider(this).get(IdentitiesViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identities)

        newIdentity
            .clicks()
            .onEach { startActivity(ChooseUsernameActivity.getIntent(this)) }
            .launchIn(lifecycleScope)

        viewModel
            .identities()
            .onEach { setIdentitiesRows(it) }
            .launchIn(lifecycleScope)

        viewModel
            .appDetails()
            .filter { it != null }
            .onEach {
                imageLoader.loadAppIcon(appIcon, it!!.icon)
                appName.isVisible = true
                appName.text = it.name
            }
            .launchIn(lifecycleScope)

        viewModel
            .sendAuthResponse()
            .onEach {

                val uri = Uri.parse(it.redirectURL)
                    .buildUpon()
                    .appendQueryParameter(AUTH_RESPONSE, it.authResponseToken)
                    .build()

                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        uri
                    )
                )

                finishAffinity()
            }
            .launchIn(lifecycleScope)

        viewModel
            .loading()
            .onEach { loading ->
                loadingSpinner.isVisible = loading
                accounts.isVisible = !loading
                newIdentity.isVisible = !loading
            }
            .launchIn(lifecycleScope)

        viewModel
            .errors()
            .onEach { messageLoader.show(R.string.error) }
            .launchIn(lifecycleScope)

        appConnectionDescription.text = getString(R.string.to_connect_to, viewModel.appDomain())
    }


    private suspend fun setIdentitiesRows(identities: List<IdentityModel>) {
        accounts.removeAllViews()
        identities.forEach { identityModel ->
            val identityRowView = IdentityRowView(this, identityModel)

            identityRowView.setOnClickListener {
                GlobalScope.launch {
                    viewModel.identitySelected(identityModel)
                }
            }

            accounts.addView(identityRowView)
        }
    }

    companion object {
        const val AUTH_RESPONSE = "authResponse"

        fun getIntent(context: Context) = Intent(context, IdentitiesActivity::class.java)
    }

}
