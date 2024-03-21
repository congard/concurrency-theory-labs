package pl.edu.agh.tw.knapp.lab8;

public class MandelbrotChunk {
    public record Params(
            int startX,
            int startY,
            int endX,
            int endY,
            int translateX,
            int translateY,
            int maxIter,
            double zoom
    ) {
        int getWidth() {
            return endX - startX;
        }

        int getHeight() {
            return endY - startY;
        }
    }

    private final Params params;

    private final int[] rgb;

    public MandelbrotChunk(Params params) {
        this.params = params;
        this.rgb = new int[params.getWidth() * params.getHeight()];
    }

    public void calculate() {
        for (int y = params.startY; y < params.endY; y++) {
            for (int x = params.startX; x < params.endX; x++) {
                double zx = 0;
                double zy = 0;
                double cX = (x - params.translateX) / params.zoom;
                double cY = (y - params.translateY) / params.zoom;
                int iter = params.maxIter;

                while (zx * zx + zy * zy < 4 && iter > 0) {
                    double tmp = zx * zx - zy * zy + cX;
                    zy = 2.0 * zx * zy + cY;
                    zx = tmp;
                    iter--;
                }

                int rgbX = x - params.startX;
                int rgbY = y - params.startY;
                int rgbValue = iter | (iter << 8);

                rgb[rgbY * params.getWidth() + rgbX] = rgbValue;
            }
        }
    }

    public Params getParams() {
        return params;
    }

    public int[] getRgb() {
        return rgb;
    }
}
