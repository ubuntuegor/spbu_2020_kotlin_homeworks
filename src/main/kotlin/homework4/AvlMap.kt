package homework4

class AvlMap<K : Comparable<K>, V> : MutableMap<K, V> {
    private val tree = AvlMapTree<K, V>()

    override val size: Int
        get() = tree.size

    override fun containsKey(key: K) = tree.retrieve(key) != null

    override fun containsValue(value: V) =
        tree.asSequence().map { it.value }.contains(value)

    override fun get(key: K) = tree.retrieve(key)?.value

    override fun isEmpty() = size == 0

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = tree.asSequence().map { Pair(it.key, it.value) }.toMap().toMutableMap().entries

    override val keys: MutableSet<K>
        get() = tree.asSequence().map { it.key }.toMutableSet()

    override val values: MutableCollection<V>
        get() = tree.asSequence().map { it.value }.toMutableList()

    override fun clear() = tree.clear()

    override fun put(key: K, value: V) = tree.insert(AvlMapNode(key, value))?.value

    override fun putAll(from: Map<out K, V>) =
        from.forEach { tree.insert(AvlMapNode(it.key, it.value)) }

    override fun remove(key: K): V? {
        val toRemove = tree.retrieve(key) ?: return null
        tree.remove(toRemove)
        return toRemove.value
    }
}
