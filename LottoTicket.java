/**
@author Kevin Higgins
27/12/19
*/
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.EventObject;
import java.awt.event.TextListener;
import java.text.*;

public class LottoTicket extends FocusAdapter implements LottoLineListener {
	final int SMALL_INSET = 10;
	final int BIG_INSET = 30;
	NumberFormat twoDecimalDigits;
	LottoRules byTheRules;
	LottoLine[] lines;
	int [][] results;
	int lineLength;
	int linesTally;	
	int amountOfLines;
 	JButton pickButton;
 	JButton playButton;
	JButton stopButton;
	JPanel linesPanel;
	JPanel buttonsPanel;
	int focusedLineIndex;
	int countOfLinesCommitted;
	String report = "";
	JTextArea textBox;

	public LottoTicket(ActionListener listeningTicket, LottoRules byTheRules) {	//this constructor needs safeguards against dodgy parameters
		pickButton = new JButton("Pick for me");
		playButton = new JButton("Play");
		stopButton = new JButton("Stop playing");
		playButton.addActionListener(listeningTicket);
		stopButton.addActionListener(listeningTicket);
		pickButton.addActionListener(listeningTicket);
		twoDecimalDigits = NumberFormat.getInstance();

		twoDecimalDigits.setMaximumFractionDigits(2);
		twoDecimalDigits.setMinimumFractionDigits(2);
		this.byTheRules = byTheRules;		//save the rules object for use in pick and play methods
		lineLength = byTheRules.getLineLength();
		amountOfLines = byTheRules.getAmountOfLines();
		focusedLineIndex = 0;
		lines = new LottoLine[amountOfLines];
		for (int i = 0; i < amountOfLines; i++) {	//a loop to do everything needed to set up the lines
			/* now call the constructor for each line, giving the desired length of line & spacing,
			 ticket is sent to be added as FocusListener to lotto fields' focus events,
			and finally the line is informed of its position, */
			lines[i] = new LottoLine(lineLength, byTheRules, this, i);			
			lines[i].addLottoLineListener(this);
		}
	}

	public void lineCommittalChanged(EventObject eo) {
		int sourceIndex = findIndexOf(eo.getSource());
		updateLinesCommitted(sourceIndex);
	}

	public void focusGained(FocusEvent fe) {
		int index = ((LottoLine.LottoField)fe.getSource()).getLineIndex();	//getting our LottoLine.LottoField inner class's line ID
		focusedLineIndex = index;
	}

