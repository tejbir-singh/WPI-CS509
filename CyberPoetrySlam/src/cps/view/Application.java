package cps.view;

import javax.swing.*;

import cps.controller.ConnectEntityController;
import cps.controller.DisconnectEntityController;
import cps.controller.MoveController;
import cps.controller.MoveEntityController;
import cps.controller.PublishPoemController;
import cps.controller.RequestSwapController;
import cps.controller.UndoRedoController;
import cps.controller.UndoRedoController.URType;
import cps.model.GameManager;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Application extends JFrame {

	private static final int BUTTON_OFFSET = 31;
	private static final long serialVersionUID = 8238490728667512551L;
	private JTextField txtProtectedArea;
	private JTextField txtUnprotectedArea;
	ApplicationPanel appPanel;
	GameManager gm;
	private JTextField textField;

	/**
	 * Create the frame.
	 * @param gm GameManager
	 */
	/**
	 * @param g
	 */
	public Application(GameManager g) {
		this.gm = g;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 900);
		getContentPane().setLayout(null);

		JPanel p = new JPanel();
		p.setBounds(this.getBounds());

		JButton moveButton = new JButton("Move");
		moveButton.setBounds(20, 31, 89, 23);
		getContentPane().add(moveButton);

		JButton connectButton = new JButton("Connect");
		connectButton.setBounds(130, 31, 89, 23);
		getContentPane().add(connectButton);

		JButton disconnectButton = new JButton("Disconnect");
		disconnectButton.setBounds(240, 31, 89, 23);
		getContentPane().add(disconnectButton);

		final JButton undoButton = new JButton("Undo");
		undoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		undoButton.setBounds(350, 8, 89, 23);
		getContentPane().add(undoButton);
		if (gm.getManipulations().isEmpty()) {
			undoButton.setEnabled(false);
		}

		final JButton redoButton = new JButton("Redo");
		redoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		redoButton.setBounds(350, 42, 89, 23);
		getContentPane().add(redoButton);
		redoButton.setEnabled(false);

		JButton publishButton = new JButton("Publish");
		publishButton.setBounds(570, 31, 89, 23);
		getContentPane().add(publishButton);

		final JPanel panel = new JPanel();
		panel.setBounds(12, 66, 678, 888);
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

		textField = new JTextField();
		textField.setText("Swap Area");
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setEditable(false);
		textField.setColumns(10);
		textField.setBounds(0, GameManager.SWAP_AREA_DIVIDER + BUTTON_OFFSET, 650, 20);
		panel.add(textField);

		final JButton swapButton = new JButton("Swap");
		swapButton.setBounds(460, 31, 89, 23);
		swapButton.setEnabled(false);
		getContentPane().add(swapButton);
		// add the application panel
		appPanel = new ApplicationPanel(gm, undoButton, redoButton, swapButton);
		panel.add(appPanel);
		appPanel.setBounds(0, BUTTON_OFFSET, 700, 800);
		appPanel.setAlignmentX(CENTER_ALIGNMENT);
		appPanel.setAlignmentY(CENTER_ALIGNMENT);
		appPanel.setOpaque(false);
		appPanel.setVisible(true);

		moveButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				//new MoveController(gm, appPanel).register();
				new MoveEntityController(gm, appPanel).register();
			}
		});

		connectButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				new ConnectEntityController(gm, appPanel).register();
			}
		});

		disconnectButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				new DisconnectEntityController(gm, appPanel).register();
			}
		});

		publishButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				new PublishPoemController(gm, appPanel).register();
			}
		});

		undoButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (!undoButton.isEnabled()) {
					return;
				}
				new UndoRedoController(gm, appPanel, URType.UNDO).process();
			}
		});

		redoButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (!redoButton.isEnabled()) {
					return;
				}
				new UndoRedoController(gm, appPanel, URType.REDO).process();
			}
		});
		
		final JTextField requestWords = new JTextField(20);
		final JTextField requestTypes = new JTextField(20);
		final JPanel swapPanel = new JPanel();
		swapPanel.add(new JLabel("Words:"));
		swapPanel.add(requestWords);
		swapPanel.add(Box.createVerticalStrut(15)); // a spacer
		swapPanel.add(new JLabel("Types:"));
		swapPanel.add(requestTypes);

		swapButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (gm.getSwapManager().getWords().isEmpty()) {
					return;
				}
				JOptionPane.showConfirmDialog(null, swapPanel, 
						"Please enter the words and types to request", JOptionPane.OK_CANCEL_OPTION);
				new RequestSwapController(gm, appPanel, requestWords.getText(), requestTypes.getText()).process();
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

	public ApplicationPanel getAppPanel() {
		return this.appPanel;
	}
}
