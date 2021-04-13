package homework5

import kotlin.math.min

class HashMap<K, V>(hashWrapper: HashFunctionWrapper<K>) {
    private var size = 0
    private var buckets = Array(1) { mutableListOf<HashMapEntry<K, V>>() }
    private val loadFactor: Double
        get() = size.toDouble() / buckets.size

    private var _hashWrapper = hashWrapper
    var hashWrapper: HashFunctionWrapper<K>
        get() = _hashWrapper
        set(value) {
            _hashWrapper = value
            initBuckets()
        }

    private fun hashOf(key: K) = hashWrapper.hashOf(key, buckets.size)

    operator fun get(key: K): V? {
        val hash = hashOf(key)
        return buckets[hash].find { key == it.key }?.value
    }

    private fun initBuckets(newSize: Int = buckets.size) {
        val oldBuckets = buckets
        buckets = Array(newSize) { mutableListOf() }
        size = 0
        oldBuckets.forEach {
            it.forEach { (key, value) -> set(key, value) }
        }
    }

    operator fun set(key: K, value: V) {
        val hash = hashOf(key)
        val existing = buckets[hash].find { key == it.key }
        if (existing != null) existing.value = value
        else {
            if (loadFactor >= EXPAND_FACTOR) {
                initBuckets(buckets.size * 2)
            }
            buckets[hash].add(HashMapEntry(key, value))
            size++
        }
    }

    fun remove(key: K) {
        val hash = hashOf(key)
        val indexOfExisting = buckets[hash].indexOfFirst { key == it.key }
        if (indexOfExisting != -1) {
            buckets[hash].removeAt(indexOfExisting)
            size--
            if (loadFactor <= SHRINK_FACTOR) {
                initBuckets(min(1, buckets.size / 2))
            }
        }
    }

    fun getStatistics(): Map<String, Any> {
        var conflictCount = 0
        var maxBucketSize = 0
        buckets.forEach {
            if (it.size > 1) conflictCount++
            if (it.size > maxBucketSize) maxBucketSize = it.size
        }

        return mapOf(
            "Total elements" to size,
            "Total buckets" to buckets.size,
            "Load factor" to loadFactor,
            "Conflict count" to conflictCount,
            "Maximum bucket size" to maxBucketSize
        )
    }

    data class HashMapEntry<K, V>(val key: K, var value: V)

    private companion object {
        const val EXPAND_FACTOR = 0.7
        const val SHRINK_FACTOR = 0.2
    }
}
