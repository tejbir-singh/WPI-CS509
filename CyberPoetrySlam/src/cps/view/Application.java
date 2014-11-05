package cps.view;

import javax.swing.*;

import cps.controller.ConnectWordController;
import cps.controller.DisconnectWordController;
import cps.controller.MoveController;
import cps.controller.UndoController;
import cps.model.GameManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Application extends JFrame {

	private static final long serialVersionUID = 8238490728667512551L;
	// private JTextField txtSwapActions;
	private JTextField txtProtectedArea;
	private JTextField txtUnprotectedArea;
	ApplicationPanel appPanel;
	GameManager gm;
	
	

	/**
	 * Create the frame.
	 * @param gm GameManager
	 */
	public Application(GameManager g) {
		this.gm = g;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 700);
		getContentPane().setLayout(null);

		JPanel p = new JPanel();
		p.setBounds(this.getBounds());

		JButton moveButton = new JButton("Move");
		moveButton.setBounds(56, 31, 89, 23);
		getContentPane().add(moveButton);

		JButton connectButton = new JButton("Connect");
		connectButton.setBounds(176, 31, 89, 23);
		getContentPane().add(connectButton);

		JButton disconnectButton = new JButton("Disconnect");
		disconnectButton.setBounds(296, 31, 89, 23);
		getContentPane().add(disconnectButton);

		final JButton undoButton = new JButton("Undo");
		undoButton.setBounds(416, 31, 89, 23);
		getContentPane().add(undoButton);
		if (gm.getManipulations().isEmpty()) {
			undoButton.setEnabled(false);
		}

		JButton publishButton = new JButton("Publish");
		publishButton.setBounds(536, 31, 89, 23);
		getContentPane().add(publishButton);
		publishButton.setEnabled(false);

		JPanel panel = new JPanel();
		panel.setBounds(12, 66, 678, 595);
		getContentPane().add(panel);
		panel.setLayout(null);

		txtProtectedArea = new JTextField();
		txtProtectedArea.setHorizontalAlignment(SwingConstants.CENTER);
		txtProtectedArea.setText("Protected Area");
		txtProtectedArea.setEditable(false);
		txtProtectedArea.setBounds(0, 11, 650, 20);
		panel.add(txtProtectedArea);
		txtProtectedArea.setColumns(10);

		txtUnprotectedArea = new JTextField();
		txtUnprotectedArea.setText("Unprotected Area");
		txtUnprotectedArea.setEditable(false);
		txtUnprotectedArea.setHorizontalAlignment(SwingConstants.CENTER);
		txtUnprotectedArea.setBounds(0, 345, 650, 20);
		panel.add(txtUnprotectedArea);
		txtUnprotectedArea.setColumns(10);

		// add the application panel
		appPanel = new ApplicationPanel(gm, undoButton);
		panel.add(appPanel);
		appPanel.setBounds(0, 31, 650, 550);
		appPanel.setAlignmentX(CENTER_ALIGNMENT);
		appPanel.setAlignmentY(CENTER_ALIGNMENT);
		appPanel.setOpaque(false);
		appPanel.setVisible(true);

		moveButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				new MoveController(gm, appPanel).register();
			}
		});

		connectButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				new ConnectWordController(gm, appPanel).register();
			}
		});
		
		disconnectButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				new DisconnectWordController(gm, appPanel).register();
			}
		});
		
		undoButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (!undoButton.isEnabled()) {
					return;
				}
				new UndoController(gm, appPanel).process();
			}
		});
		
		/*
		JCheckBox chckbxNewCheckBox = new JCheckBox("Swap Initiated");
		chckbxNewCheckBox.setBounds(566, 566, 97, 23);
		getContentPane().add(chckbxNewCheckBox);
		
		JCheckBox chckbxNewCheckBox_1 = new JCheckBox("Processing Swap");
		chckbxNewCheckBox_1.setBounds(566, 592, 112, 23);
		getContentPane().add(chckbxNewCheckBox_1);
		
		JCheckBox chckbxNewCheckBox_2 = new JCheckBox("Swap Completed");
		chckbxNewCheckBox_2.setBounds(566, 618, 108, 23);
		getContentPane().add(chckbxNewCheckBox_2);
		
		JButton btnNewButton_5 = new JButton("Request");
		btnNewButton_5.setBounds(570, 449, 89, 40);
		getContentPane().add(btnNewButton_5);
		
		JButton btnNewButton_6 = new JButton("Check");
		btnNewButton_6.setBounds(570, 489, 89, 35);
		getContentPane().add(btnNewButton_6);
		
		txtSwapActions = new JTextField();
		txtSwapActions.setHorizontalAlignment(SwingConstants.CENTER);
		txtSwapActions.setText("Swap Actions");
		txtSwapActions.setBounds(570, 426, 86, 20);
		getContentPane().add(txtSwapActions);
		txtSwapActions.setColumns(10);
		
		JButton btnNewButton_7 = new JButton("Revoke");
		btnNewButton_7.setBounds(570, 524, 89, 35);
		getContentPane().add(btnNewButton_7);
		*/
	}
}