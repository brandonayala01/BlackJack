package view;

import model.GameRecord;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class HistoryView extends JDialog {
    public HistoryView(Window owner, ArrayList<GameRecord> history) {
        super(owner, "Game history", ModalityType.APPLICATION_MODAL);
        setSize(760, 460);
        setLocationRelativeTo(owner);

        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(Theme.PANEL);
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("GAME HISTORY");
        title.setForeground(Theme.GOLD);
        title.setFont(Theme.TITLE);

        String[] columns = {"Date", "Player", "Result", "Bet", "Player total", "Dealer total"};
        Object[][] rows = new Object[history.size()][];
        for (int i = 0; i < history.size(); i++) {
            GameRecord record = history.get(history.size() - 1 - i);
            rows[i] = new Object[]{
                record.getDate(),
                record.getUsername(),
                record.getResult(),
                String.format("$%,.2f", record.getBet()),
                record.getPlayerValue(),
                record.getDealerValue()
            };
        }

        JTable table = new JTable(rows, columns);
        table.setRowHeight(28);
        table.setFont(Theme.NORMAL);
        table.getTableHeader().setFont(Theme.BUTTON);
        table.setFillsViewportHeight(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(170);

        root.add(title, BorderLayout.NORTH);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(close);
        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);
    }
}
