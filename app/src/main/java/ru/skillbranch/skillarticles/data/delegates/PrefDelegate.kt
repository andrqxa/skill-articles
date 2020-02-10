package ru.skillbranch.skillarticles.data.delegates

import ru.skillbranch.skillarticles.data.local.PrefManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefDelegate<T>(private val defaultValue: T) : ReadWriteProperty<PrefManager, T?> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: PrefManager, property: KProperty<*>): T? =
        with(thisRef.preferences) {
            when (defaultValue) {
                is Boolean -> getBoolean(property.name, defaultValue) as T
                is String -> getString(property.name, defaultValue) as T
                is Float -> getFloat(property.name, defaultValue) as T
                is Int -> getInt(property.name, defaultValue) as T
                is Long -> getLong(property.name, defaultValue) as T
                else -> null
            }
        }

    override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T?) {
        with(thisRef.preferences.edit()) {
            when (value) {
                is Boolean -> putBoolean(property.name, value)
                is String -> putString(property.name, value)
                is Float -> putFloat(property.name, value)
                is Int -> putInt(property.name, value)
                is Long -> putLong(property.name, value)
                else -> throw java.lang.ClassCastException("PrefManager setPropertyValue type illegal")
            }
                .apply()
        }
    }
}