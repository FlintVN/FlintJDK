package java.util;

import java.util.function.DoubleConsumer;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;

public class DoubleSummaryStatistics implements DoubleConsumer {
    private long count;
    private double sum;
    private double sumCompensation;
    private double simpleSum;
    private double min = Double.POSITIVE_INFINITY;
    private double max = Double.NEGATIVE_INFINITY;

    public DoubleSummaryStatistics() {

    }

    public DoubleSummaryStatistics(long count, double min, double max, double sum) throws IllegalArgumentException {
        if(count < 0L)
            throw new IllegalArgumentException("Negative count value");
        else if(count > 0L) {
            if(min > max)
                throw new IllegalArgumentException("Minimum greater than maximum");

            var ncount = DoubleStream.of(min, max, sum).filter(Double::isNaN).count();
            if(ncount > 0 && ncount < 3)
                throw new IllegalArgumentException("Some, not all, of the minimum, maximum, or sum is NaN");

            this.count = count;
            this.sum = sum;
            this.simpleSum = sum;
            this.sumCompensation = 0.0d;
            this.min = min;
            this.max = max;
        }
    }

    @Override
    public void accept(double value) {
        ++count;
        simpleSum += value;
        sumWithCompensation(value);
        min = Math.min(min, value);
        max = Math.max(max, value);
    }

    public void combine(DoubleSummaryStatistics other) {
        count += other.count;
        simpleSum += other.simpleSum;
        sumWithCompensation(other.sum);

        sumWithCompensation(-other.sumCompensation);
        min = Math.min(min, other.min);
        max = Math.max(max, other.max);
    }

    private void sumWithCompensation(double value) {
        double tmp = value - sumCompensation;
        double velvel = sum + tmp;
        sumCompensation = (velvel - sum) - tmp;
        sum = velvel;
    }

    public final long getCount() {
        return count;
    }

    public final double getSum() {
        double tmp =  sum - sumCompensation;
        if(Double.isNaN(tmp) && Double.isInfinite(simpleSum))
            return simpleSum;
        else
            return tmp;
    }

    public final double getMin() {
        return min;
    }

    public final double getMax() {
        return max;
    }

    public final double getAverage() {
        return getCount() > 0 ? getSum() / getCount() : 0.0d;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(this.getClass().getSimpleName());
        sb.append("count=");
        sb.append(getCount());
        sb.append(", sum=");
        sb.append(getSum());
        sb.append(", min=");
        sb.append(getMin());
        sb.append(", average=");
        sb.append(getAverage());
        sb.append(", max=");
        sb.append(getMax());

        return sb.toString();
    }
}
