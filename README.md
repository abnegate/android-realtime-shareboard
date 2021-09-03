# Appwrite Realtime Whiteboard 🖊️

A demo Realtime Whiteboard Android application using Appwrite's new **realtime** service.

## Quick example

```kotlin
val client = Client(context)   
    .setEndpoint("https://[YOUR_HOSTNAME_OR_IP]/v1")
    .setProject("[YOUR_PROJECT_ID]")

val realtime = Realtime(client)

val subscription = realtime.subscribe("collections.6fabc4664gjb9.documents") {
    Log.d("Realtime", it.payload.toString())
}

```

## Get Started

The [`MenuFragment.kt`](https://github.com/abnegate/android-realtime-shareboard/blob/main/app/src/main/java/io/appwrite/realboardtime/menu/MenuFragment.kt) and [`MenuViewModel.kt`](https://github.com/abnegate/android-realtime-shareboard/blob/main/app/src/main/java/io/appwrite/realboardtime/menu/MenuViewModel.kt) contain the code to set up the private rooms. You choose a name and password and then share this information with others you want to join your board.

The [`RoomViewModel.kt`](https://github.com/abnegate/android-realtime-shareboard/blob/main/app/src/main/java/io/appwrite/realboardtime/room/RoomViewModel.kt) initiates the **realtime subscription**, and contains a function to push new `DrawPath` objects to your Appwrite Database. Calling this function whenever you draw a new path is how everyone else in your room will receive your draw updates, as they're observing the same collection as you are pushing to.

This is the response model received for every real time update:

```kotlin
data class RealtimeResponseEvent<T>(
    val event: String,
    val channels: Collection<String>,
    val timestamp: Long,
    var payload: T
)
```

If subscribing to a single channel, you may provide a `payloadType` parameter to the `subscribe` function that allows you to set the type of every event's `payload`

When you leave the board, the [`ParticipantLeaveWatcherService.kt`](https://github.com/abnegate/android-realtime-shareboard/blob/main/app/src/main/java/io/appwrite/realboardtime/room/ParticipantLeaveWatcherService.kt) will ensure that the room's active participant count is decremented. This works in tandem with the [room cleaner function](https://github.com/abnegate/android-realtime-shareboard-db-cleaner) to allow automatic cleanup of your database.

### Learn more

You can use the following resources to learn more

- 🚀 [Getting Started Tutorial](https://appwrite.io/docs/getting-started-for-android)
- 📜 [Appwrite Docs](https://appwrite.io/docs)
- 💬 [Discord Community](https://appwrite.io/discord)
- 🚂 [Appwrite Android Playground](https://github.com/appwrite/playground-for-android)
