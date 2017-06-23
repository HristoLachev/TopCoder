import java.text.ParseException;

public class Main {

	public static void main(String[] args) throws ParseException {
		for (int i = 0; i < args.length; i++) {
			String argument = args[i];
			if (argument.contains("q")) {
				new ECalculator(args);
				return;
			}
		}
		new BasicSwing(args);
	}

}
