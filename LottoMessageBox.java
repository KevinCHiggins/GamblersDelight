/**
@author Kevin Higgins
27/12/19
This class is a subclass of Swing's JTextArea, with some GUI specifics added
*/
import javax.swing.JFrame;
import java.awt.Color;
import javax.swing.BorderFactory; 
import javax.swing.border.*;
import javax.swing.JTextArea;
import java.awt.Container;

import java.awt.*;
public class LottoMessageBox extends JTextArea {

	public LottoMessageBox (String text) {
	super (text, 5, 40);
	setEditable(false);
	Border textBorder = BorderFactory.createEtchedBorder();
	setBorder(textBorder);
	setBorder(BorderFactory.createCompoundBorder(this.getBorder(), BorderFactory.createMatteBorder(4, 8, 4, 8, Color.white)));
	}

	public void layOnto(Container frame, int margin) {
	GridBagLayout layout = (GridBagLayout)frame.getLayout();
	GridBagConstraints layoutRules = layout.getConstraints(frame);
	layoutRules.insets = new Insets(margin, margin, margin, margin);	
	layoutRules.gridx = 0;
	layoutRules.gridy = 5; //hardcoded
	layoutRules.gridwidth = 6;
	frame.add(this, layoutRules);
	}

	public void clearFrom(Container frame) {
		frame.remove(this);
	}
}