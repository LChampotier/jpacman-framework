package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.npc.NPC;
import nl.tudelft.jpacman.npc.ghost.Ghost;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A level of Pac-Man. A level consists of the board with the players and the
 * AIs on it.
 * 
 * @author Jeroen Roosen 
 */
public class Level {

	/**
	 * The board of this level.
	 */
	private final Board board;

	/**
	 * The lock that ensures moves are executed sequential.
	 */
	private final Object moveLock = new Object();

	/**
	 * The lock that ensures starting and stopping can't interfere with each
	 * other.
	 */
	private final Object startStopLock = new Object();

	/**
	 * The NPCs of this level and, if they are running, their schedules.
	 */
	private final Map<NPC, ScheduledExecutorService> npcs;

	/**
	 * <code>true</code> iff this level is currently in progress, i.e. players
	 * and NPCs can move.
	 */
	private boolean inProgress;

	/** Enables to know if the game started. */
	private boolean started;

	/**
	 * The squares from which players can start this game.
	 */
	private final List<Square> startSquares;

	/**
	 * The start current selected starting square.
	 */
	private int startSquareIndex;

	/** To memorize move ghosts data. */
	private int[] memory;

	/**
	 * The players on this level.
	 */
	private final List<Player> players;

	/**
	 * The ghosts on this level.
	 */
	private final List<Ghost> ghosts;

	/**
	 * The table of possible collisions between units.
	 */
	private final CollisionMap collisions;

	/**
	 * The objects observing this level.
	 */
	private final List<LevelObserver> observers;

	/**
	 * Creates a new level for the board.
	 * 
	 * @param b
	 *            The board for the level.
	 * @param ghosts
	 *            The ghosts on the board.
	 * @param startPositions
	 *            The squares on which players start on this board.
	 * @param collisionMap
	 *            The collection of collisions that should be handled.
	 */
	public Level(Board b, List<NPC> ghosts, List<Square> startPositions,
			CollisionMap collisionMap) {
		assert b != null;
		assert ghosts != null;
		assert startPositions != null;

		this.board = b;
		this.inProgress = false;
		this.started = false;
		this.npcs = new HashMap<>();
		this.ghosts = new ArrayList<>();
		this.memory = new int[]{0, 7000, 0};

		for (NPC g : ghosts){
			npcs.put(g, null);
			this.ghosts.add(g.toGhost());
		}
		this.startSquares = startPositions;
		this.startSquareIndex = 0;
		this.players = new ArrayList<>();
		this.collisions = collisionMap;
		this.observers = new ArrayList<>();
	}

	public List<Ghost> getGhosts(){ return ghosts; }

	/**
	 * Adds an observer that will be notified when the level is won or lost.
	 * 
	 * @param observer
	 *            The observer that will be notified.
	 */
	public void addObserver(LevelObserver observer) {
		if (observers.contains(observer)) return;

		observers.add(observer);
	}

	/**
	 * Removes an observer if it was listed.
	 * 
	 * @param observer
	 *            The observer to be removed.
	 */
	public void removeObserver(LevelObserver observer) {
		observers.remove(observer);
	}

	/**
	 * Registers a player on this level, assigning him to a starting position. A
	 * player can only be registered once, registering a player again will have
	 * no effect.
	 * 
	 * @param p
	 *            The player to register.
	 */
	public void registerPlayer(Player p) {
		assert p != null;
		assert !startSquares.isEmpty();

		if (players.contains(p)) return;

		players.add(p);
		Square square = startSquares.get(startSquareIndex);
		p.occupy(square);
		startSquareIndex++;
		startSquareIndex %= startSquares.size();
	}

	/**
	 * Returns the board of this level.
	 * 
	 * @return The board of this level.
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * Moves the unit into the given direction if possible and handles all
	 * collisions.
	 * 
	 * @param unit
	 *            The unit to move.
	 * @param direction
	 *            The direction to move the unit in.
	 */
	public void move(Unit unit, Direction direction) {
		assert unit != null;
		assert direction != null;

		if (!isInProgress()) return;

		synchronized (moveLock) {
			unit.setDirection(direction);
			Square location = unit.getSquare();
			Square destination = location.getSquareAt(direction);

			if (destination.isAccessibleTo(unit)) {
				List<Unit> occupants = destination.getOccupants();
				unit.occupy(destination);
				for (Unit occupant : occupants) collisions.collide(unit, occupant);
			}
			updateObservers();
		}
	}

	/**
	 * Starts or resumes this level, allowing movement and (re)starting the
	 * NPCs.
	 */
	public void start() {
		synchronized (startStopLock) {
			if(isInProgress())  return;
			if(!hasStarted()) started = true;

			startNPCs();

			inProgress = true;

			updateObservers();
		}
	}

	/**
	 * Enables to know the game started or not.
	 *
	 * @return	<code>true</code> iff the game started.
	 */
	public boolean hasStarted(){ return started; }

	/**
	 * Stops or pauses this level, no longer allowing any movement on the board
	 * and stopping all NPCs.
	 */
	public void stop() {
		synchronized (startStopLock) {
			if(!isInProgress())  return;

			stopNPCs();
			inProgress = false;
		}
	}

	/**
	 * Starts all NPC movement scheduling.
	 */
	private void startNPCs() {
		for (final NPC npc : npcs.keySet()) {
			ScheduledExecutorService service = Executors
					.newSingleThreadScheduledExecutor();

			service.schedule(new NpcMoveTask(service, npc, memory),
							 npc.getInterval() / 2, TimeUnit.MILLISECONDS);
			npc.stop(false);
			npcs.put(npc, service);
		}
	}

