package chatbot.teamcity.model;

import java.util.HashMap;

/** Bundle is for passing around context 
 *  that the messenger needs for its conversation
 *  with the chat app.
 */
public class Bundle extends HashMap<String,Object> {
	
	private static final long serialVersionUID = 3576861452968040009L;

	public static Bundle create(String key, Object value) {
		Bundle b = new Bundle();
		b.put(key, value);
		return b;
	}
	
	public Bundle set(String key, Object value) {
		this.put(key, value);
		return this;
	}

}
