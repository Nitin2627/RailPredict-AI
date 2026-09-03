package com.example.engine

import com.example.model.*

object RailwayDataRepository {

    val STATIONS = listOf(
        Station("NDLS", "New Delhi", "Delhi", 28.6431, 77.2197, 16),
        Station("AGC", "Agra Cantt", "Uttar Pradesh", 27.1574, 78.0076, 6),
        Station("GWL", "Gwalior Junction", "Madhya Pradesh", 26.2183, 78.1828, 5),
        Station("VGLJ", "V Lakshmibai Jhansi", "Uttar Pradesh", 25.4484, 78.5685, 8),
        Station("BPL", "Bhopal Junction", "Madhya Pradesh", 23.2599, 77.4126, 6),
        Station("ET", "Itarsi Junction", "Madhya Pradesh", 22.6120, 77.7600, 8),
        Station("NGP", "Nagpur Junction", "Maharashtra", 21.1525, 79.0882, 8),
        Station("BPQ", "Balharshah Junction", "Maharashtra", 19.8510, 79.3510, 5),
        Station("WL", "Warangal", "Telangana", 17.9689, 79.5941, 4),
        Station("BZA", "Vijayawada Junction", "Andhra Pradesh", 16.5186, 80.6199, 10),
        Station("MAS", "Chennai Central", "Tamil Nadu", 13.0827, 80.2707, 12),
        Station("CNB", "Kanpur Central", "Uttar Pradesh", 26.4547, 80.3507, 10),
        Station("PRYJ", "Prayagraj Junction", "Uttar Pradesh", 25.4358, 81.8463, 10),
        Station("DDU", "Pt Deen Dayal Upadhyaya", "Uttar Pradesh", 25.2818, 83.1189, 8),
        Station("PNBE", "Patna Junction", "Bihar", 25.6022, 85.1376, 10),
        Station("HWH", "Howrah Junction", "West Bengal", 22.5850, 88.3426, 23),
        Station("ADI", "Ahmedabad Junction", "Gujarat", 23.0225, 72.5714, 12),
        Station("BRC", "Vadodara Junction", "Gujarat", 22.3106, 73.1812, 7),
        Station("ST", "Surat", "Gujarat", 21.2035, 72.8402, 4),
        Station("BCT", "Mumbai Central", "Maharashtra", 18.9696, 72.8193, 8),
        Station("CSMT", "Mumbai CSMT", "Maharashtra", 18.9402, 72.8356, 18),
        Station("PUNE", "Pune Junction", "Maharashtra", 18.5289, 73.8744, 6),
        Station("SBC", "KSR Bengaluru", "Karnataka", 12.9781, 77.5694, 10),
        Station("HYB", "Hyderabad Deccan", "Telangana", 17.3924, 78.4682, 6),
        Station("JP", "Jaipur Junction", "Rajasthan", 26.9181, 75.7878, 8)
    )

    fun getStation(code: String): Station =
        STATIONS.find { it.code.equals(code, ignoreCase = true) } ?: STATIONS[0]

