# iCure device configurator app

## features 
The apps allows to setup an iCure IoT device by sending the necessary fields via Bluetooth Low Energy.
Listens to the live messages of the latest configured device (storing cid and topic in sharedPreferences)

## problems
Currently, the cid is stored in SharedPreferences
The are bugs when attempting to configure two devices in a row (without restarting the app)
