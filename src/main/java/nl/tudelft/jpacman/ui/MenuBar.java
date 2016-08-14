package nl.tudelft.jpacman.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

/**
 * Created by NONO TATOU on 07/08/2016.
 */
public class MenuBar implements ActionListener{

    private final JFrame parent;

    public  MenuBar(JFrame frame){ this.parent = frame; }

    public JMenuBar createMenuBar(){
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;

        menuBar = new JMenuBar();
        menu = new JMenu("Game");
        menuItem = new JMenuItem("Quit");

        menuItem.setActionCommand("quit");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuBar.add(menu);

        menu = new JMenu("?");
        menuItem = new JMenuItem("About");

        menuItem.setActionCommand("about");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuBar.add(menu);

        return menuBar;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()){
            case "quit":    parent.dispatchEvent(new WindowEvent(parent, WindowEvent.WINDOW_CLOSING));
                            break;

            case "about":   JOptionPane.showMessageDialog(parent, message(),
                                                          "About", JOptionPane.PLAIN_MESSAGE);
                            break;
        }
    }

    private JPanel message(){
        JPanel aboutPanel = new JPanel();

        GridBagLayout gBL = new GridBagLayout();
        gBL.columnWidths = new int[]{0, 0, 0, 0};
        gBL.rowHeights = new int[]{0, 0, 0, 0, 0};
        gBL.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
        gBL.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        aboutPanel.setLayout(gBL);

        JLabel lbl = new JLabel("JPac-Man");
        lbl.setFont(new Font("Tahoma", Font.BOLD, 29));
        GridBagConstraints gBC = new GridBagConstraints();
        gBC.anchor = GridBagConstraints.CENTER;
        gBC.insets = new Insets(0, 0, 5, 0);
        gBC.gridx = 2;
        gBC.gridy = 1;
        aboutPanel.add(lbl, gBC);

        JSeparator separator = new JSeparator();
        gBC.fill = GridBagConstraints.HORIZONTAL;
        gBC.anchor = GridBagConstraints.WEST;
        gBC.insets = new Insets(0, 0, 5, 0);
        gBC.gridwidth = 3;
        gBC.gridx = 1;
        gBC.gridy = 2;
        aboutPanel.add(separator, gBC);

        String about = "<html>"
                + "UMons<br/>"
                + "2015-2016 (Master en sciences informatiques : Bloc 1)<br/>"
                + "Copyright © 2016 UMons.Tous droits réservés.<br/><br/><br/>"
                + "Le jeu JPac-Man a été développé à l'université de Delft<br/>"
                + "au Pays-Bas pour des cours de génie logiciel. Dans le<br/>"
                + "cadre du cours <i>Software Evolution</i>, cette version du<br/>"
                + "jeu a été réalisée en grande partie par l'étudiant<br/>"
                + "NONO TATOU.<br/>"
                + "</html>";
        lbl = new JLabel(about);
        lbl.setHorizontalAlignment(SwingConstants.LEFT);
        gBC.fill = GridBagConstraints.NONE;
        gBC.insets = new Insets(3, 0, 0, 0);
        gBC.gridx = 2;
        gBC.gridy = 3;
        aboutPanel.add(lbl, gBC);

        return aboutPanel;
    }
}
