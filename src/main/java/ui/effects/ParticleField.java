package ui.effects;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Fundo animado: partículas vermelhas (brasas / gotas de luz) que nascem no canto inferior esquerdo
 * e sobem em diagonal até o canto superior direito, oscilando de leve e piscando.
 *
 * É um painel transparente que não captura o mouse; basta colocá-lo como primeiro filho da tela
 * (no FXML: {@code <ParticleField/>}). A animação só roda enquanto o painel está numa cena visível
 * e para sozinha ao sair, então não gasta processador nas outras telas.
 */
public class ParticleField extends Pane {
    private static final int PARTICLE_COUNT = 90;

    private final Canvas canvas = new Canvas();
    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();
    private final AnimationTimer timer;
    private long lastNanos;

    /** Uma partícula: posição, velocidade, tamanho, fase da oscilação e do brilho. */
    private static final class Particle {
        double x, y, vx, vy, radius, wobblePhase, wobbleSpeed, twinklePhase, twinkleSpeed, hue;
    }

    public ParticleField() {
        setMouseTransparent(true);
        setPickOnBounds(false);
        getChildren().add(canvas);
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double dt = lastNanos == 0 ? 0 : (now - lastNanos) / 1_000_000_000.0;
                lastNanos = now;
                step(Math.min(dt, 0.05)); // limita o salto se a janela travar um instante
                draw();
            }
        };

        // roda só enquanto estiver visível numa cena
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                timer.stop();
                lastNanos = 0;
            } else {
                timer.start();
            }
        });
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        if (particles.isEmpty() && getWidth() > 0 && getHeight() > 0) {
            for (int i = 0; i < PARTICLE_COUNT; i++) {
                Particle p = new Particle();
                respawn(p, true);
                particles.add(p);
            }
        }
    }

    /**
     * Coloca a partícula numa borda de partida (embaixo ou na esquerda, favorecendo o canto
     * inferior esquerdo). Na primeira vez ({@code scatter}) espalha pela tela toda para o fundo já
     * nascer "cheio".
     */
    private void respawn(Particle p, boolean scatter) {
        double w = Math.max(getWidth(), 1), h = Math.max(getHeight(), 1);
        if (scatter) {
            p.x = random.nextDouble() * w;
            p.y = random.nextDouble() * h;
        } else if (random.nextDouble() < 0.7) {   // nasce na borda de baixo, mais perto da esquerda
            p.x = Math.pow(random.nextDouble(), 1.6) * w;
            p.y = h + 10;
        } else {                                   // ou na borda da esquerda, mais perto de baixo
            p.x = -10;
            p.y = h - Math.pow(random.nextDouble(), 1.6) * h;
        }
        double speed = 22 + random.nextDouble() * 55;      // px/s
        double angle = Math.toRadians(35 + random.nextDouble() * 30); // 35° a 65° acima da horizontal
        p.vx = Math.cos(angle) * speed;
        p.vy = -Math.sin(angle) * speed;                   // negativo = sobe
        p.radius = 1.2 + random.nextDouble() * 3.4;
        p.wobblePhase = random.nextDouble() * Math.PI * 2;
        p.wobbleSpeed = 0.8 + random.nextDouble() * 1.6;
        p.twinklePhase = random.nextDouble() * Math.PI * 2;
        p.twinkleSpeed = 1.5 + random.nextDouble() * 2.5;
        p.hue = random.nextDouble();                       // 0 = vinho, 1 = vermelho vivo
    }

    private void step(double dt) {
        double w = getWidth(), h = getHeight();
        for (Particle p : particles) {
            p.wobblePhase += p.wobbleSpeed * dt;
            p.twinklePhase += p.twinkleSpeed * dt;
            p.x += (p.vx + Math.sin(p.wobblePhase) * 14) * dt;
            p.y += p.vy * dt;
            if (p.y < -12 || p.x > w + 12) {
                respawn(p, false);
            }
        }
    }

    private void draw() {
        double w = getWidth(), h = getHeight();
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, w, h);
        for (Particle p : particles) {
            double twinkle = 0.55 + 0.45 * Math.sin(p.twinklePhase);
            // some perto do topo para não "cortar" na borda
            double fade = Math.min(1.0, Math.max(0.0, p.y / (h * 0.25)));
            double alpha = twinkle * (0.25 + 0.55 * fade);
            Color core = Color.color(0.55 + 0.45 * p.hue, 0.04 + 0.08 * p.hue, 0.08 + 0.06 * p.hue, Math.min(1, alpha));
            Color halo = Color.color(0.9, 0.1, 0.15, alpha * 0.18);
            g.setFill(halo);
            g.fillOval(p.x - p.radius * 3, p.y - p.radius * 3, p.radius * 6, p.radius * 6);
            g.setFill(core);
            g.fillOval(p.x - p.radius, p.y - p.radius, p.radius * 2, p.radius * 2);
        }
    }
}
