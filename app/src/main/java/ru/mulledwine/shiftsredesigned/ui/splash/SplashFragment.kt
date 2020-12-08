package ru.mulledwine.shiftsredesigned.ui.splash

import androidx.fragment.app.viewModels
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.ui.base.BaseFragment
import ru.mulledwine.shiftsredesigned.ui.base.Binding
import ru.mulledwine.shiftsredesigned.ui.base.ToolbarBuilder
import ru.mulledwine.shiftsredesigned.ui.delegates.RenderProp
import ru.mulledwine.shiftsredesigned.viewmodels.SplashState
import ru.mulledwine.shiftsredesigned.viewmodels.SplashViewModel
import ru.mulledwine.shiftsredesigned.viewmodels.base.IViewModelState

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