package client;

import client.controller.Controller;
import javax.swing.SwingUtilities;


/** Main class for TypeRacer client. */
public class Main {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(Main::showTypeRacerGui);
  }

  private static void showTypeRacerGui() {
    Controller controller = new Controller();
    controller.start();
  }

}
