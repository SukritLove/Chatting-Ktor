import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.core.use
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

@Composable
fun App(networkingUtils: PlatformNetworkingUtils) {
    MaterialTheme {
        val ipAddress: MutableState<String?> = remember { mutableStateOf("") }
        val receivedName: MutableState<String?> = remember { mutableStateOf("") }
        val socketAddress: MutableState<String?> = remember { mutableStateOf("") }


        LaunchedEffect(Unit) {
            ipAddress.value = networkingUtils.getCurrentWifiIpAddress()
            launch(Dispatchers.IO) {
                val selectorManager = SelectorManager(Dispatchers.IO)
                val serverSocket =
                    ipAddress.value?.let { aSocket(selectorManager).tcp().bind(it, 8080) }

                serverSocket?.use {
                    println("Status:: Server is listening at ${serverSocket.localAddress}")
                    socketAddress.value = "Server is listening at ${serverSocket.localAddress}"

                    while (true) {
                        val socket = serverSocket.accept()
                        println("Status:: Accepted $socket")

                        launch(Dispatchers.IO) {
                            val receiveChannel = socket.openReadChannel()
                            val sendChannel = socket.openWriteChannel(autoFlush = true)
                            try {
                                val message: String? = receiveChannel.readUTF8Line()
                                receivedName.value = "$message"
                                println("Status:: Message Receive")
                                sendChannel.writeStringUtf8("[Receive] your message is $message")
                            } catch (e: Throwable) {
                                println("Error : $e")
                            } finally {
                                socket.close()
                            }
                        }
                    }
                }
            }
        }

        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("${socketAddress.value}")
            Text("Receive Message : ${receivedName.value}")
        }
    }
}

