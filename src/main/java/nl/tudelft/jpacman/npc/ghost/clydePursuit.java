package nl.tudelft.jpacman.npc.ghost;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Created by NONO TATOU
 */
public class ClydePursuit extends PursuitMove {
    /**
     * The amount of cells Clyde wants to stay away from Pac Man.
     */
    private static final int SHYNESS = 8;

    /**
     * A map of opposite directions.
     */
    private static final Map<Direction, Direction> OPPOSITES = new EnumMap<>(
            Direction.class);
    static {
        OPPOSITES.put(Direction.NORTH, Direction.SOUTH);
        OPPOSITES.put(Direction.SOUTH, Direction.NORTH);
        OPPOSITES.put(Direction.WEST, Direction.EAST);
        OPPOSITES.put(Direction.EAST, Direction.WEST);
    }

    public ClydePursuit(Ghost u) {
        super(u);
    }

    /**
     * The path to follow by Clyde towards the square of Pac-Man.
     *
     * @param destination
     * 		The square of Pac-Man.
     *
     * @return
     * 		The next direction to take by Clyde.
     */
    protected Direction myPathTo(Square destination) {
        Direction d = getGhost().randomMove();
        Square origin = getGhost().getSquare();
        List<Direction> path = Navigation.shortestPath(origin, destination, getGhost());

        if (path != null && !path.isEmpty()){
            d = path.get(0);

            if(path.size() <= SHYNESS) d = OPPOSITES.get(d);
        }

        return d;
    }
}
