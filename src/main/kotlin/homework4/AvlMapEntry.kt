package homework4

internal class AvlMapEntry<K, V>(override val key: K, value: V) : MutableMap.MutableEntry<K, V> {
    private var _value = value

    override val value: V
        get() = _value

    override fun setValue(newValue: V) = _value.also { _value = newValue }
}
