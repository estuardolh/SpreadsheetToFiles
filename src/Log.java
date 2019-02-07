
public class Log {
	public static void error(String message) {
		System.err.println("[error] "+message);
	}
	
	public static void message(String message) {
		System.err.println(message);
	}
	
	public static void debug(String message) {
		System.err.println("[debug] "+message);
	}
}
