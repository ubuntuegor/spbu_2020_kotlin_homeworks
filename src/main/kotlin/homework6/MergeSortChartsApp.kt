@file:Suppress("NoWildcardImports", "WildcardImport")

package homework6

import javafx.beans.property.*
import javafx.collections.MapChangeListener
import javafx.scene.chart.NumberAxis
import javafx.scene.control.Label
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import java.lang.Runtime
import kotlin.math.pow
import tornadofx.*
import kotlin.system.measureTimeMillis

open class Setting<T>(val range: List<T>, default: T) {
    open val property: Property<T> = SimpleObjectProperty(default)
}

class IntegerSetting(range: List<Int>, default: Int) : Setting<Number>(range, default) {
    override val property = SimpleIntegerProperty(default)
}

data class RecursionThreads(val recursionLimit: Int) {
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

    private const val defaultRecursionLimit = 5
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

    val elementCountSetting = IntegerSetting(elementCountRange, defaultElementCount)
    val elementCount by elementCountSetting.property

    val workingThreadsSetting = IntegerSetting(workingThreadsRange, defaultWorkingThreads)
    val workingThreads by workingThreadsSetting.property

    val createdThreadsSetting = Setting(
        recursionLimitRange.map { RecursionThreads(it) }, RecursionThreads(defaultRecursionLimit)
    )
    val createdThreads: RecursionThreads by createdThreadsSetting.property

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
    private val model = AppModel
    private val controller: MyController by inject()

    private val disableElementCountProperty = model.modeProperty.booleanBinding { it == AppModel.Mode.ByElements }
    private val disableWorkingThreadsProperty =
        model.modeProperty.booleanBinding { it == AppModel.Mode.ByWorkingThreads }
    private val disableCreatedThreadsProperty =
        model.modeProperty.booleanBinding { it == AppModel.Mode.ByCreatedThreads }

    override val root: VBox = vbox {
        addClass(AppStyles.settingsView)
        vbox {
            vboxConstraints {
                vGrow = Priority.ALWAYS
            }

            checkbox("Use parallel merge", model.useParallelMergeProperty)

            label("Number of elements:")
            combobox(model.elementCountSetting.property, model.elementCountSetting.range) {
                useMaxWidth = true
                disableProperty().bind(disableElementCountProperty)
            }

            label("Number of working threads:")
            combobox(model.workingThreadsSetting.property, model.workingThreadsSetting.range) {
                useMaxWidth = true
                disableProperty().bind(disableWorkingThreadsProperty)
            }

            label("Number of created threads:")
            combobox(model.createdThreadsSetting.property, model.createdThreadsSetting.range) {
                useMaxWidth = true
                disableProperty().bind(disableCreatedThreadsProperty)
            }
            text("Warning: too many created threads can\ncause OutOfMemoryException.") {
                fill = Color.GREY
            }

            children.filterIsInstance<Label>().addClass(AppStyles.settingsLabel)
        }

        vbox {
            button("Build chart") {
                useMaxWidth = true
                action {
                    runAsync {
                        settingsDisableProperty.value = true
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

class MyController : Controller() {
    private val model = AppModel

    private fun buildChartByElements() {
        runLater { model.xAxisLabelProperty.value = "Elements" }
        val sorter = MergeSorter<Int>(
            model.createdThreads.recursionLimit,
            model.workingThreads,
            model.useParallelMerge
        )
        for (elementCount in model.elementCountSetting.range) {
            val list = (1..elementCount as Int).shuffled()
            val elapsedTime = measureTimeMillis { sorter.sort(list) }
            model.chartData[elementCount] = elapsedTime
        }
    }

    private fun buildChartByWorkingThreads() {
        runLater { model.xAxisLabelProperty.value = "Working threads" }
        val list = (1..model.elementCount).shuffled()
        for (workingThreads in model.workingThreadsSetting.range) {
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
        for (createdThreads in model.createdThreadsSetting.range) {
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

class AppStyles : Stylesheet() {
    companion object {
        val settingsView by cssclass()
        val settingsLabel by cssclass()

        private const val standardPadding = 10
    }

    init {
        settingsView {
            padding = box(standardPadding.px)
        }
        settingsLabel {
            padding = box(standardPadding.px, 0.px, 0.px, 0.px)
        }
    }
}

class MergeSortChartsApp : App(MainView::class, AppStyles::class)

fun main(args: Array<String>) {
    launch<MergeSortChartsApp>(args)
}
