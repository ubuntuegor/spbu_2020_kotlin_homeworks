package homework4

internal class AvlMapTree<K : Comparable<K>, V> {
    private var root: AvlMapNode<K, V>? = null
    var size = 0

    fun retrieve(key: K): AvlMapNode<K, V>? {
        var node = root
        while (node != null && node.key != key) {
            if (key < node.key) node = node.leftChild
            else if (key > node.key) node = node.rightChild
        }
        return node
    }

    fun insert(key: K, value: V): V? {
        var lastDirection = Direction.UNDEFINED
        var parent: AvlMapNode<K, V>? = null
        var node = root

        while (node != null && key != node.key) {
            parent = node
            if (key < node.key) {
                node = node.leftChild
                lastDirection = Direction.LEFT
            } else if (key > node.key) {
                node = node.rightChild
                lastDirection = Direction.RIGHT
            }
        }

        var oldValue: V? = null

        if (node != null) {
            oldValue = node.setValue(value)
        } else {
            val insertedNode = AvlMapNode(key, value)

            if (parent == null) root = insertedNode
            else {
                if (lastDirection == Direction.LEFT) parent.leftChild = insertedNode
                if (lastDirection == Direction.RIGHT) parent.rightChild = insertedNode
                insertedNode.parent = parent
            }

            size++
            insertedNode.updateHeightUpwards()
            insertedNode.balanceTreeUpwards()
        }

        return oldValue
    }

    fun removeByKey(key: K): V? {
        val toRemove = retrieve(key) ?: return null
        remove(toRemove)
        return toRemove.value
    }

    private fun remove(node: AvlMapNode<K, V>) {
        val parent = node.parent
        val leftChild = node.leftChild
        val rightChild = node.rightChild

        if (leftChild == null && rightChild == null) {
            if (parent == null) root = null
            else {
                if (node === parent.leftChild) parent.leftChild = null
                if (node === parent.rightChild) parent.rightChild = null
                parent.updateHeightUpwards()
                parent.balanceTreeUpwards()
            }
        } else if (leftChild != null && rightChild == null) {
            node.replaceSubtreeWith(leftChild)
            leftChild.balanceTreeUpwards()
        } else if (leftChild == null && rightChild != null) {
            node.replaceSubtreeWith(rightChild)
            rightChild.balanceTreeUpwards()
        } else if (leftChild != null && rightChild != null) {
            val replacement = leftChild.retrieveLargestInSubtree()
            remove(replacement)
            replacement.leftChild = node.leftChild
            replacement.rightChild = node.rightChild
            node.replaceSubtreeWith(replacement)
        }

        size--
    }

    fun clear() {
        root = null
        size = 0
    }

    fun asSequence() = generateSequence(root?.retrieveSmallestInSubtree()) {
        val rightChild = it.rightChild
        if (rightChild != null) rightChild.retrieveSmallestInSubtree()
        else {
            var node = it
            while (node.parent?.rightChild === node)
                node = node.parent as AvlMapNode
            node.parent
        }
    }

    private fun AvlMapNode<K, V>.balanceTreeUpwards() {
        this.balance()
        if (this === root && root?.parent != null) root = this.parent
        this.parent?.balanceTreeUpwards()
    }

    private fun AvlMapNode<K, V>.replaceSubtreeWith(replacement: AvlMapNode<K, V>) {
        val parent = this.parent
        if (parent == null) {
            root = replacement
            replacement.parent = null
        } else {
            replacement.parent = parent
            if (this === parent.leftChild) parent.leftChild = replacement
            if (this === parent.rightChild) parent.rightChild = replacement
        }

        replacement.updateHeightUpwards()
    }

    private enum class Direction { LEFT, RIGHT, UNDEFINED }
}
