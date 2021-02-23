package com.helikanonlib.admanager

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.*

class AdmobAppOpenAdManager(val application: Application, var adOpenPlacementId: String = "ca-app-pub-3940256099942544/3419835294") : AppOpenAdBaseLifeCycle(application), LifecycleObserver {
    var platform = AdPlatformTypeEnum.ADMOB

    private var appOpenAd: AppOpenAd? = null

    // private val adOpenPlacementId = "ca-app-pub-3940256099942544/3419835294"
    private var isShowing = false
    private var loadTime: Long = 0

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    /*fun enableTestMode() {
        adOpenPlacementId = "ca-app-pub-3940256099942544/3419835294"
    }*/

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public fun onStart() {
        currentActivity?.let {
            show(it, null)
        } ?: load(null)
    }

    fun load(listener: AdPlatformLoadListener?) {
        if (isAdOpenLoaded()) {
            listener?.onLoaded(platform)
            return
        }

        val request: AdRequest = AdRequest.Builder().build();
        AppOpenAd.load(application, adOpenPlacementId, request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(ad: AppOpenAd) {
                appOpenAd = ad
                loadTime = Date().time
                listener?.onLoaded(platform)
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                listener?.onError(AdErrorMode.PLATFORM, p0?.message, platform)
            }
        })
    }

    fun isAdOpenLoaded(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    fun show(activity: Activity, listener: AdPlatformShowListener?=null) {
        if (!isAdOpenLoaded()) {
            listener?.onError(AdErrorMode.PLATFORM, "${platform.name} adopen >> noads loaded", platform)
            load(null)
            return
        }

        if (isShowing) return

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(p0: AdError?) {

            }

            override fun onAdShowedFullScreenContent() {
                isShowing = true
            }

            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowing = false
                load(null)
            }
        }
        appOpenAd?.show(activity)
    }


    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - this.loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < (numMilliSecondsPerHour * numHours)
    }
}


open class AppOpenAdBaseLifeCycle(application: Application) : Application.ActivityLifecycleCallbacks {
    protected var currentActivity: Activity? = null

    init {
        // Cannot directly use `this`
        // Issue : Leaking 'this' in constructor of non-final class BaseObserver
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

}