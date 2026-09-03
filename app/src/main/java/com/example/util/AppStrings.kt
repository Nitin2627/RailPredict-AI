package com.example.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

interface AppStrings {
    val appName: String
    val overview: String
    val dashboard: String
    val liveTrains: String
    val aiEngine: String
    val passenger: String
    val selectYourTrain: String
    val enterTrainNumber: String
    val findTrain: String
    val searchByRoute: String
    val from: String
    val to: String
    val currentStation: String // Train is now at
    val speed: String
    val currentDelay: String // Running late by
    val nextStation: String
    val distanceLeft: String // Distance left
    val predictedArrival: String // Expected to reach
    val expectedArrival: String
    val aiConfidence: String
    val expectedDelay: String // Expected delay at destination
    val aiInsight: String
    val whyEtaChanged: String
    val trackAheadBusy: String
    val slowMovementDetected: String
    val smoothFlowPredicted: String
    val arrivalTimeChange: String // Change in arrival time
    val predictedArrivalWindow: String
    val earlierPrediction: String
    val newPrediction: String
    val change: String
    val networkEffect: String // Effect of other trains
    val networkEffectSub: String
    val live: String
    val trains: String
    val stations: String
    val possibleDelay: String
    val trainsAheadAffectArrival: String
    val trainMovingSlower: String
    val rainMaySlowTrain: String
    val slowSectionAhead: String
    val technicalDetails: String
    val simpleExplanation: String
    val model: String
    val prediction: String
    val features: String
    val yourTrain: String
    val reachingAround: String
    val delay: String
    val onTime: String
    val late: String
    val language: String
    val chooseLanguage: String
    val continueText: String
    val settings: String
    val trainType: String
    val running: String
    val status: String
    val distanceCovered: String
    val arrival: String
    val actualArrival: String
    val departure: String
    val platform: String
    val expectedDeparture: String
    val timeRemaining: String
    val journeyProgress: String
    val upcomingStations: String
    val destination: String
    val whyTrainLate: String
    val trainSpeedLower: String
    val aiExpectsRecovery: String
    val approximately: String
    val hr: String
    val min: String
    val departed: String
    val current: String
    val next: String
    val trainNotFound: String
    val invalidNumber: String
    val loadingTrainDetails: String
    val noInternet: String
    val serverError: String
    val tryAgain: String
    val noTrainsFound: String
    val invalidTrainNumber: String
    val selectStation: String
    val somethingWentWrong: String
    val scheduledArrival: String
    val route: String
    val or: String
    val aiUpdate: String
    val why: String
    val searching: String
    val noTrainRoute: String
    val usual: String
    val difference: String
    val speedSlower: String
    val speedNormal: String
    val speedFaster: String
    val normal: String
    val belowNormal: String
    val aboveNormal: String
    val scheduled: String
    val actualExpected: String
    val whyTrainLateReason: String
    val viewDetails: String
    val currentPrediction: String
    val target: String
    val whyThisTime: String
    val trainSpeedFactor: String
    val currentDelayFactor: String
    val distanceRemainingFactor: String
    val trainTrafficFactor: String
    val weatherFactor: String
    val historicalPatternFactor: String
    val simpleWords: SimpleWordsStrings
}

interface SimpleWordsStrings {
    val busyTrack: String
    val moreTrains: String
    val rain: String
}

