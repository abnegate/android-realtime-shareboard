package io.appwrite.services

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import io.appwrite.Client
import io.appwrite.extensions.forEachAsync
import io.appwrite.models.RealtimeCodes
import io.appwrite.models.RealtimeError
import io.appwrite.models.RealtimeMessage
import io.appwrite.models.RealtimeSubscription
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.*
import okhttp3.internal.concurrent.TaskRunner
import okhttp3.internal.ws.RealWebSocket
import java.util.*
import kotlin.coroutines.CoroutineContext

class Realtime(client: Client) : Service(client), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private companion object {
        private const val DEBOUNCE_MILLIS = 1L

        private var socket: RealWebSocket? = null
        private var channelCallbacks = mutableMapOf<String, MutableCollection<(Any) -> Unit>>()
        private var errorCallbacks = mutableSetOf<(RealtimeError) -> Unit>()

        private var subCallDepth = 0
    }

    private fun createSocket() {
        val queryParamBuilder = StringBuilder()
            .append("project=${client.config["project"]}")

        channelCallbacks.keys.forEach {
            queryParamBuilder
                .append("&channels[]=$it")
        }

        val request = Request.Builder()
            .url("${client.endPointRealtime}/realtime?$queryParamBuilder")
            .build()

        if (socket != null) {
            closeSocket()
        }

        socket = RealWebSocket(
            taskRunner = TaskRunner.INSTANCE,
            originalRequest = request,
            listener = AppwriteWebSocketListener(),
            random = Random(),
            pingIntervalMillis = client.http.pingIntervalMillis.toLong(),
            extensions = null,
            minimumDeflateSize = client.http.minWebSocketMessageToCompress
        )

        socket?.connect(client.http)
    }

    private fun closeSocket() {
        socket?.close(RealtimeCodes.POLICY_VIOLATION.value, null)
    }

    fun subscribe(
        vararg channels: String,
        callback: (Any) -> Unit
    ) : RealtimeSubscription {
        channels.forEach {
            if (!channelCallbacks.containsKey(it)) {
                channelCallbacks[it] = mutableListOf(callback)
                return@forEach
            }
            channelCallbacks[it]?.add(callback)
        }

        launch {
            subCallDepth++
            delay(DEBOUNCE_MILLIS)
            if (subCallDepth == 1) {
                createSocket()
            }
            subCallDepth--
        }

        return RealtimeSubscription { unsubscribe() }
    }

    fun unsubscribe() {
        channelCallbacks = mutableMapOf()
        errorCallbacks = mutableSetOf()
        closeSocket()
    }

    fun doOnError(callback: (RealtimeError) -> Unit) {
        errorCallbacks.add(callback)
    }

    private inner class AppwriteWebSocketListener : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            print("WebSocket connected.")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            print("WebSocket message received: $text")

            launch(IO) {
                val message = parse<RealtimeMessage>(text)
                if (message?.channels == null) {
                    val error = parse<RealtimeError>(text) ?: return@launch
                    errorCallbacks.forEach {
                        it.invoke(error)
                    }
                    return@launch
                }

                message.channels.forEachAsync { channel ->
                    channelCallbacks[channel]?.forEachAsync { callback ->
                        callback.invoke(message.payload)
                    }
                }
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            print("WebSocket closing with code $code because: $reason.")
            if (code == RealtimeCodes.POLICY_VIOLATION.value) {
                return
            }
            print("Reconnecting in 1 second.")
            launch {
                delay(1000)
                createSocket()
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            print("WebSocket failure.")
            t.printStackTrace()
        }

        private inline fun <reified T> parse(text: String): T? {
            return try {
                Gson().fromJson(text, T::class.java)
            } catch (ex: JsonSyntaxException) {
                ex.printStackTrace()
                null
            } catch (ex: JsonParseException) {
                ex.printStackTrace()
                null
            }
        }
    }
}