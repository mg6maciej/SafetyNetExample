# SafetyNetExample

In order to run this demo app, first change SHA256 in server (MD7VgYhe4S02QWijuO8TtKbs5BLGTNnZOacdaWt6hGk=) to a value that will match with the certificate you own, e.g.

    keytool -list -v -keystore ~/.android/debug.keystore
    password: android
    
    output:
    ...
    SHA256: 30:3E:D5:81:88:5E:E1:2D:36:41:68:A3:B8:EF:13:B4:A6:EC:E4:12:C6:4C:D9:D9:39:A7:1D:69:6B:7A:84:69
    ...
    
    go to http://tomeko.net/online_tools/hex_to_base64.php?lang=en to convert SHA256 value to base64

Run server with:

    cd Server/
    ./gradlew bootRun

Run client app on emulator on the same machine (server address hardcoded http://10.0.2.2:8080/, so emu connects without problems).

### Slides

http://slides.com/mg6maciej/safetynet-android-security-mo2017
