package nl.tudelft.jpacman.ui;

import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.ui.ScorePanel.ScoreFormatter;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The default JPacMan UI frame. The PacManUI consists of the following
 * elements:
 * 
 * <ul>
 * <li>A score panel at the top, displaying the score of the player(s).
 * <li>A board panel, displaying the current level, i.e. the board and all units
 * on it.
 * <li>A button panel, containing all buttons provided upon creation.
 * </ul>
 * 
 * @author Jeroen Roosen 
 * 
 */
public class PacManUI extends JFrame {

	/**
	 * Default serialisation UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The desired frame rate interval for the graphics in milliseconds, 40
	 * being 25 fps.
	 */
	private static final int FRAME_INTERVAL = 40;

	private final static String GAME_MODE_PANEL = "GameMode";
	private final static String GAME_PANEL = "Game";

	/**
	 * The panel displaying the player scores.
	 */
	private final ScorePanel scorePanel;

	/**
	 * The panel displaying the game.
	 */
	private final BoardPanel boardPanel;

	/** The panel to show ghosts informations. */
	private final GhostPanel ghostPanel;

	/** The game engine. */
	private ScheduledExecutorService service;

	/**
	 * Creates a new UI for a JPac-Man game.
	 * 
	 * @param game
	 *            The game to play.
	 * @param buttons
	 *            The map of caption-to-action entries that will appear as
	 *            buttons on the interface.
	 * @param keyMappings
	 *            The map of keyCode-to-action entries that will be added as key
	 *            listeners to the interface.
	 * @param sf
	 *            The formatter used to display the current score. 
	 */
	public PacManUI(final Game game, final Map<String, Action> buttons,
					final Map<Integer, Action> keyMappings, ScoreFormatter sf,
					boolean ok) {
		super("JPac-Man");
		assert game != null;
		assert buttons != null;
		assert keyMappings != null;
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		PacKeyListener keys = new PacKeyListener(keyMappings);
		addKeyListener(keys);

		JMenuBar menuBar = new MenuBar(this).createMenuBar();
		this.boardPanel = new BoardPanel(game);
		JPanel buttonPanel = new ButtonPanel(buttons, this);
		this.ghostPanel = new GhostPanel(game.getGhosts());
		this.scorePanel = new ScorePanel(this, game);

		if (sf != null)	scorePanel.setScoreFormatter(sf);

		JPanel cards = new JPanel(new CardLayout());
		JPanel gamePanel = new JPanel();
		JPanel gameModePanel = new GameModePanel(this, menuBar, cards, GAME_PANEL);
		JPanel panels = new JPanel(new BorderLayout());

		gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.PAGE_AXIS));
		gamePanel.add(boardPanel);
		gamePanel.add(Box.createRigidArea(new Dimension(0,3)));
		gamePanel.add(buttonPanel);

		panels.add(gamePanel, BorderLayout.CENTER);
		panels.add(ghostPanel, BorderLayout.EAST);
		panels.add(scorePanel, BorderLayout.SOUTH);

		cards.add(GAME_MODE_PANEL, gameModePanel);
		cards.add(GAME_PANEL, panels);

		Container content = getContentPane();

		content.setLayout(new BorderLayout());
		content.add(cards, BorderLayout.CENTER);

		if(ok) ((CardLayout)cards.getLayout()).show(cards, GAME_PANEL);

		int w = getPreferredSize().width, h = ghostPanel.getPreferredSize().height + 67;
		Dimension size = new Dimension(w, h);

		setMinimumSize(size);
		setPreferredSize(size);
		pack();
	}

	/**
	 * Starts the "engine", the thread that redraws the interface at set
	 * intervals.
	 */
	public void start() {
		setVisible(true);

		this.service = Executors.newSingleThreadScheduledExecutor();

		service.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				nextFrame();
			}
		}, 0, FRAME_INTERVAL, TimeUnit.MILLISECONDS);

	}

	public void stop(){ service.shutdownNow(); }

	/**
	 * Draws the next frame, i.e. refreshes the scores and game.
	 */
	private void nextFrame() {
		boardPanel.repaint();
		scorePanel.refresh();
		ghostPanel.refresh();
	}
}
