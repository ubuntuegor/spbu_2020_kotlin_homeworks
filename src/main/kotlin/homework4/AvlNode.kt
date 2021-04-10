package homework4

import kotlin.math.max

class AvlNode<K : Comparable<K>, V>(override val key: K, value: V) : MutableMap.MutableEntry<K, V> {
    private var _value = value
    override val value: V
        get() = _value

    override fun setValue(newValue: V) = _value.also { _value = newValue }

    private var height = 1
    private var leftChild: AvlNode<K, V>? = null
    private var rightChild: AvlNode<K, V>? = null

    private val childrenCount: Int
        get() = if (leftChild == null && rightChild == null) 0
        else if (leftChild != null && rightChild != null) 2
        else 1

    private val balanceFactor: Int
        get() = (rightChild?.height ?: 0) - (leftChild?.height ?: 0)

    fun retrieveChild(key: K): AvlNode<K, V>? =
        when {
            key < this.key -> this.leftChild?.retrieveChild(key)
            key > this.key -> this.rightChild?.retrieveChild(key)
            else -> this
        }

    fun insertChild(key: K, value: V): AvlNode<K, V> =
        when {
            key < this.key -> {
                this.leftChild = this.leftChild?.insertChild(key, value) ?: AvlNode(key, value)
                this.balance()
            }
            key > this.key -> {
                this.rightChild = this.rightChild?.insertChild(key, value) ?: AvlNode(key, value)
                this.balance()
            }
            else -> {
                this.setValue(value)
                this
            }
        }

    fun removeChild(key: K): AvlNode<K, V>? =
        when {
            key < this.key -> {
                this.leftChild = this.leftChild?.removeChild(key)
                this.balance()
            }
            key > this.key -> {
                this.rightChild = this.rightChild?.removeChild(key)
                this.balance()
            }
            else -> {
                when (this.childrenCount) {
                    0 -> null
                    1 -> {
                        this.leftChild ?: this.rightChild
                    }
                    else -> {
                        val replacement = this.leftChild!!.retrieveLargestInSubtree()
                        this.leftChild = this.leftChild!!.removeChild(replacement.key)
                        replacement.leftChild = this.leftChild
                        replacement.rightChild = this.rightChild
                        replacement.height = this.height
                        replacement.balance()
                    }
                }
            }
        }

    private fun retrieveLargestInSubtree(): AvlNode<K, V> =
        generateSequence(this) { it.rightChild }.last()

    fun populateEntrySet(set: MutableSet<MutableMap.MutableEntry<K, V>>) {
        this.leftChild?.populateEntrySet(set)
        set.add(this)
        this.rightChild?.populateEntrySet(set)
    }

    private fun balance(): AvlNode<K, V> {
        this.updateHeight()
        return when (this.balanceFactor) {
            UNBALANCED_STATE_FACTOR -> {
                if (this.rightChild!!.balanceFactor < 0) {
                    this.rightChild = this.rightChild!!.rotateRight()
                }
                this.rotateLeft()
            }
            -UNBALANCED_STATE_FACTOR -> {
                if (this.leftChild!!.balanceFactor > 0) {
                    this.leftChild = this.leftChild!!.rotateLeft()
                }
                this.rotateRight()
            }
            else -> this
        }
    }

    private fun rotateLeft(): AvlNode<K, V> {
        val pivot = this.rightChild!!
        this.rightChild = pivot.leftChild
        pivot.leftChild = this
        this.updateHeight()
        pivot.updateHeight()
        return pivot
    }

    private fun rotateRight(): AvlNode<K, V> {
        val pivot = this.leftChild!!
        this.leftChild = pivot.rightChild
        pivot.rightChild = this
        this.updateHeight()
        pivot.updateHeight()
        return pivot
    }

    private fun updateHeight() {
        height = max(leftChild?.height ?: 0, rightChild?.height ?: 0) + 1
    }

    companion object Constants {
        const val UNBALANCED_STATE_FACTOR = 2
    }
}
