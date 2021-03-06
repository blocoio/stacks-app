package io.bloco.circles.ui.homepage

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_homepage.*
import kotlinx.android.synthetic.main.partial_community.*
import kotlinx.android.synthetic.main.partial_learn_about_us.*
import kotlinx.android.synthetic.main.partial_newsletter.*
import kotlinx.android.synthetic.main.partial_tool_bar.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import io.bloco.circles.R
import io.bloco.circles.ui.BaseActivity
import io.bloco.circles.ui.account.AccountActivity
import io.bloco.circles.ui.auth.ConnectActivity
import io.bloco.circles.ui.homepage.HomePageViewModel.UserAuthState
import io.bloco.circles.ui.homepage.HomePageViewModel.UserAuthState.Authenticated
import io.bloco.circles.ui.homepage.HomePageViewModel.UserAuthState.Unauthenticated
import io.bloco.circles.ui.shared.Insets.addSystemWindowInsetToPadding
import reactivecircus.flowbinding.android.view.clicks


@AndroidEntryPoint
class HomepageActivity : BaseActivity() {

    private val viewModel: HomePageViewModel by lazy {
        ViewModelProvider(this).get(HomePageViewModel::class.java)
    }

    private val error by lazy {
        intent?.getBooleanExtra(ERRORS, false) ?: false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        toolbar.addSystemWindowInsetToPadding(top = true)
        scrollView.addSystemWindowInsetToPadding(bottom = true)

        if (error) {
            messageLoader.show(R.string.error)
        }

        toolbarAvatar
            .clicks()
            .onEach {
                startActivity(AccountActivity.getIntent(this))
            }
            .launchIn(lifecycleScope)

        startHere
            .clicks()
            .onEach {
                startActivity(ConnectActivity.getIntent(this))
            }
            .launchIn(lifecycleScope)

        aboutUsCard
            .clicks()
            .onEach {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.about_us_url))
                    )
                )
            }
            .launchIn(lifecycleScope)

        newsletterSubscribe
            .clicks()
            .onEach {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.newsletter_url))
                    )
                )
            }
            .launchIn(lifecycleScope)

        discordCard
            .clicks()
            .onEach {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.discord_url))
                    )
                )
            }
            .launchIn(lifecycleScope)

        viewModel
            .authenticatedState()
            .onEach(::setViewsVisibilityFromUserAuthState)
            .launchIn(lifecycleScope)

        viewModel
            .userAvatarImageUrl()
            .onEach {
                it?.let {
                    imageLoader.loadAvatar(toolbarAvatar, it)
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun setViewsVisibilityFromUserAuthState(state: UserAuthState) {
        startLayout.isVisible = state == Unauthenticated
        toolbarAvatar.isVisible = state == Authenticated
    }


    companion object {

        const val ERRORS = "errors"

        fun getIntent(context: Context, error: Boolean = false) =
            Intent(context, HomepageActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(ERRORS, error)
    }

}
