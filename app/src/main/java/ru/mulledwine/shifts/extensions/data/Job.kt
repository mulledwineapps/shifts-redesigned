package ru.mulledwine.shifts.extensions.data

import ru.mulledwine.shifts.data.local.entities.Job
import ru.mulledwine.shifts.data.local.models.JobItem

fun Job.toJobItem() = JobItem(
    id = id!!,
    name = name
)