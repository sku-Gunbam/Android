package military.gunbam.model;

public class SuccessCountSingleton {
    private static SuccessCountSingleton instance;
    private int successCount = 0;

    private SuccessCountSingleton() {
    }

    public static synchronized SuccessCountSingleton getInstance() {
        if (instance == null) {
            instance = new SuccessCountSingleton();
        }
        return instance;
    }

    public int getSuccessCount() {
        return successCount;
    }
    public void setSuccessCount(int count) {
        this.successCount = count;
    }
    public void increaseSuccessCount(){
        this.successCount++;
    }
    public void decreaseSuccessCount(){
        this.successCount--;
    }
}
