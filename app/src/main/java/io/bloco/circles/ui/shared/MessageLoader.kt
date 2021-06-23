package io.bloco.circles.ui.shared

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

@SuppressLint("ShowToast")
class MessageLoader
@Inject constructor(
    private val activity: Activity
) {

    private val rootView get() = activity.findViewById<View>(android.R.id.content)

    fun show(@StringRes resString: Int) =
        Snackbar.make(rootView, activity.getString(resString), Snackbar.LENGTH_LONG)
            .show()

    fun show(string: String) =
        Snackbar.make(rootView, string, Snackbar.LENGTH_LONG)
            .show()

    fun show(@StringRes message: Int, @StringRes action: Int, listener: View.OnClickListener,) =
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
            .setAction(action, listener)
            .show()

    fun show(anchorView: View, @StringRes message: Int, @StringRes action: Int, listener: View.OnClickListener,) =
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
            .setAnchorView(anchorView)
            .setAction(action, listener)
            .show()

    fun show(message: String, action: String, listener: View.OnClickListener, ) =
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
            .setAction(action, listener)
            .show()

    fun show(anchorView: View, message: String, action: String, listener: View.OnClickListener, ) =
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
            .setAnchorView(anchorView)
            .setAction(action, listener)
            .show()

}
