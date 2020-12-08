package ru.mulledwine.shiftsredesigned.ui.base

import android.os.Bundle
import ru.mulledwine.shiftsredesigned.ui.delegates.RenderProp
import ru.mulledwine.shiftsredesigned.viewmodels.base.IViewModelState

abstract class Binding {
    // mutableMapOf<поле делегата, сам делегат>
    val delegates = mutableMapOf<String, RenderProp<out Any>>()
    var isInflated = false

    open val afterInflated: (() -> Unit)? = null
    fun onFinishInflate() {
        if (!isInflated) {
            afterInflated?.invoke()
            isInflated = true
        }
    }

    // Binding будет создан единожды, но метод rebind будет
    // у него вызываться каждый раз, когда будет создаваться новая вью
    fun rebind() {
        delegates.forEach { it.value.bind() }
    }

    open fun bind(data: IViewModelState) {
        //empty default implementation
    }

    /**
     * override this if need save binding in bundle
     */
    open fun saveUi(outState: Bundle) {
        //empty default implementation
    }

    /**
     * override this if need restore binding from bundle
     */
    open fun restoreUi(savedState: Bundle?) {
        //empty default implementation
    }

}