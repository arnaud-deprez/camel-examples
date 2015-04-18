package be.arndep.camel.transaction.jpa.message;

/**
 * Created by arnaud on 18/04/15.
 */
public class MessageFactory {
	public Message create(String content) {
		Message m = new Message();
		m.setContent(content);
		return m;
	}
}