package net.eraga.rxusb

import java.util.*

data class UsbInterface(
        val device: UsbDevice,
        val id: Int,
        val alternateSetting: Int,
        val name: String,
        val interfaceClass: Int,
        val interfaceSubclass: Int,
        val interfaceProtocol: Int
) {

    private var _endpoints: Array<UsbEndpoint>? = null
    var endpoints: Array<UsbEndpoint>
        set(value) = if (_endpoints == null) _endpoints = value else throw IllegalStateException("Field can be " +
                "initialized only once")
        get() = _endpoints ?: throw IllegalStateException("Initialization is not completed")


    /**
     * Returns the number of [UsbEndpoint]s this interface contains.
     *
     * @return the number of endpoints
     */
    fun getEndpointCount(): Int = endpoints.size


    /**
     * Returns the [UsbEndpoint] at the given index.
     *
     * @return the endpoint
     */
    fun getEndpoint(index: Int): UsbEndpoint = endpoints[index]


    override fun toString(): String {
        return "\n\tUsbInterface(\n" +
                "\t\tid=$id,\n" +
                "\t\talternateSetting=$alternateSetting,\n" +
                "\t\tname='$name',\n" +
                "\t\tinterfaceClass=$interfaceClass,\n" +
                "\t\tinterfaceSubclass=$interfaceSubclass,\n" +
                "\t\tprotocol=$interfaceProtocol,\n" +
                "\t\tendpoints=${Arrays.toString(_endpoints)})"
    }
}


