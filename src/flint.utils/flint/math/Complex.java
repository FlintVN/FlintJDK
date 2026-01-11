package flint.math;

public class Complex {
    final float real;
    final float imag;

    public Complex(float real, float imag) {
        this.real = real;
        this.imag = imag;
    }

    public float getReal() {
        return real;
    }

    public float getImag() {
        return imag;
    }

    public Complex add(int i) {
        return new Complex(real + i, imag);
    }

    public Complex add(float f) {
        return new Complex(real + f, imag);
    }

    public Complex add(Complex other) {
        return new Complex(real + other.real, imag + other.imag);
    }

    public Complex subtract(int i) {
        return new Complex(real - i, imag);
    }

    public Complex subtract(float f) {
        return new Complex(real - f, imag);
    }

    public Complex subtract(Complex other) {
        return new Complex(real - other.real, imag - other.imag);
    }

    public Complex multiply(int i) {
        return new Complex(real * i, imag * i);
    }

    public Complex multiply(float f) {
        return new Complex(real * f, imag * f);
    }

    public Complex multiply(Complex other) {
        float r = real * other.real - imag * other.imag;
        float i = real * other.imag + imag * other.real;
        return new Complex(r, i);
    }
    
    public Complex divide(int i) {
        float denominator = i * i;
        return new Complex((real * i) / denominator, (imag * i) / denominator);
    }

    public Complex divide(float f) {
        float denominator = f * f;
        return new Complex((real * f) / denominator, (imag * f) / denominator);
    }

    public Complex divide(Complex other) {
        float denominator = other.real * other.real + other.imag * other.imag;
        float r = (this.real * other.real + this.imag * other.imag) / denominator;
        float i = (this.imag * other.real - this.real * other.imag) / denominator;
        return new Complex(r, i);
    }

    public float modulus() {
        return (float)java.lang.Math.sqrt((double)real * real + imag * imag);
    }

    public Complex conjugate() {
        return new Complex(real, -imag);
    }

    @Override
    public int hashCode() {
        int result = 31 + Float.floatToIntBits(real);
        result = 31 * result + Float.floatToIntBits(imag);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Complex c)
            return (real == c.real && imag == c.imag);
        return false;
    }

    @Override
    public String toString() {
        if(imag >= 0)
            return real + " + " + imag + "i";
        else
            return real + " - " + (-imag) + "i";
    }
}
