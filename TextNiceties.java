/**
@author Kevin Higgins
27/12/19
*/
public class TextNiceties {
	static String[] countingPostfixes = {"st", "nd", "rd", "th"};	//assuming "zeroth" is not a word

	public static String postfix(int howMany) {
		if (howMany < 20 && howMany > 9) {
			return countingPostfixes[3];
		}
		else if (howMany < 1) {
			return countingPostfixes[3];
		}
		else {

			howMany = (howMany - 1) % 10;	//adjust for zero-based indexing before modulo - doing it after means multiples of 10 indexed -1
			if (howMany > 3) howMany = 3
				;
			return countingPostfixes[howMany];
		}
	}

	public static String wereOrWas(int howMany) {
		if (howMany > 1 || howMany < 1) return "were"; else return "was";
	}

	public static String plural(int howMany) {
		if (howMany > 1 || howMany < 1) return "s"; else return "";
	}

	public static String pluralWithE(int howMany) {
		if (howMany > 1 || howMany < 1) return "es"; else return "";
	}
}