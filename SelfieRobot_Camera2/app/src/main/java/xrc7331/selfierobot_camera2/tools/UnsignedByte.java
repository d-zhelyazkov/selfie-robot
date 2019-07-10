package xrc7331.selfierobot_camera2.tools;

/**
 * Created by XRC_7331 on 2/22/2016.
 */


public class UnsignedByte implements Comparable<UnsignedByte>, Cloneable {
    public static final int MIN_INT = 0;
    public static final int MAX_INT = 0xff;
    public static final byte MIN_BYTE = MIN_INT;
    public static final byte MAX_BYTE = (byte) MAX_INT;

    public static int toInt(byte value) {
        return value & MAX_INT;
    }

    public static int compare(byte b1, byte b2) {
        int v1 = toInt(b1);
        int v2 = toInt(b2);
        return (v1 == v2) ? 0 :
                (v1 < v2) ? -1 : 1;
    }


    private byte mByte = MIN_BYTE;

    public UnsignedByte() {
    }

    public UnsignedByte(byte mByte) {
        this.mByte = mByte;
    }

    public UnsignedByte(int value) {
        this.mByte = (byte) value;
    }

    public int toInt() {
        return toInt(mByte);
    }

    public byte getByte() {
        return mByte;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnsignedByte that = (UnsignedByte) o;

        return mByte == that.mByte;

    }

    @Override
    public int hashCode() {
        return (int) mByte;
    }

    @Override
    public String toString() {
        return Integer.toString(this.toInt());
    }

    @Override
    public int compareTo(UnsignedByte another) {
        return compare(mByte, another.mByte);
    }

    @Override
    public UnsignedByte clone() {
        return new UnsignedByte(mByte);
    }
}
