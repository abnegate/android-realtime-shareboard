package io.appwrite.realboardtime.room

import io.appwrite.realboardtime.drawing.DrawPath

class RoomDrawPath(
    val roomId: String,
    path: DrawPath
) : DrawPath(
    path.x0,
    path.y0,
    path.x1,
    path.y1,
    path.color
)