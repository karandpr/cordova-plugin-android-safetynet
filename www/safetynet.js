
var exec = require('cordova/exec');

var PLUGIN_NAME = 'safetynet';

var safetynet = {
  attest: function(nonce, api_key, cb, err){
    exec(cb, err, PLUGIN_NAME, 'attest', [nonce, api_key]);
  },  
  checkAppVerification: function(cb, err){
    exec(cb, err, PLUGIN_NAME, 'checkAppVerification', []);
  },
  listHarmfulApps: function(cb, err){
    exec(cb, err, PLUGIN_NAME, 'listHarmfulApps', []);
  } ,
  enableAppVerification: function(cb, err){
    exec(cb, err, PLUGIN_NAME, 'enableAppVerification', []);
  }  
};

module.exports = safetynet;
