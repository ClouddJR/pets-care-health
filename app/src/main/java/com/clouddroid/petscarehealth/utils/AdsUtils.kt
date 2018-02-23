package com.clouddroid.petscarehealth.utils

import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView

/**
 * Created by Arkadiusz on 22.02.2018
 */

object AdsUtils {

    const val appID = "ca-app-pub-4098342918729972~9156994820"
    private var adView: AdView? = null

    fun setAdView(adV: AdView) {
        adView = adV
    }

    object AdsListener : AdListener() {
        override fun onAdLoaded() {
            adView?.visibility = View.VISIBLE
        }

        override fun onAdFailedToLoad(errorCode: Int) {
            adView?.visibility = View.GONE
        }
    }
}