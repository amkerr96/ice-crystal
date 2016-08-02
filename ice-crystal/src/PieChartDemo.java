import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.PieStyler.AnnotationType;
import org.knowm.xchart.style.Styler.ChartTheme;

public class PieChartDemo {

  public PieChartDemo() {

    // Create Chart
    PieChart chart = new PieChartBuilder().width(400).height(300).title("My Pie Chart").theme(ChartTheme.GGPlot2).build();

    // Customize Chart
    chart.getStyler().setLegendVisible(false);
    chart.getStyler().setAnnotationType(AnnotationType.LabelAndPercentage);
    chart.getStyler().setAnnotationDistance(1.15);
    chart.getStyler().setPlotContentSize(.7);
    chart.getStyler().setStartAngleInDegrees(90);

    // Series
    chart.addSeries("Prague", 2);
    chart.addSeries("Dresden", 4);
    chart.addSeries("Munich", 34);
    chart.addSeries("Hamburg", 22);
    chart.addSeries("Berlin", 29);

    // Show it
    new SwingWrapper(chart).displayChart();
 
      JFrame frame = new JFrame("Advanced Example");
      frame.setLayout(new BorderLayout());
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      // chart
      JPanel chartPanel = new XChartPanel<PieChart>(chart);
      frame.add(chartPanel, BorderLayout.CENTER);

      // label
      JLabel label = new JLabel("Blah blah blah.", SwingConstants.CENTER);
      frame.add(label, BorderLayout.SOUTH);

      // Display the window.
      frame.pack();
      frame.setVisible(true);
    }

}