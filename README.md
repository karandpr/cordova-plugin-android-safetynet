# Cordova SafetyNet

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3292840a5d6847cdbfca7fc465a3f45f)](https://app.codacy.com/app/karandpr/cordova-plugin-android-safetynet?utm_source=github.com&utm_medium=referral&utm_content=karandpr/cordova-plugin-android-safetynet&utm_campaign=Badge_Grade_Dashboard)

A wrapper plugin for safetynet API on Play Enabled devices. This plugin extends attestation and app verification methods.
Google Play Service are required on device.
At the time of writing , I am targeting Play Service 15.0.1 in gradle.

## Installation

Cordova:
```bash
cordova plugin add cordova-plugin-android-safetynet
```

Ionic:
```bash
ionic cordova plugin add cordova-plugin-android-safetynet
```
## Safety Net URL & some links
TBA

### Steps for Attestation

*   Obtain API key from Google Developer Console. The name of the API is `Android Device Verification`. Get the API_KEY from `Credentials Section`.
*   Generate a nonce server side: This a unique token which should be generated server side. Google recommends atleast 16 byte long token.
*   Invoke the cordova method
*   Get the result from success callback. Send it to the server
*   Send trhe sesult to android check to verify the payload server side.
*   After extract the result. Compare the values and base your logic from the result.

### Syntax for Attestation
You will need API key from Google APIs Console for initiating attestation.

A nonce security token from server side, is also required.
```javascript
window.safetynet.attest(nonce ,API_Key ,successCallback ,errorCallback);
```

#### Success Callbacks
A JWS string is sent back as success. This is the same data obtained from getJwsResult(). This data should be again cross verified with google.

### Syntax for Verify Apps API
Three methods are available for the SafetyNet Verify Apps API.

#### Check Verify App is Enabled
This method allows a check for Verify App(Play Protect). The result is a boolean in successcallback.
```javascript
window.safetynet.checkAppVerification(successCallback, errorCallback)
```
#### Enable App Verification
This method allows you to enable App Verfication. A dialog is invoked to  If the verification is already enabled , the successCallback returns true. successCallback is  a boolean. It represnts the state of App verification.
```javascript
window.safetynet.enableAppVerification(successCallback, errorCallback);
```

#### List Harmful Apps
This method lists harmful apps installed in a device. App verification/Play Protect is required to be enabled to process the request. The result is an array of objects in successCallback. 
```javascript
window.safetynet.listHarmfulApps(successCallback, errorCallback);
```

### Issues
Post the issues related to this library <a href="https://github.com/karandpr/cordova-plugin-android-safetynet/issues"> here </a>. Do provide the device details as below. 
*   Device name & manufacturer
*   Android version 
*   Custom OS version (Lineage , MIUI , Exodus, DU)
