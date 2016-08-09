package nl.tudelft.jpacman.npc.ghost;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.level.Player;

import java.util.List;

/**
 * The ghost pursuit move. This class implements the function <code>nextMove</code> from interface
 * <code>{@link MoveStrategy}</code>.
 *
 * @author NONO TATOU P.G.
 */
public class PursuitMove implements MoveStrategy {
    /**
     * The ghost at which implement this move.
     */
    private Ghost ghost;

    /**
     * Creates a pursuit move for the specified ghost.
     *
     * @param g
     *      The ghost at which this will be assigned.
     */
    public PursuitMove(Ghost g){
        this.ghost = g;
    }

    @Override
    public Direction nextMove(){
        Direction d = getGhost().randomMove();
        Square origin = getGhost().getSquare();
        Unit player = Navigation.findNearest(Player.class, origin);

        if(player != null) d = myPathTo(player.getSquare());

        return d;
    }

    /**
     * @return The ghost at which this is implemented.
     */
    protected Ghost getGhost(){ return this.ghost; }

    protected Direction myPathTo(Square destination) {
        Direction d = getGhost().randomMove();
        Square origin = getGhost().getSquare();
        List<Direction> path = Navigation.shortestPath(origin, destination, getGhost());

        if (path != null && !path.isEmpty()) d = path.get(0);

        return d;
    }
}