    fun getInitialTrains(): List<Train> {
        val sNDLS = getStation("NDLS")
        val sAGC = getStation("AGC")
        val sGWL = getStation("GWL")
        val sVGLJ = getStation("VGLJ")
        val sBPL = getStation("BPL")
        val sET = getStation("ET")
        val sNGP = getStation("NGP")
        val sBPQ = getStation("BPQ")
        val sWL = getStation("WL")
        val sBZA = getStation("BZA")
        val sMAS = getStation("MAS")

        val sCNB = getStation("CNB")
        val sPRYJ = getStation("PRYJ")
        val sDDU = getStation("DDU")
        val sPNBE = getStation("PNBE")
        val sHWH = getStation("HWH")

        val sADI = getStation("ADI")
        val sBRC = getStation("BRC")
        val sST = getStation("ST")
        val sBCT = getStation("BCT")
        val sPUNE = getStation("PUNE")
        val sSBC = getStation("SBC")
        val sJP = getStation("JP")

        return listOf(
            // Train 1: 12345 Rajdhani Express (Delhi to Nagpur / Chennai corridor) - Main Featured Train
            Train(
                trainNumber = "12345",
                trainName = "Rajdhani Express",
                trainType = TrainType.RAJDHANI,
                zone = "NR",
                origin = sNDLS,
                destination = sNGP,
                currentStationName = "Bhopal Junction",
                nextStationName = "Itarsi Junction",
                currentPlatform = "1",
                nextPlatform = "2",
                destinationPlatform = "1",
                latitude = 23.2599,
                longitude = 77.4126,
                speedKmh = 74,
                maxSpeedKmh = 130,
                currentDelayMinutes = 24,
                scheduledDestinationETA = "22:15",
                predictedDestinationETA = "22:51",
                previousPredictedETA = "22:38",
                confidenceScore = 92,
                congestionPercent = 78,
                weatherCondition = "Clear (Visibility 4km)",
                status = TrainStatus.MODERATE_DELAY,
                totalDistanceKm = 1090,
                coveredDistanceKm = 705,
                precedingTrainInfo = "Freight G-889 running 12km ahead (+18m delay)",
                speedRestrictionNotice = "TSR 45 km/h at km 742/4 (Track maintenance)",
                delayFactors = listOf(
                    DelayReasonFactor("Section Congestion", 7, "CONGESTION", "High block density BPL-ET section (78% occupancy)"),
                    DelayReasonFactor("Speed Restriction (TSR)", 4, "SPEED_RESTRICTION", "45 km/h limit on bridge 118 near Hoshangabad"),
                    DelayReasonFactor("Preceding Train Delay", 3, "PRECEDING_TRAIN", "Goods train G-889 occupying primary line"),
                    DelayReasonFactor("Expected Recovery Margin", -1, "RECOVERY", "Scheduled slack time in ET-NGP section")
                ),
                route = listOf(
                    RouteStop(sNDLS, 1, "06:00", "06:00", "06:00", "06:00", "06:00", 0, 0, 0, StopStatus.PASSED, 99, "16", "Departed PF 16"),
                    RouteStop(sAGC, 2, "07:55", "08:00", "07:58", "08:03", "07:58", 195, 3, 5, StopStatus.PASSED, 98, "2", "Departed PF 2"),
                    RouteStop(sGWL, 3, "09:20", "09:25", "09:30", "09:35", "09:30", 313, 10, 5, StopStatus.PASSED, 96, "1", "Departed PF 1"),
                    RouteStop(sVGLJ, 4, "10:45", "10:55", "11:03", "11:13", "11:03", 410, 18, 10, StopStatus.PASSED, 95, "1", "Departed PF 1"),
                    RouteStop(sBPL, 5, "13:14", "13:20", "13:38", "13:46", "13:38", 705, 24, 8, StopStatus.CURRENT, 92, "1", "Live at PF 1"),
                    RouteStop(sET, 6, "14:40", "14:50", "15:18", "15:28", null, 795, 38, 10, StopStatus.UPCOMING, 91, "2", "Berth Confirmed"),
                    RouteStop(sNGP, 7, "22:15", "22:15", "22:51", "22:51", null, 1090, 36, 0, StopStatus.UPCOMING, 89, "1", "Terminal Berth")
                )
            ),

            // Train 2: 22436 Vande Bharat Express (NDLS to BSB / DDU)
            Train(
                trainNumber = "22436",
                trainName = "Vande Bharat Express",
                trainType = TrainType.VANDE_BHARAT,
                zone = "NR",
                origin = sNDLS,
                destination = sDDU,
                currentStationName = "Kanpur Central",
                nextStationName = "Prayagraj Junction",
                currentPlatform = "1",
                nextPlatform = "6",
                destinationPlatform = "3",
                latitude = 26.4547,
                longitude = 80.3507,
                speedKmh = 118,
                maxSpeedKmh = 160,
                currentDelayMinutes = 6,
                scheduledDestinationETA = "14:00",
                predictedDestinationETA = "14:04",
                previousPredictedETA = "14:07",
                confidenceScore = 96,
                congestionPercent = 32,
                weatherCondition = "Clear",
                status = TrainStatus.ON_TIME,
                totalDistanceKm = 780,
                coveredDistanceKm = 440,
                precedingTrainInfo = "Clear signal block ahead (Automatic signaling active)",
                speedRestrictionNotice = null,
                delayFactors = listOf(
                    DelayReasonFactor("Platform Dwell Variance", 2, "CONGESTION", "Passenger boarding load at Kanpur Central"),
                    DelayReasonFactor("High Speed Acceleration", -4, "RECOVERY", "Vande Bharat 130 km/h sprint on CNB-PRYJ section")
                ),
                route = listOf(
                    RouteStop(sNDLS, 1, "06:00", "06:00", "06:00", "06:00", "06:00", 0, 0, 0, StopStatus.PASSED, 99, "16", "Departed PF 16"),
                    RouteStop(sCNB, 2, "10:08", "10:12", "10:14", "10:18", "10:14", 440, 6, 4, StopStatus.CURRENT, 96, "1", "Live at PF 1"),
                    RouteStop(sPRYJ, 3, "12:08", "12:12", "12:11", "12:15", null, 635, 3, 4, StopStatus.UPCOMING, 95, "6", "Berth Confirmed"),
                    RouteStop(sDDU, 4, "14:00", "14:00", "14:04", "14:04", null, 780, 4, 0, StopStatus.UPCOMING, 94, "3", "Terminal Berth")
                )
            ),

            // Train 3: 12951 Mumbai Rajdhani (BCT to NDLS)
            Train(
                trainNumber = "12951",
                trainName = "Mumbai Rajdhani",
                trainType = TrainType.RAJDHANI,
                zone = "WR",
                origin = sBCT,
                destination = sNDLS,
                currentStationName = "Vadodara Junction",
                nextStationName = "Ahmedabad Junction",
                currentPlatform = "2",
                nextPlatform = "1",
                destinationPlatform = "2",
                latitude = 22.3106,
                longitude = 73.1812,
                speedKmh = 98,
                maxSpeedKmh = 130,
                currentDelayMinutes = 14,
                scheduledDestinationETA = "08:35",
                predictedDestinationETA = "08:44",
                previousPredictedETA = "08:50",
                confidenceScore = 94,
                congestionPercent = 45,
                weatherCondition = "Clear",
                status = TrainStatus.MINOR_DELAY,
                totalDistanceKm = 1384,
                coveredDistanceKm = 392,
                precedingTrainInfo = "Container Freight 35km ahead",
                speedRestrictionNotice = "Caution order 75 km/h at bridge 402",
                delayFactors = listOf(
                    DelayReasonFactor("Suburban Section Traffic", 8, "CONGESTION", "Mumbai suburban commuter crossover clearance"),
                    DelayReasonFactor("Speed Restriction", 3, "SPEED_RESTRICTION", "Bridge cautionary TSR 75 km/h"),
                    DelayReasonFactor("Driver Sectional Recovery", -3, "RECOVERY", "WR mainline clear track buffer")
                ),
                route = listOf(
                    RouteStop(sBCT, 1, "17:00", "17:00", "17:00", "17:00", "17:00", 0, 0, 0, StopStatus.PASSED, 99, "1", "Departed PF 1"),
                    RouteStop(sST, 2, "19:42", "19:47", "19:54", "19:59", "19:54", 263, 12, 5, StopStatus.PASSED, 96, "1", "Departed PF 1"),
                    RouteStop(sBRC, 3, "21:05", "21:15", "21:19", "21:29", "21:19", 392, 14, 10, StopStatus.CURRENT, 94, "2", "Live at PF 2"),
                    RouteStop(sNDLS, 4, "08:35", "08:35", "08:44", "08:44", null, 1384, 9, 0, StopStatus.UPCOMING, 91, "2", "Terminal Berth")
                )
            ),

            // Train 4: 12259 Sealdah Duronto (HWH/SDAH to NDLS)
            Train(
                trainNumber = "12259",
                trainName = "Sealdah Duronto Express",
                trainType = TrainType.DURONTO,
                zone = "ER",
                origin = sHWH,
                destination = sNDLS,
                currentStationName = "Pt Deen Dayal Upadhyaya",
                nextStationName = "Prayagraj Junction",
                currentPlatform = "4",
                nextPlatform = "3",
                destinationPlatform = "14",
                latitude = 25.2818,
                longitude = 83.1189,
                speedKmh = 42,
                maxSpeedKmh = 130,
                currentDelayMinutes = 58,
                scheduledDestinationETA = "11:00",
                predictedDestinationETA = "12:12",
                previousPredictedETA = "11:45",
                confidenceScore = 86,
                congestionPercent = 92,
                weatherCondition = "Moderate Fog (Visibility 350m)",
                status = TrainStatus.SEVERE_DELAY,
                totalDistanceKm = 1450,
                coveredDistanceKm = 675,
                precedingTrainInfo = "Preceding Coal rake halted on Down line block 14",
                speedRestrictionNotice = "Permanent TSR 30 km/h on DDU Yard complex",
                delayFactors = listOf(
                    DelayReasonFactor("Severe Yard Congestion", 18, "CONGESTION", "DDU Junction interlocking line backlog"),
                    DelayReasonFactor("Preceding Train Blocking", 12, "PRECEDING_TRAIN", "Goods rake delayed ahead in block"),
                    DelayReasonFactor("Fog Weather Impact", 8, "WEATHER", "Visibility restriction fog cautionary limit"),
                    DelayReasonFactor("Speed Restrictions", 6, "SPEED_RESTRICTION", "Yard speed limit 30 km/h"),
                    DelayReasonFactor("Scheduled Dwell Overrun", 4, "CONGESTION", "Crew change delay at DDU")
                ),
                route = listOf(
                    RouteStop(sHWH, 1, "20:00", "20:00", "20:00", "20:00", "20:00", 0, 0, 0, StopStatus.PASSED, 99, "9", "Departed PF 9"),
                    RouteStop(sPNBE, 2, "01:30", "01:40", "02:10", "02:20", "02:10", 530, 40, 10, StopStatus.PASSED, 92, "1", "Departed PF 1"),
                    RouteStop(sDDU, 3, "03:45", "03:55", "04:43", "04:58", "04:43", 675, 58, 15, StopStatus.CURRENT, 86, "4", "Live at PF 4"),
                    RouteStop(sPRYJ, 4, "05:40", "05:45", "06:48", "06:53", null, 825, 68, 5, StopStatus.UPCOMING, 84, "3", "Berth Confirmed"),
                    RouteStop(sCNB, 5, "07:30", "07:35", "08:42", "08:47", null, 1020, 72, 5, StopStatus.UPCOMING, 82, "2", "Expected PF 2"),
                    RouteStop(sNDLS, 6, "11:00", "11:00", "12:12", "12:12", null, 1450, 72, 0, StopStatus.UPCOMING, 80, "14", "Terminal Berth")
                )
            ),

            // Train 5: 12002 Bhopal Shatabdi (NDLS to BPL)
            Train(
                trainNumber = "12002",
                trainName = "Bhopal Shatabdi Express",
                trainType = TrainType.SHATABDI,
                zone = "NR",
                origin = sNDLS,
                destination = sBPL,
                currentStationName = "Gwalior Junction",
                nextStationName = "V Lakshmibai Jhansi",
                currentPlatform = "1",
                nextPlatform = "2",
                destinationPlatform = "1",
                latitude = 26.2183,
                longitude = 78.1828,
                speedKmh = 105,
                maxSpeedKmh = 150,
                currentDelayMinutes = 8,
                scheduledDestinationETA = "14:40",
                predictedDestinationETA = "14:46",
                previousPredictedETA = "14:48",
                confidenceScore = 95,
                congestionPercent = 38,
                weatherCondition = "Clear",
                status = TrainStatus.MINOR_DELAY,
                totalDistanceKm = 705,
                coveredDistanceKm = 313,
                precedingTrainInfo = "Clear automatic signaling",
                speedRestrictionNotice = null,
                delayFactors = listOf(
                    DelayReasonFactor("Agra Cantt Departure Rush", 5, "CONGESTION", "Platform clearance delay at AGC"),
                    DelayReasonFactor("High Speed Run (140 km/h)", -3, "RECOVERY", "WCR Agra-Gwalior high speed corridor")
                ),
                route = listOf(
                    RouteStop(sNDLS, 1, "06:00", "06:00", "06:00", "06:00", "06:00", 0, 0, 0, StopStatus.PASSED, 99, "1", "Departed PF 1"),
                    RouteStop(sAGC, 2, "07:50", "07:55", "07:54", "08:00", "07:54", 195, 5, 6, StopStatus.PASSED, 97, "1", "Departed PF 1"),
                    RouteStop(sGWL, 3, "09:23", "09:28", "09:31", "09:36", "09:31", 313, 8, 5, StopStatus.CURRENT, 95, "1", "Live at PF 1"),
                    RouteStop(sVGLJ, 4, "10:45", "10:50", "10:52", "10:57", null, 410, 7, 5, StopStatus.UPCOMING, 94, "2", "Berth Confirmed"),
                    RouteStop(sBPL, 5, "14:40", "14:40", "14:46", "14:46", null, 705, 6, 0, StopStatus.UPCOMING, 93, "1", "Terminal Berth")
                )
            ),

            // Train 6: 12616 Grand Trunk (GT) Express (NDLS to MAS)
            Train(
                trainNumber = "12616",
                trainName = "Grand Trunk Express",
                trainType = TrainType.SUPERFAST,
                zone = "SR",
                origin = sNDLS,
                destination = sMAS,
                currentStationName = "Nagpur Junction",
                nextStationName = "Balharshah Junction",
                currentPlatform = "1",
                nextPlatform = "2",
                destinationPlatform = "4",
                latitude = 21.1525,
                longitude = 79.0882,
                speedKmh = 68,
                maxSpeedKmh = 110,
                currentDelayMinutes = 32,
                scheduledDestinationETA = "06:20",
                predictedDestinationETA = "07:08",
                previousPredictedETA = "06:55",
                confidenceScore = 90,
                congestionPercent = 65,
                weatherCondition = "Clear",
                status = TrainStatus.MODERATE_DELAY,
                totalDistanceKm = 2182,
                coveredDistanceKm = 1090,
                precedingTrainInfo = "Goods train in section ahead",
                speedRestrictionNotice = "Caution order on Wardha curve",
                delayFactors = listOf(
                    DelayReasonFactor("Section Congestion", 8, "CONGESTION", "Nagpur-Wardha freight movement"),
                    DelayReasonFactor("Dwell Time Excess", 5, "CONGESTION", "Long halt at NGP for parcel loading"),
                    DelayReasonFactor("Speed Restrictions", 3, "SPEED_RESTRICTION", "TSR 50 km/h on loop line")
                ),
                route = listOf(
                    RouteStop(sNDLS, 1, "16:10", "16:10", "16:10", "16:10", "16:10", 0, 0, 0, StopStatus.PASSED, 99, "3", "Departed PF 3"),
                    RouteStop(sBPL, 2, "03:15", "03:25", "03:38", "03:48", "03:38", 705, 23, 10, StopStatus.PASSED, 93, "2", "Departed PF 2"),
                    RouteStop(sET, 3, "05:10", "05:20", "05:38", "05:48", "05:38", 795, 28, 10, StopStatus.PASSED, 92, "1", "Departed PF 1"),
                    RouteStop(sNGP, 4, "10:20", "10:30", "10:52", "11:05", "10:52", 1090, 32, 13, StopStatus.CURRENT, 90, "1", "Live at PF 1"),
                    RouteStop(sBPQ, 5, "13:50", "13:55", "14:32", "14:37", null, 1300, 42, 5, StopStatus.UPCOMING, 88, "2", "Berth Confirmed"),
                    RouteStop(sWL, 6, "17:40", "17:45", "18:25", "18:30", null, 1540, 45, 5, StopStatus.UPCOMING, 87, "1", "Expected PF 1"),
                    RouteStop(sBZA, 7, "21:30", "21:40", "22:20", "22:30", null, 1750, 50, 10, StopStatus.UPCOMING, 86, "1", "Expected PF 1"),
                    RouteStop(sMAS, 8, "06:20", "06:20", "07:08", "07:08", null, 2182, 48, 0, StopStatus.UPCOMING, 85, "4", "Terminal Berth")
                )
            ),

            // Train 7: 22691 KSR Bengaluru Rajdhani (SBC to NDLS)
            Train(
                trainNumber = "22691",
                trainName = "Bengaluru Rajdhani",
                trainType = TrainType.RAJDHANI,
                zone = "SWR",
                origin = sSBC,
                destination = sNDLS,
                currentStationName = "Balharshah Junction",
                nextStationName = "Nagpur Junction",
                currentPlatform = "1",
                nextPlatform = "2",
                destinationPlatform = "16",
                latitude = 19.8510,
                longitude = 79.3510,
                speedKmh = 86,
                maxSpeedKmh = 130,
                currentDelayMinutes = 11,
                scheduledDestinationETA = "05:30",
                predictedDestinationETA = "05:39",
                previousPredictedETA = "05:42",
                confidenceScore = 93,
                congestionPercent = 42,
                weatherCondition = "Clear",
                status = TrainStatus.MINOR_DELAY,
                totalDistanceKm = 2365,
                coveredDistanceKm = 1065,
                precedingTrainInfo = "Normal spacing",
                speedRestrictionNotice = null,
                delayFactors = listOf(
                    DelayReasonFactor("Crossing Hold", 6, "CONGESTION", "Held for express train priority"),
                    DelayReasonFactor("High Speed Section", -3, "RECOVERY", "Recovery window in MP plains")
                ),
                route = listOf(
                    RouteStop(sSBC, 1, "20:00", "20:00", "20:00", "20:00", "20:00", 0, 0, 0, StopStatus.PASSED, 99, "8", "Departed PF 8"),
                    RouteStop(sBPQ, 2, "07:30", "07:35", "07:41", "07:46", "07:41", 1065, 11, 5, StopStatus.CURRENT, 93, "1", "Live at PF 1"),
                    RouteStop(sNGP, 3, "11:00", "11:10", "11:12", "11:22", null, 1275, 12, 10, StopStatus.UPCOMING, 92, "2", "Berth Confirmed"),
                    RouteStop(sBPL, 4, "17:50", "18:00", "18:02", "18:12", null, 1660, 12, 10, StopStatus.UPCOMING, 91, "1", "Expected PF 1"),
                    RouteStop(sNDLS, 5, "05:30", "05:30", "05:39", "05:39", null, 2365, 9, 0, StopStatus.UPCOMING, 90, "16", "Terminal Berth")
                )
            ),

            // Train 8: 12903 Golden Temple Mail (CSMT/MMCT to ASR/NDLS)
            Train(
                trainNumber = "12903",
                trainName = "Golden Temple Mail",
                trainType = TrainType.SUPERFAST,
                zone = "WR",
                origin = sBCT,
                destination = sNDLS,
                currentStationName = "Surat",
                nextStationName = "Vadodara Junction",
                currentPlatform = "1",
                nextPlatform = "3",
                destinationPlatform = "5",
                latitude = 21.2035,
                longitude = 72.8402,
                speedKmh = 92,
                maxSpeedKmh = 120,
                currentDelayMinutes = 3,
                scheduledDestinationETA = "13:50",
                predictedDestinationETA = "13:52",
                previousPredictedETA = "13:50",
                confidenceScore = 97,
                congestionPercent = 25,
                weatherCondition = "Clear",
                status = TrainStatus.ON_TIME,
                totalDistanceKm = 1384,
                coveredDistanceKm = 263,
                precedingTrainInfo = "Normal spacing",
                speedRestrictionNotice = null,
                delayFactors = listOf(
                    DelayReasonFactor("Smooth Line Clearance", -1, "RECOVERY", "Green wave dispatch active on WR trunk")
                ),
                route = listOf(
                    RouteStop(sBCT, 1, "18:45", "18:45", "18:45", "18:45", "18:45", 0, 0, 0, StopStatus.PASSED, 99, "3", "Departed PF 3"),
                    RouteStop(sST, 2, "22:15", "22:20", "22:18", "22:23", "22:18", 263, 3, 5, StopStatus.CURRENT, 97, "1", "Live at PF 1"),
                    RouteStop(sBRC, 3, "23:45", "23:55", "23:48", "23:58", null, 392, 3, 10, StopStatus.UPCOMING, 96, "3", "Berth Confirmed"),
                    RouteStop(sNDLS, 4, "13:50", "13:50", "13:52", "13:52", null, 1384, 2, 0, StopStatus.UPCOMING, 95, "5", "Terminal Berth")
                )
            ),

            // Train 9: 20801 Magadh Express (PNBE to NDLS)
            Train(
                trainNumber = "20801",
                trainName = "Magadh Express",
                trainType = TrainType.SUPERFAST,
                zone = "ECR",
                origin = sPNBE,
                destination = sNDLS,
                currentStationName = "Prayagraj Junction",
                nextStationName = "Kanpur Central",
                currentPlatform = "2",
                nextPlatform = "4",
                destinationPlatform = "12",
                latitude = 25.4358,
                longitude = 81.8463,
                speedKmh = 58,
                maxSpeedKmh = 130,
                currentDelayMinutes = 44,
                scheduledDestinationETA = "11:50",
                predictedDestinationETA = "12:48",
                previousPredictedETA = "12:30",
                confidenceScore = 88,
                congestionPercent = 75,
                weatherCondition = "Haze / Fog (Visibility 600m)",
                status = TrainStatus.MODERATE_DELAY,
                totalDistanceKm = 998,
                coveredDistanceKm = 368,
                precedingTrainInfo = "Local passenger 18km ahead",
                speedRestrictionNotice = "TSR 60 km/h between PRYJ and Fatehpur",
                delayFactors = listOf(
                    DelayReasonFactor("Section Bottleneck", 9, "CONGESTION", "NCR 4th line non-interlocking work"),
                    DelayReasonFactor("Speed Restriction", 5, "SPEED_RESTRICTION", "TSR 60 km/h on loop crossing"),
                    DelayReasonFactor("Preceding Train Caution", 4, "PRECEDING_TRAIN", "Local commuter train ahead")
                ),
                route = listOf(
                    RouteStop(sPNBE, 1, "17:30", "17:30", "17:30", "17:30", "17:30", 0, 0, 0, StopStatus.PASSED, 99, "1", "Departed PF 1"),
                    RouteStop(sDDU, 2, "21:55", "22:05", "22:28", "22:38", "22:28", 213, 33, 10, StopStatus.PASSED, 91, "2", "Departed PF 2"),
                    RouteStop(sPRYJ, 3, "01:15", "01:20", "01:59", "02:04", "01:59", 368, 44, 5, StopStatus.CURRENT, 88, "2", "Live at PF 2"),
                    RouteStop(sCNB, 4, "04:00", "04:05", "04:52", "04:57", null, 563, 52, 5, StopStatus.UPCOMING, 86, "4", "Berth Confirmed"),
                    RouteStop(sNDLS, 5, "11:50", "11:50", "12:48", "12:48", null, 998, 58, 0, StopStatus.UPCOMING, 84, "12", "Terminal Berth")
                )
            ),

            // Train 10: 12431 Trivandrum Rajdhani (TVC to NZM/NDLS)
            Train(
                trainNumber = "12431",
                trainName = "Trivandrum Rajdhani",
                trainType = TrainType.RAJDHANI,
                zone = "SR",
                origin = sMAS,
                destination = sNDLS,
                currentStationName = "Vijayawada Junction",
                nextStationName = "Warangal",
                currentPlatform = "1",
                nextPlatform = "1",
                destinationPlatform = "3",
                latitude = 16.5186,
                longitude = 80.6199,
                speedKmh = 88,
                maxSpeedKmh = 130,
                currentDelayMinutes = 18,
                scheduledDestinationETA = "12:40",
                predictedDestinationETA = "12:56",
                previousPredictedETA = "13:02",
                confidenceScore = 93,
                congestionPercent = 50,
                weatherCondition = "Clear",
                status = TrainStatus.MINOR_DELAY,
                totalDistanceKm = 2182,
                coveredDistanceKm = 432,
                precedingTrainInfo = "Normal block spacing",
                speedRestrictionNotice = null,
                delayFactors = listOf(
                    DelayReasonFactor("Junction Yard Clearance", 6, "CONGESTION", "BZA yard entry point clearance"),
                    DelayReasonFactor("Sectional Sprint", -2, "RECOVERY", "Clear track buffer towards Warangal")
                ),
                route = listOf(
                    RouteStop(sMAS, 1, "06:05", "06:05", "06:05", "06:05", "06:05", 0, 0, 0, StopStatus.PASSED, 99, "2", "Departed PF 2"),
                    RouteStop(sBZA, 2, "12:10", "12:20", "12:28", "12:38", "12:28", 432, 18, 10, StopStatus.CURRENT, 93, "1", "Live at PF 1"),
                    RouteStop(sWL, 3, "15:20", "15:25", "15:36", "15:41", null, 642, 16, 5, StopStatus.UPCOMING, 92, "1", "Berth Confirmed"),
                    RouteStop(sBPQ, 4, "19:30", "19:35", "19:46", "19:51", null, 882, 16, 5, StopStatus.UPCOMING, 91, "1", "Expected PF 1"),
                    RouteStop(sNGP, 5, "23:00", "23:10", "23:15", "23:25", null, 1092, 15, 10, StopStatus.UPCOMING, 90, "1", "Expected PF 1"),
                    RouteStop(sNDLS, 6, "12:40", "12:40", "12:56", "12:56", null, 2182, 16, 0, StopStatus.UPCOMING, 89, "3", "Terminal Berth")
                )
            )
        )
    }

