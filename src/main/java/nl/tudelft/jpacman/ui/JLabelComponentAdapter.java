package nl.tudelft.jpacman.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Created by UMONS on 25/07/2016.
 */
public class JLabelComponentAdapter extends ComponentAdapter{

    private JPanel panel;
    private JLabel lbl;
    private int sizeFont, panelWidth, panelHeight;
    private boolean def;

    public JLabelComponentAdapter(JPanel panel, boolean def){
        this.def = def;
        this.panel = panel;
        this.panelWidth = 0;
        this.panelHeight = 0;
    }

    public JLabelComponentAdapter(JPanel panel, JLabel lbl, boolean def){
        this(panel, def);
        this.lbl = lbl;
    }

    @Override
    public void componentResized(ComponentEvent componentEvent) {
        if(def) defaultSize();

        int z = (((getSizeFont() * getPanelWidth()) / this.panelWidth) +
                 ((getSizeFont() * getPanelHeight()) / this.panelHeight)) / 2;

        lbl.setFont(new Font("Tahoma", getStyleFont(), z));
    }

    private int getPanelWidth(){ return panel.getWidth(); }

    private int getPanelHeight(){ return panel.getHeight(); }

    private int getStyleFont(){ return lbl.getFont().getStyle(); }

    private int getSizeFont(){ return sizeFont; }

    private void defaultSize(){
        sizeFont = lbl.getFont().getSize();
        panelWidth = getPanelWidth();
        panelHeight = getPanelHeight();
        def = !def;
    }

    public void setLabel(JLabel lbl){ this.lbl = lbl; }
}
