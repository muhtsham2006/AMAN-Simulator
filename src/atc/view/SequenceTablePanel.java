package atc.view;

import atc.model.Aircraft;
import atc.model.AirspaceListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SequenceTablePanel extends JPanel implements AirspaceListener {
    private final JTable table;
    private final DefaultTableModel tableModel;

    private static final String[] COLUMNS = {
        "Seq", "Callsign", "Type", "Wake", "Dist (NM)", "ETO (s)", "TT (s)", "Delay (s)", "State"
    };

    public SequenceTablePanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(520, 0));

        // Create table model with non-editable cells
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setBackground(new Color(20, 25, 32));
        table.setForeground(new Color(210, 230, 240));
        table.setGridColor(new Color(40, 50, 65));
        table.setRowHeight(24);
        table.setFont(new Font("Monospaced", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Monospaced", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(30, 40, 55));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(20, 25, 32));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void onAirspaceUpdated(List<Aircraft> activeAircraft, int simTimeSeconds) {
        // Sort aircraft strictly by Target Landing Time (landing queue order)
        List<Aircraft> sortedList = new ArrayList<>(activeAircraft);
        sortedList.sort(Comparator.comparingInt(Aircraft::getTargetLandingTime));

        // Clear existing rows
        tableModel.setRowCount(0);

        // Populate updated rows
        int seq = 1;
        for (Aircraft a : sortedList) {
            int delay = a.getTargetLandingTime() - a.getEstimatedTimeOverhead();
            Object[] rowData = {
                seq++,
                a.getCallSign(),
                a.getModel().split(" ")[0], // Short model name
                a.getWakeCategory(),
                String.format("%.1f", a.getDistanceToRunway()),
                a.getEstimatedTimeOverhead(),
                a.getTargetLandingTime(),
                delay > 0 ? "+" + delay : String.valueOf(delay),
                a.getState()
            };
            tableModel.addRow(rowData);
        }
    }
}