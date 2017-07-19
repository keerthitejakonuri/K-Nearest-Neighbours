package com.copart;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

class Dataset{
	float[][] dataset1;
	String[][] dataset2;
	public Dataset(float[][] dataset1, String[][] dataset2){
		this.dataset1 = dataset1;
		this.dataset2 = dataset2;
	}
}

class Points{
	int point1, point2;
	public Points(int point1, int point2){
		this.point1 = point1;
		this.point2 = point2;
	}
}

public class KNN {
	
	public static HashMap<Integer,String> mapNames;
	
	public static int count(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	    byte[] c = new byte[1024];
	    int count = 0;
	    int readChars = 0;
	    boolean empty = true;
	    while ((readChars = is.read(c)) != -1) {
	        empty = false;
	        for (int i = 0; i < readChars; ++i) {
	            if (c[i] == '\n') {
	                ++count;
	            }
	        }
	    }
	    return (count == 0 && !empty) ? 1 : count;
	    } finally {
	    is.close();
	   }
	}
	
	public static Dataset parseDataSet(String FileName, int row) throws IOException{
		BufferedReader Buff = new BufferedReader(new FileReader(FileName));
        String text = Buff.readLine();
        String[] ip = text.split(",");
        HashMap<Integer,String> map = new HashMap<Integer, String>();
        for(int i = 0; i < ip.length - 1; i++){
        	map.put(i, ip[i]);
        }
        mapNames = map;
        //printMap(mapNames);
        int col = ip.length;
        float[][] dataset1 = new float[row][2];
        String[][] dataset2 = new String[row][col-2];
        System.out.println("No of rows are "+row);
		for(int i = 0; i < row; i++){
			text = Buff.readLine();
			String ip1[] = text.split(",");
			for(int j = 0; j < ip1.length; j++){
				if(j == 1 || j == 2){
					if(ip1[j].equals("")){
						ip1[j] = "0";
					}
					dataset1[i][j-1] = Float.valueOf(ip1[j]);
					//System.out.println(j+" "+dataset1[i][j]);
				}else if(j == 0){
					dataset2[i][j] = ip1[j];
					//System.out.println(j+" "+dataset2[i][j]);
				}else{
					dataset2[i][j-2] = ip1[j];
					//System.out.println(j+" "+dataset2[i][j-2]);
				}
			}
		}
		Buff.close();
		return new Dataset(dataset1,dataset2);
	}
	
	public static Dataset handleMissingData(Dataset data){
		float d1 = 0, d2 = 0;
		for(int i = 0; i < data.dataset1.length; i++){
			for(int j = 0; j < data.dataset1[0].length; j++){
				if(j == 0){
					d1 += data.dataset1[i][j];
				}else{
					d2 += data.dataset1[i][j];
				}
			}
		}
		//System.out.println(data.dataset1[0].length);
		//System.out.println(d1+" "+d2);
		 d1 /= data.dataset1.length;
		 d2 /= data.dataset1.length;
		 //System.out.println(d1+" "+d2);
		for(int i = 0; i < data.dataset1.length; i++){
			for(int j = 0; j < data.dataset1[0].length; j++){
				if(j == 0){
					if(data.dataset1[i][j]/1 == 0){
						data.dataset1[i][j] = d1;
					}
				}else{
					if(data.dataset1[i][j]/1 == 0){
						data.dataset1[i][j] = d2;
					}
				}
			}
		}
		return data;
	}
	
	public static Points calculateTwoNearestNeighbours(Dataset data, String longitude, String latitude){
		float lo = Float.valueOf(longitude);
		float la = Float.valueOf(latitude);
		int point1 = 0, point2 = 0;
		float distance1 = Float.MAX_VALUE, distance2 = Float.MAX_VALUE;
		for(int i = 0; i < data.dataset1.length;i++){
			float x = data.dataset1[i][0];
			float y = data.dataset1[i][1];
			float distance = (x-lo)*(x-lo) + (y-la)*(y-la);
			if((distance < distance1)&&(distance < distance2)){
				distance2 = distance1;
				distance1 = distance;
				point2 = point1;
				point1 = i;
			}else if(distance < distance2){
				distance2 = distance;
				point2 = i;
			}
		}
		System.out.println("Two nearest neighbours to ["+longitude+" , "+latitude+"]");
		System.out.println("[ "+data.dataset2[point1][0] +" , "+ data.dataset1[point1][0]+" , "+data.dataset1[point1][1]+" , "+data.dataset2[point1][1]+" , "+data.dataset2[point1][2]+" , "+data.dataset2[point1][3]);
		System.out.println("[ "+data.dataset2[point2][0] +" , "+ data.dataset1[point2][0]+" , "+data.dataset1[point2][1]+" , "+data.dataset2[point2][1]+" , "+data.dataset2[point2][2]+" , "+data.dataset2[point2][3]);
	
		return new Points(point1,point2);
	}
	
	public static Points calculateNextTwoNearestNeighbours(Dataset data, String longitude, String latitude, Points p){
		float lo = Float.valueOf(longitude);
		float la = Float.valueOf(latitude);
		int point1 = 0, point2 = 0;
		float distance1 = Float.MAX_VALUE, distance2 = Float.MAX_VALUE;
		for(int i = 0; i < data.dataset1.length;i++){
			if(i != p.point1 && i != p.point2){
				float x = data.dataset1[i][0];
				float y = data.dataset1[i][1];
				float distance = (x-lo)*(x-lo) + (y-la)*(y-la);
				if((distance < distance1)&&(distance < distance2)){
					distance2 = distance1;
					distance1 = distance;
					point2 = point1;
					point1 = i;
				}else if(distance < distance2){
					distance2 = distance;
					point2 = i;
				}
			}
		}
		
		System.out.println("Next two nearest neighbours to ["+longitude+" , "+latitude+"]");
		System.out.println("[ "+data.dataset2[point1][0] +" , "+ data.dataset1[point1][0]+" , "+data.dataset1[point1][1]+" , "+data.dataset2[point1][1]+" , "+data.dataset2[point1][2]+" , "+data.dataset2[point1][3]);
		System.out.println("[ "+data.dataset2[point2][0] +" , "+ data.dataset1[point2][0]+" , "+data.dataset1[point2][1]+" , "+data.dataset2[point2][1]+" , "+data.dataset2[point2][2]+" , "+data.dataset2[point2][3]);
		
	return new Points(point1,point2);
	}
	
	public static void main(String[] args)throws IOException {
		// TODO Auto-generated method stub
			int count = count(args[0]);
			Dataset data = parseDataSet(args[0],count-1);
			data = handleMissingData(data);
			Points p = calculateTwoNearestNeighbours(data,args[1],args[2]);
			Points p1 = calculateNextTwoNearestNeighbours(data,args[1],args[2],p);
	}

}
