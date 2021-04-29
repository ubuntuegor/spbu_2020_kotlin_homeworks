package homework5

interface ExpressionTreeNode {
    val value: Int
    override fun toString(): String
}

class IntegerNode(override val value: Int) : ExpressionTreeNode {
    override fun toString() = value.toString()
}

open class OperationNode(
    private val sign: String,
    operation: (a: Int, b: Int) -> Int,
    private val firstNode: ExpressionTreeNode,
    private val secondNode: ExpressionTreeNode
) : ExpressionTreeNode {
    override val value = operation(firstNode.value, secondNode.value)
    override fun toString() = "($sign $firstNode $secondNode)"
}

class AdditionNode(firstNode: ExpressionTreeNode, secondNode: ExpressionTreeNode) :
    OperationNode("+", { a, b -> a + b }, firstNode, secondNode)

class SubtractionNode(firstNode: ExpressionTreeNode, secondNode: ExpressionTreeNode) :
    OperationNode("-", { a, b -> a - b }, firstNode, secondNode)

class MultiplicationNode(firstNode: ExpressionTreeNode, secondNode: ExpressionTreeNode) :
    OperationNode("*", { a, b -> a * b }, firstNode, secondNode)

class DivisionNode(firstNode: ExpressionTreeNode, secondNode: ExpressionTreeNode) :
    OperationNode("/", { a, b -> a / b }, firstNode, secondNode)
