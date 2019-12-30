/**
@author Kevin Higgins
27/12/19
This class uses some defaults to set up variables determining the numeric bounds of the 
lotto game. It also can generate ("draw") 6 random unique lotto numbers (within the
appropriate bounds.) This function is intended to be called by LottoTicket to play the lotto, and
also by LottoTicket for the quick pick button.
Random numbers and game variables are all made accessible with getters.
*/
import java.util.*;
//TO DO
//getResults() is fragile and needs: safety against non-uniqueness, maybe a better call to LottoLine's method

public class LottoRules {
	private int lineLength, lineAmount, rangeBottomInclusive, rangeTopInclusive;
	private final int DEFAULT_LENGTH = 6;
	private final int DEFAULT_AMOUNT = 3;
	private final int DEFAULT_RANGE_BOTTOM = 1;
	private final int DEFAULT_RANGE_TOP = 40;
	private final int[] DEFAULT_AWARDS = new int[] {0, 0, 0, 125, 300, 1500, 1000000}; //prizes for 0, 1, 2, 3, 4, 5, and 6 matches
	private final int[] awards;
	private int[] generated;
	private Random generator;

	public LottoRules() {
		lineLength = DEFAULT_LENGTH;
		lineAmount = DEFAULT_AMOUNT;
		rangeBottomInclusive = DEFAULT_RANGE_BOTTOM;
		rangeTopInclusive = DEFAULT_RANGE_TOP;
		awards = DEFAULT_AWARDS;
		generator = new Random(new Date().getTime());
	}

	public int getLineLength() {
		return lineLength;
	}

	public int getAmountOfLines() {
		return lineAmount;
	}

	public int[] getNumbersRange() {	//
		return new int[] {rangeBottomInclusive, rangeTopInclusive};
	}

	public int getMinimumMatchesAwarded() {
		int i;
		for (i = 0; i < awards.length; i++) {
			if (awards[i] > 0) return i;
		}	
		return i + 1;	//if for some reason the array of prizes is all zeros we send back an unattainable number of matches
	}

	public void drawLotteryNumbers() {
		generated = new int[lineLength];
		boolean isUnique;
		int randomNumber;
		int range = rangeTopInclusive - rangeBottomInclusive + 1;
		for (int i = 0; i < lineLength; i++) {
			int testDo = 0;
			do {
				isUnique = true;
				randomNumber = generator.nextInt(range) + rangeBottomInclusive;
				for (int j = 0; j < i; j++) {
					if (randomNumber == generated[j]) isUnique = false;
				}
			} while (!isUnique);
			generated[i] = randomNumber;
		}
	}

	public int[] getLotteryNumbers() {
		int[] copyOfGenerated = new int[generated.length];
		for (int i = 0; i < generated.length; i++) {
			copyOfGenerated[i] = generated[i];
		}
		return copyOfGenerated;
	}

	public int[][] getResults(LottoLine[] linesPlayed) {	//first dimension is line, next differentiates 0: matches; 1: winnings; 2: expected winnings
															//i.e. in case of duplicate lines or multiple awards (these don't gain any cash)
		int[][] results = new int[linesPlayed.length][3];	
		int[] numbersPlayed;
		int mostMatchesSoFar = 0;
		int indexWithMostMatchesSoFar = 0;
		int lineResult;
		//do it without duplicate/multiple test first
		for (int i = 0; i < linesPlayed.length; i++) {
			numbersPlayed = linesPlayed[i].getNumbersPlayed();	//this needs some checks... there aren't always numbers played!
			lineResult = 0;
			for (int j = 0; j < lineLength; j++) {
				for (int k = 0; k < lineLength; k++) {
					if (numbersPlayed[j] == generated[k]) lineResult++;	//I'm gonna go with this but it DEPENDS ON UNIQUENESS
				}														//IN BOTH generated and numbersPlayed! Which is okay
			}
			results[i][0] = lineResult;			//record the matches for this line
			results[i][2] = awards[lineResult];	//record the expected winnings for this line
			//test if this is the best result so far - i
			if (lineResult > mostMatchesSoFar) {
				mostMatchesSoFar = lineResult;
				indexWithMostMatchesSoFar = i;
			}
		}
		//give the best-matching line actual winnings equal to its expected winnings
		results[indexWithMostMatchesSoFar][1] = results[indexWithMostMatchesSoFar][2];	
		return results;
	}
}