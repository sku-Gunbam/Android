package military.gunbam.model;

import android.util.Log;

public class PathCountSingleton {
    private static PathCountSingleton instance;

    private static int pathCount = 0;

    private PathCountSingleton() {
    }

    public static PathCountSingleton getInstance() {
        if (instance == null) {
            synchronized(PathCountSingleton.class) {
                instance = new PathCountSingleton();
            }
        }
        return instance;
    }

    public int getPathCount() {
        return pathCount;
    }
    public void setPathCount(int count) {
        this.pathCount = count;
    }

}
