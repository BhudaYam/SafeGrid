# SafeGrid

![Android CI](https://github.com/BhudaYam/SafeGrid/actions/workflows/build.yml/badge.svg)

A community safety and loadshedding hub for South Africa. SafeGrid lets residents see loadshedding status, view and report safety incidents on a map, and trigger an SOS alert, all in one app.

## Purpose
South Africans juggle separate apps for loadshedding (EskomSePush) and community safety (Namola, Safe Community App). SafeGrid combines both, so people can plan around power cuts and stay aware of incidents in their area. The research and planning behind it are in Part 1 of this PoE.

## Features
- Register and log in (passwords are hashed before storage)
- Single sign-on with Google (Firebase Authentication)
- Loadshedding status from a REST API
- Incident map of Durban, KwaZulu-Natal, built on OpenStreetMap, with pins and a tappable incident list
- Report an incident with a photo (stored in Firebase Storage)
- SOS alert with location
- Settings: language, notifications and theme
- Dark mode
- Multi-language support: English and [LANGUAGE 2, e.g. isiZulu]
- Offline mode with background sync (Room and WorkManager)
- Real-time push notifications (Firebase Cloud Messaging)
- NoSQL cloud database (Cloud Firestore)

## Design considerations
- **Jetpack Compose** with Material 3 for a modern, consistent UI
- **MVVM**: a single `SafeGridViewModel` holds UI state, the screens only display it
- **Offline first**: Room stores data locally and WorkManager syncs it once the phone reconnects
- **Input validation** on login and register forms so invalid input never crashes the app
- **OpenStreetMap** instead of Google Maps, so there is no API key or billing and the map is free to run

## Tech stack
Kotlin, Jetpack Compose, Room, WorkManager, DataStore, Firebase (Auth, Firestore, Storage, Messaging), OkHttp, osmdroid, Credential Manager.

## GitHub and GitHub Actions
The repository holds the full project history, committed regularly during development. A GitHub Actions workflow (`.github/workflows/build.yml`) runs on every push. It checks out the code, sets up JDK 17, runs the unit tests, builds the debug APK and uploads it as a downloadable artifact. This confirms the app builds on a clean machine, not only on my computer.

[INSERT SCREENSHOT OF THE GREEN ACTIONS RUN]

## Screenshots
[INSERT 4-5 SCREENSHOTS: login, home, map, settings, dark mode]

## Demo video
[INSERT VIDEO LINK]

## Note on data
The incident pins on the map are seeded sample data for Durban, used for demonstration.

## Release notes

### v1.0-poe (final PoE)
Updates since the prototype:
- Added a real OpenStreetMap map of Durban with incident pins and a tappable incident list (innovative feature)
- Added dark mode
- Added Google single sign-on
- Added offline mode with background sync
- Added real-time push notifications
- Added multi-language support
- Added a GitHub Actions workflow that builds and tests on every push
- Connected the app to Firebase (Auth, Firestore, Storage, Messaging)
- Fixed build issues by upgrading Kotlin, KSP and Room