    val INITIAL_ALERTS = listOf(
        RailAlert(
            id = "ALT-101",
            trainNumber = "12345",
            title = "Arrival Time Changed (+13 min)",
            message = "Train 12345 Rajdhani expected to reach Nagpur at 10:51 PM (was 10:38 PM) due to too many trains ahead and speed cautions.",
            timestamp = "13:42:10",
            severity = AlertSeverity.WARNING,
            category = AlertCategory.DELAY,
            affectedSection = "Bhopal (BPL) - Itarsi (ET)",
            actionSuggested = "Dispatch priority routing via UP loop line block 4"
        ),
        RailAlert(
            id = "ALT-102",
            trainNumber = "12259",
            title = "Too Many Trains Ahead in Yard",
            message = "Too many trains ahead (92%) detected at Pt Deen Dayal Upadhyaya (DDU). AI predicts +72 min delay at destination.",
            timestamp = "13:39:45",
            severity = AlertSeverity.CRITICAL,
            category = AlertCategory.CONGESTION,
            affectedSection = "DDU Yard Interlocking",
            actionSuggested = "Hold local freight G-114 on siding 3 to grant clear signal to Duronto"
        ),
        RailAlert(
            id = "ALT-103",
            trainNumber = "22436",
            title = "Delay Recovery in Progress (-3 min)",
            message = "Vande Bharat Express speed sustained at 118 km/h. Arrival time improved by 3 minutes.",
            timestamp = "13:35:00",
            severity = AlertSeverity.RECOVERY,
            category = AlertCategory.OPERATIONAL,
            affectedSection = "Kanpur - Prayagraj 130 km/h MPS",
            actionSuggested = "Maintain green wave signal aspect"
        ),
        RailAlert(
            id = "ALT-104",
            trainNumber = "20801",
            title = "Temporary Speed Restriction Active",
            message = "TSR 60 km/h imposed near Fatehpur. AI dynamic forecasting automatically absorbed +5 min into arrival schedule.",
            timestamp = "13:30:15",
            severity = AlertSeverity.WARNING,
            category = AlertCategory.OPERATIONAL,
            affectedSection = "Prayagraj - Kanpur Section",
            actionSuggested = "Engineering team on track; caution advisory active"
        ),
        RailAlert(
            id = "ALT-105",
            trainNumber = "12616",
            title = "Effect of Other Trains",
            message = "Freight train ahead causing speed reduction. Risk of 15 min delay for following trains.",
            timestamp = "13:25:40",
            severity = AlertSeverity.CRITICAL,
            category = AlertCategory.DELAY,
            affectedSection = "Nagpur - Wardha Section",
            actionSuggested = "Divert freight rake into Sevagram goods loop"
        )
    )

