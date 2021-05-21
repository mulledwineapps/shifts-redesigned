package ru.mulledwine.shiftsredesigned.viewmodels

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.UiThread
import androidx.lifecycle.*
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import ru.mulledwine.shiftsredesigned.utils.Utils
import ru.mulledwine.shiftsredesigned.viewmodels.base.IViewModelState

abstract class BaseViewModel<T : IViewModelState>(
    private val handleState: SavedStateHandle,
    initState: T
) : ViewModel() {

    companion object {
        private const val TAG = "M_BaseViewModel"
    }

    protected val navigation = MutableLiveData<Event<NavigationCommand>>()
    private val notifications = MutableLiveData<Event<Notify>>()
    private val loading = MutableLiveData<Loading>(Loading.HideLoading(0L))

    /***
     * Инициализация начального состояния аргументом конструктоа, и объявления состояния как
     * MediatorLiveData - медиатор используется для того чтобы учитывать изменяемые данные модели
     * и обновлять состояние ViewModel исходя из полученных данных
     */
    protected val state: MediatorLiveData<T> = MediatorLiveData<T>().apply {
        value = initState
    }

    /***
     * getter для получения not null значения текущего состояния ViewModel
     */
    protected val currentState
        get() = state.value!!

    // лямбда-выражение принимает в качестве аргумента лямбду, в которую передаётся текущее состояние
    // и она возвращает модифицированное состояние, которое присваивается текущему состоянию
    @UiThread
    protected inline fun updateState(update: (currentState: T) -> T) {
        val updatedState: T = update(currentState)
        state.value = updatedState
    }

    /***
     * функция для создания уведомления пользователя о событии (событие обрабатывается только один раз)
     * соответсвенно при изменении конфигурации и пересоздании Activity уведомление не будет вызвано повторно
     */
    @UiThread
    protected fun notify(content: Notify) {
        notifications.value = Event(content)
    }

    /***
     * отображение индикатора загрузки (по умолчанию не блокирующий ui поток loading)
     */
    protected fun showLoading(loadingType: Loading = Loading.ShowLoading()): Long {
        // loading.value = loadingType
        val current = loading.value
        // не показываем простой прогресс бар поверх блокирующего UI, только запоминаем время
        val newValue =
            if (current is Loading.ShowBlockingLoading && loadingType is Loading.ShowLoading)
                Loading.ShowBlockingLoading(loadingType.startTime) else loadingType
        loading.value = newValue
        return loadingType.startTime
    }

    /***
     * скрытие индикатора загрузки
     */
    protected fun hideLoading(time: Long) {
        // loading.value = Loading.HideLoading
        when (val current = loading.value) {
            // храним в loading.value время последнего вызова загрузки и останавливаем только последнее
            is Loading.HideLoading -> return
            else -> if (current == null || current.startTime == time)
                loading.value = Loading.HideLoading(time)
        }
    }

    open fun navigate(command: NavigationCommand) {
        navigation.value = Event(command)
    }

    fun navigateUp() {
        navigate(NavigationCommand.Up)
    }

    fun navigateWithAction(navDirections: NavDirections) {
        navigate(NavigationCommand.To(navDirections.actionId, navDirections.arguments))
    }

    fun observeState(owner: LifecycleOwner, onChanged: (newState: T) -> Unit) {
        state.observe(owner, Observer { onChanged(it!!) })
    }

    fun observeLoading(owner: LifecycleOwner, onChanged: (newState: Loading) -> Unit) {
        loading.observe(owner, Observer { onChanged(it!!) })
    }

    fun observeNotifications(owner: LifecycleOwner, onNotify: (notification: Notify) -> Unit) {
        notifications.observe(owner, EventObserver { onNotify(it) })
    }

    fun observeNavigation(owner: LifecycleOwner, onNavigate: (command: NavigationCommand) -> Unit) {
        navigation.observe(owner, EventObserver { onNavigate(it) })
    }

    // функция принимает источник данных и лямбда-выражение, обрабатывающее поступающие данные источника
    // лямбда принимает новые данные и текущее состояние ViewModel в качестве аргументов,
    // изменяет его и возвращает модифицированное состояние, которое устанавливается как текущее
    protected fun <S> subscribeOnDataSource(
        source: LiveData<S>,
        onChanged: (newValue: S, currentState: T) -> T?
    ) {
        state.addSource(source) {
            state.value = onChanged(it, currentState) ?: return@addSource
        }
    }

    fun saveState() {
        currentState.save(handleState)
    }

    @Suppress("UNCHECKED_CAST")
    fun restoreState() {
        state.value = currentState.restore(handleState) as T
    }

    protected fun launchSafely(
        loadingType: Loading = Loading.ShowLoading(),
        errHandler: ((Throwable) -> Unit)? = null,
        compHandler: ((Throwable?) -> Unit)? = null,
        block: suspend CoroutineScope.() -> Unit
    ) {

        val errHand = CoroutineExceptionHandler { _, err ->

            errHandler?.invoke(err) ?: run {
                val msg = Notify.ErrorMessage(
                    message =  err.message ?: "Something went wrong",
                    errLabel = "OK"
                )
                notify(msg)
            }

            Log.d(TAG, "${err.message}")
            err.printStackTrace()
        }

        (viewModelScope + errHand).launch {
            showLoading(loadingType)
            block()
        }.invokeOnCompletion {
            hideLoading(loadingType.startTime)
            compHandler?.invoke(it)
        }
    }

    protected fun makeToast(message: String) {
        notify(Notify.ToastMessage(message))
    }

}

