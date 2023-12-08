import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.net.Inet4Address

actual class PlatformNetworkingUtils(private val context: Context) {
    actual suspend fun getCurrentWifiIpAddress(): String? {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiNetwork = connectivityManager.allNetworks.firstOrNull { network ->
            connectivityManager.getNetworkCapabilities(network)
                ?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        } ?: return null

        val linkProperties = connectivityManager.getLinkProperties(wifiNetwork) ?: return null
        return linkProperties.linkAddresses.firstOrNull { it.address is Inet4Address }?.address?.hostAddress
    }
}
