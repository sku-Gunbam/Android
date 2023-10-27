package military.gunbam.model;

public class PathCountSingleton {
    private static PathCountSingleton instance;

    private int pathCount = 0;

    private PathCountSingleton() {
    }

    public static synchronized PathCountSingleton getInstance() {
        if (instance == null) {
            instance = new PathCountSingleton();
        }
        return instance;
    }

    public int getPathCount() {
        return pathCount;
    }
    public void setPathCount(int count) {
        this.pathCount = count;
    }
    public void increasePathCount(){
        this.pathCount++;
    }
    public void decreasePathCount(){
        this.pathCount--;
    }

}
