package pl.mg6.safetynet

import kotlin.LazyThreadSafetyMode.NONE

typealias Initializer<T> = () -> T

abstract class Provider<T : Any>(initializer: Initializer<T>) {

    private val value by lazy(NONE, initializer)
    var override: Initializer<T>? = null

    fun get() = override?.invoke() ?: value
}
