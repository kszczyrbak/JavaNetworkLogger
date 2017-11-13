package logger.frame;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;

import logger.server.ServerUtilities;

/**
 * Pasek menu wyswietlany w glownym oknie.
 * 
 * @author Krzysztof Szczyrbak
 *
 */
@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {
	private ServerFrame frame;
	private JMenu windowMenu;

	MenuBar(ServerFrame f) {
		frame = f;
		initMenuBar();
	}

	/**
	 * Metoda inicjalizujaca pasek menu.
	 */
	private void initMenuBar() {
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		JMenu userMenu = new JMenu("User Panel");
		JMenuItem exitItem = new JMenuItem("Exit");
		JMenuItem optionsItem = new JMenuItem("Options");

		// listener menu opcji
		optionsItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new OptionsFrame(frame);
			}

		});

		userMenu.add(optionsItem);
		userMenu.addSeparator();
		userMenu.add(exitItem);

		exitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();

			}

		});

		windowMenu = new JMenu("Window");
		JMenuItem saveItem = new JMenuItem("Save current logs");
		JMenuItem helpItem = new JMenuItem("Help");
		helpItem.setMaximumSize(new Dimension(20, 20));
		windowMenu.add(saveItem);

		// listener przycisku zapisujacego logi
		saveItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				File tmp = ServerUtilities.chooseFile(MenuBar.this);
				if (tmp.getPath().equals(""))
					return;
				ServerUtilities.saveLogs(tmp);

			}
		});

		JMenuItem loadItem = new JMenuItem("Load logs from file");

		// listener przycisku wczytujacego logi
		loadItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				File tmp = ServerUtilities.chooseFile(MenuBar.this);
				if (tmp.getPath().equals(""))
					return;
				ServerUtilities.loadLogs(tmp);
			}
		});

		windowMenu.add(loadItem);

		final JToggleButton serverToggleBt = new JToggleButton("RUN", true);
		serverToggleBt.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		// listener przycisku toggle obslugujacego serwer
		serverToggleBt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (serverToggleBt.isSelected() == true) {
					frame.startServer();
				} else if (serverToggleBt.isSelected() == false) {
					frame.stopServer();
					System.out.println("server stop");
				}
			}

		});

		add(userMenu);
		add(windowMenu);

		add(helpItem);
		add(Box.createHorizontalGlue());

		add(serverToggleBt);

		// listener przycisku help - dokumentacja
		helpItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new HelpFrame(frame);

			}
		});

	}

}
