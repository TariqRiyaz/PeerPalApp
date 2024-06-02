# PeerPal Android Project

Welcome to the PeerPal Android Project! This guide will help you fork the project, set it up in Android Studio, and run it on your device using USB debugging.

## Prerequisites

Before you begin, ensure you have the following installed on your local machine:

- [Android Studio](https://developer.android.com/studio)
- The latest Android device with USB debugging enabled

## Fork the Repository

1. Go to the GitHub page of the repository: `https://github.com/TariqRiyaz/PeerPalApp`
2. Click the `Fork` button in the upper right corner to create a copy of the repository under your GitHub account.

## Link Your GitHub Account to Android Studio

1. In Android Studio, go to `File` -> `Settings` (or `Preferences` on macOS).
2. Navigate to `Version Control` -> `GitHub`.
3. Click the `+` button to add your GitHub account.
4. Follow the prompts to authenticate your GitHub account.

## Clone and Open the Project

1. Click the hamburger menu on the top left corner in Android Studio.
2. Go to `Git` -> `Clone`.
3. Select the PeerPal cloned repo (if not visible, make sure you linked your GitHub account and forked the repository on GitHub.com).
4. Click `Clone` and it should clone the repository locally to your machine and build it.

Android Studio will take a few moments to set up the project. Ensure that all Gradle dependencies are downloaded and the project is built without errors by going to `Build` -> `Rebuild Project`.

## Enable USB Debugging on Your Device

1. Open the `Settings` app on your Android device.
2. Scroll down and tap `About phone`.
3. Find the `Build number` and tap it seven times to enable Developer options.
4. Go back to the main `Settings` menu and tap `System`.
5. Tap `Developer options`.
6. Enable `USB debugging`.

## Run the Application on Your Device

1. Connect your Android device to your computer using a USB cable (make sure it supports connecting capabilities instead of just for charging).
2. In Android Studio, click the `Run` button (green play icon) or press `Shift + F10`.
3. A dialog will appear to select a device. Choose your connected device from the list and click `OK`.

Android Studio will build the project and install the app on your device. You should see the PeerPal app running on your device shortly.

## Troubleshooting

If you encounter any issues, try the following:

- Ensure your USB cable is properly connected and working.
- Verify that USB debugging is enabled on your device.
- Check if your device is recognized by your computer. You can run `adb devices` in your terminal to see if your device is listed.
- Make sure all dependencies are correctly set up in Android Studio. You may need to sync your project with Gradle files by clicking on `File` -> `Sync Project with Gradle Files`. Ensure that all Gradle dependencies are downloaded and the project is built without errors by going to `Build` -> `Rebuild Project`.

---

Thank you for using the PeerPal Android Project! If you have any questions or need further assistance, feel free to open an issue on GitHub!
