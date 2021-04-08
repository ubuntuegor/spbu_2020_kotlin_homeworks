package homework4

@Suppress("TooManyFunctions")
class AvlMap<K : Comparable<K>, V> : MutableMap<K, V> {
    private var _size = 0

    override val size: Int
        get() = _size

    private var rootNode: AvlNode<K, V>? = null

    private fun retrieveNode(key: K, node: AvlNode<K, V>?): AvlNode<K, V>? =
        when {
            node == null -> null
            key < node.key -> retrieveNode(key, node.leftChild)
            key > node.key -> retrieveNode(key, node.rightChild)
            else -> node
        }

    private fun insertNode(key: K, value: V, node: AvlNode<K, V>?): AvlNode<K, V> =
        when {
            node == null -> AvlNode(key, value).also { _size++ }
            key < node.key -> {
                node.leftChild = insertNode(key, value, node.leftChild)
                node.balance()
            }
            key > node.key -> {
                node.rightChild = insertNode(key, value, node.rightChild)
                node.balance()
            }
            else -> {
                node.value = value
                node
            }
        }

    private fun removeNode(key: K, node: AvlNode<K, V>?): AvlNode<K, V>? =
        when {
            node == null -> null
            key < node.key -> {
                node.leftChild = removeNode(key, node.leftChild)
                node.balance()
            }
            key > node.key -> {
                node.rightChild = removeNode(key, node.rightChild)
                node.balance()
            }
            else -> {
                _size--
                when (node.childrenCount) {
                    0 -> null
                    1 -> {
                        node.leftChild ?: node.rightChild
                    }
                    else -> {
                        val replacement = node.leftChild!!.retrieveLargestInSubtree()
                        node.leftChild = removeNode(replacement.key, node.leftChild)
                        replacement.copyChildrenFrom(node)
                        replacement.balance()
                    }
                }
            }
        }

    private fun populateEntrySet(set: MutableSet<MutableMap.MutableEntry<K, V>>, node: AvlNode<K, V>?) {
        if (node == null) return
        populateEntrySet(set, node.leftChild)
        set.add(node.entry)
        populateEntrySet(set, node.rightChild)
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = mutableSetOf<MutableMap.MutableEntry<K, V>>().also { populateEntrySet(it, rootNode) }
    override val keys: MutableSet<K>
        get() = entries.map { it.key }.toMutableSet()
    override val values: MutableCollection<V>
        get() = entries.map { it.value }.toMutableList()

    override fun containsKey(key: K) =
        get(key) != null

    override fun containsValue(value: V) =
        values.contains(value)

    override fun get(key: K) =
        retrieveNode(key, rootNode)?.value

    override fun isEmpty() =
        size == 0

    override fun clear() {
        _size = 0
        rootNode = null
    }

    override fun put(key: K, value: V): V? =
        get(key).also { rootNode = insertNode(key, value, rootNode) }

    override fun putAll(from: Map<out K, V>) {
        from.forEach { (key, value) -> rootNode = insertNode(key, value, rootNode) }
    }

    override fun remove(key: K): V? =
        get(key).also { if (it != null) rootNode = removeNode(key, rootNode) }
}
