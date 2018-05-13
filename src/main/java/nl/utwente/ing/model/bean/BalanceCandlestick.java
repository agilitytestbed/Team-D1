package nl.utwente.ing.model.bean;

/**
 * The Balance Candlestick class.
 * Used to store balance information about the balance information of a certain interval.
 *
 * @author Daan Kooij
 */
public class BalanceCandlestick {

    private float open, close, high, low, volume;

    /**
     * The constructor of BalanceCandlestick.
     * Initially, only the opening balance is specified, after which this balance can be modified by using the mutate
     * method. Along the way, this bean keeps track of the opening balance, the closing balance, the highest balance,
     * the lowest balance and the volume of all the mutations combined.
     *
     * @param open The opening balance of this BalanceCandlestick.
     */
    public BalanceCandlestick(float open) {
        this.open = open;
        this.close = open;
        this.high = open;
        this.low = open;
        this.volume = 0;
    }

    /**
     * Method used to retrieve the opening balance of BalanceCandlestick.
     *
     * @return The opening balance of BalanceCandlestick.
     */
    public float getOpen() {
        return open;
    }

    /**
     * Method used to retrieve the closing balance of BalanceCandlestick.
     *
     * @return The closing balance of BalanceCandlestick.
     */
    public float getClose() {
        return close;
    }

    /**
     * Method used to retrieve the highest balance in the lifetime of BalanceCandlestick.
     *
     * @return The highest balance in the lifetime of BalanceCandlestick.
     */
    public float getHigh() {
        return high;
    }

    /**
     * Method used to retrieve the lowest balance in the lifetime of BalanceCandlestick.
     *
     * @return The lowest balance in the lifetime of BalanceCandlestick.
     */
    public float getLow() {
        return low;
    }

    /**
     * Method used to retrieve the volume of all the mutations done on BalanceCandlestick combined.
     *
     * @return The volume of all the mutations done on BalanceCandlestick combined.
     */
    public float getVolume() {
        return volume;
    }

    /**
     * Method used to indicate a mutation of the balance of BalanceCandlestick.
     * Along the way, this method updates the closing balance, the highest balance, the lowest balance
     * and the volume of all the mutations combined accordingly.
     *
     * @param amount The value with which the balance of BalanceCandlestick should be mutated.
     */
    public void mutation(float amount) {
        close += amount;
        if (close > high) {
            high = close;
        } else if (close < low) {
            low = close;
        }
        volume += Math.abs(amount);
    }

}
