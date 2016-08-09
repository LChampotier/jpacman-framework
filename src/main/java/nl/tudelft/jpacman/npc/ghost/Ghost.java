package nl.tudelft.jpacman.npc.ghost;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.npc.NPC;
import nl.tudelft.jpacman.sprite.Sprite;

import java.util.*;

/**
 * An antagonist in the game of Pac-Man, a ghost.
 * 
 * @author Jeroen Roosen 
 */
public abstract class Ghost extends NPC {

	private final static String DISPERSION_MODE = "Dispersion";
	private final static String PURSUIT_MODE = "Pursuit";

	/**
	 * <code>name</code> : The ghost name.<br/>
	 * <code>moveStrategy</code> : The move strategy of the ghost.<br/>
	 * <code>moveDuration</code> : The move strategy moveDuration of the ghost.<br/>
	 * <code>locationTarget</code> : The place to reach by the ghost.
	 */
	private String name, homePlace, moveStrategy, moveDuration, locationTarget;

	private DurationTimer timer;
	
	/**
	 * The sprite map, one sprite for each direction.
	 */
	private final Map<Direction, Sprite> sprites;

	/**
	 * <code>currentMove</code> : Move currently followed by the ghost.<br/>
	 * <code>dispersion</code> : Ghost dispersion move.<br/>
	 * <code>pursuitMove</code> : Ghost pursuit move.
	 */
	private MoveStrategy currentMove, dispersion, pursuit;

	/**
	 * A counter for the move strategy change.
	 */
	private int altCounter;

	/**
	 * The square to state the ghost home place.
	 */
	private Square home;

	/**
	 * Boolean to know if it is in corner.
	 */
	private boolean inMyCorner;

	private boolean done;

	/**
	 * Creates a new ghost.
	 * 
	 * @param spriteMap
	 *            The sprites for every direction.
	 */
	protected Ghost(Map<Direction, Sprite> spriteMap) {
		this.sprites = spriteMap;
		this.altCounter = 0;
		this.inMyCorner = false;
		this.done = false;
	}

	@Override
	public Sprite getSprite() { return sprites.get(getDirection()); }

	/**
	 * Appoints the ghost.
	 *
	 * @param name
	 * 			Ghost name.
     */
	protected void setName(String name){ this.name = name;}

	/**
	 * Enables to know the name of the ghost.
	 *
	 * @return A string representing the name of the ghost.
     */
	public String getName(){ return this.name; }

	/**
	 * States the position on the board where the ghost home place is.
	 *
	 * @param place
	 * 			The position on the board of the ghost home place.
     */
	protected void setHomePlace(String place){ this.homePlace = place;}

	/**
	 * Enables to have the position on the board of the ghost home place.
	 *
	 * @return A string representing the position on the board of the ghost home place.
     */
	private String getHomePlace(){ return this.homePlace; }


	/**
	 * Assign a place for ghost house.
	 *
	 * @param h
	 * 		The place for the ghost house.
     */
	public void setHome(Square h){ this.home = h; }

	/**
	 * Allow to a ghost to say if it is in its corner.
	 *
	 * @param t
	 * 		Boolean value. <code>true</code> if it is in its corner, <code>false</code> else.
     */
	public void inMyCorner(boolean t){ inMyCorner = t; }

	@Override
	public boolean isInMyCorner(){ return inMyCorner; }

	/**
	 * Return the house place.
	 *
	 * @return The place for the ghost house.
	 */
	public Square getHome(){ return this.home; }

	@Override
	public MoveStrategy changeMove() {
		if(!inPursuitMove()){
			setCurrentMove(pursuit());
		}else{
			setCurrentMove(dispersion());
		}

//		System.out.println(getName()+" "+timer.getState());

		altCounter++;

		return getCurrentMove();
	}

	/**
	 *@return The move currently followed by the ghost.
     */
	protected MoveStrategy getCurrentMove(){ return currentMove; }

	/**
	 * Modify the ghost current move.
	 * @param m
	 * 		A move strategy.
     */
	private void setCurrentMove(MoveStrategy m){
		currentMove = m;
	}

	/**
	 * @return The dispersion move.
     */
	private MoveStrategy dispersion(){
		moveStrategy = DISPERSION_MODE;
		moveDuration = (altCounter <= 2) ? "7" : "5";
		locationTarget = "Home place("+getHomePlace()+")";

		timer.setDuration(moveDuration);
		timer.setStrategy(moveStrategy);

		return dispersion;
	}

