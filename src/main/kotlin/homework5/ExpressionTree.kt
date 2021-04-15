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
                    if (parenthesisBalance < 0) throw IllegalArgumentException("Unexpected close bracket")
                    if (parenthesisBalance == 0) pushArgumentIfNotEmpty()
                }
            }

            if (parenthesisBalance > 0) throw IllegalArgumentException("Close bracket required but not found")

            pushArgumentIfNotEmpty()

            return arguments
        }

        private fun parseExpressionTreeNode(input: String): ExpressionTreeNode {
            val textNode =
                if (input.startsWith('(') && input.endsWith(')')) input.drop(1).dropLast(1)
                else input

            return try {
                IntegerNode(textNode.toInt())
            } catch (e: NumberFormatException) {
                val arguments = parseArguments(textNode)

                if (arguments.size <= 2) {
                    throw IllegalArgumentException("Expression is neither an integer or a valid operation")
                }

                val operation = arguments[0]
                val firstNode = parseExpressionTreeNode(arguments[1])
                val secondNode = parseExpressionTreeNode(arguments[2])

                when (operation) {
                    "+" -> AdditionNode(firstNode, secondNode)
                    "-" -> SubtractionNode(firstNode, secondNode)
                    "*" -> MultiplicationNode(firstNode, secondNode)
                    "/" -> DivisionNode(firstNode, secondNode)
                    else -> throw IllegalArgumentException("Operation not found: $operation")
                }
            }
        }

        fun parse(input: String): ExpressionTree {
            if (input.trim().isEmpty()) throw IllegalArgumentException("Can't make ExpressionTree from empty input")
            return ExpressionTree(parseExpressionTreeNode(input.trim()))
        }
    }
}
