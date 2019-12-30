/**
@author Kevin Higgins
30/12/19
LottoTicket implements this interface to listen to
changes in LottoLine objects.
*/

import java.util.EventObject;

public abstract interface LottoLineListener {
	public abstract void lineCommittalChanged (EventObject oe);
}