object EnglishStrings : AppStrings {
    override val appName = "RailPredict AI"
    override val overview = "Overview"
    override val dashboard = "Dashboard"
    override val liveTrains = "Live Trains"
    override val aiEngine = "AI Engine"
    override val passenger = "Passenger"
    override val selectYourTrain = "Select Your Train"
    override val enterTrainNumber = "Enter Train Number"
    override val findTrain = "Find Train"
    override val searchByRoute = "Search by Route"
    override val from = "From"
    override val to = "To"
    override val currentStation = "Train is now at"
    override val speed = "Speed"
    override val currentDelay = "Running late by"
    override val nextStation = "Next Station"
    override val distanceLeft = "Distance left"
    override val predictedArrival = "Expected to reach"
    override val expectedArrival = "EXPECTED ARRIVAL"
    override val aiConfidence = "AI Confidence"
    override val expectedDelay = "Expected delay at destination"
    override val aiInsight = "AI Insight"
    override val whyEtaChanged = "Why is arrival time changing?"
    override val trackAheadBusy = "Track ahead is busy."
    override val slowMovementDetected = "Train is moving slower than usual"
    override val smoothFlowPredicted = "Track is clear"
    override val arrivalTimeChange = "Change in arrival time"
    override val predictedArrivalWindow = "Predicted Arrival Window"
    override val earlierPrediction = "Earlier prediction"
    override val newPrediction = "New prediction"
    override val change = "Change"
    override val networkEffect = "Effect of other trains"
    override val networkEffectSub = "How delays change for other trains"
    override val live = "LIVE"
    override val trains = "Trains"
    override val stations = "Stations"
    override val possibleDelay = "Possible Delay"
    override val trainsAheadAffectArrival = "2 trains ahead may affect your arrival time."
    override val trainMovingSlower = "Train is moving slower than usual"
    override val rainMaySlowTrain = "Rain may slow the train"
    override val slowSectionAhead = "Slow section ahead"
    override val technicalDetails = "Technical Details"
    override val simpleExplanation = "Simple Explanation"
    override val model = "Model"
    override val prediction = "Prediction"
    override val features = "Features"
    override val yourTrain = "Your Train"
    override val reachingAround = "Reaching around"
    override val delay = "Delay"
    override val onTime = "On time"
    override val late = "late"
    override val language = "Language"
    override val chooseLanguage = "Choose your language"
    override val continueText = "Continue"
    override val settings = "Settings"
    override val trainType = "Train Type"
    override val running = "Running"
    override val status = "Status"
    override val distanceCovered = "Distance Covered"
    override val arrival = "Arrival"
    override val actualArrival = "Actual Arrival"
    override val departure = "Departure"
    override val platform = "Platform"
    override val expectedDeparture = "Expected Departure"
    override val timeRemaining = "Time remaining"
    override val journeyProgress = "Journey Progress"
    override val upcomingStations = "Upcoming Stations"
    override val destination = "Destination"
    override val whyTrainLate = "Why is the train late?"
    override val trainSpeedLower = "Train speed is slightly lower than normal"
    override val aiExpectsRecovery = "The AI expects the train to recover some delay further ahead"
    override val approximately = "Approximately"
    override val hr = "hr"
    override val min = "min"
    override val departed = "Departed"
    override val current = "Current"
    override val next = "Next"
    override val trainNotFound = "Train not found."
    override val invalidNumber = "Invalid train number."
    override val loadingTrainDetails = "Loading train details..."
    override val noInternet = "No internet"
    override val serverError = "Server error"
    override val tryAgain = "Try again"
    override val noTrainsFound = "No trains found"
    override val invalidTrainNumber = "Invalid train number"
    override val selectStation = "Select station"
    override val somethingWentWrong = "Something went wrong"
    override val scheduledArrival = "Scheduled Arrival"
    override val route = "ROUTE"
    override val or = "OR"
    override val aiUpdate = "AI Update"
    override val why = "Why?"
    override val searching = "Searching for train..."
    override val noTrainRoute = "No train found for this route."
    override val usual = "Usual"
    override val difference = "Difference"
    override val speedSlower = "Train is moving slightly slower than usual."
    override val speedNormal = "Train is moving at normal speed."
    override val speedFaster = "Train is moving faster than usual."
    override val normal = "Normal"
    override val belowNormal = "Below normal"
    override val aboveNormal = "Above normal"
    override val scheduled = "Scheduled"
    override val actualExpected = "Actual / Expected"
    override val whyTrainLateReason = "Train is running late because the track ahead is busy."
    override val viewDetails = "View details"
    override val currentPrediction = "Current prediction"
    override val target = "Target"
    override val whyThisTime = "Why this time?"
    override val trainSpeedFactor = "Train speed"
    override val currentDelayFactor = "Current delay"
    override val distanceRemainingFactor = "Distance remaining"
    override val trainTrafficFactor = "Train traffic"
    override val weatherFactor = "Weather"
    override val historicalPatternFactor = "Historical pattern"
    override val simpleWords = object : SimpleWordsStrings {
        override val busyTrack = "Track ahead is busy"
        override val moreTrains = "More trains are ahead"
        override val rain = "Rain may slow the train"
    }
}

