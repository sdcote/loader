package coyote.i13n;

import java.text.DecimalFormat;
import java.text.NumberFormat;


@Deprecated
public class SimpleMetric {
  private String name = null;
  private String units = "ms";
  private long minValue = 0L;
  private long maxValue = 0L;
  private int samples = 0;
  private long total = 0L;
  private long sumOfSquares = 0L;




  @Deprecated
  public SimpleMetric( final String name ) {
    this.name = name;
  }




  @Deprecated
  public SimpleMetric( final String name, final String units ) {
    this( name );
    this.units = units;
  }




  @Override
  public Object clone() {
    final SimpleMetric retval = new SimpleMetric( name );
    retval.units = units;
    retval.minValue = minValue;
    retval.maxValue = maxValue;
    retval.samples = samples;
    retval.total = total;
    retval.sumOfSquares = sumOfSquares;
    return retval;
  }




  private String convertToString( final long value ) {
    final DecimalFormat numberFormat = (DecimalFormat)NumberFormat.getNumberInstance();
    numberFormat.applyPattern( "#,###" );
    return numberFormat.format( value );
  }




  private long getAverage() {
    if ( samples == 0 ) {
      return 0L;
    }
    return total / samples;
  }




  public long getAvgValue() {
    synchronized( name ) {
      return getAverage();
    }
  }




  protected String getDisplayString( final String type, final String value, final String units ) {
    return type + "=" + value + " " + units + " ";
  }




  public long getMaxValue() {
    synchronized( name ) {
      return maxValue;
    }
  }




  public long getMinValue() {
    synchronized( name ) {
      return minValue;
    }
  }




  public String getName() {
    return name;
  }




  public long getSamplesCount() {
    return samples;
  }




  public long getStandardDeviation() {
    long stdDeviation = 0L;
    if ( samples != 0 ) {
      final long sumOfX = total;
      final int n = samples;
      final int nMinus1 = n <= 1 ? 1 : n - 1;
      final long numerator = sumOfSquares - ( ( sumOfX * sumOfX ) / n );
      stdDeviation = (long)Math.sqrt( numerator / nMinus1 );
    }
    return stdDeviation;
  }




  public long getTotal() {
    return total;
  }




  public String getUnits() {
    return units;
  }




  public Metric reset() {
    synchronized( name ) {
      final Metric retval = (Metric)clone();
      minValue = 0L;
      maxValue = 0L;
      samples = 0;
      total = 0L;
      sumOfSquares = 0L;
      return retval;
    }
  }




  public synchronized void sample( final long value ) {
    samples += 1;
    if ( value < minValue ) {
      minValue = value;
    }
    if ( value > maxValue ) {
      maxValue = value;
    }
    total += value;
    sumOfSquares += value * value;
  }




  void setName( final String name ) {
    this.name = name;
  }




  public void setUnits( final String units ) {
    synchronized( name ) {
      this.units = units;
    }
  }




  @Override
  public String toString() {
    final StringBuffer message = new StringBuffer( name );
    message.append( ": " );
    message.append( getDisplayString( "Samples", convertToString( samples ), "" ) );
    if ( samples > 0 ) {
      message.append( getDisplayString( "Avg", convertToString( getAverage() ), units ) );
      message.append( getDisplayString( "Total", convertToString( total ), units ) );
      message.append( getDisplayString( "Std Dev", convertToString( getStandardDeviation() ), units ) );
      message.append( getDisplayString( "Min Value", convertToString( minValue ), units ) );
      message.append( getDisplayString( "Max Value", convertToString( maxValue ), units ) );
    }
    return message.toString();
  }
}
