package com.lqSPRimaging.util;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class ChartUtil {

    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

    private GraphicalView mChartView;

    private XYSeries series1 = null;
    private XYSeries series2 = null;
    private XYSeries series3 = null;
    private XYSeries series4 = null;


    public ChartUtil() {

        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.argb(210, 255, 250, 205));
        mRenderer.setAxisTitleTextSize(40);
        mRenderer.setChartTitleTextSize(40);
        mRenderer.setAxesColor(Color.BLACK);
        mRenderer.setLabelsColor(Color.BLACK);
        mRenderer.setChartTitle("Realtime response");
        mRenderer.setXTitle("Time(s)");
        mRenderer.setYTitle("Intensity(a.u.)");
        mRenderer.setLabelsTextSize(40);
        mRenderer.setLegendTextSize(40);
        mRenderer.setMargins(new int[]{0, 100, 40, 50});
        mRenderer.setMarginsColor(Color.argb(210, 255, 250, 205));
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setPointSize(8);
        mRenderer.setShowGrid(false);
        mRenderer.setGridColor(Color.GRAY);
        mRenderer.setXLabelsColor(Color.BLACK);
        mRenderer.setYLabelsColor(0, Color.BLACK);
    }


    public void addSeries(int mode) {
        if (mode == 1) {

            //addSeries_1();
            addSeries_2();
        } else if (mode == 2) {
            addSeries_2();
            addSeries_3();
        } else if (mode == 4) {
            addSeries_1();
            addSeries_2();
            addSeries_3();
            addSeries_4();
        }
        mChartView.repaint();
    }


    public void initChartView(LinearLayout layout, Context context) {
        if (mChartView == null) {
            mChartView = ChartFactory.getLineChartView(context, mDataset, mRenderer);
            mRenderer.setClickEnabled(false);
            mRenderer.setSelectableBuffer(0);

            layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));


        } else {
            mChartView.repaint();
        }

    }


    public void addSeries_1() {
        XYSeries series1 = new XYSeries("Blue");
        mDataset.addSeries(series1);
        XYSeriesRenderer renderer1 = new XYSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer1);
        renderer1.setFillPoints(true);
        renderer1.setDisplayChartValues(false);
        renderer1.setColor(Color.BLUE);
        setSeries1(series1);
    }

    public void addSeries_2() {
        XYSeries series2 = new XYSeries("Green");
        mDataset.addSeries(series2);
        XYSeriesRenderer renderer2 = new XYSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer2);
        renderer2.setFillPoints(true);
        renderer2.setDisplayChartValues(false);
        renderer2.setColor(Color.GREEN);
        renderer2.setLineWidth(5);
        setSeries2(series2);
    }

    public void addSeries_3() {
        XYSeries series3 = new XYSeries("Red");
        mDataset.addSeries(series3);
        XYSeriesRenderer renderer3 = new XYSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer3);
        renderer3.setFillPoints(true);
        renderer3.setDisplayChartValues(false);
        renderer3.setColor(Color.RED);
        renderer3.setLineWidth(5);
        setSeries3(series3);
    }


    public void addSeries_4() {
        XYSeries series4 = new XYSeries("Grayscale");
        mDataset.addSeries(series4);
        XYSeriesRenderer renderer4 = new XYSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer4);
        renderer4.setFillPoints(true);
        renderer4.setDisplayChartValues(false);
        renderer4.setColor(Color.BLACK);
        setSeries4(series4);
    }


    public XYSeries getSeries1() {
        return series1;
    }

    public void setSeries1(XYSeries series1) {
        this.series1 = series1;
    }

    public XYSeries getSeries2() {
        return series2;
    }

    public void setSeries2(XYSeries series2) {
        this.series2 = series2;
    }


    public XYSeries getSeries3() {
        return series3;
    }

    public void setSeries3(XYSeries series3) {
        this.series3 = series3;
    }

    public XYSeries getSeries4() {
        return series4;
    }

    public void setSeries4(XYSeries series4) {
        this.series4 = series4;
    }


    public GraphicalView getmChartView() {
        return mChartView;
    }

    public void initSeries() {

        if (series1 != null) {
            series1.clear();
        }
        if (series2 != null) {
            series2.clear();
        }
        if (series3 != null) {
            series3.clear();
        }
        if (series4 != null) {
            series4.clear();
        }

    }

}
