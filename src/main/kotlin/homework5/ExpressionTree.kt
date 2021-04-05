package homework5

class ExpressionTree(private val rootNode: ExpressionTreeNode) {
    fun calculateValue() = rootNode.value
    override fun toString() = rootNode.toString()

    companion object {
        private fun parseArguments(input: String): List<String> {
            val parenthesisStack = mutableListOf<Unit>()
            val arguments = mutableListOf<String>()
            var element = ""

            fun pushArgumentIfNotEmpty() {
                if (element.isNotEmpty()) {
                    arguments.add(element)
                    element = ""
                }
            }

            input.forEach {
                if (it == '(') {
                    if (parenthesisStack.isEmpty()) pushArgumentIfNotEmpty()
                    parenthesisStack.add(Unit)
                }

                if (it == ' ' && parenthesisStack.isEmpty()) {
                    pushArgumentIfNotEmpty()
                } else {
                    element += it.toString()
                }

                if (it == ')') {
                    parenthesisStack.removeLastOrNull()
                        ?: throw IllegalArgumentException("Unexpected close bracket")
                    if (parenthesisStack.isEmpty()) pushArgumentIfNotEmpty()
                }
            }

            if (parenthesisStack.isNotEmpty()) throw IllegalArgumentException("Close bracket required but not found")

            pushArgumentIfNotEmpty()

            return arguments
        }

        private fun parseExpressionTreeNode(input: String): ExpressionTreeNode {
            if (input[0] != '(') return IntegerNode(input.toInt())

            val arguments = parseArguments(input.substring(1, input.length - 1)) // Remove surrounding parenthesis

            val operation = arguments[0]
            val firstNode = parseExpressionTreeNode(arguments[1])
            val secondNode = parseExpressionTreeNode(arguments[2])

            return when (operation) {
                "+" -> AdditionNode(firstNode, secondNode)
                "-" -> SubtractionNode(firstNode, secondNode)
                "*" -> MultiplicationNode(firstNode, secondNode)
                "/" -> DivisionNode(firstNode, secondNode)
                else -> throw IllegalArgumentException("Operation not found: $operation")
            }
        }

        fun parse(input: String): ExpressionTree {
            if (input.trim().isEmpty()) throw IllegalArgumentException("Can't make ExpressionTree from empty input")
            return ExpressionTree(parseExpressionTreeNode(input.trim()))
        }
    }
}
