package com.clouddroid.petscarehealth.utils

import PreferenceUtils.set
import android.content.SharedPreferences
import com.clouddroid.petscarehealth.purchaseutils.IabHelper
import com.clouddroid.petscarehealth.purchaseutils.IabResult
import com.clouddroid.petscarehealth.purchaseutils.Inventory
import com.clouddroid.petscarehealth.purchaseutils.Purchase

/**
 * Created by Arkadiusz on 23.02.2018
 */

object PurchaseUtils {

    const val base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkTrBQIuDizW0EC6eyT0aJMyhXRQNV+MXD5phwoNHbwmOXhl23OrQ3q/di7E9CWfrwbD5fNHozJWOpf3gQjoT3zjUKhRYjaC2np9JcsjKcgbAUWWcjl9GlyaQQU1eIONS/x0+4EVq9ppRITj0N5Y0gR7dCAJdHSjXLhwn2DuZTToUa5VL7uT03T/YeXM67FDYpBhCRO32OeE5EQNcLBOCxLPkg+smTStKAIDABp0NbYQFyQJkEeLTz0chpSClRZQsqEpgyyy77N1DcvoSohUluw8orAZbCLyQ5FVOp9FXm+LYeAk/nB8aMpcBJ3CzMYSc0+qyyAcDT3s4rKkC+rG1xwIDAQAB"
    lateinit var sharedPreferences: SharedPreferences

    object GotInventoryListener : IabHelper.QueryInventoryFinishedListener {
        override fun onQueryInventoryFinished(result: IabResult, inv: Inventory) {
            if (result.isFailure) {
                //nothing
            } else {
                val purchase = inv.getPurchase("remove_ads")
                if (purchase != null) {
                    sharedPreferences["removed_ads"] = "true"
                }
            }
        }
    }

    object PurchaseFinishedListener : IabHelper.OnIabPurchaseFinishedListener {
        override fun onIabPurchaseFinished(result: IabResult, purchase: Purchase) {
            if (result.isFailure) {
                //nothing
            } else if (purchase.sku == "remove_ads") {
                sharedPreferences["removed_ads"] = "true"
            }
        }
    }
}