object HindiStrings : AppStrings {
    override val appName = "रेलप्रेडिक्ट AI"
    override val overview = "मुख्य"
    override val dashboard = "डैशबोर्ड"
    override val liveTrains = "लाइव ट्रेन"
    override val aiEngine = "AI सिस्टम"
    override val passenger = "यात्री"
    override val selectYourTrain = "अपनी ट्रेन चुनें"
    override val enterTrainNumber = "ट्रेन नंबर डालें"
    override val findTrain = "ट्रेन खोजें"
    override val searchByRoute = "रूट से खोजें"
    override val from = "कहाँ से"
    override val to = "कहाँ तक"
    override val currentStation = "ट्रेन अभी यहां है"
    override val speed = "गति"
    override val currentDelay = "देरी से चल रही है"
    override val nextStation = "अगला स्टेशन"
    override val distanceLeft = "बाकी दूरी"
    override val predictedArrival = "पहुंचने का अनुमानित समय"
    override val expectedArrival = "अनुमानित आगमन"
    override val aiConfidence = "AI भरोसा"
    override val expectedDelay = "गंतव्य पर अनुमानित देरी"
    override val aiInsight = "AI जानकारी"
    override val whyEtaChanged = "समय क्यों बदल रहा है?"
    override val trackAheadBusy = "आगे ट्रैक पर अधिक ट्रेनों का दबाव है"
    override val slowMovementDetected = "ट्रेन सामान्य से धीमी चल रही है"
    override val smoothFlowPredicted = "ट्रैक साफ है"
    override val arrivalTimeChange = "पहुंचने के समय में बदलाव"
    override val predictedArrivalWindow = "आगमन का अनुमानित समय"
    override val earlierPrediction = "पिछला अनुमान"
    override val newPrediction = "नया अनुमान"
    override val change = "बदलाव"
    override val networkEffect = "अन्य ट्रेनों का प्रभाव"
    override val networkEffectSub = "अन्य ट्रेनों के लिए देरी कैसे बदलती है"
    override val live = "लाइव"
    override val trains = "ट्रेनें"
    override val stations = "स्टेशन"
    override val possibleDelay = "संभावित देरी"
    override val trainsAheadAffectArrival = "आगे चल रही 2 ट्रेनें आपके पहुंचने के समय को प्रभावित कर सकती हैं।"
    override val trainMovingSlower = "ट्रेन सामान्य से धीमी चल रही है"
    override val rainMaySlowTrain = "बारिश ट्रेन को धीमा कर सकती है"
    override val slowSectionAhead = "आगे धीमा सेक्शन है"
    override val technicalDetails = "तकनीकी जानकारी"
    override val simpleExplanation = "सरल व्याख्या"
    override val model = "मॉडल"
    override val prediction = "अनुमान"
    override val features = "विशेषताएं"
    override val yourTrain = "आपकी ट्रेन"
    override val reachingAround = "पहुंचने का समय"
    override val delay = "देरी"
    override val onTime = "सही समय पर"
    override val late = "देरी"
    override val language = "भाषा"
    override val chooseLanguage = "अपनी भाषा चुनें"
    override val continueText = "आगे बढ़ें"
    override val settings = "सेटिंग्स"
    override val trainType = "ट्रेन का प्रकार"
    override val running = "चलने के दिन"
    override val status = "स्थिति"
    override val distanceCovered = "तय की गई दूरी"
    override val arrival = "आगमन"
    override val actualArrival = "वास्तविक आगमन"
    override val departure = "प्रस्थान"
    override val platform = "प्लेटफार्म"
    override val expectedDeparture = "अनुमानित प्रस्थान"
    override val timeRemaining = "बाकी समय"
    override val journeyProgress = "यात्रा की प्रगति"
    override val upcomingStations = "आने वाले स्टेशन"
    override val destination = "गंतव्य"
    override val whyTrainLate = "ट्रेन लेट क्यों है?"
    override val trainSpeedLower = "ट्रेन की गति सामान्य से थोड़ी कम है"
    override val aiExpectsRecovery = "AI को उम्मीद है कि ट्रेन आगे कुछ देरी को कवर कर लेगी"
    override val approximately = "लगभग"
    override val hr = "घंटे"
    override val min = "मिनट"
    override val departed = "निकल चुकी है"
    override val current = "वर्तमान"
    override val next = "अगला"
    override val trainNotFound = "ट्रेन नहीं मिली।"
    override val invalidNumber = "गलत ट्रेन नंबर।"
    override val loadingTrainDetails = "ट्रेन की जानकारी लोड हो रही है..."
    override val noInternet = "इंटरनेट नहीं है"
    override val serverError = "सर्वर एरर"
    override val tryAgain = "फिर कोशिश करें"
    override val noTrainsFound = "कोई ट्रेन नहीं मिली"
    override val invalidTrainNumber = "गलत ट्रेन नंबर"
    override val selectStation = "स्टेशन चुनें"
    override val somethingWentWrong = "कुछ गलत हो गया"
    override val scheduledArrival = "निर्धारित आगमन"
    override val route = "रूट"
    override val or = "या"
    override val aiUpdate = "AI अपडेट"
    override val why = "क्यों?"
    override val searching = "ट्रेन की जानकारी खोजी जा रही है..."
    override val noTrainRoute = "इस मार्ग के लिए कोई ट्रेन नहीं मिली।"
    override val usual = "सामान्य"
    override val difference = "अंतर"
    override val speedSlower = "ट्रेन सामान्य से थोड़ी धीमी चल रही है।"
    override val speedNormal = "ट्रेन सामान्य गति से चल रही है।"
    override val speedFaster = "ट्रेन सामान्य से तेज चल रही है।"
    override val normal = "सामान्य"
    override val belowNormal = "सामान्य से कम"
    override val aboveNormal = "सामान्य से अधिक"
    override val scheduled = "निर्धारित"
    override val actualExpected = "वास्तविक / अनुमानित"
    override val whyTrainLateReason = "ट्रेन देरी से चल रही है क्योंकि आगे ट्रैक व्यस्त है।"
    override val viewDetails = "विवरण देखें"
    override val currentPrediction = "वर्तमान अनुमान"
    override val target = "लक्ष्य"
    override val whyThisTime = "इतना समय क्यों?"
    override val trainSpeedFactor = "ट्रेन की गति"
    override val currentDelayFactor = "वर्तमान देरी"
    override val distanceRemainingFactor = "बाकी दूरी"
    override val trainTrafficFactor = "ट्रेन ट्रैफिक"
    override val weatherFactor = "मौसम"
    override val historicalPatternFactor = "पुराना पैटर्न"
    override val simpleWords = object : SimpleWordsStrings {
        override val busyTrack = "आगे ट्रैक पर भीड़ है"
        override val moreTrains = "आगे और ट्रेनें हैं"
        override val rain = "बारिश ट्रेन को धीमा कर सकती है"
    }
}

