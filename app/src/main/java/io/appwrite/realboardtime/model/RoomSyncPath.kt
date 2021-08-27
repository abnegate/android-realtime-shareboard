package io.appwrite.realboardtime.model

class RoomSyncPath(
    val roomId: String,
    path: SyncPath
) : SyncPath(
    path.x0,
    path.y0,
    path.x1,
    path.y1,
    path.color,
    path.strokeWidth
)