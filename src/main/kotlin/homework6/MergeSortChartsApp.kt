@file:Suppress("NoWildcardImports", "WildcardImport")

package homework6

import javafx.beans.property.*
import javafx.collections.MapChangeListener
import javafx.scene.chart.NumberAxis
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import java.lang.Runtime
import kotlin.math.pow
import tornadofx.*
import kotlin.system.measureTimeMillis

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

object AppModel {
    private val defaultMode = Mode.ByElements

    private const val defaultUseParallelMerge = true

    private const val defaultElementCount = 50000
    private const val minElementCount = 1000
    private const val elementCountStep = 1000
    private const val maxElementCount = 100000

    private val defaultWorkingThreads = Runtime.getRuntime().availableProcessors()
    private const val minWorkingThreads = 1
    private val maxWorkingThreads = Runtime.getRuntime().availableProcessors() * 2

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

    private val elementCountRange = (minElementCount..maxElementCount step elementCountStep).toList()
    private val workingThreadsRange = (minWorkingThreads..maxWorkingThreads).toList()
    private val recursionLimitRange = (minRecursionLimit..maxRecursionLimit)

    val modeProperty = SimpleObjectProperty(defaultMode)
    val mode: Mode by modeProperty

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

    val xAxisLabelProperty = SimpleStringProperty()
    val chartData = mutableMapOf<Number, Number>().asObservable()
}

class MainView : View("Merge Sort Charts") {
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
        bindSelected(model.modeProperty)
        selectWhere { it == model.mode }
    }
}

class ChartView : View() {
    private val model = AppModel

    override val root = areachart(null, NumberAxis(), NumberAxis()) {
        yAxis.animated = false
        yAxis.label = "Milliseconds"
        xAxis.animated = false
        xAxis.labelProperty().bind(model.xAxisLabelProperty)
    }

    init {
        val series = root.series("Elapsed sort time")
        model.chartData.addListener(MapChangeListener { change ->
            runLater {
                if (change.wasRemoved()) series.data.clear()
                if (change.wasAdded()) {
                    series.apply {
                        data(change.key, change.valueAdded)
                    }
                }
            }
        })
    }
}

class SettingsView : View() {
    companion object {
        private const val defaultPadding = 10
    }

    private val model = AppModel
    private val controller: ChartController by inject()

    private val disableElementCountProperty = model.modeProperty.booleanBinding { it == AppModel.Mode.ByElements }
    private val disableWorkingThreadsProperty =
        model.modeProperty.booleanBinding { it == AppModel.Mode.ByWorkingThreads }
    private val disableCreatedThreadsProperty =
        model.modeProperty.booleanBinding { it == AppModel.Mode.ByCreatedThreads }

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
            button("Build chart") {
                useMaxWidth = true
                action {
                    settingsDisableProperty.value = true
                    runAsync {
                        controller.buildChart()
                    } ui {
                        settingsDisableProperty.value = false
                    }
                }
            }
        }
    }

    private val settingsDisableProperty = root.disableProperty()
}

class ChartController : Controller() {
    private val model = AppModel

    private fun buildChartByElements() {
        runLater { model.xAxisLabelProperty.value = "Elements" }
        val sorter = MergeSorter<Int>(
            model.createdThreads.recursionLimit,
            model.workingThreads,
            model.useParallelMerge
        )
        for (elementCount in model.elementCountParameter.range) {
            val list = (1..elementCount as Int).shuffled()
            val elapsedTime = measureTimeMillis { sorter.sort(list) }
            model.chartData[elementCount] = elapsedTime
        }
    }

    private fun buildChartByWorkingThreads() {
        runLater { model.xAxisLabelProperty.value = "Working threads" }
        val list = (1..model.elementCount).shuffled()
        for (workingThreads in model.workingThreadsParameter.range) {
            val sorter = MergeSorter<Int>(
                model.createdThreads.recursionLimit,
                workingThreads as Int,
                model.useParallelMerge
            )
            val elapsedTime = measureTimeMillis { sorter.sort(list) }
            model.chartData[workingThreads] = elapsedTime
        }
    }

    private fun buildChartByCreatedThreads() {
        runLater { model.xAxisLabelProperty.value = "Created threads" }
        val list = (1..model.elementCount).shuffled()
        for (createdThreads in model.createdThreadsParameter.range) {
            val sorter = MergeSorter<Int>(
                createdThreads.recursionLimit,
                model.workingThreads,
                model.useParallelMerge
            )
            val elapsedTime = measureTimeMillis { sorter.sort(list) }
            model.chartData[createdThreads.threads] = elapsedTime
        }
    }

    fun buildChart() {
        model.chartData.clear()

        when (model.mode) {
            AppModel.Mode.ByElements -> buildChartByElements()
            AppModel.Mode.ByWorkingThreads -> buildChartByWorkingThreads()
            AppModel.Mode.ByCreatedThreads -> buildChartByCreatedThreads()
        }
    }
}

class MergeSortChartsApp : App(MainView::class)

fun main(args: Array<String>) {
    launch<MergeSortChartsApp>(args)
}