class Event<out E>(private val content: E) {
    var hasBeenHandled = false

    // возвращает контент, который ещё не был обработан, иначе null
    fun getContentIfNotHandled(): E? {
        return if (hasBeenHandled) null
        else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): E = content
}

class EventObserver<E>(private val onEventUnhandledContent: (E) -> Unit) : Observer<Event<E>> {
    // в качестве аргумента принимает лямбда-выражение обработчик в которую передаётся необработанное
    // ранее событие получаемое в реализации метода Observer'а onChanged
    override fun onChanged(event: Event<E>?) {
        // если есть необработанное событие (контент),
        // передай в качестве аргумента в лямбду onEventUnhandledContent
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}

// концепция sealed классов в kotlin очень похожа на enum, только они могут сохранять внутри себя какое-то состояине,
// то есть хранить внутри экземпляры, поэтому их удобно применять в when конструкции
sealed class Notify() {
    // data class'ы не оддерживают наследование, могут только реализовывать интерфейсы,
    // исключение - если они являются подклассами sealed класса

    abstract val message: String
    open val duration: Int = Snackbar.LENGTH_LONG
    open val anchorViewId: Int? = null

    data class TextMessage(
        override val message: String,
        @IdRes override val anchorViewId: Int? = null,
    ) : Notify()

    data class ActionMessage(
        override val message: String,
        override val duration: Int = Snackbar.LENGTH_LONG,
        val actionLabel: String,
        val actionHandler: (() -> Unit)
    ) : Notify()

    data class ErrorMessage(
        override val message: String,
        override val duration: Int = Snackbar.LENGTH_INDEFINITE,
        val errLabel: String? = null,
        val errHandler: (() -> Unit)? = null
    ) : Notify()

    data class ToastMessage(
        override val message: String,
        override val duration: Int = Toast.LENGTH_LONG
    ): Notify()
}

sealed class NavigationCommand() {
    data class To(
        val destination: Int,
        val args: Bundle? = null,
        val options: NavOptions? = null,
        val extras: Navigator.Extras? = null
    ) : NavigationCommand()

    object Up : NavigationCommand()
}

sealed class Loading() {

    abstract val startTime: Long

    data class ShowLoading(override val startTime: Long = Utils.getTime()) : Loading()
    data class ShowBlockingLoading(override val startTime: Long = Utils.getTime()) : Loading()
    data class HideLoading(override val startTime: Long) : Loading()
}