package homework4

class AvlMap<K : Comparable<K>, V> : MutableMap<K, V> {
    private var _size = 0

    override val size: Int
        get() = _size

    private var rootNode: AvlNode<K, V>? = null

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = mutableSetOf<MutableMap.MutableEntry<K, V>>().also { rootNode?.populateEntrySet(it) }
    override val keys: MutableSet<K>
        get() = entries.map { it.key }.toMutableSet()
    override val values: MutableCollection<V>
        get() = entries.map { it.value }.toMutableList()

    override fun containsKey(key: K) =
        get(key) != null

    override fun containsValue(value: V) =
        values.contains(value)

    override fun get(key: K) =
        rootNode?.retrieveChild(key)?.value

    override fun isEmpty() =
        size == 0

    override fun clear() {
        _size = 0
        rootNode = null
    }

    override fun put(key: K, value: V): V? =
        get(key).also {
            if (it == null) _size++
            rootNode = rootNode?.insertChild(key, value) ?: AvlNode(key, value)
        }

    override fun putAll(from: Map<out K, V>) {
        from.forEach { (key, value) -> put(key, value) }
    }

    override fun remove(key: K): V? =
        get(key).also {
            if (it != null) {
                _size--
                rootNode = rootNode?.removeChild(key)
            }
        }
}
