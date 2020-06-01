package com.helikanonlib.admanager.adplatforms


import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import com.helikanonlib.admanager.*
import com.startapp.sdk.ads.banner.Banner
import com.startapp.sdk.ads.banner.BannerListener
import com.startapp.sdk.ads.banner.Mrec
import com.startapp.sdk.adsbase.StartAppAd
import com.startapp.sdk.adsbase.StartAppSDK
import com.startapp.sdk.adsbase.VideoListener
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener
import com.startapp.sdk.adsbase.adlisteners.AdEventListener


/**
 * *************************************************************************************************
 * STARTAPP ADS HELPER
 * *************************************************************************************************
 */
class StartAppAdWrapper(override var appId: String, override var activity: Activity, override var context: Context) :
    AdPlatformWrapper(appId, activity, context) {
    override val platform = AdPlatformTypeEnum.STARTAPP

    var startAppAd: StartAppAd? = null
    var bannerAdView: Banner? = null
    var startAppAdRewarded: StartAppAd? = null
    var mrecAdView: Mrec? = null


    companion object {
        var isInitialized = false
    }

    override fun initialize() {
        if (isInitialized) return

        StartAppSDK.init(context, appId, false);
        StartAppAd.disableSplash();

        /*StartAppSDK.setUserConsent(
            context,
            "pas",
            System.currentTimeMillis(),
            true
        );*/

        startAppAd = StartAppAd(context)
        startAppAdRewarded = StartAppAd(context)

        isInitialized = true
    }

    override fun enableTestMode(deviceId: String?) {
        StartAppSDK.setTestAdsEnabled(BuildConfig.DEBUG);
    }

    override fun loadInterstitial(listener: AdPlatformLoadListener?) {
        if (isInterstitialLoaded()) {
            listener?.onLoaded()
            return
        }

        startAppAd?.loadAd(object : AdEventListener {
            override fun onFailedToReceiveAd(p0: com.startapp.sdk.adsbase.Ad?) {
                listener?.onError()
            }

            override fun onReceiveAd(p0: com.startapp.sdk.adsbase.Ad?) {
                listener?.onLoaded()
            }
        })
    }

    override fun showInterstitial(listener: AdPlatformShowListener?) {
        if (!isInterstitialLoaded()) return

        startAppAd?.showAd(object : AdDisplayListener {
            override fun adHidden(p0: com.startapp.sdk.adsbase.Ad?) {
                listener?.onClosed()
            }

            override fun adDisplayed(p0: com.startapp.sdk.adsbase.Ad?) {
                listener?.onDisplayed()
            }

            override fun adNotDisplayed(p0: com.startapp.sdk.adsbase.Ad?) {
                listener?.onError()
            }

            override fun adClicked(p0: com.startapp.sdk.adsbase.Ad?) {
                listener?.onClicked()
            }
        })
    }

    override fun isInterstitialLoaded(): Boolean {
        return startAppAd?.isReady ?: false
    }

    override fun showBanner(containerView: RelativeLayout, listener: AdPlatformShowListener?) {

        val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT).apply {
            addRule(RelativeLayout.CENTER_HORIZONTAL)
        }

        if (isBannerLoaded(bannerAdView)) {
            try {
                removeBannerViewIfExists(bannerAdView)
                containerView.addView(bannerAdView, lp)
                listener?.onDisplayed()
            } catch (e: Exception) {
                listener?.onError()
            }
            return
        }

        bannerAdView = Banner(activity, object : BannerListener {
            override fun onClick(p0: View?) {
                listener?.onClicked()
            }

            override fun onFailedToReceiveAd(p0: View?) {
                listener?.onError()
            }

            override fun onReceiveAd(p0: View?) {
                removeBannerViewIfExists(bannerAdView)
                containerView.addView(bannerAdView, lp)
                listener?.onDisplayed()
            }

            override fun onImpression(p0: View?) {}

        })
        bannerAdView?.loadAd()
    }


    override fun loadRewarded(listener: AdPlatformLoadListener?) {
        if (isRewardedLoaded()) {
            listener?.onLoaded()
            return
        }

        startAppAdRewarded?.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, object : AdEventListener {
            override fun onFailedToReceiveAd(p0: com.startapp.sdk.adsbase.Ad?) {
                listener?.onError()
            }

            override fun onReceiveAd(p0: com.startapp.sdk.adsbase.Ad?) {
                listener?.onLoaded()
            }
        })
    }

    override fun showRewarded(listener: AdPlatformShowListener?) {
        if (!isRewardedLoaded()) {
            listener?.onError()
            return
        }

        startAppAdRewarded?.setVideoListener(object : VideoListener {
            override fun onVideoCompleted() {
                listener?.onRewarded()
            }
        })
        startAppAdRewarded?.showAd(object : AdDisplayListener {
            override fun adHidden(p0: com.startapp.sdk.adsbase.Ad?) {
                listener?.onClosed()
            }

            override fun adDisplayed(p0: com.startapp.sdk.adsbase.Ad?) {
                listener?.onDisplayed()
            }

            override fun adNotDisplayed(p0: com.startapp.sdk.adsbase.Ad?) {
                listener?.onError()
            }

            override fun adClicked(p0: com.startapp.sdk.adsbase.Ad?) {
                listener?.onClicked()
            }
        })
    }

    override fun isRewardedLoaded(): Boolean {
        return startAppAdRewarded?.isReady ?: false
    }

    override fun showMrec(containerView: RelativeLayout, listener: AdPlatformShowListener?) {
        val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT).apply {
            addRule(RelativeLayout.CENTER_HORIZONTAL)
        }

        if (isBannerLoaded(mrecAdView)) {
            try {
                removeBannerViewIfExists(mrecAdView)
                containerView.addView(mrecAdView, lp)
                listener?.onDisplayed()
            } catch (e: Exception) {
                listener?.onError()
            }
            return
        }

        mrecAdView = Mrec(activity, object : BannerListener {
            override fun onClick(p0: View?) {
                listener?.onClicked()
            }

            override fun onFailedToReceiveAd(p0: View?) {
                listener?.onError()
            }

            override fun onReceiveAd(p0: View?) {
                removeBannerViewIfExists(mrecAdView)
                containerView.addView(mrecAdView, lp)

                listener?.onDisplayed()
            }

            override fun onImpression(p0: View?) {}

        })
        mrecAdView?.loadAd()
    }


    override fun destroy() {
        startAppAd = null
        startAppAdRewarded = null
        bannerAdView = null
    }

    override fun onPause() {}
    override fun onStop() {}
    override fun onResume() {}

}

