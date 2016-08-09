package nl.tudelft.jpacman.npc.ghost;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.level.Player;

import java.util.List;

/**
 * Created by NONO TATOU
 */
public class InkyPursuit extends PursuitMove{
    private static final int SQUARES_AHEAD = 2;

    public InkyPursuit(Ghost u){
        super(u);
    }

    public Direction nextMove(){
        Direction d = getGhost().randomMove();
        Square origin = getGhost().getSquare();
        Unit blinky = Navigation.findNearest(Blinky.class, origin);
        Unit player = Navigation.findNearest(Player.class, origin);

        if (blinky != null && player != null) {
            Square destination = destination(player.getDirection(), player.getSquare(), blinky.getSquare());
            d = myPathTo(destination);
        }

        return d;
    }

    /**
     * Locate two squares in front of Pac-Man.
     *
     * @param playerDirection
     * 		The direction currently followed by Pac-Man.
     *
     * @param playerSquare
     * 		The Pac-Man current position.
     *
     * @return
     * 		Two squares in front of Pac-Man in his current direction of travel.
     */
    private Square twoSquaresAway(Direction playerDirection, Square playerSquare){

        for (int i = 0; i < SQUARES_AHEAD; i++) {
            playerSquare = playerSquare.getSquareAt(playerDirection);
        }

        return playerSquare;
    }

    /**
     * Computes the distance between Blinky and Pac-Man and double it.
     *
     * @param blinkyPlace
     *      The place where Blinky is.
     *
     * @param s
     *      The place at two squares in front of Pac-Man place.
     *
     * @return
     *      The real path to follow by Inky.
     */
    private List<Direction> placeToReach(Square blinkyPlace, Square s){
        List<Direction> firstHalf = Navigation.shortestPath(blinkyPlace,s, null);

        if(firstHalf != null) firstHalf.addAll(firstHalf);

        return firstHalf;
    }

    /**
     * Computes the place that Inky have to reach.
     *
     * @param playerDirection
     *      The direction followed by Pac-Man.
     *
     * @param playerPlace
     *      The current place occupied by Pac-Man.
     *
     * @param blinkyPlace
     *      The current place occupied by Inky.
     * @return
     *      The place that Inky have to reach.
     */
    private Square destination(Direction playerDirection, Square playerPlace, Square blinkyPlace){
        Square destination = twoSquaresAway(playerDirection, playerPlace);
        List<Direction> path = placeToReach(blinkyPlace, destination);

        if(path != null)
            for (Direction e : path) destination = destination.getSquareAt(e);

        return destination;
    }
}
