# ProntoDroid

## Features
* Ability to translate ProntoHEX IR code format directly to audio signals
  * Control your TV! Stereo! AC Unit! Amplifier! Blu-Ray Player!
* Simplistic black design looks fantastic on AMOLED screens and in low-lit settings
  * Perfect to keep from killing your eyes or distracting from the movie!
* **Ditch** other remote apps that want to track you and serve you **ads**
  * Who wants to see ads in the first place? Much less on the _remote_!

## Requirements
* Android Studio 3.0+ (Beta or Canary - required for Java 1.8)
  * Can be installed alongside a stable version. You can get it [here](https://developer.android.com/studio/preview/index.html).
* Gradle 4.0+ (Comes with Android Studio 3)
* 3.5mm IR adapter
  * These are super cheap - I lazily picked mine up off [eBay](https://www.ebay.com/itm/351790827736), but you could also just [build](https://electronics.stackexchange.com/questions/56540/ir-audio-receiver-and-transmitter) one.
* A device running Android Nougat 7.0+

## Building / Installation
1. Clone this github repo
2. Import the project into Android Studio
3. Build, deploy, and enjoy!

I only have a handful of functional Android devices lying around the place.
As such, I've only tested this on my Google Pixel running PureNexus 7.1.2 and my girlfriend's Samsung Galaxy S7 so far.

## Screenshots
![Screenshot 1](/doc/Screen1.png)
![Screenshot 2](/doc/Screen2.png)
![Screenshot 3](/doc/Screen3.png)
![Screenshot 4](/doc/Screen4.png)

## To Do:

* Add ability to change remotes/buttons
* Add support for earlier versions of Android (or you could upgrade!)
* Add ability to change themes.
* Add ability to generate playable sounds from other code formats
* Consider adding functionality to record signals from other remotes (would require another adapter and defeat the main purpose)

## WARNING
DO NOT test any remote codes on with this app with you phone connected to anything other than an IR transmitter.
You'll regret it.
