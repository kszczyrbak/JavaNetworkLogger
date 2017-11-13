package logger.frame;

import javax.swing.*;

import logger.server.ServerUtilities;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
/**
 * Panel wyswietlajacy informacje o ilosci podpietych socketow. Zawiera tez
 * przyciski chowajace wszystkich klientow.
 * 
 * @author Krzysztof Szczyrbak
 *
 */
public class InfoPanel extends JPanel {
	private JTextField numberOfSockets;
	// chwilowo niepotrzebny. dodany w przypadku gdy implementacja panelu
	// zostanie rozwinieta i zajdzie potrzeba wyswietlenia okna z wyrzuconymi
	// wyjatkami.
	private ServerFrame frame;

	public InfoPanel(ServerFrame serverFrame) {
		frame = serverFrame;
		initUI();
	}

	/**
	 * Metoda inicjalizujaca panel.
	 */
	private void initUI() {
		numberOfSockets = new JTextField(18);

		numberOfSockets.setBorder(null);
		setBorder(BorderFactory.createEtchedBorder());
		JPanel leftSide = new JPanel();
		leftSide.add(numberOfSockets);
		leftSide.setLayout(new FlowLayout(FlowLayout.LEFT));

		numberOfSockets.setEditable(false);
		JPanel rightSide = new JPanel();
		rightSide.setLayout(new FlowLayout(FlowLayout.RIGHT));

		setLayout(new BorderLayout());
		add(leftSide, BorderLayout.WEST);
		add(rightSide, BorderLayout.EAST);

		JButton hideAllBt = new JButton("Hide all");
		JButton showAllBt = new JButton("Show all");

		rightSide.add(showAllBt);
		rightSide.add(hideAllBt);

		hideAllBt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (JCheckBox box : frame.getBoxList()) {
					if (box.isSelected())
						box.doClick(0);
				}
			}

		});

		showAllBt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (JCheckBox box : frame.getBoxList()) {
					if (!box.isSelected())
						box.doClick(0);
				}
			}

		});
	}

	/**
	 * Metoda zwracajaca referencje do pola tekstowego, w ktorym ma sie znalezc
	 * informacja o polu tekstowym.
	 * 
	 * @return Krzysztof Szczyrbak
	 */
	protected JTextField getTextField() {
		return numberOfSockets;
	}

}