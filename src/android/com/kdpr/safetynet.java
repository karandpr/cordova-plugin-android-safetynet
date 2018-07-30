/**
 */
package com.kdpr;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;
import java.util.Date;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.safetynet.HarmfulAppsData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.util.Base64;

public class safetynet extends CordovaPlugin {
  private static final String TAG = "safetynet";

  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);  
  }

  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException { 
    if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this.cordova.getActivity().getApplicationContext())
    == ConnectionResult.SUCCESS) {    
    if(action.equals("attest")) {                      
        String nonceStr = args.getString(0);          
        SafetyNet.getClient(this.cordova.getActivity()).attest(nonceStr.getBytes(), args.getString(1))
            .addOnSuccessListener(this.cordova.getActivity(),
                new OnSuccessListener<SafetyNetApi.AttestationResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.AttestationResponse response) {                                   
                        /*Success - SafetNet Attestation*/                               
                        callbackContext.success(response.getJwsResult());                                    
                    }
                })
            .addOnFailureListener(this.cordova.getActivity(), new OnFailureListener() {
            @Override
                public void onFailure(Exception e) {
                    if (e instanceof ApiException) {                                
                    ApiException apiException = (ApiException) e;                              
                    /** SafetyNet Failed */
                    callbackContext.error("failed "+e.getMessage());
                } else {                               
                     /** SafetyNet Failed */
                    callbackContext.error("failed " + e.getMessage());
                }
            }                        
        });
    } else if (action.equals("checkAppVerification")) {
        SafetyNet.getClient(this.cordova.getActivity())
            .isVerifyAppsEnabled()
            .addOnCompleteListener(new OnCompleteListener<SafetyNetApi.VerifyAppsUserResponse>() {
                @Override
                public void onComplete(Task<SafetyNetApi.VerifyAppsUserResponse> task) {
                if (task.isSuccessful()) {
                    SafetyNetApi.VerifyAppsUserResponse result = task.getResult();
                    if (result.isVerifyAppsEnabled()) {
                        /**Verify Apps is Enabled */
                        callbackContext.success("true");
                    } else {
                        callbackContext.success("false");
                        /**Verify Apps is Disabled */
                    }
                } else {
                    /**Error checking Verify Appss */
                    callbackContext.error("failed ");
                }
            }
        });                
                

    } else if (action.equals("listHarmfulApps")){

        SafetyNet.getClient(this.cordova.getActivity())
            .listHarmfulApps()
            .addOnCompleteListener(new OnCompleteListener<SafetyNetApi.HarmfulAppsResponse>() {
                @Override
                public void onComplete(Task<SafetyNetApi.HarmfulAppsResponse> task) {
                   
                    if (task.isSuccessful()) {
                        SafetyNetApi.HarmfulAppsResponse result = task.getResult();
                        String harmfulAppArray = "[";
                        long scanTimeMs = result.getLastScanTimeMs();
                        List<HarmfulAppsData> appList = result.getHarmfulAppsList();
                        if (appList.isEmpty()) {
                            harmfulAppArray = harmfulAppArray + "]";
                            /**No Harmful Apps were found */
                        } else {
                            Log.e(TAG, "Potentially harmful apps are installed!");
                        for (HarmfulAppsData harmfulApp : appList) {
                            harmfulAppArray = harmfulAppArray + "{APK: "+ harmfulApp.apkPackageName + ", SHA-256: "+ harmfulApp.apkSha256 + "Category: " + harmfulApp.apkCategory+ "},";
                            Log.d(TAG, "APK: " + harmfulApp.apkPackageName);
                            Log.d(TAG, "SHA-256: " + harmfulApp.apkSha256);
                        // Categories are defined in VerifyAppsConstants.
                            Log.d(TAG,"Category: " + harmfulApp.apkCategory);
                            }
                            harmfulAppArray = harmfulAppArray + "]";
                        }
                        callbackContext.success(harmfulAppArray);
                    } else {
                        /**Error finding harmful Apps. Check App Verification */
                        callbackContext.error("Error finding harmful Apps");
                    }   
            }
        });
    } else if(action.equals("enableAppVerification")){

            SafetyNet.getClient(this.cordova.getActivity())
                .enableVerifyApps()
                .addOnCompleteListener(new OnCompleteListener<SafetyNetApi.VerifyAppsUserResponse>() {
                    @Override
                    public void onComplete(Task<SafetyNetApi.VerifyAppsUserResponse> task) {
                        if (task.isSuccessful()) {
                            SafetyNetApi.VerifyAppsUserResponse result = task.getResult();
                            if (result.isVerifyAppsEnabled()) {
                                /**User gave consent for Verify Apps or verify apps is enabled */
                                        callbackContext.success("true");
                            } else {
                               /**User refused to give consent */
                                        callbackContext.success("false");
                            }
                        } else {
                            /**Error processning verify Apps */
                            callbackContext.error("failed ");
                        }
                    }
                });
    }
            
}         else {
     /** Play services not found */
     callbackContext.error("Play Services not found");
   }
    return true;
  }

}
