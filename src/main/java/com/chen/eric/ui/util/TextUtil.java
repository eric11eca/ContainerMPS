package com.chen.eric.ui.util;

import java.util.HashMap;
import java.util.Map;

public class TextUtil {
	
	public static final String[] countries = new String[] {"U.S.", "China", "Russia", "Japan", "Australia", 
    		"Canada", "South Korea", "Indonesia"};
	public static final String[] usStates = new String[] {"CA","WA"};
	public static final String[] chinaStates = new String[] {"Liaoning", "Shanghai", "Zhejiang", "Guangdong", "Jiangsu"};
	public static final String[] russiaStates = new String[] {"Primorskij kraj"};
	public static final String[] japanStates = new String[] {"Aichi", "Honshu", "Tokyo", "Hyogo"};
	public static final String[] koreaStates = new String[] {"Yeongnam"};
	public static final String[] indonesiaStates = new String[] {"Jakarta"};
	public static final String[] canadaStates = new String[] {"British Columbia", "Nova Scotia", "Québec"};
	public static final String[] australiaStates = new String[] {"Queensland", "New South Wales", "Victoria"};
	
	public static final String[] primorski = new String[] {"Vladivostok"};
	public static final String[] liaoning = new String[] {"Dalian"};
	public static final String[] shanghai = new String[] {"Shanghai"};
	public static final String[] zhejiang = new String[] {"Ningbo", "Zhoushan", "Wenzhou"};
	public static final String[] guangdong = new String[] {"Zhanjiang", "Shenzheng", "Zhuhai"};
	public static final String[] jiangsu = new String[] {"Taicang", "Nantong"};
	public static final String[] wa = new String[] {"Seattle"};
	public static final String[] ca = new String[] {"San Francisco", "Los Angelas", "San Diego"};

	public static final String[] Aichi = new String[] {"Nagoya"};
	public static final String[] Honshu = new String[] {"Osaka"};
	public static final String[] Tokyo = new String[] {"Tokyo"};
	public static final String[] Hyogo = new String[] {"Kobe"};
	
	public static final String[] Yeongnam  = new String[] {"Buson"};
	public static final String[] Jakarta = new String[] {"Tanjung Priok"};
	
	public static final String[] British  = new String[] {"Prince Rupert"};
	public static final String[] Nova  = new String[] {"Halifax"};
	public static final String[] Quebec  = new String[] {"Montreal"};
	
	public static final String[] Queensland = new String[] {"Brisbane"};
	public static final String[] Wales = new String[] {"Sydney"};
	public static final String[] Victoria = new String[] {"Melbourne"};
	
	public static Map<String, String[]> stateMap = initStateMap();
	public static Map<String, String[]> portMap = initPortMap();

	public static Map<String, String[]> initStateMap() {
		stateMap = new HashMap<>();
		stateMap.put("U.S.", usStates);
		stateMap.put("China", chinaStates);
		stateMap.put("Russia", russiaStates);
		stateMap.put("Japan", japanStates);
		stateMap.put("Australia", canadaStates);
		stateMap.put("Canada", canadaStates);
		stateMap.put("South Korea", koreaStates);
		stateMap.put("Indonesia", indonesiaStates);
		stateMap.put("Australia", australiaStates);
		return stateMap;
	}
	
	public static Map<String, String[]> initPortMap() {
		portMap = new HashMap<>();
		portMap.put("Primorskij kraj", primorski);
		portMap.put("Aichi", Aichi);
		portMap.put("Honshu", Honshu);
		portMap.put("Hyogo", Hyogo);
		portMap.put("Yeongnam", Yeongnam);
		portMap.put("Tokyo", Tokyo);
		portMap.put("Jakarta", Jakarta);
		portMap.put("Liaoning", liaoning);
		portMap.put("Zhejiang", zhejiang);
		portMap.put("Guangdong", guangdong);
		portMap.put("Shanghai", shanghai);
		portMap.put("Jiangsu", jiangsu);
		portMap.put("CA", ca);
		portMap.put("WA", wa);
		
		portMap.put("British Columbia", British);
		portMap.put("Nova Scotia", Nova);
		portMap.put("Québec", Quebec);	
		
		portMap.put("Queensland", Queensland);
		portMap.put("New South Wales", Wales);
		portMap.put("Victoria", Victoria);
		return portMap;
	}
}