	/**
	 * @return The pursuit move.
	 */
	private MoveStrategy pursuit(){
		((DispersionMove)dispersion).resetDirectionCounter();

		moveStrategy = PURSUIT_MODE;

		if(altCounter <= 5){
			moveDuration = "20";

			timer.setDuration(moveDuration);
		}else{
			moveDuration = "∞";
		}

		locationTarget = "Pacman place";

		timer.setStrategy(moveStrategy);

		return pursuit;
	}

	public boolean inPursuitMove(){ return currentMove instanceof PursuitMove; }

	/**
	 * Define the ghost move moveStrategies.
	 *
	 * @param d
	 * 		The ghost dispersion move.
	 *
	 * @param p
	 * 		The ghost pursuit move.
     */
	protected void moveStrategies(MoveStrategy d, MoveStrategy p){
		this.dispersion = d;
		this.pursuit = p;

		initStrategy(d, DISPERSION_MODE, "7", "Home place ("+getHomePlace()+")");
	}

	private void initStrategy(MoveStrategy d, String strategy, String duration, String location){
		this.currentMove = d;
		this.moveStrategy = strategy;
		this.moveDuration = duration;
		this.timer = new DurationTimer(duration, strategy);
		this.locationTarget = location;

		this.timer.start();
	}

	/**
	 * Enables to know the move strategy of the ghost.
	 *
	 * @return A string representing the move strategy of the ghost.
	 */
	public String getMoveStrategy(){ return moveStrategy; }

	/**
	 * Enables to know the moveDuration move strategy of the ghost. In pursuit mode, this
	 * moveDuration is decremented every one second. This is the same in dispersion mode only if the
	 * ghost is in its home place.
	 *
	 * @return A string representing the moveDuration move strategy of the ghost.
	 */
	public String getMoveDuration(){ return (altCounter <= 6) ? timer.getDuration() + " s" : ""; }

	/**
	 * Enables to know the place to reach by the ghost. In the particular case of dispersion move,
	 * when the location target is reached, a dash is displayed.
	 *
	 * @return A string representing the place to reach by the ghost.
     */
	public String getLocationTarget(){
		return (this.moveStrategy.equals(DISPERSION_MODE) && isInMyCorner()) ? "-" :
				this.locationTarget;
	}

	/**
	 * Determines a possible move in a random direction.
	 * 
	 * @return A direction in which the ghost can move, or <code>null</code> if
	 *         the ghost is shut in by inaccessible squares.
	 */
	protected Direction randomMove() {
		List<Direction> thePath = path(new ArrayList<>(), getSquare());
		Direction d = null;

		if (!thePath.isEmpty()) d = thePath.get(new Random().nextInt(thePath.size()));

		return d;
	}

	/**
	 * Determmines the accessible adjacent squares next to the square where he is.
	 *
	 * @param path
	 * 		List of the accessible adjacent squares.
	 *
	 * @param s
	 * 		The ghost square
	 *
     * @return List of the accessible adjacent squares by the ghost from where he is.
     */
	private List<Direction> path(List<Direction> path, Square s){
		for (Direction d : Direction.values())
			if (s.getSquareAt(d).isAccessibleTo(this)) path.add(d);

		return path;
	}

	public void done(){ done = true; }

	protected boolean isDone(){ return done; }

	private class DurationTimer extends Thread{

		private String strategy;
		private int d;
		private boolean decrement;

		public DurationTimer(String duration, String strategy){
			this.strategy = strategy;
			this.d = Integer.parseInt(duration);
			this.decrement = true;
		}

		@Override
		public void run() {
			while(decrement && !isDone()){
				decrement();

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void decrement(){
			switch(strategy){
				case DISPERSION_MODE:	if(isInMyCorner()) d--;
										break;

				case PURSUIT_MODE:		if(altCounter < 6 ) d--;
										break;
			}

			if(d <= 0) d = 0;
		}

		public String getDuration(){ return String.valueOf(d); }

		public void setDuration(String duration){ d = Integer.parseInt(duration); }

		public void setStrategy(String strategy){ this.strategy = strategy; }

		public void stopDecrement(){ decrement = false; }
	}
}
