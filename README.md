# Weather App

Weather App is an android app for consulting weather conditions.

## Screenshots

![](https://github.com/javiersv777333/Weather_App/blob/master/screenshots/city_weather_light.jpg) ![](https://github.com/javiersv777333/Weather_App/blob/master/screenshots/cities_light.jpg) ![](https://github.com/javiersv777333/Weather_App/blob/master/screenshots/city_weather_dark.jpg) ![](https://github.com/javiersv777333/Weather_App/blob/master/screenshots/cities_dark.jpg)

## Features

The app displays the current city weather condition and allows the user to search for other cities weather conditions providing the name of the city. The user should grant the location permission and activate the gps if he wants to get the weather location of the city in which he is, but if he doesn't provide the access, he can still search for other cities. The app cache all cities which the user already asked for its weather, for that, the users can see a list of those and pick one to research its weather or can delete which he wants.

## Requirements

### OpenWeather API key

Weather App uses the OpenWeather API to get the desired city weather. To use the API, you will need to obtain a free developer API key. See the OpenWeather API Documentation for instructions.

Once you have the key, add this line to the `gradle.properties` file, either in your user home directory (usually `~/.gradle/gradle.properties` on Linux and Mac) or in the project's root folder:

``` API_KEY=<your OpenWeather API key> ```

## Development Environment

The app was developed using Android Studio Dolphin | 2021.3.1 Patch 1 and uses the Gradle build system.

To build the app, use the gradlew build command or use "Import Project" in Android Studio.

## Architecture

The implemented architecture is a Clean Architecture which take into account the recommendations laid out in the Guide to App Architecture and take advantages of Jetpack libraries. This architecture is composed of three layers: presentation or UI, domain and data.

- UI layer: The role of the UI is to display the application data on the screen and also to serve as the primary point of user interaction. An UDF (Unidirectional Data Flow) is implemented between Fragments and ViewModels. The ViewModel holds and exposes the state to be consumed by the UI, the UI notifies the ViewModel of user events, the ViewModel handles the user actions and updates the state, and the updated state is fed back to the UI to render.

- Data layer: In essence, this layer is composed by the repository's implementations and the data sources interfaces. The Repository layer handles data operations and abstract the data sources from the rest of the app. There are two data sources in this app: a local and a remote, the data must be fetched from a backend, but one feature of the app is caching the data. The implementation of this data sources were kept out of this layer in order to leave this module without any android framework dependencies (only dagger-hilt for dependencies injection).

- Domain layer: This layer is responsible for encapsulating the business logic. This business logic was expressed in form of use cases. Repositories interfaces were provided to this layer for decoupling it from the data layer. This layer contains the models.

- The local data source is implemented using Room.

- The remote data source is implemented with Retrofit.

- The Navigation component is used to implement navigation in the app, handling Fragment transactions and providing a consistent user experience.

- Kotlin coroutines were used to asynchronously working.

- Data is observed using Kotlin Flows.

- For internet connection detection is use WorkManager.

UI tests are written with Espresso and unit tests use Junit4 with Mockito when necessary.

## Kotlin

The app is written in Kotlin. Asynchronous tasks are handled with coroutines. Coroutines allow for simple and safe management of one-shot operations as well as building and consuming streams of data using Kotlin Flows.
