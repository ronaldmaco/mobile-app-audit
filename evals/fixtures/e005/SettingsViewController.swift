final class SettingsViewController: UITableViewController {
    @IBAction func trackingSwitchChanged(_ sender: UISwitch) {
        UserDefaults.standard.set(sender.isOn, forKey: "allowTracking")
        api.updatePreferences(allowTracking: sender.isOn) { _ in }
        Tracker.event("tracking_toggled", ["on": sender.isOn])
    }
    @IBAction func signOutTapped() {
        Keychain.delete("token")
        Session.shared.user = nil
        showSignIn()
    }
}
