package app.circles

import android.app.Application
import app.circles.consent.ConsentManager
import app.circles.vendor.AdsSdk
import app.circles.vendor.AnalyticsSdk
import app.circles.vendor.AttributionSdk

class CirclesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AnalyticsSdk.start(this, collectAdvertisingId = true)
        AttributionSdk.start(this, key = BuildConfig.ATTRIBUTION_KEY)
        AdsSdk.initialize(this)

        // Show the consent form on first launch (EEA and UK users only).
        ConsentManager.init(this) { choices ->
            AnalyticsSdk.setEnabled(choices.analytics)
            AdsSdk.setPersonalized(choices.ads)
        }
    }
}
