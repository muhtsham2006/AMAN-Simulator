package atc.view;

import atc.model.Aircraft;
import atc.model.AirspaceListener;
import atc.model.FlightState;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

public class RadarPanel extends JPanel implements AirspaceListener {
    // Sector scale: Display a radius of 25 NM from center
    private static final double MAX_RANGE_NM = 25.0;

    private List<Aircraft> aircraftToDraw;
    private int simTimeSeconds;

    public RadarPanel() {
        this.aircraftToDraw = new ArrayList<>();
        this.simTimeSeconds = 0;
        this.setBackground(new Color(10, 15, 20)); // Deep dark-navy radar scope
    }

    @Override
    public void onAirspaceUpdated(List<Aircraft> activeAircraft, int simTimeSeconds) {
        // Update local render cache on the Swing event thread
        this.aircraftToDraw = new ArrayList<>(activeAircraft);
        this.simTimeSeconds = simTimeSeconds;
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Enable anti-aliasing for smooth lines and text
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;

        // Scale factor: pixels per Nautical Mile
        double pixelsPerNM = Math.min(width, height) / (MAX_RANGE_NM * 2.0);

        // 1. Draw Range Rings & Compass Grids
        drawRangeRings(g2, centerX, centerY, pixelsPerNM);

        // 2. Draw Runway Threshold at (0, 0)
        drawRunway(g2, centerX, centerY);

        // 3. Draw Aircraft Blips & ATC Data Blocks
        drawAircraft(g2, centerX, centerY, pixelsPerNM);

        // 4. Draw Header Overlay
        drawHeaderOverlay(g2);
    }

    private void drawRangeRings(Graphics2D g2, int cx, int cy, double scale) {
        g2.setColor(new Color(25, 55, 40)); // Subtle radar phosphor green
        g2.setStroke(new BasicStroke(1.0f));

        // Concentric rings at 5, 10, 15, 20 NM
        int[] ringsNM = {5, 10, 15, 20};
        for (int r : ringsNM) {
            int radiusPx = (int) (r * scale);
            g2.drawOval(cx - radiusPx, cy - radiusPx, radiusPx * 2, radiusPx * 2);
            g2.drawString(r + " NM", cx + radiusPx + 4, cy - 2);
        }

        // Crosshairs intersecting runway center
        g2.setColor(new Color(20, 45, 35));
        g2.drawLine(cx, 0, cx, getHeight());
        g2.drawLine(0, cy, getWidth(), cy);
    }

    private void drawRunway(Graphics2D g2, int cx, int cy) {
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3.0f));
        // Simple runway representation aligned North-South (18/36)
        g2.drawLine(cx, cy - 12, cx, cy + 12);
        g2.setFont(new Font("Monospaced", Font.BOLD, 10));
        g2.drawString("RWY 18/36", cx + 6, cy + 4);
    }

    private void drawAircraft(Graphics2D g2, int cx, int cy, double scale) {
        g2.setFont(new Font("Monospaced", Font.PLAIN, 11));

        for (Aircraft a : aircraftToDraw) {
            // Coordinate transformation:
            // Aviation X is East (+), Screen X is right (+)
            // Aviation Y is North (+), Screen Y is down (-)
            int screenX = cx + (int) (a.getX() * scale);
            int screenY = cy - (int) (a.getY() * scale);

            // Color coding based on flight state
            if (a.getState() == FlightState.HOLDING) {
                g2.setColor(new Color(255, 170, 0)); // Amber for holding
            } else if (a.getState() == FlightState.APPROACH) {
                g2.setColor(new Color(50, 220, 100)); // Bright green for final approach
            } else {
                g2.setColor(new Color(0, 200, 255)); // Cyan for standard en-route
            }

            // Draw target position blip
            g2.fillOval(screenX - 4, screenY - 4, 8, 8);

            // Draw heading vector line (speed vector indication)
            double headingRad = Math.toRadians(a.getHeading());
            int vectorLength = 18;
            int endX = screenX + (int) (vectorLength * Math.sin(headingRad));
            int endY = screenY - (int) (vectorLength * Math.cos(headingRad));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(screenX, screenY, endX, endY);

            // Draw ATC Data Tag
            int tagX = screenX + 10;
            int tagY = screenY - 6;

            String line1 = a.getCallSign() + " (" + a.getModel().split(" ")[0] + ")";
            String line2 = String.format("A%03.0f %3.0fkt", a.getAltitude() / 100.0, a.getSpeed());
            String line3 = a.getState().name() + " | TT:" + a.getTargetLandingTime() + "s";

            g2.drawString(line1, tagX, tagY);
            g2.drawString(line2, tagX, tagY + 12);
            g2.drawString(line3, tagX, tagY + 24);
        }
    }

    private void drawHeaderOverlay(Graphics2D g2) {
        g2.setColor(new Color(180, 220, 200));
        g2.setFont(new Font("Monospaced", Font.BOLD, 12));
        g2.drawString("RADAR SCOPE | 25 NM TMA", 15, 20);
        g2.drawString(String.format("SIM TIME: %04d SEC | TRACKS: %d", simTimeSeconds, aircraftToDraw.size()), 15, 38);
    }
}