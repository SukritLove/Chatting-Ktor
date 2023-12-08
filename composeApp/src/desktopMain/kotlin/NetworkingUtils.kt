import java.net.NetworkInterface

actual class PlatformNetworkingUtils {
    actual suspend fun getCurrentWifiIpAddress(): String? {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val iface = interfaces.nextElement()
            if (iface.isLoopback || !iface.isUp) continue

            val addresses = iface.inetAddresses
            while (addresses.hasMoreElements()) {
                val addr = addresses.nextElement()
                if (addr.isSiteLocalAddress) {
                    return addr.hostAddress
                }
            }
        }
        return null
    }
}