	/**
	 * Stops all NPC movement scheduling and interrupts any movements being
	 * executed.
	 */
	private void stopNPCs() {
		for (Entry<NPC, ScheduledExecutorService> e : npcs.entrySet()) {
			if(!isAnyPlayerAlive() || remainingPellets() == 0) e.getKey().done();

			e.getKey().stop(true);
			e.getValue().shutdownNow();
		}
	}

	/**
	 * Returns whether this level is in progress, i.e. whether moves can be made
	 * on the board.
	 * 
	 * @return <code>true</code> iff this level is in progress.
	 */
	public boolean isInProgress() {
		return inProgress;
	}

	/**
	 * Updates the observers about the state of this level.
	 */
	private void updateObservers() {
		if (!isAnyPlayerAlive())
			for (LevelObserver o : observers) o.levelLost();

		if (remainingPellets() == 0)
			for (LevelObserver o : observers) o.levelWon();
	}

	/**
	 * Returns <code>true</code> iff at least one of the players in this level
	 * is alive.
	 * 
	 * @return <code>true</code> if at least one of the registered players is
	 *         alive.
	 */
	public boolean isAnyPlayerAlive() {
		boolean alive = false;

		for (Player p : players)
			if (p.isAlive()) alive = true;

		return alive;
	}


	/**
	 * Counts the pellets remaining on the board.
	 * 
	 * @return The amount of pellets remaining on the board.
	 */
	public int remainingPellets() {
		Board b = getBoard();
		int pellets = 0;
		for (int x = 0; x < b.getWidth(); x++)
			for (int y = 0; y < b.getHeight(); y++)
				for (Unit u : b.squareAt(x, y).getOccupants())
					if (u instanceof Pellet) pellets++;

		return pellets;

	}

	/**
	 * A task that moves an NPC and reschedules itself after it finished.
	 * 
	 * @author Jeroen Roosen 
	 */
	private final class NpcMoveTask implements Runnable {

		/**
		 * The service executing the task.
		 */
		private final ScheduledExecutorService service;

		/**
		 * The NPC to move.
		 */
		private final NPC npc;

		/**
		 * The time to change move strategy.
		 */
		private long time;

		/**
		 * <code>counter</code>: To count the transition from one move mode to another.
		 * <code>duration</code>: The move duration.
		 */
		private int counter, duration;

		/**
		 * Creates a new task.
		 * 
		 * @param s
		 *            The service that executes the task.
		 * @param n
		 *            The NPC to move.
		 */
		private NpcMoveTask(ScheduledExecutorService s, NPC n) {
			this.service = s;
			this.npc = n;
		}

		private NpcMoveTask(ScheduledExecutorService s, NPC n, int[] m) {
			this(s, n);
			this.time = m[0];
			this.duration = m[1];
			this.counter = m[2];
		}

		@Override
		public void run() {
			if(npc.isInMyCorner() && !npc.inPursuitMove() && getCounter()<7){
				pursuit();
			}

			if(!npc.isInMyCorner() && npc.inPursuitMove() && getCounter()<6){
				dispersion();
			}

			Direction nextMove = npc.nextMove();
			if (nextMove != null) {
				move(npc, nextMove);
			}
			long interval = npc.getInterval();

			service.schedule(this, interval, TimeUnit.MILLISECONDS);
		}

		/**
		 * Stopwatch the time spent in move mode.
		 *
		 * @return
		 * 		The time spent in move mode.
         */
		private long timer(){
			time += 238;
			memory[0] = (int)time;

			return time;
		}

		/**
		 * Reset <code>time</code>.
		 */
		private void resetTimer(){
			time = 0;
		}

		/**
		 * Modify the move duration.
		 *
		 * @param d
		 * 		The new duration.
         */
		private void setDuration(int d){
			duration = d;
			memory[1] = d;
		}

		/**
		 * @return
		 * 		The move duration.
         */
		private int getDuration(){
			return duration;
		}

		/**
		 * It counts the transition from one move mode to another.
		 */
		private void incrementCounter(){
			counter++;
			memory[2] = counter;
		}

		/**
		 * @return
		 * 		The number of transition already did.
         */
		private int getCounter(){
			return counter;
		}

		/**
		 * To pass to the pursuit mode.
		 */
		private void pursuit(){
			if((timer()/getDuration()) >= 1){
				npc.changeMove();
				incrementCounter();
				resetTimer();
				setPursuitDuration();
			}
		}

		/**
		 * To pass to the dispersion mode.
		 */
		private void dispersion(){
			if((timer()/getDuration()) >= 1){
				npc.changeMove();
				incrementCounter();
				resetTimer();
				setDispersionDuration();
			}
		}

		/**
		 * To set the dispersion move duration.
		 */
		private void setDispersionDuration(){
			if(getCounter() < 4)
				setDuration(7000);
			else
				setDuration(5000);
		}

		/**
		 * To set the pursuit move duration.
		 */
		private void setPursuitDuration(){
			setDuration(20000);
		}
	}

	/**
	 * An observer that will be notified when the level is won or lost.
	 * 
	 * @author Jeroen Roosen 
	 */
	public interface LevelObserver {

		/**
		 * The level has been won. Typically the level should be stopped when
		 * this event is received.
		 */
		void levelWon();

		/**
		 * The level has been lost. Typically the level should be stopped when
		 * this event is received.
		 */
		void levelLost();
	}
}
