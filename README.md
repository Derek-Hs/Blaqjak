# BlaccJacc - Blackjack Basic Strategy Trainer

A blackjack training app that helps you learn perfect basic strategy through interactive gameplay. The app enforces basic strategy by preventing incorrect moves and providing immediate visual feedback.

## Features

- ✅ Standard blackjack gameplay with splitting, doubling, and standing
- ✅ Real-time basic strategy enforcement
- ✅ Visual feedback on incorrect moves (strikethrough + red text)
- ✅ Multi-hand support when splitting pairs
- ✅ Full-screen immersive experience
- ✅ Portrait and landscape orientation support
- ✅ No betting - focus purely on learning strategy
- ✅ Undo functionality to correct mistakes

## Requirements

- **Java Development Kit (JDK) 17** or higher
- **Android SDK** (included with Android Studio)
- **Gradle** (wrapper included in project)
- **Android device** running Android 5.0 (API 21) or higher

## Building the APK

### Option 1: Using Command Line (Recommended)

1. **Clone or download the project**
   ```bash
   cd /path/to/BlaccJacc
   ```

2. **Set JAVA_HOME environment variable**

   On macOS/Linux:
   ```bash
   export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
   ```

   On Windows:
   ```cmd
   set JAVA_HOME=C:\Program Files\Java\jdk-17
   ```

3. **Build the debug APK**

   On macOS/Linux:
   ```bash
   ./gradlew assembleDebug
   ```

   On Windows:
   ```cmd
   gradlew.bat assembleDebug
   ```

4. **Locate the APK**

   The APK will be created at:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

### Option 2: Using Android Studio

1. **Open the project in Android Studio**
   - Launch Android Studio
   - Click "Open" and select the `BlaccJacc` folder

2. **Build the APK**
   - Go to `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
   - Wait for the build to complete
   - Click "locate" in the notification to find the APK

3. **APK Location**
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

## Installing on Android Device

### Method 1: Via USB Cable (ADB)

1. **Enable Developer Options on your Android device**
   - Go to `Settings` → `About Phone`
   - Tap `Build Number` 7 times
   - You'll see "You are now a developer!"

2. **Enable USB Debugging**
   - Go to `Settings` → `Developer Options`
   - Enable `USB Debugging`

3. **Connect your device to computer via USB**
   - When prompted on your device, select "Allow USB debugging"

4. **Install the APK using ADB**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

   If you have multiple devices connected:
   ```bash
   adb devices  # List devices
   adb -s <device-id> install app/build/outputs/apk/debug/app-debug.apk
   ```

5. **Launch the app**
   - Find "BlaccJacc" in your app drawer
   - Tap to open

### Method 2: Direct Install (Side-loading)

1. **Transfer the APK to your device**
   - Email it to yourself
   - Use Google Drive, Dropbox, or any file sharing service
   - Transfer via USB to your device's storage

2. **Enable installation from unknown sources**

   **Android 8.0+ (Oreo and newer):**
   - When you try to install, you'll be prompted to allow installation from that source
   - Tap `Settings` → Enable `Allow from this source`

   **Android 7.1 and older:**
   - Go to `Settings` → `Security`
   - Enable `Unknown Sources`

3. **Install the APK**
   - Open your file manager
   - Navigate to where you saved the APK
   - Tap on `app-debug.apk`
   - Tap `Install`

4. **Launch the app**
   - Tap `Open` after installation completes
   - Or find "BlaccJacc" in your app drawer

### Method 3: Using Android Studio

1. **Connect your device via USB**
   - Enable USB debugging as described above

2. **Run from Android Studio**
   - Click the "Run" button (green play icon)
   - Select your device from the list
   - The app will build, install, and launch automatically

## How to Play

1. **Start the game** - A new hand is dealt automatically
2. **Choose your action**:
   - **Hit** - Take another card
   - **Stand** - Keep your current hand
   - **Double** - Double your bet and take one more card (only available on first two cards)
   - **Split** - Split pairs into two hands (only available with matching cards)

3. **Learning basic strategy**:
   - Buttons that deviate from basic strategy will show a red strikethrough when tapped
   - Only the correct basic strategy action will execute
   - Try different actions to learn the correct play

4. **Game flow**:
   - After each hand completes, tap "New Hand" to continue
   - The dealer reveals their hole card when your turn ends
   - Results are shown for each hand

## Troubleshooting

### Build Issues

**"JAVA_HOME not set" error:**
- Make sure you have JDK 17 installed
- Set the JAVA_HOME environment variable correctly
- Verify with: `echo $JAVA_HOME` (Mac/Linux) or `echo %JAVA_HOME%` (Windows)

**"SDK not found" error:**
- Install Android Studio which includes the Android SDK
- Or set ANDROID_HOME to your SDK location

### Installation Issues

**"App not installed" error:**
- Make sure you have enough storage space
- Try uninstalling any previous version first
- Check that your Android version is 5.0 or higher

**APK won't install:**
- Verify "Unknown Sources" is enabled (for direct install)
- Check if the APK file downloaded completely
- Try a different transfer method

### Runtime Issues

**App crashes on startup:**
- Check your Android version (minimum: 5.0/API 21)
- Clear app data: `Settings` → `Apps` → `BlaccJacc` → `Clear Data`
- Reinstall the app

**Screen rotation issues:**
- The app supports both portrait and landscape
- If orientation is locked, unlock it in your device settings

## Development

### Project Structure
```
BlaccJacc/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/blaccjacc/
│   │   │   │   ├── model/          # Game logic
│   │   │   │   ├── viewmodel/      # Business logic
│   │   │   │   ├── ui/             # Compose UI
│   │   │   │   └── interface/      # Data interfaces
│   │   │   └── res/                # Resources
│   │   └── test/                   # Unit tests
│   └── build.gradle                # App build config
├── gradle/                         # Gradle wrapper
└── build.gradle                    # Project build config
```

### Technologies Used
- **Kotlin** - Primary language
- **Jetpack Compose** - Modern UI toolkit
- **Coroutines** - Asynchronous programming
- **ViewModel** - MVVM architecture
- **StateFlow** - Reactive state management

## License

This project is for educational purposes.

## Support

For issues or questions, please create an issue in the repository.

---

**Happy learning! Master basic strategy and improve your blackjack game!** ♠️♥️♣️♦️
