import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

actual class PlatformNetworkingUtils {
    private val client = HttpClient()

    actual suspend fun getCurrentWifiIpAddress(): String? {
        return try {
            // Using a public service to get the public IP
            val response: HttpResponse = client.get("https://api.ipify.org")
            response.toString()
        } catch (e: Exception) {
            e.toString()
        }
    }
}