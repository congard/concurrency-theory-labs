package pl.edu.agh.tw.knapp.lab8;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class MandelbrotVisualizer extends JFrame {
    private final BufferedImage image;

    public MandelbrotVisualizer(@NotNull List<MandelbrotChunk> chunks) {
        super("MandelbrotVisualizer");

        int width = chunks.stream().mapToInt(chunk -> chunk.getParams().endX()).max().orElseThrow();
        int height = chunks.stream().mapToInt(chunk -> chunk.getParams().endY()).max().orElseThrow();

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        chunks.forEach(chunk -> {
            var params = chunk.getParams();
            var rgb = chunk.getRgb();

            image.setRGB(
                    params.startX(), params.startY(),
                    params.getWidth(), params.getHeight(),
                    rgb,
                    0, params.getWidth());
        });

        setResizable(false);
        setSize(width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }

    public static void visualize(@NotNull List<MandelbrotChunk> chunks) {
        new MandelbrotVisualizer(chunks).setVisible(true);
    }
}
