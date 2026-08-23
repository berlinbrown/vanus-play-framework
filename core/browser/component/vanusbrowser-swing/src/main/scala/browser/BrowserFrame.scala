package browser

import javax.swing.*
import java.awt.{BorderLayout, Dimension}

/** Minimal Swing browser window: address bar (top), content area (middle), status bar (bottom). */
class BrowserFrame extends JFrame("Vanus Browser"):
  private val addressField = new JTextField("https://example.com", 40)
  private val goButton = new JButton("Go")
  private val contentArea = new JTextArea()
  private val statusLabel = new JLabel("Ready")

  contentArea.setEditable(false)
  contentArea.setLineWrap(true)
  contentArea.setWrapStyleWord(true)

  private val addressPanel = new JPanel(new BorderLayout())
  addressPanel.add(new JLabel("Address: "), BorderLayout.WEST)
  addressPanel.add(addressField, BorderLayout.CENTER)
  addressPanel.add(goButton, BorderLayout.EAST)

  setLayout(new BorderLayout())
  add(addressPanel, BorderLayout.NORTH)
  add(new JScrollPane(contentArea), BorderLayout.CENTER)
  add(statusLabel, BorderLayout.SOUTH)

  setSize(new Dimension(800, 600))
  setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

  goButton.addActionListener(_ => loadPage())
  addressField.addActionListener(_ => loadPage())

  private def loadPage(): Unit =
    val url = addressField.getText.trim
    statusLabel.setText(s"Loading $url ...")
    contentArea.setText("")
    // fetch off the EDT so the UI stays responsive
    new Thread(() =>
      try
        val text = PageLoader.loadPageText(url)
        SwingUtilities.invokeLater(() =>
          contentArea.setText(text)
          statusLabel.setText(s"Loaded $url")
        )
      catch
        case e: Exception =>
          SwingUtilities.invokeLater(() =>
            statusLabel.setText(s"Error loading $url: ${e.getMessage}")
          )
    ).start()