    val CONGESTED_SECTIONS = listOf(
        CongestedSection("Bhopal (BPL) – Itarsi (ET)", "WCR", 88, 14, 52, 16, "Critical Bottleneck"),
        CongestedSection("Pt Deen Dayal (DDU) – Prayagraj (PRYJ)", "NCR", 92, 19, 44, 24, "Critical Bottleneck"),
        CongestedSection("Delhi (NDLS) – Ghaziabad (GZB)", "NR", 95, 26, 38, 28, "Critical Bottleneck"),
        CongestedSection("Mumbai (BCT) – Surat (ST)", "WR", 74, 18, 76, 8, "Heavy Load"),
        CongestedSection("Kanpur (CNB) – Prayagraj (PRYJ)", "NCR", 62, 12, 88, 6, "Normal"),
        CongestedSection("Nagpur (NGP) – Balharshah (BPQ)", "CR", 58, 9, 82, 5, "Normal"),
        CongestedSection("Vijayawada (BZA) – Chennai (MAS)", "SR", 45, 8, 94, 2, "Clear")
    )

    val HOTSPOT_STATIONS = listOf(
        HotspotStation("DDU", "Pt Deen Dayal Upadhyaya", "NCR", 210, 8.4f, 96, "Yard Interlocking & Freight Sorting"),
        HotspotStation("BPL", "Bhopal Junction", "WCR", 145, 6.2f, 88, "Single-Bridge River Crossing Hoshangabad"),
        HotspotStation("CNB", "Kanpur Central", "NCR", 280, 7.8f, 92, "Route Junction Platform Saturation"),
        HotspotStation("ET", "Itarsi Junction", "WCR", 190, 5.9f, 85, "Four-Way Trunk Crossing Intersection"),
        HotspotStation("HWH", "Howrah Junction", "ER", 340, 9.1f, 94, "Suburban Trunk Terminal Throat Congestion")
    )

