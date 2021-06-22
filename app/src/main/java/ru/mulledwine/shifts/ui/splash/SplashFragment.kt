package ru.mulledwine.shifts.ui.splash

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.ui.base.BaseFragment
import ru.mulledwine.shifts.ui.base.Binding
import ru.mulledwine.shifts.ui.base.ToolbarBuilder
import ru.mulledwine.shifts.ui.delegates.RenderProp
import ru.mulledwine.shifts.viewmodels.SplashState
import ru.mulledwine.shifts.viewmodels.SplashViewModel
import ru.mulledwine.shifts.viewmodels.base.IViewModelState

@AndroidEntryPoint
class SplashFragment : BaseFragment<SplashViewModel>() {

    companion object {
        private const val TAG = "M_SplashFragment"
    }

    override val viewModel: SplashViewModel by viewModels()
    override val layout: Int = R.layout.fragment_splash
    override val binding: Binding = SplashBinding()

    override val prepareToolbar: (ToolbarBuilder.() -> Unit) = {
        setVisibility(false)
    }

    override fun setupViews() {

    }

    inner class SplashBinding : Binding() {
        private var isAppReady by RenderProp(false, needInit = false) {
            if (it) {
                val action = SplashFragmentDirections.actionNavSplashToNavMain()
                viewModel.navigateWithAction(action)
            }
        }

        override fun bind(data: IViewModelState) {
            data as SplashState
            isAppReady = data.isAppReady
        }
    }

}