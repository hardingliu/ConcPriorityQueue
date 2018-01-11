package ca.dal.cs6057.project;

public class BitReversedCounter {
    private int counter;
    private int reversed;
    private int highBit;

    public BitReversedCounter() {
        this.counter = 0;
        this.reversed = 0;
        this.highBit = -1;
    }

    public int getCounter() {
        return counter;
    }

    public int getReversed() {
        return reversed;
    }

    public int increment() {
        int bit, mask;

        counter++;

        for (bit = highBit - 1; bit >= 0; bit--) {
            mask = 1 << bit;
            reversed ^= mask;
            if ((reversed & mask) != 0) {
                break;
            }
        }

        if (bit < 0) {
            reversed = counter;
            highBit++;
        }

        return reversed;
    }


    public int decrement() {
        int bit;
        int mask;
        int oldReversed;
        oldReversed = reversed;
        counter--;

        for (bit = highBit - 1; bit >= 0; bit--) {
            mask = 1 << bit;
            reversed ^= mask;
            if ((reversed & mask) == 0) {
                break;
            }
        }
        if (bit < 0) {
            reversed = counter;
            highBit--;
        }
        return oldReversed;
    }
}
