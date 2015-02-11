package FeatureExtraction;

public class KJH_Utility {
	
	//Round Function
   static int Round(double _x){
	   int y;
	   if(_x >= (int)_x+05)
	      y = (int)_x++;
	   else
	      y = (int)_x;
	   return y;
   }
}
