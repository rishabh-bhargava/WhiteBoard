package client;

import java.awt.*;

public interface CanvasDelegate {
    public void drewLine(Color colour, float strokeWidth, int x1, int y1, int x2, int y2);
}
