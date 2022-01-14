package org.um.nine.experiments.rhea.graph;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.DefaultCategoryDataset;
import org.um.nine.headless.agents.rhea.state.GameStateFactory;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class WinLossRate extends ApplicationFrame {
    private JFreeChart chart;
    private ChartPanel chartPanel;
    private DefaultCategoryDataset dataset;
    private Timer timer = new Timer();

    public WinLossRate(String title) {
        super(title);

        this.chart = ChartFactory.createBarChart(
                "Action Counts",
                "Outcome", "Amount",
                createDataset(),
                PlotOrientation.VERTICAL,
                true, true, false
        );

        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(560, 367));
        setContentPane(chartPanel);
    }

    private DefaultCategoryDataset createDataset() {
        dataset = new DefaultCategoryDataset();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                dataset.setValue(GameStateFactory.getAnalyticsRepository().winCount(), "Amount Won", "Won");
                dataset.setValue(GameStateFactory.getAnalyticsRepository().lossCount(), "Amount Loss", "Loss");
                dataset.setValue(GameStateFactory.getAnalyticsRepository().winLossRatio(), "Ratio", "Win / Loss Ratio");
            }
        }, 5000, 1000);

        return dataset;
    }

    public JFreeChart getChart() {
        return chart;
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public DefaultCategoryDataset getDataset() {
        return dataset;
    }
}
