package ru.mulledwine.shifts.ui.base

import android.os.Bundle
import ru.mulledwine.shifts.ui.delegates.RenderProp
import ru.mulledwine.shifts.viewmodels.base.IViewModelState
import kotlin.reflect.KProperty

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

    @Suppress("UNCHECKED_CAST")
    fun <A, B> dependsOn(
        vararg fields: KProperty<*>,
        onChange: (A, B) -> Unit
    ) {
        check(fields.size == 2) { "Names size must be 2, current ${fields.size}" }
        val names = fields.map { it.name }

        names.forEach {
            delegates[it]?.addListener {
                onChange(
                    delegates[names[0]]?.value as A,
                    delegates[names[1]]?.value as B
                )
            }
        }

    }

}