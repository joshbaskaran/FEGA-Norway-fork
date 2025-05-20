package no.elixir.crypt4gh.app;

import java.io.Console;

/** Console utility class, not a public API. */
class ConsoleUtils {

  private static ConsoleUtils ourInstance =
          new ConsoleUtils();

  /**
   * Returns a singleton instance of this class.
   *
   * @return a ConsoleUtils object
   */
  static ConsoleUtils getInstance() {
    return ourInstance;
  }

  private ConsoleUtils() {}

  /**
   * Prompts the user to enter a "yes/no" response to a question on the command-line. The question
   * will be repeated if the user's response does not start with either 'y' or 'n'.
   *
   * @param prompt a yes/no-question to ask the user
   * @return {@code true} if the user entered a response starting with 'y'; {@code false} if the
   *     response started with 'n' (case-insensitive)
   */
  boolean promptForConfirmation(String prompt) {Console console = System.console();
    Boolean confirm = null;
    while (confirm == null) {
      String response = console.readLine(prompt + " (y/n) ");
      if (response.toLowerCase().startsWith("y")) {
        confirm = true;
      } else if (response.toLowerCase().startsWith("n")) {
        confirm = false;
      }
    }
    return confirm;
  }

  /**
   * Prompts the user to enter a password on the command-line. The prompt will be repeated if the
   * length of the provided password is shorter than a specified minimum.
   *
   * @param prompt a message to display to the user
   * @param minLength a required minimum length for the password
   * @return A character array containing the password read from the console
   */
  char[] readPassword(String prompt, int minLength) {
    while (true) {
      char[] password = System.console().readPassword(prompt);
      if (password.length >= minLength) {
        return password;
      } else {
        System.out.println("Passphrase is too short: min length is " + minLength);
      }
    }
  }
}
