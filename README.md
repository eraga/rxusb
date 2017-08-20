# RxUsb
High level cross-platform usb library built with Kotlin with reactive streams

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
val usbService = UsbService.instance

usbService.getDeviceList().forEach {
    println(it)
}
```

