package utilities;

/**
 This class is used to model the statistics
 of a fix of numbers.  For the statistics
 we choose here it is not necessary to store
 all the numbers - just keeping a running total
 of how many, the sum and the sum of the squares
 is sufficient (plus max and min, for max and min).

  This is a simpler version of StatisticalSummary that does
  not include statistical tests, or the Watch class.

 */

public class StatSummary {


    // following line can cause prog to hang - bug in Java?
    // protected long serialVersionUID = new Double("-1490108905720833569").longValue();
    // protected long serialVersionUID = 123;
    public String name; // defaults to ""
    private double sum;
    private double sumsq;
    private double min;
    private double max;

    private double mean;
    private double sd;

    // trick class loader into loading this now
    // private static StatisticalTests dummy = new StatisticalTests();

    int n;
    boolean valid;

    public StatSummary() {
        this("");
        // System.out.println("Exited default...");
    }

    public StatSummary(String name) {
        // System.out.println("Creating SS");
        this.name = name;
        n = 0;
        sum = 0;
        sumsq = 0;
        // ensure that the first number to be
        // added will fix up min and max to
        // be that number
        min = Double.POSITIVE_INFINITY;
        max = Double.NEGATIVE_INFINITY;
        // System.out.println("Finished Creating SS");
        valid = false;
    }

    public final void reset() {
        n = 0;
        sum = 0;
        sumsq = 0;
        // ensure that the first number to be
        // added will fix up min and max to
        // be that number
        min = Double.POSITIVE_INFINITY;
        max = Double.NEGATIVE_INFINITY;
    }


    
    /** 
     * @return double
     */
    public double max() {
        return max;
    }

    
    /** 
     * @return double
     */
    public double min() {
        return min;
    }

    
    /** 
     * @return double
     */
    public double mean() {
        if (!valid)
            computeStats();
        return mean;
    }

    
    /** 
     * @return double
     */
    // returns the sum of the squares of the differences
    //  between the mean and the ith values
    public double sumSquareDiff() {
        return sumsq - n * mean() * mean();
    }


    private void computeStats() {
        if (!valid) {
            mean = sum / n;
            double num = sumsq - (n * mean * mean);
            if (num < 0) {
                // avoids tiny negative numbers possible through imprecision
                num = 0;
            }
            // System.out.println("Num = " + num);
            sd = Math.sqrt(num / (n - 1));
            // System.out.println(" Test: sd = " + sd);
            // System.out.println(" Test: n = " + n);
            valid = true;
        }
    }

    
    /** 
     * @return double
     */
    public double sd() {
        if (!valid)
            computeStats();
        return sd;
    }

    
    /** 
     * @return int
     */
    public int n() {
        return n;
    }

    
    /** 
     * @return double
     */
    public double stdErr() {
        return sd() / Math.sqrt(n);
    }

    
    /** 
     * @param ss
     */
    public void add(StatSummary ss) {
        // implications for Watch?
        n += ss.n;
        sum += ss.sum;
        sumsq += ss.sumsq;
        max = Math.max(max, ss.max);
        min = Math.min(min, ss.min);
        valid = false;
    }

    
    /** 
     * @param d
     */
    public void add(double d) {
        n++;
        sum += d;
        sumsq += d * d;
        min = Math.min(min, d);
        max = Math.max(max, d);
        valid = false;
    }

    
    /** 
     * @param n
     */
    public void add(Number n) {
        add(n.doubleValue());
    }

//    public void add(double[] d) {
//        for (int i = 0; i < d.length; i++) {
//            add(d[i]);
//        }
//    }
//
    public void add(double... xa) {
        for (double x : xa) {
            add(x);
        }
    }

    
    /** 
     * @return String
     */
    public String toString() {
        String s = (name == null) ? "" : name + "\n";
        s +=  " min = " + min() + "\n" +
                " max = " + max() + "\n" +
                " ave = " + mean() + "\n" +
                " sd  = " + sd() + "\n" +
                " se  = " + stdErr() + "\n" +
                " sum  = " + sum + "\n" +
                " sumsq  = " + sumsq + "\n" +
                " n   = " + n;
        return s;

    }
}