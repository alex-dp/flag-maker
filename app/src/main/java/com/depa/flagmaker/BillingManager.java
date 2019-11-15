package com.depa.flagmaker;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;

import org.json.JSONException;

import java.util.List;

class BillingManager implements PurchasesUpdatedListener {
    private BillingClient mBillingClient;
    private Activity mActivity;
    private boolean purchasable = false;
    private Callback callback;

    BillingManager(Activity ac, Callback cb) {
        mActivity = ac;
        callback = cb;
        mBillingClient = BillingClient.newBuilder(mActivity).setListener(this).enablePendingPurchases().build();
        mBillingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    purchasable = true;
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                purchasable = false;
            }
        });
    }

    private BillingFlowParams params;
    int purchase(String sku) {
        if (!purchasable) return BillingClient.BillingResponseCode.BILLING_UNAVAILABLE;
        try {
            params = BillingFlowParams.newBuilder()
                    .setSkuDetails(new SkuDetails(
                            "{\"title\":\"Remove all Ads\"," +
                                    "\"type\":\"inapp\"," +
                                    "\"description\":\"Remove all Ads\"," +
                                    "\"productId\":\"" + sku + "\"}"))
                    .build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (params == null)
            Toast.makeText(mActivity, "I screwed up", Toast.LENGTH_SHORT).show();
        else
            return mBillingClient.launchBillingFlow(mActivity, params).getResponseCode();
        return -1;
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                callback.onSuccess(purchase.getSku());
                mBillingClient.consumeAsync(
                        ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build(),
                        new ConsumeResponseListener() {
                    @Override
                    public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {

                    }
                });
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            callback.onError();
        } else {
            callback.onError();
        }
    }
}