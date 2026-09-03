package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.engine.PredictionEngine
import com.example.engine.RailwayDataRepository
import com.example.engine.SimulationEngine
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RailPredictViewModel : ViewModel() {

    private val _trains = MutableStateFlow<List<Train>>(RailwayDataRepository.getInitialTrains())
    val trains: StateFlow<List<Train>> = _trains.asStateFlow()

    private val _selectedTrainNumber = MutableStateFlow("12345")
    val selectedTrainNumber: StateFlow<String> = _selectedTrainNumber.asStateFlow()

    val selectedTrain: StateFlow<Train> = combine(_trains, _selectedTrainNumber) { trainList, selectedNum ->
        trainList.find { it.trainNumber == selectedNum } ?: trainList.first()
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        _trains.value.first()
    )

    private val _alerts = MutableStateFlow<List<RailAlert>>(RailwayDataRepository.INITIAL_ALERTS)
    val alerts: StateFlow<List<RailAlert>> = _alerts.asStateFlow()

    // Train Search State
    private val _searchTrainNumber = MutableStateFlow("")
    val searchTrainNumber = _searchTrainNumber.asStateFlow()

    private val _searchFromStation = MutableStateFlow("")
    val searchFromStation = _searchFromStation.asStateFlow()

    private val _searchToStation = MutableStateFlow("")
    val searchToStation = _searchToStation.asStateFlow()

    val stationOptions = RailwayDataRepository.STATIONS

    // Simulation Engine State
    private val _isSimulationRunning = MutableStateFlow(true)
    val isSimulationRunning: StateFlow<Boolean> = _isSimulationRunning.asStateFlow()

    private var simulationJob: Job? = null

    // Hackathon Presentation / Demo Mode State
    private val _isDemoModeActive = MutableStateFlow(false)
    val isDemoModeActive: StateFlow<Boolean> = _isDemoModeActive.asStateFlow()

    private val _alertCategoryFilter = MutableStateFlow(AlertCategory.ALL)
    val alertCategoryFilter = _alertCategoryFilter.asStateFlow()

    private val _apiDataFeedMode = MutableStateFlow("SIMULATED")
    val apiDataFeedMode = _apiDataFeedMode.asStateFlow()

    private val _zoneFilter = MutableStateFlow("ALL")
    val zoneFilter = _zoneFilter.asStateFlow()

    private val _delayFilter = MutableStateFlow("ALL")
    val delayFilter = _delayFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _whatIfCongestion = MutableStateFlow(0)
    val whatIfCongestion = _whatIfCongestion.asStateFlow()

    private val _whatIfWeather = MutableStateFlow("Clear")
    val whatIfWeather = _whatIfWeather.asStateFlow()

    private val _whatIfTsrMinutes = MutableStateFlow(0)
    val whatIfTsrMinutes = _whatIfTsrMinutes.asStateFlow()

    private val _whatIfPrecedingMinutes = MutableStateFlow(0)
    val whatIfPrecedingMinutes = _whatIfPrecedingMinutes.asStateFlow()

    val networkOverview = MutableStateFlow(
        NetworkOverviewMetrics(
            activeTrains = 24, onTimeTrains = 18, delayedTrains = 6, criticalDelays = 2,
            avgPredictionAccuracy = 92.4f, networkCongestionLevel = "68% (Elevated)",
            avgEtaErrorMinutes = 4.8f, traditionalEtaErrorMinutes = 14.6f,
            maeMinutes = 4.2f, rmseMinutes = 6.7f
        )
    ).asStateFlow()

    val congestedSections = MutableStateFlow(RailwayDataRepository.CONGESTED_SECTIONS).asStateFlow()
    val hotspotStations = MutableStateFlow(RailwayDataRepository.HOTSPOT_STATIONS).asStateFlow()
    val delayPropagationNodes = MutableStateFlow(RailwayDataRepository.DELAY_PROPAGATION_CHAIN).asStateFlow()

    val filteredTrains: StateFlow<List<Train>> = combine(_trains, _searchQuery, _zoneFilter, _delayFilter) { trains, query, zone, delay ->
        trains.filter { train ->
            (query.isEmpty() || train.trainNumber.contains(query, ignoreCase = true) || train.trainName.contains(query, ignoreCase = true)) &&
            (zone == "ALL" || train.zone == zone) &&
            (delay == "ALL" || matchesDelayFilter(train, delay))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _trains.value)

    private fun matchesDelayFilter(train: Train, filter: String): Boolean {
        return when (filter) {
            "ON_TIME" -> train.currentDelayMinutes <= 0
            "MINOR" -> train.currentDelayMinutes in 1..20
            "MODERATE" -> train.currentDelayMinutes in 21..45
            "SEVERE" -> train.currentDelayMinutes > 45
            else -> true
        }
    }

    fun setAlertCategoryFilter(category: AlertCategory) { _alertCategoryFilter.value = category }
    fun setApiDataFeedMode(mode: String) { _apiDataFeedMode.value = mode }
    fun setZoneFilter(zone: String) { _zoneFilter.value = zone }
    fun setDelayFilter(filter: String) { _delayFilter.value = filter }
    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun dismissAlert(id: String) { _alerts.value = _alerts.value.filter { it.id != id } }
    fun toggleSimulation() { _isSimulationRunning.value = !_isSimulationRunning.value }

    fun updateWhatIfScenario(congestion: Int, weather: String, tsr: Int, preceding: Int) {
        _whatIfCongestion.value = congestion
        _whatIfWeather.value = weather
        _whatIfTsrMinutes.value = tsr
        _whatIfPrecedingMinutes.value = preceding
    }

    fun triggerSimulationEvent(type: String, trainNumber: String? = null) {
        viewModelScope.launch {
            val targetNum = trainNumber ?: _selectedTrainNumber.value
            _trains.value = _trains.value.map { train ->
                if (train.trainNumber == targetNum) {
                    val (updated, alert) = SimulationEngine.applyDelayEvent(train, type)
                    _alerts.value = listOf(alert) + _alerts.value
                    updated
                } else train
            }
        }
    }

    private val _demoStep = MutableStateFlow(1)
    val demoStep: StateFlow<Int> = _demoStep.asStateFlow()

    init {
        startSimulationLoop()
    }

    private fun startSimulationLoop() {
        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            while (true) {
                delay(1500L)
                if (_isSimulationRunning.value && !_isDemoModeActive.value) {
                    _trains.value = _trains.value.map { SimulationEngine.updateTrainTick(it) }
                }
            }
        }
    }

    fun setDemoStep(step: Int) {
        _demoStep.value = step
        // In a real app, we would update the train state to match the demo step
    }

    fun nextDemoStep() {
        if (_demoStep.value < 7) {
            _demoStep.value += 1
        }
    }

    fun prevDemoStep() {
        if (_demoStep.value > 1) {
            _demoStep.value -= 1
        }
    }

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _searchError = MutableStateFlow<String?>(null)
    val searchError = _searchError.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Train>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    fun selectTrain(trainNumber: String) {
        if (_trains.value.any { it.trainNumber == trainNumber }) {
            _selectedTrainNumber.value = trainNumber
        }
    }

    fun updateSearchTrainNumber(query: String) {
        _searchTrainNumber.value = query
    }

    fun updateSearchFromStation(station: String) {
        _searchFromStation.value = station
    }

    fun updateSearchToStation(station: String) {
        _searchToStation.value = station
    }

    fun findTrainByNumber() {
        val query = _searchTrainNumber.value.trim()
        if (query.isBlank() || !query.all { it.isDigit() }) {
            _searchError.value = "INVALID_NUMBER"
            return
        }
        
        viewModelScope.launch {
            _isSearching.value = true
            _searchError.value = null
            delay(800L) // Simulate network/search delay
            
            val found = _trains.value.find { it.trainNumber == query }
            if (found != null) {
                _selectedTrainNumber.value = found.trainNumber
                _isSearching.value = false
            } else {
                _searchError.value = "NOT_FOUND"
                _isSearching.value = false
            }
        }
    }

    fun findTrainByRoute() {
        if (_searchFromStation.value.isBlank() || _searchToStation.value.isBlank()) return

        viewModelScope.launch {
            _isSearching.value = true
            _searchError.value = null
            _searchResults.value = emptyList()
            delay(800L)

            val found = _trains.value.filter { 
                it.origin.name.contains(_searchFromStation.value, ignoreCase = true) &&
                it.destination.name.contains(_searchToStation.value, ignoreCase = true)
            }
            
            if (found.isNotEmpty()) {
                if (found.size == 1) {
                    _selectedTrainNumber.value = found.first().trainNumber
                    _isSearching.value = false
                } else {
                    _searchResults.value = found
                    _isSearching.value = false
                }
            } else {
                _searchError.value = "ROUTE_NOT_FOUND"
                _isSearching.value = false
            }
        }
    }

    fun clearSearchError() {
        _searchError.value = null
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }

    fun setDemoModeActive(active: Boolean) {
        _isDemoModeActive.value = active
        if (!active) {
            _trains.value = RailwayDataRepository.getInitialTrains()
        }
    }

    fun triggerDemoEvent(type: String) {
        // Logic to simulate events for demo
    }
}
