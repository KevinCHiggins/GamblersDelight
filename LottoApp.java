/**
@author Kevin Higgins
v. 1.0 coded between 11/12/19 and 27/12/19
A lottery game app using the LottoMessageBox, LottoTicket, LottoLine, LottoRules,
LottoInputVerifier, LottoHistory and TextNiceties classes. Inspired by a school
project brief and using techniques from John P. Russell's Java Programming For
The Absolute Beginner and the Oracle Swing tutorials, but all my own work.
*/
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class LottoApp implements ActionListener {
	private LottoRules byTheRules;
	private LottoHistory history;
	private LottoTicket ticket;
	private LottoMessageBox messageBox;
	private GridBagLayout layout;
	private JFrame frame;
	private Container pane;
	private int BIG_INSET = 30;

	public LottoApp() {
		byTheRules = new LottoRules();
		history = new LottoHistory(byTheRules.getMinimumMatchesAwarded());
		frame = new JFrame("Gambler's Delight");
		pane = frame.getContentPane();
		layout = new GridBagLayout();
		frame.setLayout(layout);
		messageBox = new LottoMessageBox("Welcome to the Gambler's Delight!\n\nPlay lines of lottery numbers between " + byTheRules.getNumbersRange()[0] + " and " + byTheRules.getNumbersRange()[1] + " inclusive.\nAny number can only be played once per line.\nSelect \'Stop Playing\' to see a history of all games played.");
		//app class sent as ActionListener for buttons, rules rules needed for LottoLine's creation of LottoInputVerifier, & line length
		ticket = new LottoTicket(this, byTheRules);	//app class sent as ActionListener for buttons
		ticket.layComponentsOnto(pane);
		messageBox.layOnto(frame.getContentPane(), BIG_INSET);
		showWindow();
	}

	private void processResults() {
		history.updateWith(ticket.getResults());
	}
	
	private void newRound() {	
		String newMessage = ticket.getReport(); //before we get rid of this ticket object, get the report
		ticket.clearComponentsFrom(pane);
		messageBox.clearFrom(pane);
		messageBox = new LottoMessageBox(newMessage);
		//app class sent as ActionListener for buttons, rules needed for LottoLine's creation of LottoInputVerifier, & line length
		ticket = new LottoTicket(this, byTheRules);	
		ticket.layComponentsOnto(pane);
		messageBox.layOnto(pane, BIG_INSET);
		frame.pack();
	}

	private void showWindow() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	private void end() {
		history.makeFinalReport();
		ticket.clearComponentsFrom(pane);
		messageBox.clearFrom(pane);
		history.layReportOnto(pane, BIG_INSET);
		frame.pack();
	}

	public void actionPerformed(ActionEvent ae) {
		String selectedAction = new StringBuffer(ae.getActionCommand()).substring(0, 4); //Get the first four chars of button label
		switch (selectedAction) {
			case "Pick":	ticket.pick(); ticket.advanceFocus(); break;
			case "Play":	ticket.play(); processResults(); newRound(); break;
			case "Stop":	end();
		}
	}

	public static void main(String[] args) {
		LottoApp instance = new LottoApp();
	}
}