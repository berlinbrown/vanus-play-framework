import javax.swing.SwingUtilities
import browser.BrowserFrame

@main def main(): Unit =
  SwingUtilities.invokeLater(() =>
    val frame = new BrowserFrame()
    frame.setVisible(true)
  )
