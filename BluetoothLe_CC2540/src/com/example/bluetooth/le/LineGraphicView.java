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

        int length = titles.length;                  //�м����� 
         for (int i = 0; i < length; i++) 
        { 
            XYSeries series = new XYSeries(titles[i]);    //����ÿ���ߵ����ƴ��� 
              double[] xV = (double[]) xValues.get(i);                 //��ȡ��i���ߵ����� 
              double[] yV = (double[]) yValues.get(i); 
            int seriesLength = xV.length;                 //�м�����

              for (int k = 0; k < seriesLength; k++)        //ÿ�������м����� 
              { 
                series.add(xV[k], yV[k]); 
            }

            dataset.addSeries(series); 
        }

        return dataset; 
    }
	
	public static XYSeries SeriesName(String titles) 
		{ 
		
		XYSeries series = new XYSeries(titles);    //����ÿ���ߵ����ƴ��� 
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
    	renderer.setAxisTitleTextSize(20);// ��������������ı���С
    	renderer.setChartTitleTextSize(20); // ����ͼ������ı���С
    	renderer.setLabelsTextSize(20); // �������ǩ�ı���С
    	renderer.setLegendTextSize(20); // ����ͼ���ı���С    
    	 renderer.setYLabelsAlign(Align.RIGHT);//���ÿ̶�����Y��֮������λ�ù�ϵ
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