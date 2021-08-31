package io.appwrite.realboardtime.room

import android.app.Service
import android.content.Intent
import android.os.*
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.util.Log
import androidx.core.os.bundleOf
import io.appwrite.Client
import io.appwrite.extensions.fromJson
import io.appwrite.realboardtime.core.ENDPOINT
import io.appwrite.realboardtime.core.PROJECT_ID
import io.appwrite.realboardtime.core.ROOM_COLLECTION_ID
import io.appwrite.services.Database
import kotlinx.coroutines.runBlocking

class ParticipantLeaveWatcherService : Service() {

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    val db by lazy {
        Database(
            Client(this)
                .setEndpoint(ENDPOINT)
                .setProject(PROJECT_ID)
        )
    }

    companion object {
        const val ROOM_ID_EXTRA = "ROOM_ID_EXTRA"

        private var roomId: String? = null
    }

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            runBlocking {
                try {
                    val room =
                        db.getDocument(ROOM_COLLECTION_ID, msg.data[ROOM_ID_EXTRA]!! as String)
                            .body
                            ?.string()
                            ?.fromJson<Room>()

                    db.updateDocument(
                        ROOM_COLLECTION_ID,
                        msg.data[ROOM_ID_EXTRA]!! as String,
                        mapOf("participants" to --room!!.participants)
                    )
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
            stopSelf(msg.arg1)
        }
    }

    override fun onCreate() {
        HandlerThread("ServiceStartArguments", THREAD_PRIORITY_BACKGROUND).apply {
            start()
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        roomId = intent.getStringExtra(ROOM_ID_EXTRA)!!

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onLowMemory() {
        super.onDestroy()
        if (roomId == null) {
            return
        }
        Log.e("ROOMID", roomId!!)
        serviceHandler?.obtainMessage()?.let {
            it.data = bundleOf(ROOM_ID_EXTRA to roomId)
            serviceHandler?.sendMessage(it)
        }
    }
}