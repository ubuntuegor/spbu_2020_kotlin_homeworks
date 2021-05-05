@file:Suppress("NoWildcardImports", "WildcardImport")
package homework6

import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ListChangeListener
import javafx.collections.MapChangeListener
import javafx.scene.chart.NumberAxis
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import kotlin.math.pow
import tornadofx.*
import kotlin.system.measureTimeMillis

object AppModel {
    private val MODE_DEFAULT = Mode.ByElements

    private const val defaultUseCoroutines = false
    private const val USE_PARALLEL_MERGE_DEFAULT = true

    private const val ELEMENT_COUNT_DEFAULT = 50_000
    private const val ELEMENT_COUNT_MIN = 1000
    private const val ELEMENT_COUNT_STEP = 1000
    private const val ELEMENT_COUNT_MAX = 100_000

    private const val RECURSION_LIMIT_DEFAULT = 3
    private const val RECURSION_LIMIT_MIN = 0
    private const val RECURSION_LIMIT_MAX = 8

    enum class Mode {
        ByElements {
            override fun toString() = "By elements"
        },
        ByCreatedThreads {
            override fun toString() = "By created threads"
        }
    }

    open class RangedParameter<T>(val range: List<T>, default: T) {
        open val property: Property<T> = SimpleObjectProperty(default)
    }

    class IntegerRangedParameter(range: List<Int>, default: Int) : RangedParameter<Number>(range, default) {
        override val property = SimpleIntegerProperty(default)
    }

    private val ELEMENT_COUNT_RANGE = (ELEMENT_COUNT_MIN..ELEMENT_COUNT_MAX step ELEMENT_COUNT_STEP).toList()
    private val RECURSION_LIMIT_RANGE = (RECURSION_LIMIT_MIN..RECURSION_LIMIT_MAX).toList()

    val selectedModeProperty = SimpleObjectProperty(MODE_DEFAULT)
    val selectedMode: Mode by selectedModeProperty

    val useCoroutinesProperty = SimpleBooleanProperty(defaultUseCoroutines)
    val useCoroutines by useCoroutinesProperty

    val useParallelMergeProperty = SimpleBooleanProperty(USE_PARALLEL_MERGE_DEFAULT)
    val useParallelMerge by useParallelMergeProperty

    val elementCountParameter = IntegerRangedParameter(ELEMENT_COUNT_RANGE, ELEMENT_COUNT_DEFAULT)
    val elementCount by elementCountParameter.property

    data class RecursionThreadsWrapper(val recursionLimit: Int) {
        val threads: Int
            get() = (2.0).pow(recursionLimit).toInt()

        override fun toString() = threads.toString()
    }

    val createdThreadsParameter = RangedParameter(
        RECURSION_LIMIT_RANGE.map { RecursionThreadsWrapper(it) }, RecursionThreadsWrapper(RECURSION_LIMIT_DEFAULT)
    )
    val createdThreads: RecursionThreadsWrapper by createdThreadsParameter.property

    class Graph(val name: String) {
        val data = mutableMapOf<Number, Number>().asObservable()
    }

    object Chart {
        val modeProperty = SimpleObjectProperty<Mode>()
        var mode: Mode? by modeProperty
        val graphs = mutableListOf<Graph>().asObservable()
    }

    fun getSorter(recursionLimit: Int, workingThreads: Int, useParallelMerge: Boolean): Sorter<Int> =
        if (useCoroutines) CoroutinesMergeSorter(recursionLimit, workingThreads, useParallelMerge)
        else MergeSorter(recursionLimit, workingThreads, useParallelMerge)
}

class MainView : View("Merge Sort Chart") {
    override val root = borderpane {
        left<ModeSelectorView>()
        center<ChartView>()
        right<SettingsView>()
    }
}

class ModeSelectorView : View() {
    private val model = AppModel
    private val modes = AppModel.Mode.values().toList()

    override val root = listview(modes.asObservable()) {
        bindSelected(model.selectedModeProperty)
        selectWhere { it == model.selectedMode }
    }
}

class ChartView : View() {
    private val graphs = AppModel.Chart.graphs

    private val xAxisLabelProperty = AppModel.Chart.modeProperty.stringBinding {
        when (it) {
            AppModel.Mode.ByElements -> "Number of elements"
            AppModel.Mode.ByCreatedThreads -> "Number of created threads"
            null -> null
        }
    }

    override val root = areachart(null, NumberAxis(), NumberAxis()) {
        yAxis.animated = false
        yAxis.label = "Milliseconds"
        xAxis.animated = false
        xAxis.labelProperty().bind(xAxisLabelProperty)
    }

    init {
        graphs.addListener(ListChangeListener { graphsChange ->
            while (graphsChange.next()) {
                runLater {
                    if (graphsChange.wasRemoved()) root.data.clear()
                    if (graphsChange.wasAdded()) addGraphs(graphsChange.addedSubList)
                }
            }
        })
    }

    private fun addGraphs(graphs: List<AppModel.Graph>) {
        graphs.forEach {
            val series = root.series(it.name)
            it.data.addListener(MapChangeListener { dataChange ->
                runLater {
                    if (dataChange.wasAdded()) series.apply { data(dataChange.key, dataChange.valueAdded) }
                }
            })
        }
    }
}

