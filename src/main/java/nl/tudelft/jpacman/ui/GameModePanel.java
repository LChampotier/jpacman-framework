package nl.tudelft.jpacman.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A panel with buttons for game mode choice.
 *
 * Created by NONO TATOU on 06/05/2016.
 */
public class GameModePanel extends JPanel{

    /** For all that is label on the pane */
    private JLabel lbl;

    /** The parent container which is a JFrame. */
    private JFrame parent;

    /** The menu bar. */
    private JMenuBar menuBar;

    /** The panes to display on the JFRame. */
    private JPanel panels;

    /** Label of the game pane */
    private String gamePanelName;

    /** Caption for the solo game mode button. */
    private final static String SOLO_MODE = "Solo";

    /** Caption for the I.A. game mode button. */
    private final static String IA_MODE = "I.A.";

    /**
     * Creates a panel for the game mode choice. For this JPac-Man we have only two game mode.
     *
     * @param parent
     *              The container that will content this pane.
     *
     * @param menuBar
     *              The menu bar for the <code>parent</code>.
     *
     * @param panels
     *              The set of panels for JPac-Man including this panel.
     *
     * @param gamePanelName
     *              The name of the game panel.
     */
    public GameModePanel(JFrame parent, JMenuBar menuBar, JPanel panels, String gamePanelName){
        super();

        init(parent, menuBar, panels, gamePanelName);

        GridBagLayout gBL = new GridBagLayout();
        gBL.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
        gBL.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
        gBL.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gBL.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        setLayout(gBL);

        GridBagConstraints gBC = new GridBagConstraints();

        lbl = new JLabel("JPac-Man");

        lbl.setFont(new Font("Tahoma", Font.BOLD, 29));
        addComponentListener(new JLabelComponentAdapter(this, lbl, true));

        gBC.anchor = GridBagConstraints.NORTH;
        gBC.weightx = 0.6;
        gBC.weighty = 0.2;
        gBC.insets = new Insets(0, 0, 5, 5);
        gBC.gridx = 2;
        gBC.gridy = 1;

        add(lbl, gBC);

        lbl = new JLabel("Fa\u00EEtes votre choix...");

        lbl.setFont(new Font("Tahoma", Font.ITALIC, 11));
        addComponentListener(new JLabelComponentAdapter(this, lbl, true));

        gBC.weighty = 0.1;
        gBC.insets = new Insets(19, 0, 5, 0);
        gBC.gridy = 3;

        add(lbl, gBC);
        addButtons(gBC);

        lbl = new JLabel("UMons1516");

        lbl.setFont(new Font("Tahoma", Font.ITALIC, 11));

        gBC.anchor = GridBagConstraints.SOUTH;
        gBC.fill = GridBagConstraints.HORIZONTAL;
        gBC.weighty = 0.2;
        gBC.insets = new Insets(19, 5, 0, 0);
        gBC.gridx = 0;
        gBC.gridy = 7;
        add(lbl, gBC);

        lbl = new JLabel("gX",SwingConstants.RIGHT);

        lbl.setPreferredSize(new Dimension(56,14));
        lbl.setMinimumSize(new Dimension(56,14));
        lbl.setFont(new Font("Tahoma", Font.ITALIC, 11));

        gBC.insets = new Insets(19, 0, 0, 5);
        gBC.gridx = 3;
        gBC.gridy = 7;
        add(lbl, gBC);
    }

    /**
     * Initializes the parent of this panel, its menu bar. It also initializes the panels set and the
     * game panel label.
     *
     * @param parent
     *              The parent container.
     *
     * @param menuBar
     *              The menu bar of the parent container.
     *
     * @param panels
     *              The panels set.
     *
     * @param gamePanelName
     *              The game panel label.
     */
    private void init(JFrame parent, JMenuBar menuBar, JPanel panels, String gamePanelName){
        this.parent = parent;
        this.menuBar = menuBar;
        this.panels = panels;
        this.gamePanelName = gamePanelName;
    }

    /**
     * Adds two buttons to this pane under some constraints defined by <code>gBC</code>.
     *
     * @param gBC
     *          Constraints under which buttons are added.
     */
    private void addButtons(GridBagConstraints gBC){
        gBC.weighty = 0.35;
        gBC.fill = GridBagConstraints.BOTH;

        addButton(SOLO_MODE, gBC,  4);
        addButton(IA_MODE, gBC,  5);
    }

    /**
     * Button defintion to add.
     *
     * @param caption
     *          The label on the button.
     *
     * @param gBC
     *          Constraints to be respected by the button.
     *
     * @param y
     *          The button position on the Y-Axis.
     */
    private void addButton(String caption,
                           GridBagConstraints gBC, int y){
        JButton btn = new JButton(caption);

        btn.setPreferredSize(new Dimension(53,23));

        switch(caption){
            case SOLO_MODE: btn.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent actionEvent) {
                                    soloMode();
                                }
                            });
                            break;

            case IA_MODE:   btn.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent actionEvent) {
                                    iaMode();
                                }
                            });
                            break;
        }

        btn.addComponentListener(new JButtonComponentAdapter(btn, true));

        gBC.gridy = y;
        gBC.insets = new Insets(0,0,5,0);
        add(btn, gBC);
    }

    /** To execute for the sole game mode. */
    private void soloMode(){
        parent.setJMenuBar(menuBar);

        CardLayout cl = (CardLayout)panels.getLayout();
        
        cl.show(panels, gamePanelName);
    }

    /** To execute for the I.A. game mode. */
    private void iaMode(){}
}
