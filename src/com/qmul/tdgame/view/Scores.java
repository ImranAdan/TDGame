package com.qmul.tdgame.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import com.qmul.tdgame.R;
import com.qmul.tdgame.util.GameResources;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * The activity that represents the high scores leader board. 
 * @author Imran Adan
 *
 */
public class Scores extends Activity {

	private static final String TAG = Scores.class.getSimpleName();
	private static final String FILENAME = "scores.txt";
	private static final float FONT_SIZE = 50;

	private static File SAVE_FILE;
	private static boolean configured = false;
	private static Scores instance;
	private TableLayout scoresTable;
	private float fontSize;
	private SoundPlayer soundPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_scores);
		initComponents();
		displayScores();
	}

	/**
	 * Initialise all required components, this includes
	 * all the required files.
	 */
	private void initComponents() {
		SAVE_FILE = new File(getFilesDir(), FILENAME);
		GameResources.currentContext = this;
		soundPlayer = SoundPlayer.getInstance();
		instance = this;
		scoresTable = (TableLayout) findViewById(R.id.scoresTable);
	}

	/**
	 * Display all the players scores in a table.
	 */
	private void displayScores() {
		List<String> highScores = load();
		TableRow[] rows = new TableRow[highScores.size()];
		
		for (int i = 0; i < rows.length; i++) {
			rows[i] = new TableRow(getApplicationContext());
			TextView v1 = new TextView(getApplicationContext());
			if (i == 0)
				v1.setText("\t" + highScores.get(i));
			else
				v1.setText(i + "- \t" + highScores.get(i));
			v1.setTextColor(Color.BLACK);
			v1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50);
			rows[i].addView(v1);
			scoresTable.addView(rows[i]);
		}
	}

	/**
	 * Return a formatted string of the player name and score.
	 * @param name
	 * @param score
	 * @return The formatted string of the player and thier associated scores.
	 */
	public static String formattedString(String name, String score) {
		return name + "\t\t\t\t" + score;
	}

	private List<String> load() {
		LinkedList<String> lines = new LinkedList<String>();
		List<String> names = new LinkedList<String>();
		List<Integer> scores = new LinkedList<Integer>();

		try {
			Scanner s = new Scanner(new File(getFilesDir(), FILENAME));
			int index = 0;
			while (s.hasNext()) {
				String line = s.nextLine();
				for (String str : line.split("="))
					Log.d(TAG, str);

				names.add(line.split("=")[0]);
				scores.add((Integer.parseInt(line.split("=")[1])));
			}

			lines = sort(names, scores);
			if (!lines.contains(formattedString("Name", "Score")))
				lines.addFirst(formattedString("Name", "Score"));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return lines;
	}
	

	/**
	 * Sort the list of names and scores using bubble sort.
	 * @param names
	 * @param scores
	 * @return
	 */
	private LinkedList<String> sort(List<String> names, List<Integer> scores) {
		LinkedList<String> sortedScores = new LinkedList<String>();
		for (int i = 0; i < names.size(); i++) {
			for (int j = 0; j < names.size() - 1; j++) {
				if (scores.get(j) < scores.get(j + 1)) {
					String temp = names.get(j);
					names.set(j, names.get(j + 1));
					names.set(j + 1, temp);

					int temp1 = scores.get(j);
					scores.set(j, scores.get(j + 1));
					scores.set(j + 1, temp1);
				}
			}
		}

		for (int i = 0; i < names.size(); i++)
			sortedScores.add(formattedString(names.get(i),
					Integer.toString(scores.get(i))));

		return sortedScores;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		soundPlayer.play();		
	}

	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onPause() {
		soundPlayer.pause();
		super.onPause();

	}
	
	public void onBackPressed(){
		this.finish();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
}
