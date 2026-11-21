package tests;

import org.junit.jupiter.api.Test;
import ui.effects.RadialMath;

import static org.junit.jupiter.api.Assertions.*;

public class RadialMathTest {
    private static final double EPS = 1e-9;

    @Test
    public void firstItemIsAtTheTop() {
        double[] p = RadialMath.position(0, 8, 500, 300, 200, 100);
        assertEquals(500, p[0], EPS);
        assertEquals(200, p[1], EPS); // cy - ry (topo)
    }

    @Test
    public void itemsFollowClockwiseAndOppositeItemIsAtTheBottom() {
        double[] right = RadialMath.position(2, 8, 500, 300, 200, 100); // 1/4 da volta: direita
        assertEquals(700, right[0], EPS);
        assertEquals(300, right[1], EPS);
        double[] bottom = RadialMath.position(4, 8, 500, 300, 200, 100); // metade: embaixo
        assertEquals(500, bottom[0], EPS);
        assertEquals(400, bottom[1], EPS);
    }

    @Test
    public void allItemsLieOnTheEllipseAndAreDistinct() {
        for (int i = 0; i < 8; i++) {
            double[] p = RadialMath.position(i, 8, 0, 0, 3, 2);
            assertEquals(1.0, (p[0] * p[0]) / 9 + (p[1] * p[1]) / 4, 1e-9);
        }
        double[] a = RadialMath.position(0, 8, 0, 0, 3, 2);
        double[] b = RadialMath.position(1, 8, 0, 0, 3, 2);
        assertNotEquals(a[0], b[0]);
    }

    @Test
    public void rejectsInvalidIndex() {
        assertThrows(IllegalArgumentException.class, () -> RadialMath.position(8, 8, 0, 0, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> RadialMath.position(0, 0, 0, 0, 1, 1));
    }
}
