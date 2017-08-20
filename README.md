# RxUsb
High level cross-platform usb library built with Kotlin with reactive streams.

**LIBRARY IS UNDER DEVELOPMENT AND MAY LACK SOME IMPORTANT FEATURES.** Currently it can be used primarily for reading data from usb bulk transfers. 

## How to use

### Library setup 

Add custom maven repository
```gradle
repositories {
    mavenCentral()
    maven {
        url 'http://nexus.eraga.net/content/repositories/public-releases/'
    }
}
```

Dependency for Desktop apps:
```gradle
compile "net.eraga.rxusb:libusb:1.0.0"
```

Dependency for Android apps (libusb):

```gradle
compile "net.eraga.rxusb:libusb-android:1.0.0"
```

### Usage 

List all connected USB devices:
```kotlin
val usbManager = UsbService.instance

usbManager.getDeviceList().forEach {
    println(it)
}
```

Subscribe to bulk transfers' content and print it to `stdout` as soon as it comes with the power of `rxjava2`:
```kotlin
val usbManager = UsbService.instance

val device = usbManager.findDevice(0x1a86, 0x7523)

val deviceConnection = usbManager.openDevice(device)

val interfaceConnection = deviceConnection.claimInterface(device.getInterface(0))

val bulkEndpoint = interfaceConnection.open(
        device.getInterface(0).getEndpoint(0)
) as BulkReadableChannel

bulkEndpoint.subscribeOn(Schedulers.io())
        .map<String> { byteArray ->
            // Lets think that this usb device sends UTF-8 text
            byteArray.toString(Charsets.UTF_8)
        }
        .observeOn(Schedulers.single())
        .subscribe({ text ->
            // output incoming text
            println(text)
        })
```

Share bulk transfer data between two different parts of code and treat it differentely: 
```kotlin
val sharedEndpoint = bulkEndpoint.share()

sharedEndpoint
        .subscribeOn(Schedulers.io())
        .subscribe {
    // do some incredible stuff
}

sharedEndpoint
        .subscribeOn(Schedulers.io())
        .subscribe {
    // do some other stuff
}
```
