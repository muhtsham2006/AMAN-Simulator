package atc;

import atc.controller.FlightController;
import atc.controller.SimulationClock;
import atc.model.AircraftFactory;
import atc.model.Airspace;
import atc.model.FCFSSeparationStrategy;
import atc.model.WakeOptimizedStrategy;
import atc.view.RadarPanel;
import atc.view.SequenceTablePanel;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Initialize Model layer
            Airspace airspace = new Airspace();

            // 2. Initialize Controller layer
            FlightController controller = new FlightController(airspace);
            SimulationClock clock = new SimulationClock(controller);

            // 3. Initialize View layer (Observers)
            RadarPanel radarPanel = new RadarPanel();
            SequenceTablePanel tablePanel = new SequenceTablePanel();

            // Register Observers
            airspace.addListener(radarPanel);
            airspace.addListener(tablePanel);

            // 4. Create Main Application Window
            JFrame frame = new JFrame("ATC Terminal Arrival Manager (AMAN) Simulator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 760);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());

            // 5. Control Toolbar Panel
            JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
            toolBar.setBackground(new Color(30, 35, 45));

            // Play / Pause Button
            JButton playPauseBtn = new JButton("Pause");
            playPauseBtn.addActionListener(e -> {
                if (clock.isRunning()) {
                    clock.pause();
                    playPauseBtn.setText("Resume");
                } else {
                    clock.start();
                    playPauseBtn.setText("Pause");
                }
            });

            // Simulation Speed Multiplier Selector
            JLabel speedLabel = new JLabel("Sim Speed:");
            speedLabel.setForeground(Color.WHITE);
            JComboBox<String> speedCombo = new JComboBox<>(new String[]{"1x Real-Time", "2x Accelerated", "5x Fast-Forward"});
            speedCombo.addActionListener(e -> {
                int selected = speedCombo.getSelectedIndex();
                if (selected == 0) clock.setTimeMultiplier(1);
                else if (selected == 1) clock.setTimeMultiplier(2);
                else if (selected == 2) clock.setTimeMultiplier(5);
            });

            // AMAN Scheduling Strategy Selector (Demonstrating Strategy Pattern)
            JLabel strategyLabel = new JLabel("AMAN Strategy:");
            strategyLabel.setForeground(Color.WHITE);
            JComboBox<String> strategyCombo = new JComboBox<>(new String[]{"First-Come, First-Served (FCFS)", "Wake Turbulence Optimized"});
            strategyCombo.addActionListener(e -> {
                int selected = strategyCombo.getSelectedIndex();
                if (selected == 0) {
                    airspace.getArrivalManager().setStrategy(new FCFSSeparationStrategy());
                } else {
                    airspace.getArrivalManager().setStrategy(new WakeOptimizedStrategy());
                }
                airspace.refreshSchedule();
            });

            // Spawn Traffic Button
            JButton spawnBtn = new JButton("Spawn Inbound Flight");
            String[] testModels = {"A380", "A350", "B777", "A320", "C172"};
            final int[] flightCounter = {101};
            spawnBtn.addActionListener(e -> {
                int idx = (int) (Math.random() * testModels.length);
                String model = testModels[idx];
                String callsign = "FL" + flightCounter[0]++;
                
                // Spawn along outer perimeter (~22 NM) at random angle
                double angle = Math.random() * 2 * Math.PI;
                double x = 22.0 * Math.sin(angle);
                double y = 22.0 * Math.cos(angle);
                // Compute heading toward runway (0,0)
                double heading = Math.toDegrees(Math.atan2(-x, -y));
                if (heading < 0) heading += 360;

                airspace.addAircraft(AircraftFactory.createAircraft(model, callsign, x, y, heading));
            });

            // Add elements to toolbar
            toolBar.add(playPauseBtn);
            toolBar.add(speedLabel);
            toolBar.add(speedCombo);
            toolBar.add(strategyLabel);
            toolBar.add(strategyCombo);
            toolBar.add(spawnBtn);

            // 6. Split Pane Layout (Radar on Left, Sequence Table on Right)
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, radarPanel, tablePanel);
            splitPane.setResizeWeight(0.65);
            splitPane.setDividerSize(4);

            frame.add(toolBar, BorderLayout.NORTH);
            frame.add(splitPane, BorderLayout.CENTER);

            // 7. Seed Initial Airspace Traffic
            // A380 entering from North-East
            airspace.addAircraft(AircraftFactory.createAircraft("A380", "UAE01", 14.0, 16.0, 220.0));
            // A350 entering from North-West
            airspace.addAircraft(AircraftFactory.createAircraft("A350", "BAW12", -12.0, 18.0, 150.0));
            // B777 entering from South-East
            airspace.addAircraft(AircraftFactory.createAircraft("B777", "QTR08", 15.0, -15.0, 315.0));
            // A320 entering from South-West
            airspace.addAircraft(AircraftFactory.createAircraft("A320", "DLH44", -16.0, -10.0, 060.0));
            // Cessna 172 entering close inbound
            airspace.addAircraft(AircraftFactory.createAircraft("C172", "G-BXYZ", 4.0, 6.0, 210.0));

            // Display GUI and launch background clock
            frame.setVisible(true);
            clock.setTimeMultiplier(2); // Default to 2x for smooth desktop viewing
            speedCombo.setSelectedIndex(1);
            clock.start();
        });
    }
}