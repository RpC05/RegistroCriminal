package com.example.registrocriminal

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Crimen::class], version = 1)
@TypeConverters(CrimenTypeConverter::class)
abstract class CrimenDatabase : RoomDatabase() {
    abstract fun crimenDAO(): CrimenDAO
}
