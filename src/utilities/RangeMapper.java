package utilities;

public class RangeMapper {

  // allows direct call to the static function,
  // or for greater convenience, simpler calls can be made
  // to an instantiated object

  public double omin, omax, nmin, nmax;

  
  /** 
   * @param oval
   * @param omin
   * @param omax
   * @param nmin
   * @param nmax
   * @return double
   */
  public static double changeRange(double oval, double omin, double omax, double nmin, double nmax) {
    // normalise to zero .. one range
    double normVal = (oval - omin) / (omax - omin);
    // then map to new range
    return nmin + normVal * (nmax - nmin);
  }

  public RangeMapper() {
    this(0, 1, 0, 1);
  }

  public RangeMapper(double omin, double omax, double nmin, double nmax) {
    this.omin = omin;
    this.omax = omax;
    this.nmin = nmin;
    this.nmax = nmax;
  }

  
  /** 
   * @param omin
   * @param omax
   * @param nmin
   * @param nmax
   */
  public void setRange(double omin, double omax, double nmin, double nmax) {
    this.omin = omin;
    this.omax = omax;
    this.nmin = nmin;
    this.nmax = nmax;
  }

  
  /** 
   * @param val
   * @return double
   */
  public double map(double val) {
    double x = changeRange(val, omin, omax, nmin, nmax);
    // System.out.println(x);
    return x;
  }

  
  /** 
   * @param val
   * @return double
   */
  public double inverse(double val) {
    double x = changeRange(val, nmin, nmax, omin, omax);
    // System.out.println(x);
    return x;
  }



  
  /** 
   * @return String
   */
  public String toString() {
    return "RangeMapper: " + omin + " : " + omax + " : " + nmin +  " : " + nmax;
  }

}

