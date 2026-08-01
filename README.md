# Update

Update is an Android news reader for keeping up with developments across six technology domains:

- Cyber Security
- Artificial Intelligence and Machine Learning
- Software Engineering
- Networking
- Data Science and Analytics
- UI/UX Design

The app fetches articles from public RSS feeds, caches them locally for offline reading, and lets users maintain a separate list of saved articles.

## Features

- Browse recent technology news by category.
- Read full articles in an in-app WebView.
- Cache fetched articles locally with Room.
- Browse cached article summaries without an internet connection.
- Long-press an article to add it to Saved Articles.
- Long-press a saved article to remove it.
- Detect unavailable internet access and offer Wi-Fi settings, retry, or offline mode.
- Display animated loading states while content is being retrieved.

## News sources

| Category | RSS source |
| --- | --- |
| Cyber Security | The Hacker News |
| AI/ML | MIT Technology Review |
| Software Engineering | Toptal Engineering Blog |
| Networking | Cisco Networking Blog |
| Data Science | KDnuggets |
| UI/UX | Nielsen Norman Group |

The application depends on the structure and availability of these third-party RSS feeds. A feed format change may require an update to its corresponding parser.

## How it works

Each online category follows the same data flow:

```text
Category Activity
      |
      v
One-time WorkManager task
      |
      v
Fetch and parse RSS XML
      |
      v
Store category articles in Room
      |
      v
Observe Room with LiveData
      |
      v
Display articles in a RecyclerView
```

Room acts as the offline cache. Saved Articles use a separate Gson-serialized list stored in `SharedPreferences`.

## Technology stack

- Java
- Android Views and XML layouts
- AndroidX AppCompat and Material Components
- WorkManager for background RSS fetching
- Room for cached article storage
- LiveData for database observation
- RecyclerView and CardView for article lists
- Gson and SharedPreferences for saved articles
- Lottie for loading animations

## Requirements

- Android Studio with a compatible JDK (Android Studio's bundled JDK works)
- Android SDK 35 for compilation
- An Android device or emulator running Android 12/API 31 or newer
- Internet access for fetching live news and opening full articles

The application currently uses:

- `compileSdk 35`
- `targetSdk 34`
- `minSdk 31`

## Getting started

1. Clone or download the project.
2. Open the project directory in Android Studio.
3. Allow Gradle to synchronize and download the required dependencies.
4. Select an Android 12 or newer device/emulator.
5. Run the `app` configuration.

To build from a Windows terminal:

```powershell
.\gradlew.bat assembleDebug
```

On macOS or Linux:

```bash
./gradlew assembleDebug
```

The generated debug APK is placed under `app/build/outputs/apk/debug/`.

## Using the app

1. Select a category from the home screen to fetch its latest articles.
2. Tap an article to open the full page in the in-app browser.
3. Long-press an article and confirm to save it.
4. Open **Saved Articles** from the home or offline screen to view saved items.
5. Long-press a saved article to remove it.
6. Select **Offline Mode** to browse summaries previously cached in Room.

Offline mode becomes available after articles have been fetched successfully at least once. Full web articles still require an internet connection.

## Project structure

```text
app/src/main/
|-- AndroidManifest.xml
|-- java/com/systemtech/update/
|   |-- MainActivity.java              # Home screen and connectivity handling
|   |-- *Activity.java                 # Online category screens
|   |-- OfflineActivity.java           # Offline category menu
|   |-- SavedPreferencesActivity.java  # Saved article screen
|   |-- WebPageActivity.java           # In-app article browser
|   |-- Utils.java                     # SharedPreferences saved-article storage
|   |-- adapters/                      # RecyclerView adapters
|   |-- backgroundTasks/               # RSS-fetching WorkManager workers
|   |-- database/                      # Room entity, DAO, and database
|   `-- offlineMode/                   # Offline category screens
`-- res/
    |-- layout/                        # XML screen and list layouts
    |-- drawable/                      # Category backgrounds and graphics
    |-- raw/                           # Lottie loading animation
    `-- values/                        # Strings, colors, fonts, and themes
```

## Testing

Run the local unit tests with:

```powershell
.\gradlew.bat testDebugUnitTest
```

The project also contains an instrumentation test for the Cyber Security worker. It requires an Android device or emulator and live network access because it fetches the real RSS feed.

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

The debug build and local unit-test task currently complete successfully. The Gradle `check` lifecycle is not yet usable because it references a `checkstyleDebug` task that has not been defined.

## Current limitations

- RSS parsing is implemented separately for each category and assumes a conventional RSS structure.
- Category Activities, workers, offline Activities, and layouts contain substantial duplicated logic.
- Offline data is refreshed only when its online category is opened successfully.
- Some RSS descriptions may contain HTML markup that is shown as plain text.
- Saved articles are not deduplicated.
- Saved articles are stored independently from the Room offline cache.
- Full articles cannot be opened in offline mode.
- Automated test coverage is currently limited.

## Permissions

The app requests:

- `android.permission.INTERNET` to download RSS feeds and load article pages.
- `android.permission.ACCESS_NETWORK_STATE` to inspect connectivity.

## License

No license has been added to this repository.

