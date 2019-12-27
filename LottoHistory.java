/**
@author Kevin Higgins
27/12/19
*/
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.text.*;

//Apart from the GUI building code which, like LottoMessageBox and LottoTicket takes a Container to build upon,
//I decided to make this  class so it doesn't need to call any other classes' methods to reduce dependencies
//therefore the constructor takes a variable and the update method takes an array variable
public class LottoHistory {
	private NumberFormat twoDecimalDigits = NumberFormat.getInstance();
	private String report;
	private int minimumMatches;	//BTW use his in final report
	private int gamesPlayed;	//kept apart from length of array, for con
	private int[][] history;	//lines played; lines with sufficient matches to deserve a prize; munz wun;
	private int HISTORY_COLUMNS_NEEDED = 3;	//as above comment, played; won; award
	private final int CHUNK_SIZE = 5;	//the amount of rows allocated at a time to the array

	public LottoHistory(int minimumMatchesConsideredAWin) {
		twoDecimalDigits.setMaximumFractionDigits(2);
		twoDecimalDigits.setMinimumFractionDigits(2);
		history = new int[CHUNK_SIZE][HISTORY_COLUMNS_NEEDED];
		minimumMatches = minimumMatchesConsideredAWin;
		gamesPlayed = 0;
		report = "";
	}

	public void updateWith(int[][] results) {
		history[gamesPlayed][0] = results.length;	//store amount of lines played
		int sumWinnings = 0;
		for (int i = 0; i < results.length; i++) {	//go through each line to check it we can call it a win (according to minimum matches)
			if (results[i][0] >= minimumMatches) history[gamesPlayed][1]++;	//calling it a win even if it wasn't awarded a prize due to duplicates or multiple wins in one ticket
			sumWinnings += results[i][1];
		}	
		history[gamesPlayed][2] = sumWinnings;
		gamesPlayed++;	
		if ((gamesPlayed % CHUNK_SIZE) == 0) expandArray();	//if the current chunk of game records is filled, expand array
	}

	private void expandArray() {
		int[][] expandedArray = new int[history.length + CHUNK_SIZE][HISTORY_COLUMNS_NEEDED];
		for (int i = 0; i < history.length; i++) {
			expandedArray[i] = history[i];	//copy over current history array into new expanded array
		}
		history = expandedArray;
	}

	public void makeFinalReport() {
		double avgWinnings = 0;		
		double sumWinnings = 0;
		report += "Counting more than " + minimumMatches + " match" + TextNiceties.pluralWithE(minimumMatches) + " per line as a win...\n";
		for (int i = 0; i < gamesPlayed; i++) {
			sumWinnings += history[i][2];
			int played = history[i][0];
			report += "In the " + (i + 1) + TextNiceties.postfix(i + 1) + " game, " + played + " line" + TextNiceties.plural(played) + " " + TextNiceties.wereOrWas(played) + " played, " + history[i][1] + " won, and \u20ac" + twoDecimalDigits.format(history[i][2]) + " was awarded.\n";
		}
		if (gamesPlayed > 0) avgWinnings = sumWinnings / gamesPlayed;
		report += "In all, " + gamesPlayed + " game" + TextNiceties.plural(gamesPlayed) + " were played and the average prize per game was \u20ac" + twoDecimalDigits.format(avgWinnings) + ".";
	}

	public void layReportOnto(Container frame, int margin) {
		GridBagLayout layout = (GridBagLayout)frame.getLayout();
		GridBagConstraints layoutRules = layout.getConstraints(frame);	//messy way of recovering the frame's layout manager

		JLabel reportLabel = new JLabel("Stats on all games played:");
		layoutRules.insets = new Insets(margin, margin, 23, margin);	//hard-coding pixels to maintain window height
		layoutRules.gridx = 0;
		layoutRules.gridy = 0;
		frame.add(reportLabel, layoutRules);

		JTextArea reportTextArea = new JTextArea(report, 13, 40);		//hard-coding rows to maintain window height
		Border textBorder = BorderFactory.createEtchedBorder();
		JScrollPane reportScrollPane = new JScrollPane(reportTextArea);
		reportTextArea.setEditable(false);
		reportScrollPane.setBorder(BorderFactory.createCompoundBorder(reportScrollPane.getBorder(), BorderFactory.createMatteBorder(4, 8, 4, 8, Color.white)));
		layoutRules.insets = new Insets(0, margin, margin, margin);
		layoutRules.gridx = 0;
		layoutRules.gridy = 1;
		layoutRules.gridwidth = 6;
		//add the scrolling text area to the Container and refresh the window
		frame.add(reportScrollPane, layoutRules);
	}
}