    val DELAY_PROPAGATION_CHAIN = listOf(
        DelayPropagationNode(
            trainNumber = "G-889 (Freight)",
            trainName = "Coal Rake Special",
            originalDelayMinutes = 10,
            propagatedDelayMinutes = 10,
            bottleneckStation = "Bhopal (BPL)",
            sectionName = "BPL-ET Ghat Section",
            riskLevel = "MEDIUM",
            cause = "Brake pressure drop on gradient descent",
            recommendedAction = "Move into Budni passing loop"
        ),
        DelayPropagationNode(
            trainNumber = "12345",
            trainName = "Rajdhani Express",
            originalDelayMinutes = 10,
            propagatedDelayMinutes = 24,
            bottleneckStation = "Hoshangabad",
            sectionName = "Mid-section automatic block",
            riskLevel = "HIGH",
            cause = "Held behind freight at amber/red signal blocks (+14m added)",
            recommendedAction = "Give priority over take at Itarsi bypass"
        ),
        DelayPropagationNode(
            trainNumber = "12616",
            trainName = "Grand Trunk Express",
            originalDelayMinutes = 15,
            propagatedDelayMinutes = 32,
            bottleneckStation = "Itarsi Junction",
            sectionName = "ET Platform approach",
            riskLevel = "HIGH",
            cause = "Platform 2 occupied by late-running Rajdhani (+17m cascade)",
            recommendedAction = "Reassign to Platform 4 with clear signal"
        ),
        DelayPropagationNode(
            trainNumber = "12722",
            trainName = "Dakshin Express",
            originalDelayMinutes = 4,
            propagatedDelayMinutes = 22,
            bottleneckStation = "Betul",
            sectionName = "ET-NGP single line clearance",
            riskLevel = "MEDIUM",
            cause = "Cascaded crossing precedence delay (+18m)",
            recommendedAction = "Cross at Ghoradongri station"
        )
    )

    val DELAY_TREND_HOURLY = listOf(
        DelayTrendPoint("00:00", 12.4f, 13.1f, 32),
        DelayTrendPoint("03:00", 18.6f, 17.9f, 28),
        DelayTrendPoint("06:00", 28.2f, 27.5f, 48),
        DelayTrendPoint("09:00", 36.4f, 35.8f, 62),
        DelayTrendPoint("12:00", 31.8f, 32.2f, 58),
        DelayTrendPoint("15:00", 26.5f, 25.9f, 54),
        DelayTrendPoint("18:00", 34.2f, 33.6f, 66),
        DelayTrendPoint("21:00", 22.0f, 21.4f, 44)
    )
}
