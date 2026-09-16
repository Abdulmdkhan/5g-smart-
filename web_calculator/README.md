# Simple Calculator - Capacitor Wrap Instructions

You requested a web-based mobile app that can be converted into an Android APK using a wrapper like Capacitor.js. This folder contains the complete HTML, CSS, and JavaScript source code for the requested Simple Calculator app.

## Step 1: Download this Code
In Google AI Studio, you can export this workspace as a ZIP file. Once downloaded, extract it to your computer and navigate to this `web_calculator` folder in your terminal.

## Step 2: Install Node.js
If you don't already have Node.js installed, download and install it from [nodejs.org](https://nodejs.org/).

## Step 3: Initialize the Project
Open your terminal/command prompt inside this `web_calculator` directory and run:
```bash
npm init -y
```

## Step 4: Install Capacitor
Install the Capacitor CLI and Core packages:
```bash
npm install @capacitor/cli @capacitor/core
```

## Step 5: Initialize Capacitor
Run the Capacitor init command. It will ask for your App Name and App ID (e.g., `com.example.calculator`). When it asks for your "web asset directory", type `.` (just a period, meaning the current directory).
```bash
npx cap init
```

## Step 6: Add the Android Platform
Install the Android package and add the Android project:
```bash
npm install @capacitor/android
npx cap add android
```

## Step 7: Build the APK
To compile the HTML/CSS/JS into an actual Android APK, you need Android Studio installed on your computer. Run the following command to open the generated project in Android Studio:
```bash
npx cap open android
```

Once Android Studio opens:
1. Wait for the initial Gradle sync to finish.
2. In the top menu, click **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
3. Once finished, a popup will appear in the bottom right. Click **locate** to find your compiled `.apk` file!
