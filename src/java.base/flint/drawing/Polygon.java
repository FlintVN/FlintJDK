package flint.drawing;

public class Polygon {
    int npoints;
    int[] xpoints;
    int[] ypoints;

    private static final int MIN_LENGTH = 4;

    public Polygon() {
        npoints = 0;
        xpoints = new int[MIN_LENGTH];
        ypoints = new int[MIN_LENGTH];
    }

    public Polygon(int[] xpoints, int[] ypoints, int npoints) {
        if(npoints > xpoints.length || npoints > ypoints.length)
            throw new IndexOutOfBoundsException("npoints > xpoints.length || npoints > ypoints.length");
        if(npoints < 0)
            throw new NegativeArraySizeException("npoints < 0");
        this.npoints = npoints;
        this.xpoints = new int[npoints];
        this.ypoints = new int[npoints];
        System.arraycopy(xpoints, 0, this.xpoints, 0, npoints);
        System.arraycopy(ypoints, 0, this.ypoints, 0, npoints);
    }

    public Polygon(Point... points) {
        int npoints = points.length;
        int[] xpoints = new int[npoints];
        int[] ypoints = new int[npoints];
        for(int i = 0; i < npoints; i++) {
            xpoints[i] = points[i].x;
            ypoints[i] = points[i].y;
        }
        this.npoints = npoints;
        this.xpoints = xpoints;
        this.ypoints = ypoints;
    }

    public void reset() {
        npoints = 0;
    }

    public void translate(int deltaX, int deltaY) {
        for(int i = 0; i < npoints; i++){
            xpoints[i] += deltaX;
            ypoints[i] += deltaY;
        }
    }

    public void addPoint(int x, int y) {
        if(npoints >= xpoints.length || npoints >= ypoints.length) {
            int newLength = npoints * 2;
            if(newLength < MIN_LENGTH)
                newLength = MIN_LENGTH;
            else if((newLength & (newLength - 1)) != 0)
                newLength = Integer.highestOneBit(newLength);
            int[] xpoints = new int[newLength];
            int[] ypoints = new int[newLength];
            System.arraycopy(this.xpoints, 0, xpoints, 0, npoints);
            System.arraycopy(this.ypoints, 0, ypoints, 0, npoints);
            this.xpoints = xpoints;
            this.ypoints = ypoints;
        }
        this.xpoints[npoints] = x;
        this.ypoints[npoints] = y;
        this.npoints++;
    }

    public void addPoint(Point p) {
        addPoint(p.x, p.y);
    }
}
