# AudioIR

## Requirements
* Android Studio 3.0+ (Beta or Canary - required for Java 1.8)
  * Can be installed alongside a stable version. You can get it [here](https://developer.android.com/studio/preview/index.html).
* Gradle 4.0+ (Comes with Android Studio 3)
* 3.5mm IR adapter
  * These are super cheap - I lazily picked mine from [eBay](https://www.ebay.com/itm/351790827736), but you could also [build](https://electronics.stackexchange.com/questions/56540/ir-audio-receiver-and-transmitter) one.
* A device running Android Lollipop 5.0+

## Building / Installation
1. Clone this github repo
2. Import the project into Android Studio
3. Build, deploy, and enjoy!

I only have a handful of functional Android devices lying around the place. So far I've only tested this on my Google Pixel running Android Nougat.


## Screenshots
![Screenshot 1](/doc/Screenshot01.png)


## To Do:
I've just begun work on this project in my free time to fill the gap I found with other lackluster app solutions. There's still much to be done. As of right now, it only functions with my living room Sanyo tv thanks to .wav files I made on my laptop.

* Add functionality to translate codes to audio
* Add ability to change remotes/buttons
* Make the GUI look less... basic.
* Consider adding functionality to record signals from other remotes (would require another adapter)
