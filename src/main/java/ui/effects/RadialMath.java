package ui.effects;

/**
 * Geometria do layout circular (sem JavaFX, para poder ser testada): onde ficam {@code count}
 * itens distribuídos igualmente numa elipse, começando no topo e seguindo em sentido horário.
 */
public final class RadialMath {
    private RadialMath() {}

    /** Centro do item {@code index} de {@code count}, numa elipse de centro (cx, cy) e raios rx, ry. */
    public static double[] position(int index, int count, double cx, double cy, double rx, double ry) {
        if (count <= 0 || index < 0 || index >= count) {
            throw new IllegalArgumentException("index " + index + " fora de 0.." + (count - 1));
        }
        double angle = -Math.PI / 2 + 2 * Math.PI * index / count; // -90° = topo
        return new double[]{cx + rx * Math.cos(angle), cy + ry * Math.sin(angle)};
    }
}
