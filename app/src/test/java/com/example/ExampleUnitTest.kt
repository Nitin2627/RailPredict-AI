package com.example

import com.example.engine.PredictionEngine
import com.example.engine.RailwayDataRepository
import com.example.engine.SimulationEngine
import com.example.model.TrainStatus
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testInitialTrainsCount() {
        val trains = RailwayDataRepository.getInitialTrains()
        assertEquals(10, trains.size)
        assertTrue(trains.any { it.trainNumber == "12345" })
    }

    @Test
    fun testPredictionEngineCalculation() {
        val train = RailwayDataRepository.getInitialTrains().first { it.trainNumber == "12345" }
        val prediction = PredictionEngine.calculateDynamicPrediction(train)

        assertNotNull(prediction.predictedETA)
        assertTrue(prediction.confidenceScore in 70..100)
        assertTrue(prediction.factorsList.isNotEmpty())
    }

    @Test
    fun testSimulationEngineHeavyCongestionEvent() {
        val train = RailwayDataRepository.getInitialTrains().first { it.trainNumber == "12345" }
        val (updatedTrain, alert) = SimulationEngine.applyDelayEvent(train, "HEAVY_CONGESTION")

        assertEquals(94, updatedTrain.congestionPercent)
        assertTrue(updatedTrain.currentDelayMinutes > train.currentDelayMinutes)
        assertEquals(TrainStatus.SEVERE_DELAY, updatedTrain.status)
        assertEquals("12345", alert.trainNumber)
        assertTrue(alert.title.contains("Heavy Congestion"))
    }

    @Test
    fun testSimulationEngineGreenWaveRecoveryEvent() {
        val train = RailwayDataRepository.getInitialTrains().first { it.trainNumber == "12345" }
        val (updatedTrain, alert) = SimulationEngine.applyDelayEvent(train, "GREEN_WAVE_RECOVERY")

        assertEquals(25, updatedTrain.congestionPercent)
        assertEquals(120, updatedTrain.speedKmh)
        assertTrue(alert.title.contains("Green Wave"))
    }

    @Test
    fun testTimeMathUtils() {
        val baseTime = "14:30"
        val addedTime = PredictionEngine.addMinutesToTime(baseTime, 45)
        assertEquals("15:15", addedTime)

        val rolloverTime = PredictionEngine.addMinutesToTime("23:45", 30)
        assertEquals("00:15", rolloverTime)
    }
}
