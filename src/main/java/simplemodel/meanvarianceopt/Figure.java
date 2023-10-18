package simplemodel.meanvarianceopt;

import java.awt.*;
import javax.swing.*;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.xy.*;

public class Figure {
    private JFrame frame;
    private XYPlot plot;
    private int dataSetsCounter = 0;
    public Figure(String name, String xName, String yName) {
        frame = new JFrame("Figure");
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        plot = new XYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        NumberAxis domainAxis = new NumberAxis(xName);
        ValueAxis rangeAxis = new NumberAxis(yName);
        plot.setDomainAxis(domainAxis);
        plot.setRangeAxis(rangeAxis);
        JFreeChart chart = new JFreeChart(plot);
        RenderingHints renderingHints =
                new  RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
        renderingHints.put(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        chart.setRenderingHints(renderingHints);
        chart.removeLegend();
        chart.setTitle(name);
        ChartPanel chartPanel = new ChartPanel(chart);
        frame.getContentPane().add(chartPanel, BorderLayout.CENTER);
        frame.setSize(400, 400);
        frame.setVisible(true);
    }

    public void line(double x[], double y[], Color color, float lineWidth) {
        XYDataset dataset = createDataset(x,y);
        plot.setDataset(dataSetsCounter, dataset);
        XYLineAndShapeRenderer renderer =
                new XYLineAndShapeRenderer(true, false);
        BasicStroke basicStroke = new BasicStroke(lineWidth);
        renderer.setSeriesStroke(0, basicStroke);
        renderer.setSeriesPaint(0, color);
        plot.setRenderer(dataSetsCounter, renderer);
        dataSetsCounter++;
    }

    public void stem(double x[], double y[], Color color, float lineWidth) {
        XYDataset dataset = createDataset(x,y);
        plot.setDataset(dataSetsCounter, dataset);
        XYBarRenderer renderer = new XYBarRenderer(0.98f);
        renderer.setShadowVisible(false);
        renderer.setSeriesPaint(0, color);
        plot.setRenderer(dataSetsCounter, renderer);
        dataSetsCounter++;
    }

    private XYDataset createDataset(double x[], double y[]) {
        XYSeries series = new XYSeries("");
        for(int i=0; i<y.length;i++){
            series.add(x[i],y[i]);
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }

}

