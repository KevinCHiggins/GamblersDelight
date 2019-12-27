/**
@author Kevin Higgins
27/12/19
*/
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.event.TextListener;
import java.util.Vector;
import java.util.EventObject;

public class LottoLine implements DocumentListener {
	protected Vector<LottoLineListener> listeners;
	private int state;	//0 unactivated (disabled) 1 activated 2 committed (validated). Can go between 1 and 2 but not back to 0
	private LottoField[] fields;
	private LottoInputVerifier validator;

	public LottoLine(int length, LottoRules byTheRules, FocusListener listeningTicket, int index) {	
		listeners = new Vector<LottoLineListener>();
		validator = new LottoInputVerifier(this, byTheRules);	//construct a LottoInputVerifier that knows which line to work on!
		fields = new LottoField[length];
		if (index == 0) state = 1; else state = 0; //first line starts activated, rest start unactivated
		for (int i = 0; i < length; i++) {
			fields[i] = new LottoField(4, index);
			if (index == 0) fields[i].setEnabled(true); else fields[i].setEnabled(false);
			fields[i].getDocument().addDocumentListener(this);
			fields[i].addFocusListener(listeningTicket);
			fields[i].setInputVerifier(validator);
		}
	}

	public class LottoField extends JTextField {
		private int lineIndex;
		@Override
		public Dimension getPreferredSize() {	//to stop GridBagLayout from shifting edges slightly when numbers are input
			return (new Dimension(50, 20));
		}
		public LottoField(int size, int lineIndex) {
			super(size);
			this.lineIndex = lineIndex;
		}
		public int getLineIndex() {
			return lineIndex;
		}
	}

	public boolean isValid() {
		return validator.lineValidates();
	}

	public void passFocus() {
		fields[0].requestFocus();
	}

	public void activate() {
		for (int i = 0; i < fields.length; i++) {
			fields[i].setEnabled(true);
		}
		state = 1;
	}

	public int getState() {
		return state;
	}

	public void addLottoLineListener(LottoLineListener listener) {	//this is really overkill, my system is built on there being one only
		listeners.addElement(listener);
	}

	public void removeLottoLineListener(LottoLineListener listener) {
		listeners.removeElement(listener);
	}

	private void fireLineCommittalChanged(EventObject eo) {
		for (int i = 0; i < listeners.size(); i++) {
			((LottoLineListener)listeners.elementAt(i)).lineCommittalChanged(eo);	//tricky! you can retrieve an object's methods
		}																			//from the Vector but only by careful casting 
	}																				//with the right parentheses

	public void fillWith(int[] pickedNumbers) {
		for (int i = 0; i < fields.length; i++) {	//could do with checks on array lengths!
			String number = "" + pickedNumbers[i];		//to make it a string not an int
			fields[i].setText(number);
		}
	}

	public void buildFieldsOnTicket(Container frame, int lineIndex, int bigInset, int smallInset) {
		GridBagConstraints layoutRules = new GridBagConstraints();	//could be tidied out
		int leftInset = 0;
		int rightInset = 0;
		int topInset = 0;
		int bottomInset = 0;

		for (int i = 0; i < fields.length; i++) {
			layoutRules.fill = GridBagConstraints.HORIZONTAL;
			layoutRules.gridx = i;
			layoutRules.gridy = lineIndex;
			layoutRules.weightx = .1;
			layoutRules.weighty = 0.50;

			if (lineIndex == 0) topInset = bigInset; else topInset = smallInset;
			rightInset = leftInset = smallInset;
			if (i == 0) {

				leftInset = bigInset;
			}
			else if (i == (fields.length - 1)) {
				rightInset = bigInset;
			}
			layoutRules.insets = new Insets(topInset, leftInset, 0, rightInset);
			layoutRules.anchor = GridBagConstraints.CENTER;
			frame.add(fields[i], layoutRules);
		}
	}

	public void clearFieldsFrom(Container frame) {
		for (int i = 0; i < fields.length; i++) {
			frame.remove(fields[i]);
		}
	}

	public int[] getNumbersPlayed() {
		String[] fieldTexts = getAllTexts();
		int[] played = new int[fields.length];
		for (int i = 0; i < fields.length; i++) {
			try {
				played[i] = Integer.valueOf(fieldTexts[i]);
			}
			catch (NumberFormatException nfe) { played[i] = 0; }	//this needs to be beefed up into a better check but will do for now
		}
		return played;
	}

	public String getTextAt(int index) {
		return fields[index].getText();
	}

	public String[] getAllTexts() {
		String[] texts = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			texts[i] = fields[i].getText();
		}
		return texts;
	}

	public String[] getTextsFromAllBut(JTextField unwantedField) {
		String[] texts = new String[fields.length - 1];	//one shorter than fields[] because it won't contain unwantedField's text
		int textsIncrementer = 0;	//a separate counter which will not be incremented when skipping over unwantedField
		for (int i = 0; i < fields.length; i++) {
			if (unwantedField != fields[i]) {
				texts[textsIncrementer++] = getTextAt(i);	//for the sake of it reuse of my function, heheh
			}
		}
		return texts;
	}

	private void evaluateState() {
		//note state == 0 isn't dealt with here - LottoTicket calls the activate() method to get a line out of it into state 1
		if (state == 1) {
			if (validator.lineValidates()) {
				state = 2;
				fireLineCommittalChanged(new EventObject(this));
			}
		}
		else if (state == 2) {
			if (!validator.lineValidates()) {
				state = 1;
				fireLineCommittalChanged(new EventObject(this));
			}
		}
	}

	public void changedUpdate(DocumentEvent de) {
		evaluateState();
	}

	public void insertUpdate(DocumentEvent de) {
		evaluateState();
	}

	public void removeUpdate(DocumentEvent de) {
		evaluateState();
	}
}