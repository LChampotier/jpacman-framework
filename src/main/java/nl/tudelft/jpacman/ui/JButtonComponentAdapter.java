package nl.tudelft.jpacman.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Created by UMONS on 25/07/2016.
 */
public class JButtonComponentAdapter extends ComponentAdapter {
    private JButton btn;
    private int sizeFont, btnWidth, btnHeight;
    private boolean def;

    public JButtonComponentAdapter(JButton btn, boolean def){
        this.btn = btn;
        this.btnHeight = 0;
        this.btnWidth = 0;
        this.def = def;
    }

    @Override
    public void componentResized(ComponentEvent componentEvent) {
        if(def) defaultSize();

        int z = (((getSizeFont() * getButtonWidth()) / this.btnWidth) +
                 ((getSizeFont() * getButtonHeight()) / this.btnHeight)) / 2;


        componentEvent.getComponent().setFont(new Font("Tahoma", Font.BOLD, z));
    }

    private int getButtonWidth(){ return this.btn.getWidth(); }

    private int getButtonHeight(){ return this.btn.getHeight(); }

    private int getSizeFont(){ return this.sizeFont; }

    private void defaultSize(){
        this.btnHeight = getButtonHeight();
        this.btnWidth = getButtonWidth();
        this.def = !this.def;
        this.sizeFont = this.btn.getFont().getSize();
    }
}