object HinglishStrings : AppStrings {
    override val appName = "RailPredict AI"
    override val overview = "Home"
    override val dashboard = "Dashboard"
    override val liveTrains = "Live Trains"
    override val aiEngine = "AI System"
    override val passenger = "Passenger"
    override val selectYourTrain = "Apni Train Chuno"
    override val enterTrainNumber = "Train Number dalo"
    override val findTrain = "Train Khojo"
    override val searchByRoute = "Route se search karo"
    override val from = "Kahan se"
    override val to = "Kahan tak"
    override val currentStation = "Train abhi yahan hai"
    override val speed = "Speed"
    override val currentDelay = "Running late by"
    override val nextStation = "Agla station"
    override val distanceLeft = "Distance left"
    override val predictedArrival = "Yahan pahunchne ka time"
    override val expectedArrival = "EXPECTED ARRIVAL"
    override val aiConfidence = "AI Confidence"
    override val expectedDelay = "Expected delay at destination"
    override val aiInsight = "AI Update"
    override val whyEtaChanged = "Arrival time kyun badal raha hai?"
    override val trackAheadBusy = "Aage track par zyada trains hain"
    override val slowMovementDetected = "Train normal se dheere chal rahi hai"
    override val smoothFlowPredicted = "Track clear hai"
    override val arrivalTimeChange = "Arrival time mein badlav"
    override val predictedArrivalWindow = "Predicted Arrival Window"
    override val earlierPrediction = "Pehle ka time"
    override val newPrediction = "Naya time"
    override val change = "Kitna badla"
    override val networkEffect = "Doosri trains ka effect"
    override val networkEffectSub = "Doosri trains par delay kaise padti hai"
    override val live = "LIVE"
    override val trains = "Trains"
    override val stations = "Stations"
    override val possibleDelay = "Possible Delay"
    override val trainsAheadAffectArrival = "Aage chal rahi 2 trains arrival time ko affect kar sakti hain."
    override val trainMovingSlower = "Train normal se dheere chal rahi hai"
    override val rainMaySlowTrain = "Baarish ki wajah se train dheere ho sakti hai"
    override val slowSectionAhead = "Aage slow section hai"
    override val technicalDetails = "Technical Details"
    override val simpleExplanation = "Simple Explanation"
    override val model = "Model"
    override val prediction = "Prediction"
    override val features = "Features"
    override val yourTrain = "Aapki Train"
    override val reachingAround = "Pahunchne ka time"
    override val delay = "Late"
    override val onTime = "Sahi time par"
    override val late = "late"
    override val language = "Bhasha"
    override val chooseLanguage = "Apni bhasha chuno"
    override val continueText = "Aage badhein"
    override val settings = "Settings"
    override val trainType = "Train Type"
    override val running = "Running"
    override val status = "Status"
    override val distanceCovered = "Kitni doori cover ki"
    override val arrival = "Arrival"
    override val actualArrival = "Actual Arrival"
    override val departure = "Departure"
    override val platform = "Platform"
    override val expectedDeparture = "Expected Departure"
    override val timeRemaining = "Time baki hai"
    override val journeyProgress = "Journey Progress"
    override val upcomingStations = "Upcoming Stations"
    override val destination = "Destination"
    override val whyTrainLate = "Train kyun late hai?"
    override val trainSpeedLower = "Train speed normal se thodi kam hai"
    override val aiExpectsRecovery = "AI expects train aage thodi delay recover kar legi"
    override val approximately = "Lagbhag"
    override val hr = "hr"
    override val min = "min"
    override val departed = "Depart ho gayi"
    override val current = "Abhi yahan"
    override val next = "Agla station"
    override val trainNotFound = "Train nahi mili."
    override val invalidNumber = "Galat train number."
    override val loadingTrainDetails = "Train ki details load ho rahi hain..."
    override val noInternet = "Internet nahi hai"
    override val serverError = "Server error"
    override val tryAgain = "Try again"
    override val noTrainsFound = "No trains found"
    override val invalidTrainNumber = "Invalid train number"
    override val selectStation = "Station select karo"
    override val somethingWentWrong = "Kuch galat ho gaya"
    override val scheduledArrival = "Scheduled Arrival"
    override val route = "ROUTE"
    override val or = "YA"
    override val aiUpdate = "AI Update"
    override val why = "Kyun?"
    override val searching = "Train ki details search ho rahi hain..."
    override val noTrainRoute = "Is route ke liye koi train nahi mili."
    override val usual = "Usual"
    override val difference = "Difference"
    override val speedSlower = "Train normal se thodi dheere chal rahi hai."
    override val speedNormal = "Train normal speed par chal rahi hai."
    override val speedFaster = "Train normal se tez chal rahi hai."
    override val normal = "Normal"
    override val belowNormal = "Normal se kam"
    override val aboveNormal = "Normal se zyada"
    override val scheduled = "Scheduled"
    override val actualExpected = "Actual / Expected"
    override val whyTrainLateReason = "Train late hai kyunki aage track par traffic hai."
    override val viewDetails = "View details"
    override val currentPrediction = "Abhi ka time"
    override val target = "Target"
    override val whyThisTime = "Itna time kyun?"
    override val trainSpeedFactor = "Train ki speed"
    override val currentDelayFactor = "Abhi ki delay"
    override val distanceRemainingFactor = "Baki doori"
    override val trainTrafficFactor = "Doosri trains"
    override val weatherFactor = "Mausam"
    override val historicalPatternFactor = "Puraana pattern"
    override val simpleWords = object : SimpleWordsStrings {
        override val busyTrack = "Aage track par zyada trains hain"
        override val moreTrains = "Aage aur trains hain"
        override val rain = "Baarish ki wajah se train dheere ho sakti hai"
    }
}

val LocalStrings = staticCompositionLocalOf<AppStrings> { EnglishStrings }

@Composable
@ReadOnlyComposable
fun stringResource(): AppStrings {
    return LocalStrings.current
}
