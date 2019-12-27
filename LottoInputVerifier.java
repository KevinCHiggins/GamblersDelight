/**
@author Kevin Higgins
27/12/19
*/
import javax.swing.*;

public class LottoInputVerifier extends InputVerifier {
	LottoLine lineToWorkOn;
	LottoRules byTheRules;
	//every LottoInputVerifier has an associated LottoLine to itself
	//because I needed a method to validate an entire line 
	//and more to the point because uniqueness tests span an entire line
	public LottoInputVerifier(LottoLine lineToWorkOn, LottoRules byTheRules) {	
		this.lineToWorkOn = lineToWorkOn;	
		this.byTheRules = byTheRules;			
	}													

	private boolean verifyString (String input) {	//I'm splitting up functionality like this so that lineValidates() can
		boolean isValid = false;					//reuse this check (and do so using Strings rather than the less
		int bottomOfRangeInclusive = byTheRules.getNumbersRange()[0];
		int topOfRangeInclusive = byTheRules.getNumbersRange()[1];
		try {										//secure option of handing around the actual JTextFields)
			int currentVal = Integer.valueOf(input); //messy parsing of field's value to an integer
			if (currentVal <= topOfRangeInclusive && currentVal >= bottomOfRangeInclusive) {
				isValid = true;
			}
		}
		//catch the exception if it can't be parsed as an int
		catch (NumberFormatException exception) { //no need to do anything as isValid will be false unless it's an int between 1-40 inclusive
		}
		return isValid;
	}

	@Override
	public boolean verify(JComponent input) {
		JTextField currentField = (JTextField) input;
		return verifyString (currentField.getText());
	}

	@Override
	public boolean shouldYieldFocus(JComponent source, JComponent target) {
		int thisNumber, otherNumber;

		JTextField sourceField = (JTextField)source;
		if (sourceField.getText().length() == 0) return true;	//messy test... let the user navigate away from an empty panel
		if (!verify(source)) { //first things first, no need to check anything more if invalid
			JOptionPane.showMessageDialog(null, "Please enter a whole number between 1-40 inclusive.", "Invalid input", JOptionPane.ERROR_MESSAGE);
			return false;	//don't yield focus
		}

		try {	//If we got here, number in source is valid, so check for uniqueness against other locations in line
			thisNumber = Integer.valueOf(sourceField.getText());	//by grabbing the integer value of this field...
		}
		catch (NumberFormatException nfe) {	//(this should never happen as an invalid val
			thisNumber=0;					//would be caught by verify(), however we assign -
		} 									//to avoid not initialised error)

		boolean isUnique = true;	//true unless proven not to be below
		String[] otherTextsInLine = lineToWorkOn.getTextsFromAllBut((JTextField) source);
		for (String otherText: otherTextsInLine) {	//for every other field located on this line
		
			try {
				otherNumber = Integer.valueOf(otherText);
			}
			catch (NumberFormatException nfe) { //if it's an empty string
				otherNumber = 0;				//we can make it zero w/o triggering uniqueness popup
			}									//because the case of the user entering zero in
												//the current field has been checked by verify()
			if (thisNumber == otherNumber) {	//... FINALLY we get to the comparison
				JOptionPane.showMessageDialog(null, "This number has already been entered for this line.", "Invalid input", JOptionPane.ERROR_MESSAGE);
				return false; //don't yield focus!
			}	
				
		}
		return true; //it's okay, number is valid and unique, yield focus
	}
	
	public boolean lineValidates () {
		String[] texts = lineToWorkOn.getAllTexts();
		boolean isValid = true;
		for (int i = 0; i < texts.length; i++) {
			if (!verifyString(texts[i])) isValid = false;
		}
		return isValid;
	}
}