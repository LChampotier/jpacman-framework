package nl.tudelft.jpacman.ui;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Created by NONO TATOU on 07/08/2016.
 */
public class GhostInfoPanel extends JPanel {

    private JLabel lbl, moveStrategy, moveDuration, targetLocation;

    public GhostInfoPanel(String name, String strategy, String duration, String location){
        super();

        init(strategy, duration, location);

        setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), name, TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        GridBagLayout gBL = new GridBagLayout();
        gBL.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gBL.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
        gBL.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gBL.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        setLayout(gBL);

        lbl = new JLabel("Move strategy");
        GridBagConstraints gBC = new GridBagConstraints();
        gBC.anchor = GridBagConstraints.WEST;
        gBC.insets = new Insets(3, 3, 3, 3);
        gBC.gridx = 0;
        gBC.gridy = 0;

        add(lbl, gBC);

        lbl = new JLabel(":");
        gBC.insets = new Insets(3, 0, 3, 3);
        gBC.gridx = 1;
        gBC.gridy = 0;

        add(lbl, gBC);

        gBC.fill = GridBagConstraints.HORIZONTAL;
        gBC.insets = new Insets(3, 0, 3, 3);
        gBC.gridx = 2;
        gBC.gridy = 0;

        add(moveStrategy, gBC);

        lbl = new JLabel("Move duration");
        gBC.fill = GridBagConstraints.NONE;
        gBC.insets = new Insets(0, 3, 3, 3);
        gBC.gridx = 0;
        gBC.gridy = 1;

        add(lbl, gBC);

        lbl = new JLabel(":");
        gBC.insets = new Insets(0, 0, 3, 3);
        gBC.gridx = 1;
        gBC.gridy = 1;

        add(lbl, gBC);

        gBC.fill = GridBagConstraints.HORIZONTAL;
        gBC.insets = new Insets(0, 0, 3, 3);
        gBC.gridx = 2;
        gBC.gridy = 1;

        add(moveDuration, gBC);

        lbl = new JLabel("Location target");
        gBC.fill = GridBagConstraints.NONE;
        gBC.anchor = GridBagConstraints.WEST;
        gBC.insets = new Insets(0, 3, 3, 3);
        gBC.gridx = 0;
        gBC.gridy = 2;

        add(lbl, gBC);

        lbl = new JLabel(":");
        gBC.insets = new Insets(0, 0, 3, 3);
        gBC.gridx = 1;
        gBC.gridy = 2;

        add(lbl, gBC);

        gBC.fill = GridBagConstraints.HORIZONTAL;
        gBC.insets = new Insets(0, 0, 3, 3);
        gBC.gridx = 2;
        gBC.gridy = 2;

        add(targetLocation, gBC);

        int w = getPreferredSize().width, h = getPreferredSize().height;

        setPreferredSize(new Dimension(w, h));
    }

    private void init(String strategy, String duration, String location){
        this.moveStrategy = new JLabel(strategy);
        this.moveDuration = new JLabel(duration);
        this.targetLocation = new JLabel(location);
    }

    public void update(String strategy, String duration, String location){
        moveStrategy.setText(strategy);
        moveDuration.setText(duration);
        targetLocation.setText(location);
    }
}
