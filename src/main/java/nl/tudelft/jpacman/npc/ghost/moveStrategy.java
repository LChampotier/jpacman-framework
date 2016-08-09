package nl.tudelft.jpacman.npc.ghost;

import nl.tudelft.jpacman.board.Direction;

/**
 * Allow to handle the different move strategies.
 *
 * @author NONO TATOU P.G.
 */
public interface MoveStrategy {

    /**
     * The next direction to take depend on the current ghost move strategy.
     *
     * @return The next direction.
     */
    public Direction nextMove();
}
