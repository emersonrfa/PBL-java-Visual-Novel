package ui.effects;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * Painel que distribui os filhos em círculo (elipse) em volta do centro, acompanhando o tamanho da
 * janela. O primeiro filho fica no topo e os demais seguem em sentido horário.
 *
 * {@code radiusFactor} é a fração da largura/altura usada pelos raios (0,38 deixa folga nas bordas).
 */
public class RadialPane extends Pane {
    private double radiusFactor = 0.38;

    public double getRadiusFactor() {
        return radiusFactor;
    }

    public void setRadiusFactor(double radiusFactor) {
        this.radiusFactor = radiusFactor;
        requestLayout();
    }

    @Override
    protected void layoutChildren() {
        int count = getChildren().size();
        double cx = getWidth() / 2, cy = getHeight() / 2;
        double rx = getWidth() * radiusFactor, ry = getHeight() * radiusFactor;
        for (int i = 0; i < count; i++) {
            Node child = getChildren().get(i);
            double[] center = RadialMath.position(i, count, cx, cy, rx, ry);
            double w = child.prefWidth(-1), h = child.prefHeight(-1);
            child.resizeRelocate(center[0] - w / 2, center[1] - h / 2, w, h);
        }
    }
}
