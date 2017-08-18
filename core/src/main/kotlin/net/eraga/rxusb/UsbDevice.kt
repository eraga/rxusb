package net.eraga.rxusb

import java.util.*

data class UsbDevice(
        val vendorId: Int,
        val productId: Int,
        val usbClass: Int,
        val subClass: Int,
        val protocol: Int,
        val version: String,
        val productName: String,
        val manufacturerName: String,
        val serialNumber: String,
        private var _configurations: Array<UsbConfiguration>? = null) {

    var _device: Any? = null

    var configurations: Array<UsbConfiguration>
        set(value) = if (_configurations == null) _configurations = value else throw IllegalStateException("Field can be " +
                "initialized only once")
        get() = _configurations ?: throw IllegalStateException("Initialization is not completed")


    private var _interfaces: Array<UsbInterface>? = null
        set(value) {
            field = value ?: throw NullPointerException("Attempt to set null on non nullable field")
        }

    val interfaces: Array<UsbInterface>
        get() {
            if (_interfaces == null)
                _interfaces = initInterfaces()

            return _interfaces ?: throw IllegalAccessException("Mysterious somewhat concurrent thread modification?")
        }




    private fun initInterfaces(): Array<UsbInterface> {
        var interfaces = configurations[0].interfaces

        if (configurations.size > 1) {
            for (i in 1..configurations.size) {
                interfaces = interfaces.concat(configurations[i].interfaces).toTypedArray()
            }
        }
        return interfaces
    }


    /**
     * Returns the number of [UsbConfiguration]s this device contains.

     * @return the number of configurations
     */
    fun getConfigurationCount(): Int {
        return configurations.size
    }

    /**
     * Returns the [UsbConfiguration] at the given index.
     * @return the configuration
     */
    fun getConfiguration(index: Int): UsbConfiguration {
        return configurations[index]
    }


    /**
     * Returns the number of [UsbInterface]s this device contains.
     * For devices with multiple configurations, you will probably want to use
     * [UsbConfiguration.getInterfaceCount] instead.

     * @return the number of interfaces
     */
    fun getInterfaceCount(): Int {
        return interfaces.size
    }

    /**
     * Returns the [UsbInterface] at the given index.
     * For devices with multiple configurations, you will probably want to use
     * [UsbConfiguration.getInterface] instead.

     * @return the interface
     */
    fun getInterface(index: Int): UsbInterface {
        return interfaces[index]
    }

    override fun toString(): String {
        return "UsbDevice(\n" +
                "\tvendorId=0x${vendorId.toString(16)}, productId=0x${productId.toString(16)},\n" +
                "\tusbClass=$usbClass, subClass=$subClass,\n" +
                "\tprotocol=$protocol, version='$version',\n" +
                "\tproductName='$productName',\n" +
                "\tmanufacturerName='$manufacturerName',\n" +
                "\tserialNumber='$serialNumber',\n" +
                "\tconfigurations=${Arrays.toString(_configurations)})"
    }


}
