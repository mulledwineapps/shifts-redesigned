package ru.mulledwine.shifts.viewmodels.base

//class ViewModelFactory(
//    owner: SavedStateRegistryOwner, // owner будет сохранять saved state в bundle
//    defaultArgs: Bundle = bundleOf(),
//    private val params: Any
//) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
//
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel?> create(
//        key: String,
//        modelClass: Class<T>,
//        handle: SavedStateHandle
//    ): T {
//        if (modelClass.isAssignableFrom(VacationsViewModel::class.java)) {
//            return VacationsViewModel(
//                handle,
//                params as JobWithVacationItems
//            ) as T
//        }
//        if (modelClass.isAssignableFrom(SchedulesViewModel::class.java)) {
//            return SchedulesViewModel(
//                handle,
//                params as JobWithScheduleItems
//            ) as T
//        }
//        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
//            return StatisticsViewModel(
//                handle,
//                params as JobWithStatisticItems
//            ) as T
//        }
//        if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
//            return AlarmViewModel(
//                handle,
//                params as AlarmFullParcelable
//            ) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}