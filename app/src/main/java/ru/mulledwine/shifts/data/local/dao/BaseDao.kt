package ru.mulledwine.shifts.data.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

interface BaseDao<T : Any> {

    @Insert
    suspend fun insert(list: List<T>): List<Long>

    @Insert
    suspend fun insert(obj: T): Long // returns row id, -1L - insert неуспешный

    @Update
    suspend fun update(list: List<T>)

    /**
     * The implementation of the method will update its parameters in the database
     * if they already exists (checked by primary keys).
     * If they don't already exists, this option will not change the database.
     */
    @Update
    suspend fun update(obj: T)

    @Delete
    suspend fun delete(obj: T)

}