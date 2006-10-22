package org.sunflow.core.tesselatable;

public class Teapot extends BezierMesh {
    // teapot data, from: http://www.cs.ucsb.edu/~cs280/winter2004/hw2/
    private double patchData[][] = { { -80.00, 0.00, 30.00, -80.00, -44.80, 30.00, -44.80, -80.00, 30.00, 0.00, -80.00, 30.00, -80.00, 0.00, 12.00, -80.00, -44.80, 12.00, -44.80, -80.00, 12.00, 0.00, -80.00, 12.00, -60.00, 0.00, 3.00, -60.00, -33.60, 3.00, -33.60, -60.00, 3.00, 0.00, -60.00, 3.00, -60.00, 0.00, 0.00, -60.00, -33.60, 0.00, -33.60, -60.00, 0.00, 0.00, -60.00, 0.00, }, { 0.00, -80.00, 30.00, 44.80, -80.00, 30.00, 80.00, -44.80, 30.00, 80.00, 0.00, 30.00, 0.00, -80.00, 12.00, 44.80, -80.00, 12.00, 80.00, -44.80, 12.00, 80.00, 0.00, 12.00, 0.00, -60.00, 3.00, 33.60, -60.00, 3.00, 60.00, -33.60, 3.00, 60.00, 0.00, 3.00, 0.00, -60.00, 0.00, 33.60, -60.00, 0.00, 60.00, -33.60, 0.00, 60.00, 0.00, 0.00, }, { -60.00, 0.00, 90.00, -60.00, -33.60, 90.00, -33.60, -60.00, 90.00, 0.00, -60.00, 90.00, -70.00, 0.00, 69.00, -70.00, -39.20, 69.00, -39.20, -70.00, 69.00, 0.00, -70.00, 69.00, -80.00, 0.00, 48.00, -80.00, -44.80, 48.00, -44.80, -80.00, 48.00, 0.00, -80.00, 48.00, -80.00, 0.00, 30.00, -80.00, -44.80, 30.00, -44.80, -80.00, 30.00, 0.00, -80.00, 30.00, }, { 0.00, -60.00, 90.00, 33.60, -60.00, 90.00, 60.00, -33.60, 90.00, 60.00, 0.00, 90.00, 0.00, -70.00, 69.00, 39.20, -70.00, 69.00, 70.00, -39.20, 69.00, 70.00, 0.00, 69.00, 0.00, -80.00, 48.00, 44.80, -80.00, 48.00, 80.00, -44.80, 48.00, 80.00, 0.00, 48.00, 0.00, -80.00, 30.00, 44.80, -80.00, 30.00, 80.00, -44.80, 30.00, 80.00, 0.00, 30.00, }, { -56.00, 0.00, 90.00, -56.00, -31.36, 90.00, -31.36, -56.00, 90.00, 0.00, -56.00, 90.00, -53.50, 0.00, 95.25, -53.50, -29.96, 95.25, -29.96, -53.50, 95.25, 0.00, -53.50, 95.25, -57.50, 0.00, 95.25, -57.50, -32.20, 95.25, -32.20, -57.50, 95.25, 0.00, -57.50, 95.25, -60.00, 0.00, 90.00, -60.00, -33.60, 90.00, -33.60, -60.00, 90.00, 0.00, -60.00, 90.00, }, { 0.00, -56.00, 90.00, 31.36, -56.00, 90.00, 56.00, -31.36, 90.00, 56.00, 0.00, 90.00, 0.00, -53.50, 95.25, 29.96, -53.50, 95.25, 53.50, -29.96, 95.25, 53.50, 0.00, 95.25, 0.00, -57.50, 95.25, 32.20, -57.50, 95.25, 57.50, -32.20, 95.25, 57.50, 0.00, 95.25, 0.00, -60.00, 90.00, 33.60, -60.00, 90.00, 60.00, -33.60, 90.00, 60.00, 0.00, 90.00, }, { 80.00, 0.00, 30.00, 80.00, 44.80, 30.00, 44.80, 80.00, 30.00, 0.00, 80.00, 30.00, 80.00, 0.00, 12.00, 80.00, 44.80, 12.00, 44.80, 80.00, 12.00, 0.00, 80.00, 12.00, 60.00, 0.00, 3.00, 60.00, 33.60, 3.00, 33.60, 60.00, 3.00, 0.00, 60.00, 3.00, 60.00, 0.00, 0.00, 60.00, 33.60, 0.00, 33.60, 60.00, 0.00, 0.00, 60.00, 0.00, }, { 0.00, 80.00, 30.00, -44.80, 80.00, 30.00, -80.00, 44.80, 30.00, -80.00, 0.00, 30.00, 0.00, 80.00, 12.00, -44.80, 80.00, 12.00, -80.00, 44.80, 12.00, -80.00, 0.00, 12.00, 0.00, 60.00, 3.00, -33.60, 60.00, 3.00, -60.00, 33.60, 3.00, -60.00, 0.00, 3.00, 0.00, 60.00, 0.00, -33.60, 60.00, 0.00, -60.00, 33.60, 0.00, -60.00, 0.00, 0.00, }, { 60.00, 0.00, 90.00, 60.00, 33.60, 90.00, 33.60, 60.00, 90.00, 0.00, 60.00, 90.00, 70.00, 0.00, 69.00, 70.00, 39.20, 69.00, 39.20, 70.00, 69.00, 0.00, 70.00, 69.00, 80.00, 0.00, 48.00, 80.00, 44.80, 48.00, 44.80, 80.00, 48.00, 0.00, 80.00, 48.00, 80.00, 0.00, 30.00, 80.00, 44.80, 30.00, 44.80, 80.00, 30.00, 0.00, 80.00, 30.00, }, { 0.00, 60.00, 90.00, -33.60, 60.00, 90.00, -60.00, 33.60, 90.00, -60.00, 0.00, 90.00, 0.00, 70.00, 69.00, -39.20, 70.00, 69.00, -70.00, 39.20, 69.00, -70.00, 0.00, 69.00, 0.00, 80.00, 48.00, -44.80, 80.00, 48.00, -80.00, 44.80, 48.00, -80.00, 0.00, 48.00, 0.00, 80.00, 30.00, -44.80, 80.00, 30.00, -80.00, 44.80, 30.00, -80.00, 0.00, 30.00, }, { 56.00, 0.00, 90.00, 56.00, 31.36, 90.00, 31.36, 56.00, 90.00, 0.00, 56.00, 90.00, 53.50, 0.00, 95.25, 53.50, 29.96, 95.25, 29.96, 53.50, 95.25, 0.00, 53.50, 95.25, 57.50, 0.00, 95.25, 57.50, 32.20, 95.25, 32.20, 57.50, 95.25, 0.00, 57.50, 95.25, 60.00, 0.00, 90.00, 60.00, 33.60, 90.00, 33.60, 60.00, 90.00, 0.00, 60.00, 90.00, }, { 0.00, 56.00, 90.00, -31.36, 56.00, 90.00, -56.00, 31.36, 90.00, -56.00, 0.00, 90.00, 0.00, 53.50, 95.25, -29.96, 53.50, 95.25, -53.50, 29.96, 95.25, -53.50, 0.00, 95.25, 0.00, 57.50, 95.25, -32.20, 57.50, 95.25, -57.50, 32.20, 95.25, -57.50, 0.00, 95.25, 0.00, 60.00, 90.00, -33.60, 60.00, 90.00, -60.00, 33.60, 90.00, -60.00, 0.00, 90.00, }, { -64.00, 0.00, 75.00, -64.00, 12.00, 75.00, -60.00, 12.00, 84.00, -60.00, 0.00, 84.00, -92.00, 0.00, 75.00, -92.00, 12.00, 75.00, -100.00, 12.00, 84.00, -100.00, 0.00, 84.00, -108.00, 0.00, 75.00, -108.00, 12.00, 75.00, -120.00, 12.00, 84.00, -120.00, 0.00, 84.00, -108.00, 0.00, 66.00, -108.00, 12.00, 66.00, -120.00, 12.00, 66.00, -120.00, 0.00, 66.00, }, { -60.00, 0.00, 84.00, -60.00, -12.00, 84.00, -64.00, -12.00, 75.00, -64.00, 0.00, 75.00, -100.00, 0.00, 84.00, -100.00, -12.00, 84.00, -92.00, -12.00, 75.00, -92.00, 0.00, 75.00, -120.00, 0.00, 84.00, -120.00, -12.00, 84.00, -108.00, -12.00, 75.00, -108.00, 0.00, 75.00, -120.00, 0.00, 66.00, -120.00, -12.00, 66.00, -108.00, -12.00, 66.00, -108.00, 0.00, 66.00, }, { -108.00, 0.00, 66.00, -108.00, 12.00, 66.00, -120.00, 12.00, 66.00, -120.00, 0.00, 66.00, -108.00, 0.00, 57.00, -108.00, 12.00, 57.00, -120.00, 12.00, 48.00, -120.00, 0.00, 48.00, -100.00, 0.00, 39.00, -100.00, 12.00, 39.00, -106.00, 12.00, 31.50, -106.00, 0.00, 31.50, -80.00, 0.00, 30.00, -80.00, 12.00, 30.00, -76.00, 12.00, 18.00, -76.00, 0.00, 18.00, }, { -120.00, 0.00, 66.00, -120.00, -12.00, 66.00, -108.00, -12.00, 66.00, -108.00, 0.00, 66.00, -120.00, 0.00, 48.00, -120.00, -12.00, 48.00, -108.00, -12.00, 57.00, -108.00, 0.00, 57.00, -106.00, 0.00, 31.50, -106.00, -12.00, 31.50, -100.00, -12.00, 39.00, -100.00, 0.00, 39.00, -76.00, 0.00, 18.00, -76.00, -12.00, 18.00, -80.00, -12.00, 30.00, -80.00, 0.00, 30.00, }, { 68.00, 0.00, 51.00, 68.00, 26.40, 51.00, 68.00, 26.40, 18.00, 68.00, 0.00, 18.00, 104.00, 0.00, 51.00, 104.00, 26.40, 51.00, 124.00, 26.40, 27.00, 124.00, 0.00, 27.00, 92.00, 0.00, 78.00, 92.00, 10.00, 78.00, 96.00, 10.00, 75.00, 96.00, 0.00, 75.00, 108.00, 0.00, 90.00, 108.00, 10.00, 90.00, 132.00, 10.00, 90.00, 132.00, 0.00, 90.00, }, { 68.00, 0.00, 18.00, 68.00, -26.40, 18.00, 68.00, -26.40, 51.00, 68.00, 0.00, 51.00, 124.00, 0.00, 27.00, 124.00, -26.40, 27.00, 104.00, -26.40, 51.00, 104.00, 0.00, 51.00, 96.00, 0.00, 75.00, 96.00, -10.00, 75.00, 92.00, -10.00, 78.00, 92.00, 0.00, 78.00, 132.00, 0.00, 90.00, 132.00, -10.00, 90.00, 108.00, -10.00, 90.00, 108.00, 0.00, 90.00, }, { 108.00, 0.00, 90.00, 108.00, 10.00, 90.00, 132.00, 10.00, 90.00, 132.00, 0.00, 90.00, 112.00, 0.00, 93.00, 112.00, 10.00, 93.00, 141.00, 10.00, 93.75, 141.00, 0.00, 93.75, 116.00, 0.00, 93.00, 116.00, 6.00, 93.00, 138.00, 6.00, 94.50, 138.00, 0.00, 94.50, 112.00, 0.00, 90.00, 112.00, 6.00, 90.00, 128.00, 6.00, 90.00, 128.00, 0.00, 90.00, }, { 132.00, 0.00, 90.00, 132.00, -10.00, 90.00, 108.00, -10.00, 90.00, 108.00, 0.00, 90.00, 141.00, 0.00, 93.75, 141.00, -10.00, 93.75, 112.00, -10.00, 93.00, 112.00, 0.00, 93.00, 138.00, 0.00, 94.50, 138.00, -6.00, 94.50, 116.00, -6.00, 93.00, 116.00, 0.00, 93.00, 128.00, 0.00, 90.00, 128.00, -6.00, 90.00, 112.00, -6.00, 90.00, 112.00, 0.00, 90.00, }, { 50.00, 0.00, 90.00, 50.00, 28.00, 90.00, 28.00, 50.00, 90.00, 0.00, 50.00, 90.00, 52.00, 0.00, 90.00, 52.00, 29.12, 90.00, 29.12, 52.00, 90.00, 0.00, 52.00, 90.00, 54.00, 0.00, 90.00, 54.00, 30.24, 90.00, 30.24, 54.00, 90.00, 0.00, 54.00, 90.00, 56.00, 0.00, 90.00, 56.00, 31.36, 90.00, 31.36, 56.00, 90.00, 0.00, 56.00, 90.00, }, { 0.00, 50.00, 90.00, -28.00, 50.00, 90.00, -50.00, 28.00, 90.00, -50.00, 0.00, 90.00, 0.00, 52.00, 90.00, -29.12, 52.00, 90.00, -52.00, 29.12, 90.00, -52.00, 0.00, 90.00, 0.00, 54.00, 90.00, -30.24, 54.00, 90.00, -54.00, 30.24, 90.00, -54.00, 0.00, 90.00, 0.00, 56.00, 90.00, -31.36, 56.00, 90.00, -56.00, 31.36, 90.00, -56.00, 0.00, 90.00, }, { -50.00, 0.00, 90.00, -50.00, -28.00, 90.00, -28.00, -50.00, 90.00, 0.00, -50.00, 90.00, -52.00, 0.00, 90.00, -52.00, -29.12, 90.00, -29.12, -52.00, 90.00, 0.00, -52.00, 90.00, -54.00, 0.00, 90.00, -54.00, -30.24, 90.00, -30.24, -54.00, 90.00, 0.00, -54.00, 90.00, -56.00, 0.00, 90.00, -56.00, -31.36, 90.00, -31.36, -56.00, 90.00, 0.00, -56.00, 90.00, }, { 0.00, -50.00, 90.00, 28.00, -50.00, 90.00, 50.00, -28.00, 90.00, 50.00, 0.00, 90.00, 0.00, -52.00, 90.00, 29.12, -52.00, 90.00, 52.00, -29.12, 90.00, 52.00, 0.00, 90.00, 0.00, -54.00, 90.00, 30.24, -54.00, 90.00, 54.00, -30.24, 90.00, 54.00, 0.00, 90.00, 0.00, -56.00, 90.00, 31.36, -56.00, 90.00, 56.00, -31.36, 90.00, 56.00, 0.00, 90.00, }, { 8.00, 0.00, 102.00, 8.00, 4.48, 102.00, 4.48, 8.00, 102.00, 0.00, 8.00, 102.00, 16.00, 0.00, 96.00, 16.00, 8.96, 96.00, 8.96, 16.00, 96.00, 0.00, 16.00, 96.00, 52.00, 0.00, 96.00, 52.00, 29.12, 96.00, 29.12, 52.00, 96.00, 0.00, 52.00, 96.00, 52.00, 0.00, 90.00, 52.00, 29.12, 90.00, 29.12, 52.00, 90.00, 0.00, 52.00, 90.00, }, { 0.00, 8.00, 102.00, -4.48, 8.00, 102.00, -8.00, 4.48, 102.00, -8.00, 0.00, 102.00, 0.00, 16.00, 96.00, -8.96, 16.00, 96.00, -16.00, 8.96, 96.00, -16.00, 0.00, 96.00, 0.00, 52.00, 96.00, -29.12, 52.00, 96.00, -52.00, 29.12, 96.00, -52.00, 0.00, 96.00, 0.00, 52.00, 90.00, -29.12, 52.00, 90.00, -52.00, 29.12, 90.00, -52.00, 0.00, 90.00, }, { -8.00, 0.00, 102.00, -8.00, -4.48, 102.00, -4.48, -8.00, 102.00, 0.00, -8.00, 102.00, -16.00, 0.00, 96.00, -16.00, -8.96, 96.00, -8.96, -16.00, 96.00, 0.00, -16.00, 96.00, -52.00, 0.00, 96.00, -52.00, -29.12, 96.00, -29.12, -52.00, 96.00, 0.00, -52.00, 96.00, -52.00, 0.00, 90.00, -52.00, -29.12, 90.00, -29.12, -52.00, 90.00, 0.00, -52.00, 90.00, }, { 0.00, -8.00, 102.00, 4.48, -8.00, 102.00, 8.00, -4.48, 102.00, 8.00, 0.00, 102.00, 0.00, -16.00, 96.00, 8.96, -16.00, 96.00, 16.00, -8.96, 96.00, 16.00, 0.00, 96.00, 0.00, -52.00, 96.00, 29.12, -52.00, 96.00, 52.00, -29.12, 96.00, 52.00, 0.00, 96.00, 0.00, -52.00, 90.00, 29.12, -52.00, 90.00, 52.00, -29.12, 90.00, 52.00, 0.00, 90.00, }, { 0.00, 0.00, 120.00, 0.00, 0.00, 120.00, 0.00, 0.00, 120.00, 0.00, 0.00, 120.00, 32.00, 0.00, 120.00, 32.00, 18.00, 120.00, 18.00, 32.00, 120.00, 0.00, 32.00, 120.00, 0.00, 0.00, 108.00, 0.00, 0.00, 108.00, 0.00, 0.00, 108.00, 0.00, 0.00, 108.00, 8.00, 0.00, 102.00, 8.00, 4.48, 102.00, 4.48, 8.00, 102.00, 0.00, 8.00, 102.00, }, { 0.00, 0.00, 120.00, 0.00, 0.00, 120.00, 0.00, 0.00, 120.00, 0.00, 0.00, 120.00, 0.00, 32.00, 120.00, -18.00, 32.00, 120.00, -32.00, 18.00, 120.00, -32.00, 0.00, 120.00, 0.00, 0.00, 108.00, 0.00, 0.00, 108.00, 0.00, 0.00, 108.00, 0.00, 0.00, 108.00, 0.00, 8.00, 102.00, -4.48, 8.00, 102.00, -8.00, 4.48, 102.00, -8.00, 0.00, 102.00, }, { 0.00, 0.00, 120.00, 0.00, 0.00, 120.00, 0.00, 0.00, 120.00, 0.00, 0.00, 120.00, -32.00, 0.00, 120.00, -32.00, -18.00, 120.00, -18.00, -32.00, 120.00, 0.00, -32.00, 120.00, 0.00, 0.00, 108.00, 0.00, 0.00, 108.00, 0.00, 0.00, 108.00, 0.00, 0.00, 108.00, -8.00, 0.00, 102.00, -8.00, -4.48, 102.00, -4.48, -8.00, 102.00, 0.00, -8.00, 102.00, }, { 0.00, 0.00, 120.00, 0.00, 0.00, 120.00, 0.00, 0.00, 120.00, 0.00, 0.00, 120.00, 0.00, -32.00, 120.00, 18.00, -32.00, 120.00, 32.00, -18.00, 120.00, 32.00, 0.00, 120.00, 0.00, 0.00, 108.00, 0.00, 0.00, 108.00, 0.00, 0.00, 108.00, 0.00, 0.00, 108.00, 0.00, -8.00, 102.00, 4.48, -8.00, 102.00, 8.00, -4.48, 102.00, 8.00, 0.00, 102.00, } };

    protected double[][] getPatches() {
        return patchData;
    }
}