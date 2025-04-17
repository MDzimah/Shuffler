package View;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.prefs.Preferences;

import javax.swing.Timer;

import javax.swing.*;

import Controller.Controller;
import Messages.Messages;
import Utils.Constants;

public class ControlPanel extends JPanel {
	private Controller ctrl;
	private boolean stopped;
	private final Map<String, JButton> buttons = new HashMap<>() {{
		put("run", null);
		put("stop", null);
		put("open", null);
		put("setDirectory", null);
	}};
	private JSpinner delay;
	private JLabel batchLabel;
	private JLabel traversedLabel;
	private String workSpacePath;
	private JPanel displayPanel; 
	private Timer timer;
	private Timer hideLabelTimer;
	private String currentSentence;

	//Constructor to initialize the control panel
	public ControlPanel(Controller c, JPanel display) {
		this.ctrl = c;
		this.displayPanel = display;
		this.displayPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (currentSentence != null) {
					displaySentence(currentSentence);
				}
			}
		});
		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,2));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setPreferredSize(new Dimension(screenSize.width, screenSize.height / 15));
		this.initGUI();
	}

	//Initialize the GUI components
	private void initGUI() {
		this.setLayout(new BorderLayout());
		JToolBar jtb = new JToolBar();
		
		for (Map.Entry<String, JButton> p : this.buttons.entrySet()) {
			p.setValue(new JButton());
			p.getValue().setPreferredSize(Constants.BUTTON_SIZE);
		}
		this.delay = new JSpinner(new SpinnerNumberModel(10, 1, Constants.MAX_DELAY, 1));	
		
		this.addButtonsToToolBar(jtb);
		this.add(jtb, BorderLayout.CENTER);
	}
	
	//Add buttons and components to the toolbar
	private void addButtonsToToolBar(JToolBar tb) {
		for (Map.Entry<String, JButton> p : this.buttons.entrySet()) {
			URL path = getClass().getResource("/Resources/" + p.getKey() + ".png");
			ImageIcon icon = new ImageIcon(path);
			Image img = icon.getImage();
			Image scaledImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			p.getValue().setIcon(new ImageIcon(scaledImg));
			p.getValue().setToolTipText(Messages.getToolTipMessage(p.getKey()));
		}
		this.delay.setToolTipText(Messages.getToolTipMessage("delay"));
		
		JLabel t = new JLabel("Delay (seconds):");
		t.setFont(Constants.LABEL_FONT);

		this.delay.setMaximumSize(new Dimension(70, 50));
		((JSpinner.NumberEditor)this.delay.getEditor()).getTextField().setFont(Constants.LABEL_FONT);
		this.batchLabel = new JLabel();
		this.batchLabel.setFont(Constants.LABEL_FONT);
		this.batchLabel.setVisible(false);

		this.traversedLabel = new JLabel("Batch already shuffled through!");
		this.traversedLabel.setFont(Constants.LABEL_FONT);
		this.traversedLabel.setForeground(Color.RED);
		this.traversedLabel.setVisible(false);
		
		tb.addSeparator(new Dimension(5, Constants.NA));
		tb.add(this.buttons.get("run"));
		tb.addSeparator(new Dimension(10, Constants.NA));
		tb.add(this.buttons.get("stop"));
		tb.addSeparator(new Dimension(30, Constants.NA));
		tb.add(this.buttons.get("open"));
		tb.addSeparator(new Dimension(10, Constants.NA));
		tb.add(this.buttons.get("setDirectory"));
		tb.addSeparator(new Dimension(20, Constants.NA));
		tb.add(t);
		tb.addSeparator(new Dimension(10, Constants.NA));		
		tb.add(this.delay);
		tb.addSeparator(new Dimension(30, Constants.NA));
		tb.add(this.traversedLabel);
		tb.add(Box.createHorizontalGlue());
		tb.add(this.batchLabel);
		tb.addSeparator(new Dimension(5, Constants.NA));
		
		//Add listeners for the buttons
		this.buttons.get("run").addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!ctrl.getSentences().isEmpty()) {
					stopped = false;
					try {	
						delay.commitEdit(); 
					} 
					catch (Exception ex) { 
						delay.updateUI(); 
					}
					run_sim((Integer)delay.getValue());
				}
				else {
					JOptionPane.showMessageDialog(null, 
							Messages.NO_SENTENCES_DIALOG, 
							Messages.NO_SENTENCES_DIALOG_NAME, 
							JOptionPane.INFORMATION_MESSAGE);
				}
			}  
		});
		this.buttons.get("stop").addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopped = true;
				stopSimulation();
			}
		});
		this.buttons.get("open").addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String lastVisitedDir = workSpacePath == null ? getLastVisitedDirectory() : workSpacePath;

				JFileChooser fc = new JFileChooser(lastVisitedDir != null ? lastVisitedDir : System.getProperty("user.home"));
				
				int aux = fc.showOpenDialog(null);
				if (aux == JFileChooser.APPROVE_OPTION) {
					try {
						InputStream in = new FileInputStream(fc.getSelectedFile());
						try {
							ctrl.load(in);
						} catch (IOException ex) {
							JOptionPane.showMessageDialog(null, 
									Messages.FILE_LOAD_ERROR_DIALOG,
									Messages.FILE_ERROR_DIALOG_NAME, 
									JOptionPane.ERROR_MESSAGE);
						}
						batchLabel.setText("Batch loaded: " + fc.getSelectedFile().getName());
						batchLabel.setVisible(true);
						
						saveLastVisitedDirectory(fc.getSelectedFile().getAbsoluteFile().getParent());

						in.close();
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(null, 
								Messages.FILE_NOT_FOUND_DIALOG,
								Messages.FILE_ERROR_DIALOG_NAME, 
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		this.buttons.get("setDirectory").addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser directoryChooser = new JFileChooser();
				directoryChooser.setDialogTitle("Select Work Directory");
				directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				directoryChooser.setAcceptAllFileFilterUsed(false);
				
				int result = directoryChooser.showOpenDialog(null);
				
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedDirectory = directoryChooser.getSelectedFile();
					workSpacePath = selectedDirectory.getAbsolutePath();
					JOptionPane.showMessageDialog(null, 
						"Work directory set to: " + workSpacePath,
						"Directory Set", 
						JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});		
	}
	
	//Start the simulation with a delay
	private void run_sim(int tickSeconds) {
		int delay = tickSeconds * 1000;
	
		if (timer != null && timer.isRunning()) {
			timer.stop();
		}
	
		for (Map.Entry<String, JButton> p : this.buttons.entrySet()) {
			if (!p.getKey().equals("stop")) p.getValue().setEnabled(false);
		}
		this.delay.setEnabled(false);
		this.stopped = false;
	
		timer = new Timer(delay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (stopped) {
					stopSimulation();
					return;
				}
	
				String sentence = ctrl.getRandomSentence();
				displaySentence(sentence);
	
				if (ctrl.isListTraversed()) {
					traversedLabel.setVisible(true);
	
					if (hideLabelTimer != null && hideLabelTimer.isRunning()) {
						hideLabelTimer.stop();
					}
	
					hideLabelTimer = new Timer(2000, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent evt) {
							traversedLabel.setVisible(false);
						}
					});
					hideLabelTimer.setRepeats(false);
					hideLabelTimer.start();
				}
			}
		});
	
		timer.setInitialDelay(0);
		timer.start();
	}
	
	//Display a sentence in the display panel
	private void displaySentence(String sentence) {
		this.currentSentence = sentence;
		this.displayPanel.removeAll();
	
		int fontSize = (int) (this.displayPanel.getWidth() * 0.05);
		if (fontSize < 18) {
			fontSize = 18;
		}
		
		JLabel label = new JLabel("<html><div style='text-align: center;'>" + sentence + "</div></html>");
		label.setFont(new Font("Arial", Font.PLAIN, fontSize));
		label.setForeground(Color.BLACK);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
	
		int horizontalMargin = (int) (this.displayPanel.getWidth() * 0.05);
		int verticalMargin = (int) (this.displayPanel.getHeight() * 0.05);
		label.setBorder(BorderFactory.createEmptyBorder(verticalMargin, horizontalMargin, verticalMargin, horizontalMargin));
		
		this.displayPanel.setLayout(new BorderLayout());
		this.displayPanel.add(label, BorderLayout.CENTER);
		this.displayPanel.revalidate();
		this.displayPanel.repaint();
	}
	
	//Stop the simulation
	private void stopSimulation() {
		if (timer != null && timer.isRunning()) {
			timer.stop();
			for (Map.Entry<String, JButton> p : buttons.entrySet()) {
				p.getValue().setEnabled(true);
			}
			delay.setEnabled(true);
		}
	}

	//Retrieve the last visited directory
	private String getLastVisitedDirectory() {
		Preferences prefs = Preferences.userNodeForPackage(this.getClass());
		return prefs.get("lastVisitedDirectory", null);
	}

	//Save the last visited directory
	private void saveLastVisitedDirectory(String directory) {
		Preferences prefs = Preferences.userNodeForPackage(this.getClass());
		prefs.put("lastVisitedDirectory", directory);
	}
}
