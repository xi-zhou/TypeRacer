package communication;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Contains a socket and streams on it. The class serves as a container for 'interfaces' to an
 * established connection.
 */
public class Connection implements Closeable {
  private final Socket socket;
  private final BufferedReader reader;
  private final OutputStreamWriter writer;

  /**
   * Creates a new Connection object holding the given socket and streams and use UTF8 to
   * en/decoding.
   *
   * @param socket The socket of the connection.
   */
  public Connection(Socket socket) throws IOException {
    this.socket = socket;
    writer = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
    reader =
        new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
  }

  /**
   * Reads JSON from the socket stream.
   *
   * @return JSON
   * @throws IOException If there are connection errors
   */
  public String readJson() throws IOException {
    String content = reader.readLine();
    if (content == null) {
      throw new IOException();
    }
    return content;


  }

  /**
   * Write JSON to the socket stream.
   *
   * @param json The JSON to write
   * @throws IOException If there are connection errors.
   */
  public void writeJson(String json) throws IOException {
    writer.write(json + "\n");
    writer.flush();
  }

  @Override
  public void close() throws IOException {
    socket.close();
  }
}
