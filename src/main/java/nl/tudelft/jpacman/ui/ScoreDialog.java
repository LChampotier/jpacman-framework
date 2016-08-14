package nl.tudelft.jpacman.ui;

import nl.tudelft.jpacman.Launcher;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by NONO TATOU on 12/08/2016.
 */
public class ScoreDialog extends JDialog implements PropertyChangeListener {
    private static final String LOST_GAME = "Lost game";
    private static final String WON_GAME = "Won game";

    private JFrame parent;
    private JOptionPane optionPane;

    private String type;
    private String btnRestart = "Re-start";
    private String btnQuit = "Quit";

    public ScoreDialog(final JFrame parent){
        super(parent, true);
        this.parent = parent;
    }

    public void createScoreDialog(String type, String time, String score){
        setTitle(type);
        String msg = "";

        if(type.equals(LOST_GAME))
            msg = "Sorry, you lost this game. May be you would be more lucky next time !!!";

        if(type.equals(WON_GAME)) msg = "Congratulations, you won !";

        Object[] options = {btnRestart, btnQuit};
        optionPane = new JOptionPane(message(msg, time, score),
                                     JOptionPane.PLAIN_MESSAGE,
                                     JOptionPane.YES_NO_OPTION,
                                     null,
                                     options,
                                     options[0]);

        setContentPane(optionPane);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
/*      addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
            }
        });*/

        optionPane.addPropertyChangeListener(this);
    }

    private JPanel message(String msg, String time, String score){
        JPanel scorePanel = new JPanel();

        GridBagLayout gBL = new GridBagLayout();
        gBL.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
        gBL.rowHeights = new int[]{0, 0, 0, 0, 0};
        gBL.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gBL.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        scorePanel.setLayout(gBL);

        JLabel lbl = new JLabel(msg);
        GridBagConstraints gBC = new GridBagConstraints();
        gBC.insets = new Insets(0, 0, 5, 0);
        gBC.gridwidth = 7;
        gBC.gridx = 1;
        gBC.gridy = 1;
        scorePanel.add(lbl, gBC);

        lbl = new JLabel("Time");
        gBC.anchor = GridBagConstraints.WEST;
        gBC.insets = new Insets(0, 0, 5, 3);
        gBC.gridwidth = 1;
        gBC.gridx = 1;
        gBC.gridy = 2;
        scorePanel.add(lbl, gBC);

        lbl = new JLabel(":");
        gBC.gridx = 2;
        gBC.gridy = 2;
        scorePanel.add(lbl, gBC);

        lbl = new JLabel(time + " s");
        gBC.insets = new Insets(0, 0, 5, 0);
        gBC.gridx = 3;
        gBC.gridy = 2;
        scorePanel.add(lbl, gBC);

        lbl = new JLabel("Score");
        gBC.insets = new Insets(0, 0, 0, 3);
        gBC.gridx = 1;
        gBC.gridy = 3;
        scorePanel.add(lbl, gBC);

        lbl = new JLabel(":");
        gBC.gridx = 2;
        gBC.gridy = 3;
        scorePanel.add(lbl, gBC);

        lbl = new JLabel(score);
        gBC.insets = new Insets(0, 0, 0, 0);
        gBC.gridx = 3;
        gBC.gridy = 3;
        scorePanel.add(lbl, gBC);

        return scorePanel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if(isVisible() &&
          (e.getSource() == optionPane) &&
          (JOptionPane.VALUE_PROPERTY.equals(prop))){
            Object value = optionPane.getValue();

            if(value == JOptionPane.UNINITIALIZED_VALUE) return;

            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

            if(btnRestart.equals(value)){
                setVisible(false);
                Launcher.getTheOne().relaunch(true);
            }else if(btnQuit.equals(value)) System.exit(0);
        }

    }
}
