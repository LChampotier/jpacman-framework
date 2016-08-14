package nl.tudelft.jpacman.ui;

import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.level.Player;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A panel consisting of a column for each player, with the numbered players on
 * top and their respective scores underneath.
 * 
 * @author Jeroen Roosen 
 * 
 */
public class ScorePanel extends JPanel {

	/**
	 * Default serialisation ID.
	 */
	private static final long serialVersionUID = 1L;

	/** The frame parent of this panel. */
	private JFrame parent;

	/** The message to display in the event of loss or victory. */
	private ScoreDialog sd;

	/**
	 * The map of players and the labels their scores are on.
	 */
	private final Map<Player, JLabel> scoreLabels;
	
	/**
	 * The default way in which the score is shown.
	 */
	public static final ScoreFormatter DEFAULT_SCORE_FORMATTER = 
			// this lambda breaks cobertura 2.7 ...
			// player) -> String.format("Score: %3d", player.getScore());
			new ScoreFormatter() {
				public String format(Player p) {
					return String.format("%2d", p.getScore());
				}
			};
	
	/**
	 * The way to format the score information.
	 */
	private ScoreFormatter scoreFormatter = DEFAULT_SCORE_FORMATTER;

	/** The game to play. */
	private final Game game;

	/** Time of the game. */
	private JLabel time;

	/** Game chronometer. */
	private GameChrono chrono;

	/**
	 * Creates a new score panel with a column for each player.
	 * 
	 * @param game
	 *            The game which we want to show the score of the players.
	 */
	public ScorePanel(final JFrame parent, Game game) {
		super();
		assert game != null;

		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		this.parent = parent;
		JLabel lbl = new JLabel("Time : ", JLabel.RIGHT);
		this.time = new JLabel("0", JLabel.LEFT);
		this.chrono = new GameChrono(time.getText());
		this.sd = new ScoreDialog(parent);
		this.game = game;

		add(lbl);
		add(time);
		add(Box.createHorizontalGlue());
		add(Box.createRigidArea(new Dimension(3,0)));

		int i = 1;

		scoreLabels = new LinkedHashMap<>();
		for (Player p : game.getPlayers()) {
			add(new JLabel("Player " + i + " : ", JLabel.LEFT));

			JLabel scoreLabel = new JLabel("0", JLabel.LEFT);

			scoreLabels.put(p, scoreLabel);
			add(scoreLabel);
			add(Box.createRigidArea(new Dimension(7,0)));

			i++;
		}
		int w = getPreferredSize().width, h = getPreferredSize().height;

		setPreferredSize(new Dimension(w, h+7));
		chrono.start();
	}

	/** Enables to know the number of remaining pellets */
	private int remainingPellets(){ return game.getLevel().remainingPellets(); }

	/**
	 * Refreshes the scores of the players.
	 */
	protected void refresh() {
		for (Player p : scoreLabels.keySet()) {
			String score = "";

			if (!p.isAlive()) message("Lost game", p.getScore());

			score += scoreFormatter.format(p);

			scoreLabels.get(p).setText(score);
			time.setText(chrono.getTime());

			if(remainingPellets() == 0) message("Won game", p.getScore());
		}
	}

	/**
	 * Displays the appropriate message.
	 *
	 * @param msg
	 * 			The message to display.
	 *
	 * @param score
	 * 			The player score.
     */
	private void message(String msg, int score){
		sd.createScoreDialog(msg, chrono.getTime(), String.valueOf(score));
		sd.pack();
		sd.setLocationRelativeTo(parent);
		sd.setVisible(true);
	}
	
	/**
	 * Provide means to format the score for a given player.
	 */
	public interface ScoreFormatter {
		
		/**
		 * Format the score of a given player.
		 * @param p The player and its score
		 * @return Formatted score.
		 */
		String format(Player p);
	}
	
	/**
	 * Let the score panel use a dedicated score formatter.
	 * @param sf Score formatter to be used.
	 */
	public void setScoreFormatter(ScoreFormatter sf) {
		assert sf != null;
		scoreFormatter = sf;
	}

	private class GameChrono extends Thread{
		private int t;

		public GameChrono(String time){
			this.t = Integer.parseInt(time);
		}

		@Override
		public void run(){ while(count()) chrono(); }

		private boolean count(){
			boolean count = false;

			for(Player p : scoreLabels.keySet())
				if(p.isAlive()){
					count = true;

					break;
				}

			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return count;
		}

		private void chrono(){ if(game.hasStarted()) t++; }

		public String getTime(){ return String.valueOf(t); }
	}
}
