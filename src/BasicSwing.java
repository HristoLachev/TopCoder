import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class BasicSwing extends JFrame {

	JFrame mainFrame;
	JPanel controlPanel;

	JLabel headerLabel;
	JLabel statusLabel;

	JTextField precision;
	JTextField numberOfCalculations;
	JTextField threads;
	JTextField outputFile;

	ECalculator currentCalculator;

	private void prepareGUI() {
		precision = new JTextField();
		numberOfCalculations = new JTextField();
		threads = new JTextField();
		outputFile = new JTextField();

		mainFrame = new JFrame("E Calculator");
		mainFrame.setSize(500, 600);
		mainFrame.setLayout(new GridLayout(3, 1));

		headerLabel = new JLabel("", JLabel.CENTER);
		statusLabel = new JLabel("", JLabel.CENTER);
		statusLabel.setSize(350, 100);

		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				System.exit(0);
			}
		});
		controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());

		mainFrame.add(headerLabel);
		mainFrame.add(controlPanel);
		mainFrame.add(statusLabel);
		mainFrame.setVisible(true);
	}

	public BasicSwing(String[] args) {
		showEventDemo();
	}

	private void showEventDemo() {
		prepareGUI();
		headerLabel.setText("Arguments for calculating e");

		precision.setPreferredSize(new Dimension(200, 24));
		numberOfCalculations.setPreferredSize(new Dimension(200, 24));
		threads.setPreferredSize(new Dimension(200, 24));
		outputFile.setPreferredSize(new Dimension(200, 24));

		JButton submitButton = new JButton("Start execution");
		JButton cancelButton = new JButton("Stop execution");

		submitButton.setActionCommand("Submit");
		submitButton.setPreferredSize(new Dimension(200, 24));
		submitButton.addActionListener(new StartButtonListener());

		cancelButton.setActionCommand("Cancel");
		cancelButton.setPreferredSize(new Dimension(200, 24));
		cancelButton.addActionListener(new StopButtonListener());

		JLabel precisionLabel = new JLabel("Number of digits after the decimal point:");
		JLabel numberOfCalculationsLabel = new JLabel("Number of calculations to be done:");
		JLabel threadsLabel = new JLabel("Maximum number of threads working:");
		JLabel outputFileLabel = new JLabel("Output file:(not mandatory)");

		controlPanel.add(precisionLabel, BorderLayout.WEST);
		controlPanel.add(precision, BorderLayout.CENTER);
		controlPanel.add(numberOfCalculationsLabel, BorderLayout.WEST);
		controlPanel.add(numberOfCalculations);
		controlPanel.add(threadsLabel, BorderLayout.WEST);
		controlPanel.add(threads);
		controlPanel.add(outputFileLabel, 6);
		controlPanel.add(outputFile);
		controlPanel.add(submitButton);
		controlPanel.add(cancelButton);
		mainFrame.setVisible(true);

	}

	private class StartButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if (command.equals("Submit")) {
				statusLabel.setText("Execution started");
				final String precisionText = precision.getText();
				final String numberOfCalculationsText = numberOfCalculations.getText();
				final String threadsText = threads.getText();
				final String outputFileText = outputFile.getText();

				SwingWorker worker = new SwingWorker<ECalculator, Void>() {
					@Override
					public ECalculator doInBackground() {
						currentCalculator = new ECalculator(numberOfCalculationsText, precisionText, threadsText,
								outputFileText, BasicSwing.this);
						currentCalculator.calculate();
						return currentCalculator;
					}
				};

				worker.execute();
			}
		}
	}

	private class StopButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if (command.equals("Cancel")) {
				if (currentCalculator != null) {
					currentCalculator.stop();
				}
				statusLabel.setText("Execution stopped.");
			}
		}
	}

	public void notifyMe(long duration) {
		statusLabel.setText("Execution finished in: " + String.valueOf(duration));
	}

}