	public void updateLinesCommitted(int indexOfLineChanged) {
		countOfLinesCommitted = 0;
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].getState() == 2) countOfLinesCommitted++;
			if (i == (indexOfLineChanged + 1) && lines[i].getState() == 0) lines[i].activate();	//safe way (can't go beyond array bounds)
																								//of activating disabled lines 1-by-1
			
		}
		//update text and status of play button
		linesTally = countOfLinesCommitted;
		if (linesTally < 1) playButton.setEnabled(false); else playButton.setEnabled(true);
		playButton.setText("Play " + linesTally + " line" + TextNiceties.plural(linesTally));
	}

	public int findIndexOf(Object line) {
		for (int i = 0; i < lines.length; i++) {
			if (line == lines[i]) return i;
		}
		return -1;
	}

	public void pick() {	
		byTheRules.drawLotteryNumbers();
		int[] drawn = byTheRules.getLotteryNumbers();
		lines[focusedLineIndex].fillWith(drawn);
		//updateLinesCommitted(focusedLineIndex);	//needs to be called because the usual DocumentEvents may not have happened?
	}

	public void advanceFocus() {	//quick version for testing... ideally should check for next *invalid line*
		if (focusedLineIndex < (amountOfLines-1)) {

			lines[focusedLineIndex + 1].passFocus();
		}
		else {
			lines[0].passFocus();
		}
	}

	public void play() {
		LottoLine[] playedLines = new LottoLine[countOfLinesCommitted];
		int increment = 0; //separate incrementer to creep up through playedLines independently of i scanning through lines
		for (int i = 0; i < countOfLinesCommitted; i++) { //we rely on the countOfLinesCommitted being an accurate counter
			if (lines[i].getState() == 2) playedLines[increment++] = lines[i];
		}
		byTheRules.drawLotteryNumbers();
		int[] drawn = byTheRules.getLotteryNumbers();
		results = byTheRules.getResults(playedLines);
		makeReport(results, drawn);
	} 

	private void makeReport(int[][] results, int[] drawn) {
		boolean win = false;
		report += "The numbers drawn were ";
		for (int j = 0; j < drawn.length; j++) {
			report += drawn[j];
			if (j < (drawn.length - 1)) report += ", ";
		}
		report += ".\n";
		for (int i = 0; i < results.length; i++) {	//THIS MAY NOT MATCH THE ORIGINAL LINE NUMBER... but no harm. I made the text vague to fit.
			if (results[i][0] == lineLength) {
				report += "The " + (i + 1) + TextNiceties.postfix(i + 1) + " line played won the jackpot!!!";
				win = true;
				break;
			}
			else {
				report += "The " + (i + 1) + TextNiceties.postfix(i + 1) + " line played had " + results[i][0] + " match" + TextNiceties.pluralWithE(results[i][0]) + " earning \u20ac" + twoDecimalDigits.format(results[i][1]) + ".";
				if (results[i][1] > 0) { report += " Congratulations!\n"; win = true; } else { report += "\n";}	//congratulate prize win
			}
		}
		if (!win) {
			report += "Better luck next time!";
		}
	}

	public String getReport() {
		return report;
	}

	public int[][] getResults() {
		int[][] copyOfResults = new int[results.length][results[0].length];	//assume rectangular array
		for (int i = 0; i < results.length; i++) {			//make copy of array for better encapsulation
			for (int j = 0; j < results[0].length; j++) {	//so the asking class can't modify this class' private variable
				copyOfResults[i][j] = results[i][j];
			}
		}
		return copyOfResults;
	}

	public void layComponentsOnto(Container frame) {
		int amountOfLines = lines.length;
		for (int i = 0; i < amountOfLines; i++) {
			lines[i].buildFieldsOnTicket(frame, i, BIG_INSET, SMALL_INSET);
		}

		GridBagLayout layout = (GridBagLayout)frame.getLayout();
		GridBagConstraints layoutRules = layout.getConstraints(frame);	//messy way of recovering the frame's layout manager
		int topInset, rightInset, bottomInset, leftInset;
		layoutRules.gridx = 0;	//this is hardcoded to line up with six numbers per line... would be nicer (more general) if it adapted
		layoutRules.fill = GridBagConstraints.HORIZONTAL;
		layoutRules.gridy = amountOfLines + 1;
		layoutRules.gridwidth = 2;
		layoutRules.weightx = 0;
		topInset = bottomInset = BIG_INSET;
		rightInset = SMALL_INSET;
		leftInset = BIG_INSET;
		layoutRules.insets = new Insets(topInset, leftInset, 0, rightInset);
		frame.add(pickButton, layoutRules);

		rightInset = SMALL_INSET;
		leftInset = SMALL_INSET;
		layoutRules.gridx = 2;
		layoutRules.insets = new Insets(topInset, leftInset, 0, rightInset);
		playButton.setEnabled(false);
		frame.add(playButton, layoutRules);

		rightInset = BIG_INSET;
		leftInset = SMALL_INSET;
		layoutRules.gridx = 4;	//hardcoded
		//layoutRules.gridwidth = 2;	
		layoutRules.insets = new Insets(topInset, leftInset, 0, rightInset);
		frame.add(stopButton, layoutRules);
		lines[0].passFocus();	//quick fix to put focus in the first field for all rounds after the first
		}

		public void clearComponentsFrom(Container frame) {
			frame.remove(pickButton);
			frame.remove(playButton);
			frame.remove(stopButton);
			for (int i = 0; i < lines.length; i++) {
				lines[i].clearFieldsFrom(frame);
			}
		}
}