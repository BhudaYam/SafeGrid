package com.example.safegrid.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.safegrid.auth.GoogleSignInHelper
import com.example.safegrid.core.LoadsheddingParser
import com.example.safegrid.core.PasswordHasher
import com.example.safegrid.core.Validators
import com.example.safegrid.data.CloudAuthResult
import com.example.safegrid.data.CloudStore
import com.example.safegrid.data.IncidentEntity
import com.example.safegrid.data.SafeGridRepository
import com.example.safegrid.data.ScheduleEntity
import com.example.safegrid.notify.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class Screen {
    SPLASH, LOGIN, REGISTER, HOME, POWER, MAP, REPORT, SOS, SETTINGS
}

data class UiState(
    val screen: Screen = Screen.SPLASH,
    val lang: Lang = Lang.EN,
    val busy: Boolean = false,
    val banner: S? = null,
    val name: String = "",
    val email: String = "",
    val uid: String = "",
    val provider: String = "",
    val signedIn: Boolean = false,
    val notifications: Boolean = true,
    val radiusKm: Int = 5,
    val area: String = "Johannesburg",
    val points: Int = 0,
    val stage: Int? = null,
    val apiRaw: String = "",
    val apiDetail: String = "",
    val apiLoading: Boolean = false,
    val fromCache: Boolean = false,
    val lastSync: Long = 0L,
    val schedule: List<ScheduleEntity> = emptyList(),
    val incidents: List<IncidentEntity> = emptyList(),
    val pendingCount: Int = 0,
    val cloudReady: Boolean = false,
    val lastCloud: String = "",
    val sosActive: Boolean = false,
    val draftPhoto: String? = null,
    val draftPhotoLabel: String = ""
)

class SafeGridViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SafeGridRepository.get(application)
    private val _state = MutableStateFlow(UiState(cloudReady = repository.cloudReady()))
    val state: StateFlow<UiState> = _state

    init {
        viewModelScope.launch {
            repository.settings.collect { settings ->
                _state.update {
                    it.copy(
                        lang = Lang.from(settings.lang),
                        notifications = settings.notifications,
                        radiusKm = settings.radiusKm,
                        area = settings.area,
                        name = settings.name.ifBlank { it.name },
                        email = settings.email.ifBlank { it.email },
                        points = settings.points,
                        lastCloud = settings.lastCloud.ifBlank { it.lastCloud }
                    )
                }
            }
        }
        viewModelScope.launch {
            repository.observeIncidents().collect { incidents ->
                _state.update {
                    it.copy(
                        incidents = incidents,
                        pendingCount = incidents.count { item -> item.pendingSync }
                    )
                }
            }
        }
        viewModelScope.launch {
            val cached = repository.cachedPower()
            applyPower(cached)
            refreshPower(notify = false)
        }
    }

    fun open(screen: Screen) {
        _state.update { it.copy(screen = screen, banner = null) }
    }

    fun continueFromSplash() {
        viewModelScope.launch {
            val session = repository.currentSession()
            if (session == null) {
                _state.update { it.copy(screen = Screen.LOGIN, signedIn = false, cloudReady = repository.cloudReady()) }
            } else {
                _state.update {
                    it.copy(
                        screen = Screen.HOME,
                        signedIn = true,
                        uid = session.uid,
                        email = session.email.ifBlank { it.email },
                        name = session.name.ifBlank { it.name },
                        provider = session.provider,
                        cloudReady = true
                    )
                }
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        when {
            !Validators.name(name) -> banner(S.ErrorName)
            !Validators.email(email) -> banner(S.ErrorEmail)
            !Validators.password(password) -> banner(S.ErrorPassword)
            else -> authenticate(email.trim(), password, name.trim(), register = true)
        }
    }

    fun login(email: String, password: String) {
        when {
            !Validators.email(email) -> banner(S.ErrorEmail)
            !Validators.password(password) -> banner(S.ErrorPassword)
            else -> authenticate(email.trim(), password, name = "", register = false)
        }
    }

    fun loginWithGoogle(activity: android.app.Activity) {
        if (!repository.cloudReady()) {
            banner(S.FirebaseMissing)
            return
        }
        if (CloudStore.webClientId(activity) == null) {
            banner(S.GoogleUnavailable)
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(busy = true, banner = null) }
            try {
                val token = GoogleSignInHelper.idToken(activity)
                applyAuth(repository.signInWithGoogle(token))
            } catch (error: Exception) {
                _state.update { it.copy(busy = false, banner = S.GoogleUnavailable) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.signOut()
            _state.update {
                it.copy(
                    screen = Screen.LOGIN,
                    signedIn = false,
                    uid = "",
                    provider = "",
                    name = "",
                    email = "",
                    sosActive = false,
                    banner = null
                )
            }
        }
    }

    fun setLanguage(lang: Lang) {
        viewModelScope.launch {
            repository.updateSettings { it.copy(lang = lang.code) }
            banner(S.SettingsSaved)
        }
    }

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings { it.copy(notifications = enabled) }
        }
    }

    fun setRadius(km: Int) {
        viewModelScope.launch {
            repository.updateSettings { it.copy(radiusKm = km.coerceIn(1, 20)) }
        }
    }

    fun saveArea(area: String, name: String) {
        val cleanArea = area.trim().ifBlank { "Johannesburg" }
        val cleanName = name.trim()
        viewModelScope.launch {
            repository.updateSettings {
                it.copy(
                    area = cleanArea,
                    name = cleanName.ifBlank { it.name }
                )
            }
            banner(S.SettingsSaved)
            refreshPower(notify = false)
        }
    }

    fun refreshPower(notify: Boolean = true) {
        viewModelScope.launch {
            _state.update { it.copy(apiLoading = true) }
            val snapshot = repository.refreshLoadshedding()
            applyPower(snapshot)
            if (notify && snapshot.stageChanged && _state.value.notifications && snapshot.stage != null) {
                val lang = _state.value.lang
                NotificationHelper.show(
                    getApplication(),
                    "SafeGrid",
                    Strings.format(lang, S.StageLabel, snapshot.stage)
                )
            }
        }
    }

    fun onPhotoPicked(uri: Uri) {
        val path = CloudStore.copyPhoto(getApplication(), uri, "draft")
        _state.update {
            it.copy(
                draftPhoto = path,
                draftPhotoLabel = if (path == null) "" else uri.lastPathSegment.orEmpty()
            )
        }
    }

    fun submitReport(category: String, description: String) {
        if (!Validators.description(description)) {
            banner(S.ErrorDescription)
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(busy = true, banner = null) }
            val incident = repository.submitIncident(category, description, _state.value.draftPhoto)
            val synced = repository.cloudReady() && repository.currentSession() != null
            if (_state.value.notifications) {
                NotificationHelper.show(
                    getApplication(),
                    "SafeGrid",
                    incident.category
                )
            }
            _state.update {
                it.copy(
                    busy = false,
                    screen = Screen.MAP,
                    draftPhoto = null,
                    draftPhotoLabel = "",
                    banner = if (synced) S.ReportSent else S.SavedOffline
                )
            }
        }
    }

    fun activateSos() {
        viewModelScope.launch {
            repository.submitSos()
            if (_state.value.notifications) {
                NotificationHelper.show(
                    getApplication(),
                    "SafeGrid",
                    Strings.get(_state.value.lang, S.SosSent)
                )
            }
            _state.update { it.copy(sosActive = true, banner = S.SosRecorded) }
        }
    }

    fun cancelSos() {
        _state.update { it.copy(sosActive = false, banner = null) }
    }

    fun syncNow() {
        viewModelScope.launch {
            _state.update { it.copy(busy = true) }
            val message = repository.syncPending()
            _state.update {
                it.copy(
                    busy = false,
                    cloudReady = repository.cloudReady(),
                    lastCloud = message,
                    banner = if (repository.cloudReady()) S.SavedCloud else S.SavedOffline
                )
            }
        }
    }

    fun sendTestNotification() {
        if (!_state.value.notifications) {
            setNotifications(true)
        }
        NotificationHelper.show(
            getApplication(),
            "SafeGrid",
            Strings.get(_state.value.lang, S.NotifSub)
        )
        banner(S.NotificationSent)
    }

    fun dayLabel(key: String): String {
        val lang = _state.value.lang
        return when (key) {
            "today" -> Strings.get(lang, S.Today)
            "tomorrow" -> Strings.get(lang, S.Tomorrow)
            "day3" -> Strings.get(lang, S.Day3)
            "day4" -> Strings.get(lang, S.Day4)
            else -> key
        }
    }

    fun timeLabel(slot: ScheduleEntity): String {
        return if (slot.time == "none") {
            Strings.get(_state.value.lang, S.NoneScheduled)
        } else {
            slot.time
        }
    }

    fun categoryLabel(category: String): String {
        val lang = _state.value.lang
        return when (category) {
            "Suspicious" -> Strings.get(lang, S.Suspicious)
            "Traffic" -> Strings.get(lang, S.Traffic)
            else -> Strings.get(lang, S.Crime)
        }
    }

    fun formatSync(millis: Long): String {
        if (millis <= 0L) return Strings.get(_state.value.lang, S.NotSynced)
        val format = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        return format.format(Date(millis))
    }

    fun providerLabel(): String {
        val lang = _state.value.lang
        return when (_state.value.provider) {
            "google.com" -> Strings.get(lang, S.ProviderGoogle)
            "password" -> Strings.get(lang, S.ProviderPassword)
            else -> Strings.get(lang, S.NotSignedIn)
        }
    }

    private fun authenticate(email: String, password: String, name: String, register: Boolean) {
        if (!repository.cloudReady()) {
            banner(S.FirebaseMissing)
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(busy = true, banner = null) }
            val hash = PasswordHasher.sha256(password)
            val result = if (register) {
                repository.register(email, hash, name)
            } else {
                repository.signIn(email, hash)
            }
            applyAuth(result)
        }
    }

    private fun applyAuth(result: CloudAuthResult) {
        when (result) {
            is CloudAuthResult.Success -> {
                _state.update {
                    it.copy(
                        busy = false,
                        screen = Screen.HOME,
                        signedIn = true,
                        uid = result.session.uid,
                        email = result.session.email,
                        name = result.session.name.ifBlank { it.name },
                        provider = result.session.provider,
                        cloudReady = true,
                        banner = null
                    )
                }
                refreshPower(notify = false)
            }
            is CloudAuthResult.Failure -> {
                _state.update { it.copy(busy = false, banner = bannerFor(result.code)) }
            }
        }
    }

    private fun applyPower(snapshot: com.example.safegrid.data.PowerSnapshot) {
        _state.update {
            it.copy(
                apiLoading = false,
                stage = snapshot.stage,
                apiRaw = snapshot.raw,
                apiDetail = snapshot.detail,
                fromCache = snapshot.fromCache,
                lastSync = snapshot.syncedAt,
                schedule = snapshot.slots
            )
        }
    }

    private fun banner(key: S) {
        _state.update { it.copy(banner = key, busy = false) }
    }

    private fun bannerFor(code: String): S {
        return when (code) {
            "NOT_CONFIGURED" -> S.FirebaseMissing
            "ERROR_EMAIL_ALREADY_IN_USE" -> S.ErrorEmailUsed
            "ERROR_WRONG_PASSWORD", "ERROR_USER_NOT_FOUND", "ERROR_INVALID_CREDENTIAL",
            "ERROR_INVALID_LOGIN_CREDENTIALS" -> S.ErrorBadLogin
            "ERROR_NETWORK_REQUEST_FAILED" -> S.ErrorNetwork
            "ERROR_INVALID_EMAIL" -> S.ErrorEmail
            "ERROR_WEAK_PASSWORD" -> S.ErrorPassword
            else -> S.ErrorGeneric
        }
    }

    fun apiUrl(): String = LoadsheddingParser.STATUS_URL
}
