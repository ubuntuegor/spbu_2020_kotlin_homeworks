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
import java.lang.Runtime
import kotlin.math.pow
import tornadofx.*
import kotlin.system.measureTimeMillis

object AppModel {
    private val defaultMode = Mode.ByElements

    private const val defaultUseParallelMerge = true

    private const val defaultElementCount = 50000
    private const val minElementCount = 1000
    private const val elementCountStep = 1000
    private const val maxElementCount = 100000

    private val defaultWorkingThreads = Runtime.getRuntime().availableProcessors()
    private const val minWorkingThreads = 1
    private const val maxWorkingThreads = 128

    private const val defaultRecursionLimit = 3
    private const val minRecursionLimit = 0
    private const val maxRecursionLimit = 8

    enum class Mode {
        ByElements {
            override fun toString() = "By elements"
        },
        ByWorkingThreads {
            override fun toString() = "By working threads"
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

    data class RecursionThreadsWrapper(val recursionLimit: Int) {
        val threads: Int
            get() = (2.0).pow(recursionLimit).toInt()

        override fun toString() = threads.toString()
    }

    private val elementCountRange = (minElementCount..maxElementCount step elementCountStep).toList()
    private val workingThreadsRange = (minWorkingThreads..maxWorkingThreads).toList()
    private val recursionLimitRange = (minRecursionLimit..maxRecursionLimit)

    val selectedModeProperty = SimpleObjectProperty(defaultMode)
    val selectedMode: Mode by selectedModeProperty

    val useParallelMergeProperty = SimpleBooleanProperty(defaultUseParallelMerge)
    val useParallelMerge by useParallelMergeProperty

    val elementCountParameter = IntegerRangedParameter(elementCountRange, defaultElementCount)
    val elementCount by elementCountParameter.property

    val workingThreadsParameter = IntegerRangedParameter(workingThreadsRange, defaultWorkingThreads)
    val workingThreads by workingThreadsParameter.property

    val createdThreadsParameter = RangedParameter(
        recursionLimitRange.map { RecursionThreadsWrapper(it) }, RecursionThreadsWrapper(defaultRecursionLimit)
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
            AppModel.Mode.ByWorkingThreads -> "Number of working threads"
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
}

class SettingsView : View() {
    private val model = AppModel
    private val controller: ChartController by inject()

    private val disableElementCountProperty =
        model.selectedModeProperty.booleanBinding { it == AppModel.Mode.ByElements }
    private val disableWorkingThreadsProperty =
        model.selectedModeProperty.booleanBinding { it == AppModel.Mode.ByWorkingThreads }
    private val disableCreatedThreadsProperty =
        model.selectedModeProperty.booleanBinding { it == AppModel.Mode.ByCreatedThreads }

    private val chartNotEmptyProperty = AppModel.Chart.modeProperty.booleanBinding { it != null }
    private val disableAddingGraphsProperty =
        AppModel.selectedModeProperty.isNotEqualTo(AppModel.Chart.modeProperty)
            .and(chartNotEmptyProperty)

    override val root: VBox = vbox {
        paddingAll = defaultPadding

        vbox {
            vboxConstraints {
                vGrow = Priority.ALWAYS
            }

            checkbox("Use parallel merge", model.useParallelMergeProperty)

            label("Number of elements:")
            combobox(model.elementCountParameter.property, model.elementCountParameter.range) {
                disableProperty().bind(disableElementCountProperty)
            }

            label("Number of working threads:")
            combobox(model.workingThreadsParameter.property, model.workingThreadsParameter.range) {
                disableProperty().bind(disableWorkingThreadsProperty)
            }

            label("Number of created threads:")
            combobox(model.createdThreadsParameter.property, model.createdThreadsParameter.range) {
                disableProperty().bind(disableCreatedThreadsProperty)
            }
            text("Warning: too many created threads can\ncause OutOfMemoryException.") {
                fill = Color.GREY
            }

            children.filterIsInstance<Label>().forEach {
                it.apply {
                    paddingTop = defaultPadding
                }
            }
            children.filterIsInstance<ComboBox<Any>>().forEach {
                it.apply {
                    useMaxWidth = true
                }
            }
        }

        vbox {
            spacing = defaultPadding.toDouble()

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
        private const val defaultPadding = 10
    }
}

class ChartController : Controller() {
    private val model = AppModel
    private val chart = AppModel.Chart

    private val elementsString: String
        get() = "${model.elementCount} elements"
    private val workingThreadsString: String
        get() = "${model.workingThreads} working"
    private val createdThreadsString: String
        get() = "${model.createdThreads} created"
    private val parallelMergeString: String
        get() = if (model.useParallelMerge) "parallel merge" else "normal merge"

    fun clear() {
        runLater { chart.mode = null }
        chart.graphs.clear()
    }

    private fun createRandomList(size: Int) = (1..size).shuffled()

    private fun buildGraphByElements() {
        val chartSeries = AppModel.Graph("$workingThreadsString, $createdThreadsString, $parallelMergeString")
        chart.graphs.add(chartSeries)
        val sorter = MergeSorter<Int>(
            model.createdThreads.recursionLimit,
            model.workingThreads,
            model.useParallelMerge
        )
        for (elementCount in model.elementCountParameter.range) {
            val list = createRandomList(elementCount as Int)
            val elapsedTime = measureTimeMillis { sorter.sort(list) }
            chartSeries.data[elementCount] = elapsedTime
        }
    }

    private fun buildGraphByWorkingThreads() {
        val chartSeries = AppModel.Graph("$elementsString, $createdThreadsString, $parallelMergeString")
        chart.graphs.add(chartSeries)
        val list = createRandomList(model.elementCount)
        for (workingThreads in model.workingThreadsParameter.range) {
            val sorter = MergeSorter<Int>(
                model.createdThreads.recursionLimit,
                workingThreads as Int,
                model.useParallelMerge
            )
            val elapsedTime = measureTimeMillis { sorter.sort(list) }
            chartSeries.data[workingThreads] = elapsedTime
        }
    }

    private fun buildGraphByCreatedThreads() {
        val chartSeries = AppModel.Graph("$elementsString, $workingThreadsString, $parallelMergeString")
        chart.graphs.add(chartSeries)
        val list = createRandomList(model.elementCount)
        for (createdThreads in model.createdThreadsParameter.range) {
            val sorter = MergeSorter<Int>(
                createdThreads.recursionLimit,
                model.workingThreads,
                model.useParallelMerge
            )
            val elapsedTime = measureTimeMillis { sorter.sort(list) }
            chartSeries.data[createdThreads.threads] = elapsedTime
        }
    }

    fun buildGraph() {
        runLater { AppModel.Chart.mode = model.selectedMode }
        when (model.selectedMode) {
            AppModel.Mode.ByElements -> buildGraphByElements()
            AppModel.Mode.ByWorkingThreads -> buildGraphByWorkingThreads()
            AppModel.Mode.ByCreatedThreads -> buildGraphByCreatedThreads()
        }
    }
}

class MergeSortChartsApp : App(MainView::class)

fun main(args: Array<String>) {
    launch<MergeSortChartsApp>(args)
}
