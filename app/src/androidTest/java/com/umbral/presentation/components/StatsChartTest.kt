package com.umbral.presentation.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.umbral.presentation.ui.components.ChartData
import com.umbral.presentation.ui.components.StatsChart
import org.junit.Rule
import org.junit.Test

class StatsChartTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testData = listOf(
        ChartData(label = "Instagram", value = 45f, maxValue = 100f),
        ChartData(label = "TikTok", value = 30f, maxValue = 100f),
        ChartData(label = "Twitter", value = 15f, maxValue = 100f)
    )

    @Test
    fun statsChart_displaysTitle() {
        composeTestRule.setContent {
            StatsChart(
                title = "Uso de Apps",
                data = testData
            )
        }

        composeTestRule.onNodeWithText("Uso de Apps").assertIsDisplayed()
    }

    @Test
    fun statsChart_displaysAllLabels() {
        composeTestRule.setContent {
            StatsChart(
                title = "Uso de Apps",
                data = testData
            )
        }

        composeTestRule.onNodeWithText("Instagram").assertIsDisplayed()
        composeTestRule.onNodeWithText("TikTok").assertIsDisplayed()
        composeTestRule.onNodeWithText("Twitter").assertIsDisplayed()
    }

    @Test
    fun statsChart_displaysAllValues() {
        composeTestRule.setContent {
            StatsChart(
                title = "Uso de Apps",
                data = testData
            )
        }

        composeTestRule.onNodeWithText("45").assertIsDisplayed()
        composeTestRule.onNodeWithText("30").assertIsDisplayed()
        composeTestRule.onNodeWithText("15").assertIsDisplayed()
    }

    @Test
    fun statsChart_emptyData_displaysEmptyMessage() {
        composeTestRule.setContent {
            StatsChart(
                title = "Uso de Apps",
                data = emptyList()
            )
        }

        composeTestRule.onNodeWithText("No hay datos disponibles").assertIsDisplayed()
    }

    @Test
    fun statsChart_emptyData_displaysCustomEmptyMessage() {
        composeTestRule.setContent {
            StatsChart(
                title = "Uso de Apps",
                data = emptyList(),
                emptyMessage = "Sin información"
            )
        }

        composeTestRule.onNodeWithText("Sin información").assertIsDisplayed()
    }

    @Test
    fun statsChart_emptyData_hidesLabels() {
        composeTestRule.setContent {
            StatsChart(
                title = "Uso de Apps",
                data = emptyList()
            )
        }

        // Should not show any labels when empty
        composeTestRule.onNodeWithText("Instagram").assertDoesNotExist()
        composeTestRule.onNodeWithText("TikTok").assertDoesNotExist()
    }

    @Test
    fun statsChart_singleItem_displays() {
        val singleData = listOf(
            ChartData(label = "Solo uno", value = 50f, maxValue = 100f)
        )

        composeTestRule.setContent {
            StatsChart(
                title = "Uso de Apps",
                data = singleData
            )
        }

        composeTestRule.onNodeWithText("Solo uno").assertIsDisplayed()
        composeTestRule.onNodeWithText("50").assertIsDisplayed()
    }

    @Test
    fun statsChart_zeroValue_displays() {
        val zeroData = listOf(
            ChartData(label = "Cero", value = 0f, maxValue = 100f)
        )

        composeTestRule.setContent {
            StatsChart(
                title = "Uso de Apps",
                data = zeroData
            )
        }

        composeTestRule.onNodeWithText("Cero").assertIsDisplayed()
        composeTestRule.onNodeWithText("0").assertIsDisplayed()
    }

    @Test
    fun statsChart_maxValue_displays() {
        val maxData = listOf(
            ChartData(label = "Máximo", value = 100f, maxValue = 100f)
        )

        composeTestRule.setContent {
            StatsChart(
                title = "Uso de Apps",
                data = maxData
            )
        }

        composeTestRule.onNodeWithText("Máximo").assertIsDisplayed()
        composeTestRule.onNodeWithText("100").assertIsDisplayed()
    }

    @Test
    fun statsChart_valueExceedsMax_stillDisplays() {
        val exceedingData = listOf(
            ChartData(label = "Excede", value = 150f, maxValue = 100f)
        )

        composeTestRule.setContent {
            StatsChart(
                title = "Uso de Apps",
                data = exceedingData
            )
        }

        composeTestRule.onNodeWithText("Excede").assertIsDisplayed()
        composeTestRule.onNodeWithText("150").assertIsDisplayed()
    }

    @Test
    fun statsChart_decimalValue_displaysAsInteger() {
        val decimalData = listOf(
            ChartData(label = "Decimal", value = 45.7f, maxValue = 100f)
        )

        composeTestRule.setContent {
            StatsChart(
                title = "Uso de Apps",
                data = decimalData
            )
        }

        // Should display as integer (45)
        composeTestRule.onNodeWithText("45").assertIsDisplayed()
    }

    @Test
    fun statsChart_longLabel_displays() {
        val longLabelData = listOf(
            ChartData(
                label = "Esta es una etiqueta muy larga",
                value = 25f,
                maxValue = 100f
            )
        )

        composeTestRule.setContent {
            StatsChart(
                title = "Uso de Apps",
                data = longLabelData
            )
        }

        composeTestRule.onNodeWithText(
            "Esta es una etiqueta muy larga",
            substring = true
        ).assertIsDisplayed()
    }

    @Test
    fun statsChart_multipleItemsSameValue_displays() {
        val sameValueData = listOf(
            ChartData(label = "App 1", value = 50f, maxValue = 100f),
            ChartData(label = "App 2", value = 50f, maxValue = 100f),
            ChartData(label = "App 3", value = 50f, maxValue = 100f)
        )

        composeTestRule.setContent {
            StatsChart(
                title = "Uso de Apps",
                data = sameValueData
            )
        }

        composeTestRule.onNodeWithText("App 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("App 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("App 3").assertIsDisplayed()
    }

    @Test
    fun statsChart_differentMaxValues_displays() {
        val differentMaxData = listOf(
            ChartData(label = "App 1", value = 50f, maxValue = 100f),
            ChartData(label = "App 2", value = 25f, maxValue = 50f),
            ChartData(label = "App 3", value = 10f, maxValue = 20f)
        )

        composeTestRule.setContent {
            StatsChart(
                title = "Uso de Apps",
                data = differentMaxData
            )
        }

        composeTestRule.onNodeWithText("App 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("App 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("App 3").assertIsDisplayed()
    }

    @Test
    fun statsChart_negativeValue_displays() {
        val negativeData = listOf(
            ChartData(label = "Negativo", value = -10f, maxValue = 100f)
        )

        composeTestRule.setContent {
            StatsChart(
                title = "Uso de Apps",
                data = negativeData
            )
        }

        // Should still display (even though negative doesn't make sense for a chart)
        composeTestRule.onNodeWithText("Negativo").assertIsDisplayed()
    }

    @Test
    fun statsChart_manyItems_displaysAll() {
        val manyItems = (1..10).map {
            ChartData(label = "App $it", value = it * 10f, maxValue = 100f)
        }

        composeTestRule.setContent {
            StatsChart(
                title = "Uso de Apps",
                data = manyItems
            )
        }

        // Check first and last items
        composeTestRule.onNodeWithText("App 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("App 10").assertIsDisplayed()
    }
}
