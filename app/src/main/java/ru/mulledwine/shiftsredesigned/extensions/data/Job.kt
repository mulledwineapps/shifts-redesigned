package ru.mulledwine.shiftsredesigned.extensions.data

import ru.mulledwine.shiftsredesigned.data.local.entities.Job
import ru.mulledwine.shiftsredesigned.data.local.models.JobItem

fun Job.toJobItem() = JobItem(
    id = id!!,
    name = name
)