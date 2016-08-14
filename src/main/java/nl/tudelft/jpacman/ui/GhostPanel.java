package nl.tudelft.jpacman.ui;

import nl.tudelft.jpacman.npc.ghost.Ghost;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by NONO TATOU on 08/08/2016.
 */
public class GhostPanel extends JPanel {

    /**
     * The map of ghosts and the panels their information is on.
     */
    private final Map<Ghost, JPanel> ghostInfoLabels;

    /**
     * Creates a panel whith some information on ghosts.
     *
     * @param ghosts
     *          The ghosts are the information will be displayed.
     */
    public GhostPanel(List<Ghost> ghosts){
        super();
        assert ghosts != null;

        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        ghostInfoLabels = new LinkedHashMap<>();
        int i = 0;
        JLabel lbl = new JLabel("Ghosts Info.");


        lbl.setAlignmentX(CENTER_ALIGNMENT);
        addComponentListener(new JLabelComponentAdapter(this, lbl, true));
        add(lbl);
        add(Box.createRigidArea(new Dimension(0,19)));

        for(Ghost g : ghosts){
            JPanel gIP = new GhostInfoPanel(g.getName(), g.getMoveStrategy(),
                                            g.getMoveDuration(), g.getLocationTarget());

            ghostInfoLabels.put(g, gIP);
            add(gIP);

            if(i < 3){
                add(Box.createRigidArea(new Dimension(0,7)));
                i++;
            }
        }

        int w = getPreferredSize().width, h = getPreferredSize().height;
        Dimension size = new Dimension(w, h+19);

        setPreferredSize(size);
    }

    public void refresh(){
        for(Ghost g : ghostInfoLabels.keySet()){
            String strategy = g.getMoveStrategy();
            String duration = g.getMoveDuration();
            String location = g.getLocationTarget();

            ((GhostInfoPanel)ghostInfoLabels.get(g)).update(strategy, duration, location);
        }
    }
}
