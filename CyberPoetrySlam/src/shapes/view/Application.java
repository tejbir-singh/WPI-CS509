package shapes.view;

import javax.swing.*;

import shapes.controller.MoveController;
import shapes.model.GameManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Application extends JFrame {

	private static final long serialVersionUID = 8238490728667512551L;
	private JTextField txtSwapActions;
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
		
		JButton btnNewButton = new JButton("Move");
		btnNewButton.setBounds(56, 31, 89, 23);
		getContentPane().add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Connect");
		btnNewButton_1.setBounds(176, 31, 89, 23);
		getContentPane().add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("Disconnect");
		btnNewButton_2.setBounds(296, 31, 89, 23);
		getContentPane().add(btnNewButton_2);
		
		JButton btnNewButton_3 = new JButton("Release");
		btnNewButton_3.setBounds(416, 31, 89, 23);
		getContentPane().add(btnNewButton_3);
		
		JButton btnNewButton_4 = new JButton("Publish");
		btnNewButton_4.setBounds(536, 31, 89, 23);
		getContentPane().add(btnNewButton_4);
		btnNewButton_4.setEnabled(false);
		
		JPanel panel = new JPanel();
		panel.setBounds(12, 66, 678, 595);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		txtProtectedArea = new JTextField();
		txtProtectedArea.setHorizontalAlignment(SwingConstants.CENTER);
		txtProtectedArea.setText("Protected Area");
		txtProtectedArea.setBounds(0, 11, 650, 20);
		panel.add(txtProtectedArea);
		txtProtectedArea.setColumns(10);
		
		txtUnprotectedArea = new JTextField();
		txtUnprotectedArea.setText("Unprotected Area");
		txtUnprotectedArea.setHorizontalAlignment(SwingConstants.CENTER);
		txtUnprotectedArea.setBounds(0, 344, 650, 19);
		panel.add(txtUnprotectedArea);
		txtUnprotectedArea.setColumns(10);
		
		// add the application panel
		appPanel = new ApplicationPanel(gm);
		panel.add(appPanel);
		appPanel.setBounds(0, 31, 650, 550);
		appPanel.setAlignmentX(CENTER_ALIGNMENT);
		appPanel.setAlignmentY(CENTER_ALIGNMENT);
		appPanel.setOpaque(false);
		appPanel.setVisible(true);
		
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				new MoveController(gm, appPanel).register();
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