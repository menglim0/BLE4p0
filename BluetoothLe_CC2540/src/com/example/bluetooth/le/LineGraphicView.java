package com.example.bluetooth.le;

import java.util.List;
import java.util.ArrayList;

import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;





import android.content.Context;
import android.graphics.Paint.Align;

public class LineGraphicView{
	
	private static XYMultipleSeriesDataset dataset;
	
	
	public static XYMultipleSeriesDataset buildDataset(String[] titles, 
                                                   List xValues, 
                                                   List yValues) 
    { 
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        int length = titles.length;                  //有几条线 
         for (int i = 0; i < length; i++) 
        { 
            XYSeries series = new XYSeries(titles[i]);    //根据每条线的名称创建 
              double[] xV = (double[]) xValues.get(i);                 //获取第i条线的数据 
              double[] yV = (double[]) yValues.get(i); 
            int seriesLength = xV.length;                 //有几个点

              for (int k = 0; k < seriesLength; k++)        //每条线里有几个点 
              { 
                series.add(xV[k], yV[k]); 
            }

            dataset.addSeries(series); 
        }

        return dataset; 
    }
	
	public static XYSeries SeriesName(String titles) 
		{ 
		
		XYSeries series = new XYSeries(titles);    //根据每条线的名称创建 
		return series; 
		}



	public static XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles, boolean fill) 
    { 
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer(); 
        int length = colors.length; 
        for (int i = 0; i < length; i++) 
        { 
            XYSeriesRenderer r = new XYSeriesRenderer(); 
            r.setColor(colors[i]); 
            r.setPointStyle(styles[i]); 
            r.setFillPoints(fill); 
            renderer.addSeriesRenderer(r); 
        } 
        return renderer; 
    }

	public static void setChartSettings(XYMultipleSeriesRenderer renderer, String title, 
                                String xTitle,String yTitle, double xMin, 
                                double xMax, double yMin, double yMax, 
                                int axesColor,int labelsColor) 
    { 
    	renderer.setAxisTitleTextSize(20);// 设置坐标轴标题文本大小
    	renderer.setChartTitleTextSize(20); // 设置图表标题文本大小
    	renderer.setLabelsTextSize(20); // 设置轴标签文本大小
    	renderer.setLegendTextSize(20); // 设置图例文本大小    
    	 renderer.setYLabelsAlign(Align.RIGHT);//设置刻度线与Y轴之间的相对位置关系
        renderer.setChartTitle(title); 
        renderer.setXTitle(xTitle); 
        renderer.setYTitle(yTitle); 
        renderer.setXAxisMin(xMin); 
        renderer.setXAxisMax(xMax); 
        renderer.setYAxisMin(yMin); 
        renderer.setYAxisMax(yMax); 
        renderer.setAxesColor(axesColor); 
        renderer.setLabelsColor(labelsColor); 
    } 
	
	public static void refresh(String[] titles, List<double[]> xValues, List<double[]> yValues, GraphicalView view){
		 cleardata();
		 //XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		 for (int i = 0; i < xValues.size(); i++) {
			  XYSeries series= new XYSeries(titles[i]);
			  double[] xV = xValues.get(i);
		      double[] yV = yValues.get(i);
		      int seriesLength = xV.length;
		      for (int k = 0; k < seriesLength; k++) {
		        series.add(xV[k], yV[k]);
		      }
		      dataset.addSeries(series);
		      view.repaint();
		 }
//		 view.repaint();
	  }
	  
	  public static void cleardata(){
		  //XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		  while(dataset.getSeries().length > 0){
			  XYSeries series= dataset.getSeries()[0];
			  dataset.removeSeries(series);
			  series.clear();
		  }
	  }


}