class SettingsView : View() {
    private val model = AppModel
    private val controller: ChartController by inject()

    private val disableElementCountProperty =
        model.selectedModeProperty.booleanBinding { it == AppModel.Mode.ByElements }
    private val disableCreatedThreadsProperty =
        model.selectedModeProperty.booleanBinding { it == AppModel.Mode.ByCreatedThreads }

    private val chartNotEmptyProperty = AppModel.Chart.modeProperty.booleanBinding { it != null }
    private val disableAddingGraphsProperty =
        AppModel.selectedModeProperty.isNotEqualTo(AppModel.Chart.modeProperty)
            .and(chartNotEmptyProperty)

    override val root: VBox = vbox {
        paddingAll = DEFAULT_PADDING

        vbox {
            vboxConstraints {
                vGrow = Priority.ALWAYS
            }

            vbox {
                spacing = defaultPadding.toDouble()
                checkbox("Use coroutines", model.useCoroutinesProperty)
                checkbox("Use parallel merge", model.useParallelMergeProperty)
            }

            label("Number of elements:")
            combobox(model.elementCountParameter.property, model.elementCountParameter.range) {
                disableProperty().bind(disableElementCountProperty)
            }

            label("Number of created threads:")
            combobox(model.createdThreadsParameter.property, model.createdThreadsParameter.range) {
                disableProperty().bind(disableCreatedThreadsProperty)
            }
            text("Warning: too many created threads can\ncause various exceptions.") {
                fill = Color.GREY
            }

            children.filterIsInstance<Label>().forEach {
                it.apply {
                    paddingTop = DEFAULT_PADDING
                }
            }
            children.filterIsInstance<ComboBox<Any>>().forEach {
                it.apply {
                    useMaxWidth = true
                }
            }
        }

        vbox {
            spacing = DEFAULT_PADDING.toDouble()

            button("Clear") {
                useMaxWidth = true
                action {
                    controller.clear()
                }
            }
            button("Add another graph") {
                useMaxWidth = true
                disableProperty().bind(disableAddingGraphsProperty)
                action {
                    settingsDisableProperty.value = true
                    runAsync {
                        controller.buildGraph()
                    } finally {
                        settingsDisableProperty.value = false
                    }
                }
            }
            button("Rebuild graph") {
                useMaxWidth = true
                style {
                    fontWeight = FontWeight.BOLD
                    textFill = Color.WHITE
                    backgroundColor += Color.DODGERBLUE
                }
                action {
                    settingsDisableProperty.value = true
                    runAsync {
                        controller.clear()
                        controller.buildGraph()
                    } finally {
                        settingsDisableProperty.value = false
                    }
                }
            }
        }
    }

    private val settingsDisableProperty = root.disableProperty()

    companion object {
        private const val DEFAULT_PADDING = 10
    }
}

class ChartController : Controller() {
    private val model = AppModel
    private val chart = AppModel.Chart

    private val elementsString: String
        get() = "${model.elementCount} elements"
    private val createdThreadsString: String
        get() = "${model.createdThreads} threads"
    private val parallelMergeString: String
        get() = if (model.useParallelMerge) "parallel merge" else "normal merge"
    private val coroutinesString: String
        get() = if (model.useCoroutines) "coroutines" else "threads"

    fun clear() {
        runLater { chart.mode = null }
        chart.graphs.clear()
    }

    private fun createRandomList(size: Int) = (1..size).shuffled()

    private fun buildGraphByElements() {
        val chartSeries = AppModel.Graph("$createdThreadsString, $parallelMergeString, $coroutinesString")
        chart.graphs.add(chartSeries)

        val sorter = model.getSorter(model.createdThreads.recursionLimit, model.useParallelMerge)
        for (elementCount in model.elementCountParameter.range) {
            val list = createRandomList(elementCount as Int)
            val elapsedTime = measureTimeMillis { sorter.sort(list) }
            chartSeries.data[elementCount] = elapsedTime
        }
    }

    private fun buildGraphByCreatedThreads() {
        val chartSeries = AppModel.Graph("$elementsString, $parallelMergeString, $coroutinesString")
        chart.graphs.add(chartSeries)

        val list = createRandomList(model.elementCount)
        for (createdThreads in model.createdThreadsParameter.range) {
            val sorter = model.getSorter(createdThreads.recursionLimit, model.useParallelMerge)
            val elapsedTime = measureTimeMillis { sorter.sort(list) }
            chartSeries.data[createdThreads.threads] = elapsedTime
        }
    }

    fun buildGraph() {
        runLater { AppModel.Chart.mode = model.selectedMode }
        when (model.selectedMode) {
            AppModel.Mode.ByElements -> buildGraphByElements()
            AppModel.Mode.ByCreatedThreads -> buildGraphByCreatedThreads()
        }
    }
}

class MergeSortChartsApp : App(MainView::class)

fun main(args: Array<String>) {
    launch<MergeSortChartsApp>(args)
}
