package io.bloco.circles.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.bloco.circles.R
import io.bloco.circles.ui.auth.identities.IdentitiesActivity
import io.bloco.circles.ui.auth.login.LoginActivity
import io.bloco.circles.ui.homepage.HomepageActivity
import io.bloco.circles.ui.secret.SecretKeyActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private val viewModel: SplashViewModel by lazy {
        ViewModelProvider(this).get(SplashViewModel::class.java)
    }

    private lateinit var forActivityResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        forActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

        GlobalScope.launch {
            delay(SPLASH_SCREEN_DELAY_MILLISECONDS)
            viewModel
                .dataReceived(intent?.data?.fragment)
        }

        viewModel
            .openHomepage()
            .onEach { startActivity(HomepageActivity.getIntent(this)) }
            .launchIn(lifecycleScope)

        viewModel
            .openSignUp()
            .onEach { startActivity(SecretKeyActivity.getIntent(this, true)) }
            .launchIn(lifecycleScope)

        viewModel
            .openLogin()
            .onEach { startActivity(LoginActivity.getIntent(this)) }
            .launchIn(lifecycleScope)

        viewModel
            .openIdentities()
            .onEach { forActivityResult.launch(IdentitiesActivity.getIntent(this)) }
            .launchIn(lifecycleScope)

        viewModel
            .errors()
            .onEach { startActivity(HomepageActivity.getIntent(this, true)) }
            .launchIn(lifecycleScope)
    }

    companion object {
        const val SPLASH_SCREEN_DELAY_MILLISECONDS = 1 * 1000L
    }

}
