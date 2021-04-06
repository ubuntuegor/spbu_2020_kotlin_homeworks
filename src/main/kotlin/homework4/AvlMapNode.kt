package homework4

import kotlin.math.max

internal class AvlMapNode<K, V>(override val key: K, override var value: V) : MutableMap.MutableEntry<K, V> {
    var height = 0
    var parent: AvlMapNode<K, V>? = null
    var leftChild: AvlMapNode<K, V>? = null
    var rightChild: AvlMapNode<K, V>? = null

    override fun setValue(newValue: V) = value.also { value = newValue }

    private val balanceFactor: Int
        get() = (rightChild?.height ?: 0) - (leftChild?.height ?: 0)

    fun updateHeightUpwards() {
        height = max(leftChild?.height ?: 0, rightChild?.height ?: 0) + 1
        parent?.updateHeightUpwards()
    }

    private fun rotateLeft() {
        val savedParent = this.parent
        val savedRightChild = this.rightChild as AvlMapNode

        this.rightChild = savedRightChild.leftChild
        savedRightChild.leftChild?.parent = this
        savedRightChild.leftChild = this
        this.parent = savedRightChild
        savedRightChild.parent = savedParent
        if (savedParent != null) {
            if (this === savedParent.leftChild) savedParent.leftChild = savedRightChild
            if (this === savedParent.rightChild) savedParent.rightChild = savedRightChild
        }

        this.updateHeightUpwards()
    }

    private fun rotateRight() {
        val savedParent = this.parent
        val savedLeftChild = this.leftChild as AvlMapNode

        this.leftChild = savedLeftChild.rightChild
        savedLeftChild.rightChild?.parent = this
        savedLeftChild.rightChild = this
        this.parent = savedLeftChild
        savedLeftChild.parent = savedParent
        if (savedParent != null) {
            if (this === savedParent.leftChild) savedParent.leftChild = savedLeftChild
            if (this === savedParent.rightChild) savedParent.rightChild = savedLeftChild
        }

        this.updateHeightUpwards()
    }

    fun balance() {
        if (this.balanceFactor == BALANCE_LIMIT) {
            if ((this.rightChild?.balanceFactor ?: 0) < 0) {
                this.rightChild?.rotateRight()
            }
            this.rotateLeft()
        }
        if (this.balanceFactor == -BALANCE_LIMIT) {
            if ((this.leftChild?.balanceFactor ?: 0) > 0) {
                this.leftChild?.rotateLeft()
            }
            this.rotateRight()
        }
    }

    fun retrieveSmallestInSubtree(): AvlMapNode<K, V> {
        var node: AvlMapNode<K, V> = this
        while (node.leftChild != null)
            node = node.leftChild as AvlMapNode
        return node
    }

    fun retrieveLargestInSubtree(): AvlMapNode<K, V> {
        var node: AvlMapNode<K, V> = this
        while (node.rightChild != null)
            node = node.rightChild as AvlMapNode
        return node
    }

    companion object Constants {
        private const val BALANCE_LIMIT = 2
    }
}
