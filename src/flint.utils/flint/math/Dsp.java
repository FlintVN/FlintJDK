package flint.math;

public class Dsp  {
    private Dsp() {

    }

    private static native float[] toFloatArray(int[] values);

    public static native void applyWindowInPlace(float[] values, WindowType type);

    public static float[] applyWindow(int[] values, WindowType type) {
        float[] ret = toFloatArray(values);
        applyWindowInPlace(ret, type);
        return ret;
    }

    public static float[] applyWindow(float[] values, WindowType type) {
        float[] ret = values.clone();
        applyWindowInPlace(ret, type);
        return ret;
    }

    public static native void fftInPlace(float[] reals, float[] imags);

    public static native void ifftInPlace(float[] reals, float[] imags);

    public static native void dctInPlace(float[] values);

    public static native void idctInPlace(float[] values);

    public static ComplexList fft(int[] values) {
        float[] reals = toFloatArray(values);
        float[] imags = new float[values.length];
        fftInPlace(reals, imags);
        return new ComplexList(reals, imags);
    }

    public static ComplexList fft(float[] values) {
        float[] reals = values.clone();
        float[] imags = new float[values.length];
        fftInPlace(reals, imags);
        return new ComplexList(reals, imags);
    }

    public static ComplexList ifft(ComplexList values) {
        float[] reals = values.getRealArray().clone();
        float[] imags = values.getImagArray().clone();
        ifftInPlace(reals, imags);
        return new ComplexList(reals, imags);
    }

    public static float[] dct(int[] values) {
        float[] ret = toFloatArray(values);
        dctInPlace(ret);
        return ret;
    }

    public static float[] dct(float[] values) {
        float[] ret = values.clone();
        dctInPlace(ret);
        return ret;
    }

    public static float[] idct(int[] values) {
        float[] ret = toFloatArray(values);
        idctInPlace(ret);
        return ret;
    }

    public static float[] idct(float[] values) {
        float[] ret = values.clone();
        idctInPlace(ret);
        return ret;
    }
}
