class SettingsFragment : Fragment() {
    private fun onTrackingChanged(isOn: Boolean) {
        Tracker.event("tracking_toggled", mapOf("on" to isOn))
        profileRepository.update(allowTracking = isOn)   // result ignored
    }
}
