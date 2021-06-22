package ru.mulledwine.shifts.ui.delegates

import ru.mulledwine.shifts.ui.base.Binding
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class RenderProp<T : Any>(
    var value: T,
    private val needInit: Boolean = true,
    private val onChange: ((T) -> Unit)? = null
) : ReadWriteProperty<Binding, T> {

    private val listeners: MutableList<() -> Unit> = mutableListOf()

    fun bind() {
        if (needInit) onChange?.invoke(value)
    }

    // provide delegate (when by call)
    // можно было бы обращаться к каждому свойству при помощи рефлексии и получить его делегат,
    // но она небыстрая и kotlin.reflection - тяжёлая библиотека
    operator fun provideDelegate(
        thisRef: Binding,
        prop: KProperty<*>
    ): ReadWriteProperty<Binding, T> {
        val delegate = RenderProp(
            value,
            needInit,
            onChange
        )
        registerDelegate(thisRef, prop.name, delegate)
        return delegate
    }

    override fun getValue(thisRef: Binding, property: KProperty<*>): T = value

    override fun setValue(thisRef: Binding, property: KProperty<*>, value: T) {
        if (value == this.value) return
        this.value = value
        onChange?.invoke(this.value)
        if (listeners.isNotEmpty()) listeners.forEach { it.invoke() }
    }

    // register additional listener
    fun addListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    // register new delegate for property in Binding
    private fun registerDelegate(thisRef: Binding, name: String, delegate: RenderProp<T>) {
        thisRef.delegates[name] = delegate
    }
}