package logger.server;

import javax.swing.JCheckBox;

/**
 * Interfejs klasy opakowujacej kliencki socket. Uruchamiany w nowym watku
 * obsluguje klienta.
 * 
 * @author Krzysztof Szczyrbak
 *
 */
public interface ClientWrapper {

	/**
	 * Metoda zatrzymujaca prace klienta.
	 */
	public void stop();

	/**
	 * Przyznaje wrapperowi podana nazwe.
	 * 
	 * @param name
	 *            nazwa
	 */
	public void setName(String name);

	/**
	 * Getter dla nazwy
	 * 
	 * @return String nazwa wrappera
	 */
	public String getName();

	/**
	 * Metoda zmieniajaca widocznosc wrappera - czy uzytkownik chce widziec
	 * wyniki pracy klienta. Logi dalej sa odbierane w tle.
	 * 
	 * @param b
	 *            stan na jaki zmienia sie widocznosc
	 */
	public void setVisibility(boolean b);

	/**
	 * Metoda zwracajaca port reprezentujacy klienta.
	 * 
	 * @return port na ktory podpiety jest klient
	 */
	public int getPort();

}
