package homework5

class ExpressionTree(private val rootNode: ExpressionTreeNode) {
    fun calculateValue() = rootNode.value
    override fun toString() = rootNode.toString()

    companion object {
        private fun parseArguments(input: String): List<String> {
            var parenthesisBalance = 0
            val arguments = mutableListOf<String>()
            val element = StringBuilder()

            fun pushArgumentIfNotEmpty() {
                if (element.isNotEmpty()) {
                    arguments.add(element.toString())
                    element.clear()
                }
            }

            input.forEach {
                if (it == '(') {
                    if (parenthesisBalance == 0) pushArgumentIfNotEmpty()
                    parenthesisBalance++
                }

                if (it == ' ' && parenthesisBalance == 0) {
                    pushArgumentIfNotEmpty()
                } else {
                    element.append(it)
                }

                if (it == ')') {
                    parenthesisBalance--
                    if (parenthesisBalance == 0) pushArgumentIfNotEmpty()
                }
            }

            if (parenthesisBalance > 0) throw IllegalArgumentException("Close bracket required but not found")
            if (parenthesisBalance < 0) throw IllegalArgumentException("Unexpected close bracket")

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
