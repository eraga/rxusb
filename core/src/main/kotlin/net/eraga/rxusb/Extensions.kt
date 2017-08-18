package net.eraga.rxusb

internal fun <T> Array<out T>.concat(other: Array<out T>): List<T> {
    return (this.toList() + other.toList())
}
