package utilities;

public class ElapsedTimer {
    // allows for easy reporting of elapsed time
    long oldTime;

    public ElapsedTimer() {
        oldTime = System.currentTimeMillis();
    }

    
    /** 
     * @return long
     */
    public long elapsed() {
        return System.currentTimeMillis() - oldTime;
    }

    public void reset() {
        oldTime = System.currentTimeMillis();
    }

    
    /** 
     * @return String
     */
    public String toString() {
        // now resets the timer...
        String ret = elapsed() + " ms elapsed";
        reset();
        return ret;
    }

    
    /** 
     * @param args
     */
    public static void main(String[] args) {
        ElapsedTimer t = new ElapsedTimer();
        System.out.println("ms elasped: " + t.elapsed());
    }
}


