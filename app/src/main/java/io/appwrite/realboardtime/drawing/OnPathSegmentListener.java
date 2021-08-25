package io.appwrite.realboardtime.drawing;

@FunctionalInterface
public interface OnPathSegmentListener {
    void onNewPath(DrawPath path);
}
