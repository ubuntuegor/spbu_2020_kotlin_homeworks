package homework4

import kotlin.math.max

internal class AvlNode<K : Comparable<K>, V>(val key: K, value: V) {
    val entry = AvlMapEntry(key, value)
    var value: V
        get() = entry.value
        set(newValue) {
            entry.setValue(newValue)
        }

    private var height = 1
    var leftChild: AvlNode<K, V>? = null
    var rightChild: AvlNode<K, V>? = null

    val childrenCount: Int
        get() = if (leftChild == null && rightChild == null) 0
        else if (leftChild != null && rightChild != null) 2
        else 1

    private val balanceFactor: Int
        get() = (rightChild?.height ?: 0) - (leftChild?.height ?: 0)

    fun retrieveLargestInSubtree(): AvlNode<K, V> =
        generateSequence(this) { it.rightChild }.last()

    fun copyChildrenFrom(node: AvlNode<K, V>) {
        this.leftChild = node.leftChild
        this.rightChild = node.rightChild
        this.height = node.height
    }

    fun balance(): AvlNode<K, V> {
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
        private const val UNBALANCED_STATE_FACTOR = 2
    }
}
