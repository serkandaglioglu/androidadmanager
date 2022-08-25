package com.helikanonlib.admanager

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.google.android.gms.ads.nativead.NativeAd
import java.util.*
import kotlin.collections.ArrayList

abstract class AdPlatformWrapper(open var appId: String) {

    abstract val platform: AdPlatformTypeEnum
    var placementGroups: ArrayList<AdPlacementGroupModel> = ArrayList()
    protected fun getPlacementGroupByIndex(placementGroupIndex: Int): AdPlacementGroupModel {
        /*if (index >= placementGroups.size) {
            return null
        }*/
        return placementGroups.get(placementGroupIndex)
    }

    /*var interstitialPlacementId: String? = null
    var bannerPlacementId: String? = null
    var rewardedPlacementId: String? = null
    var mrecPlacementId: String? = null
    var nativePlacementId: String? = null
    var appOpenAdPlacementId: String? = null*/

    abstract fun initialize(activity: Activity, testMode: Boolean = false)
    abstract fun initialize(context: Context, testMode: Boolean = false)
    abstract fun enableTestMode(context: Context, deviceId: String? = null)

    abstract fun loadInterstitial(activity: Activity, listener: AdPlatformLoadListener? = null, placementGroupIndex: Int = 0)
    abstract fun showInterstitial(activity: Activity, listener: AdPlatformShowListener? = null, placementGroupIndex: Int = 0)
    abstract fun isInterstitialLoaded(placementGroupIndex: Int = 0): Boolean

    abstract fun isBannerLoaded(placementGroupIndex: Int = 0): Boolean
    abstract fun showBanner(activity: Activity, containerView: RelativeLayout, listener: AdPlatformShowListener? = null, placementGroupIndex: Int = 0)

    abstract fun loadRewarded(activity: Activity, listener: AdPlatformLoadListener? = null, placementGroupIndex: Int = 0)
    abstract fun showRewarded(activity: Activity, listener: AdPlatformShowListener? = null, placementGroupIndex: Int = 0)
    abstract fun isRewardedLoaded(placementGroupIndex: Int = 0): Boolean

    abstract fun isMrecLoaded(placementGroupIndex: Int = 0): Boolean
    abstract fun showMrec(activity: Activity, containerView: RelativeLayout, listener: AdPlatformShowListener? = null, placementGroupIndex: Int = 0)


    // val nativeAds: ArrayList<Any> = arrayListOf()
    abstract fun isNativeLoaded(placementGroupIndex: Int = 0): Boolean
    abstract fun loadNativeAds(activity: Activity, count: Int, listener: AdPlatformLoadListener? = null, placementGroupIndex: Int = 0)

    // adSize >> [small,medium]
    abstract fun showNative(activity: Activity, pos: Int, listener: AdPlatformShowListener? = null, placementGroupIndex: Int = 0): NativeAd?
    abstract fun getNativeAds(activity: Activity, placementGroupIndex: Int = 0): ArrayList<Any>

    abstract fun destroy(activity: Activity)
    abstract fun destroyBanner(activity: Activity)
    abstract fun destroyMrec(activity: Activity)

    open fun onCreate(activity: Activity) {}
    open fun onPause(activity: Activity) {}
    open fun onStop(activity: Activity) {}
    open fun onResume(activity: Activity) {}

    protected fun _isBannerLoaded(bannerAdView: ViewGroup?): Boolean {
        return bannerAdView != null && bannerAdView.parent != null
    }

    protected fun _removeBannerViewIfExists(bannerAdView: ViewGroup?): Boolean {
        if (_isBannerLoaded(bannerAdView)) {
            (bannerAdView?.parent as ViewGroup).removeView(bannerAdView)
            return true
        }

        return false
    }






    var lastLoadedInterstitialsDateByAdPlatform = mutableMapOf<String, Date>()
    var lastLoadedRewardedDateByAdPlatform = mutableMapOf<String, Date>()
    var lastLoadedBannerDateByAdPlatform = mutableMapOf<String, Date>()
    var lastLoadedMrecDateByAdPlatform = mutableMapOf<String, Date>()
    var loadedInterstitialAvailableDuration = 60 * 6
    var loadedRewardedAvailableDuration = 60 * 6
    var loadedBannerAvailableDuration = 60 * 2
    var loadedMrecAvailableDuration = 60 * 2

    fun updateLastLoadInterstitialDateByAdPlatform(adPlatformEnum: AdPlatformTypeEnum) {
        lastLoadedInterstitialsDateByAdPlatform[adPlatformEnum.name] = Date()
    }

    fun isValidLoadedInterstitial(adPlatformEnum: AdPlatformTypeEnum): Boolean {
        var isValid = true

        val lastLoadedDate = lastLoadedInterstitialsDateByAdPlatform.get(adPlatformEnum.name)
        if (lastLoadedDate != null) {
            val now = Date()
            val elapsedSeconds = (now.time - lastLoadedDate.time) / 1000
            isValid = elapsedSeconds < loadedInterstitialAvailableDuration
        }

        return isValid
    }

    fun updateLastLoadRewardedDateByAdPlatform(adPlatformEnum: AdPlatformTypeEnum) {
        lastLoadedRewardedDateByAdPlatform[adPlatformEnum.name] = Date()
    }

    fun isValidLoadedRewarded(adPlatformEnum: AdPlatformTypeEnum): Boolean {
        var isValid = true

        val lastLoadedDate = lastLoadedRewardedDateByAdPlatform.get(adPlatformEnum.name)
        if (lastLoadedDate != null) {
            val now = Date()
            val elapsedSeconds = (now.time - lastLoadedDate.time) / 1000
            isValid = elapsedSeconds < loadedRewardedAvailableDuration
        }

        return isValid
    }


    fun updateLastLoadBannerDateByAdPlatform(adPlatformEnum: AdPlatformTypeEnum) {
        lastLoadedBannerDateByAdPlatform[adPlatformEnum.name] = Date()
    }

    fun isValidLoadedBanner(adPlatformEnum: AdPlatformTypeEnum): Boolean {
        var isValid = true

        val lastLoadedDate = lastLoadedBannerDateByAdPlatform.get(adPlatformEnum.name)
        if (lastLoadedDate != null) {
            val now = Date()
            val elapsedSeconds = (now.time - lastLoadedDate.time) / 1000
            isValid = elapsedSeconds < loadedBannerAvailableDuration
        }

        return isValid
    }


    fun updateLastLoadMrecDateByAdPlatform(adPlatformEnum: AdPlatformTypeEnum) {
        lastLoadedMrecDateByAdPlatform[adPlatformEnum.name] = Date()
    }

    fun isValidLoadedMrec(adPlatformEnum: AdPlatformTypeEnum): Boolean {
        var isValid = true

        val lastLoadedDate = lastLoadedMrecDateByAdPlatform.get(adPlatformEnum.name)
        if (lastLoadedDate != null) {
            val now = Date()
            val elapsedSeconds = (now.time - lastLoadedDate.time) / 1000
            isValid = elapsedSeconds < loadedMrecAvailableDuration
        }

        return isValid
    }


}


