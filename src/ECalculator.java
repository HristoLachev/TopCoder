import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ECalculator {
	private BigDecimal e = new BigDecimal(0.0);
	private int numberOfCalculations = 5000;
	private int precision = 10;
	private int threads = 2;
	private String outputFilePath = "";
	private BasicSwing notified;
	private boolean quiet = false;
	private ThreadPoolExecutor executor;

	public ECalculator(String[] args) {
		getArguments(args);
		calculate();
	}

	public ECalculator(String numberOfCalculations, String precision, String threads, String outputFilePath,
			BasicSwing notified) {
		super();
		if (!numberOfCalculations.isEmpty()) {
			this.numberOfCalculations = Integer.valueOf(numberOfCalculations);
		}
		if (!precision.isEmpty()) {
			this.precision = Integer.valueOf(precision);
		}
		if (!threads.isEmpty()) {
			this.threads = Integer.valueOf(threads);
		}
		if (!outputFilePath.isEmpty()) {
			this.outputFilePath = outputFilePath;
		}
		this.notified = notified;

	}

	public void calculate() {
		long startTime = System.currentTimeMillis();
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);

		for (int i = 0; i <= numberOfCalculations; i++) {
			final Integer number = new Integer(i);
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					long threadTime = System.currentTimeMillis();
					if (!quiet) {
						System.out.println("Thread:" + number + " started");
					}
					BigDecimal current = (BigDecimal.valueOf(number * 2 + 1)).divide(bigNumberFactorial(number * 2),
							precision, RoundingMode.HALF_UP);
					addBigDecimal(current);
					current = null;
					if (!quiet) {
						long executionThreadTime = System.currentTimeMillis() - threadTime;
						System.out.println("Thread " + number + " execution time was " + executionThreadTime);
						System.out.println("Temp " + e);
						System.out.println("Thread:" + number + " stopped");
					}
				}
			};
			executor.execute(runnable);
		}

		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e1) {
		}

		long duration = (System.currentTimeMillis() - startTime);
		long second = TimeUnit.MILLISECONDS.toSeconds(duration);
		System.out.println("Total execution time for current run in seconds " + second);
		System.out.println("Total execution time for current run in miliseconds " + duration);
		if (notified != null) {
			notified.notifyMe(duration);
		}
		printResult(outputFilePath);
	}

	public void addBigDecimal(BigDecimal current) {
		synchronized (e) {
			e = e.add(current);
		}
	}

	public static BigDecimal bigNumberFactorial(int factorial) {
		BigDecimal returned = BigDecimal.valueOf(1);
		for (int i = 1; i <= factorial; i++) {
			returned = returned.multiply(BigDecimal.valueOf(i));
		}
		return returned;
	}

	public void printResult(String path) {
		if (path == null || path.isEmpty()) {
			path = "c:/tmp/result.txt";
		}
		File file = new File(path);

		try {
			file.createNewFile();
			PrintWriter writer = null;
			writer = new PrintWriter(file);
			writer.write(e.toString());
			writer.close();
		} catch (IOException e1) {
			System.out.println("The path:" + path + " does not exist");
		}

	}

	public void getArguments(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String argument = args[i];
			if (argument.equals("-p")) {
				precision = getIntValue(args[i + 1]);
			}
			if (argument.contains("-n")) {
				numberOfCalculations = getIntValue(args[i + 1]);
			}
			if (argument.contains("-t")) {
				threads = getIntValue(args[i + 1]);
			}
			if (argument.contains("-o")) {
				outputFilePath = args[i + 1].substring(3);
			}
			if (argument.contains("-q")) {
				quiet = true;
			}
		}
	}

	private int getIntValue(String argument) {
		String number = argument.replaceAll("[^0-9?!\\.]", "");
		return Integer.valueOf(number);
	}

	public void stop() {
		executor.shutdownNow();
	}
}
