import processing.core.PApplet;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;

import java.util.HashMap;

import org.geonames.*;

/**
 * Demonstrates how to use ImageMarkers with different icons. Note, the used icons contain translucent (the shadows) and
 * transparent (the inner holes) areas.
 */
public class SimpleMapApp extends PApplet {

	Location berlinLocation = new Location(52.5f, 13.4f);
	Location veniceLocation = new Location(45.44f, 12.34f);
	Location lisbonLocation = new Location(38.71f, -9.14f);

	UnfoldingMap currentMap;
	UnfoldingMap map1;
	UnfoldingMap map2;
	UnfoldingMap map3;
	ImageMarker imgMarker1;
	ImageMarker imgMarker2;
	ImageMarker imgMarker3;
	ImageMarker img4;
	
	int numLocations;
	HashMap<String, Location> coords = new HashMap<String, Location>();
	
	// Just an example of how to use geoNames
	String searchName = "CHATTANOOGA TN";
	ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();

	public void setup() {
		size(800, 600, OPENGL);

		map1 = new UnfoldingMap(this, new Google.GoogleMapProvider());
	    map2 = new UnfoldingMap(this, new Microsoft.AerialProvider());
	    map3 = new UnfoldingMap(this, new OpenStreetMap.OpenStreetMapProvider());
	    MapUtils.createDefaultEventDispatcher(this, map1, map2, map3);
	    
	    currentMap = map1;
		
		// geoNames example continued
		WebService.setUserName("icecrystal");
		searchCriteria.setQ(searchName);
		try {
			ToponymSearchResult searchResult = WebService.search(searchCriteria);
			println("\n---RESULTS---\n");
			Toponym toponym = searchResult.getToponyms().get(0);
			img4 = new ImageMarker(new Location(toponym.getLatitude(), toponym.getLongitude()), loadImage("ui/marker_gray.png"));
			println(toponym.getName() + " " + toponym.getCountryName() + " " + toponym.getLongitude() + " " + toponym.getLatitude());
		} catch(Exception e) {
			e.printStackTrace();
		}		
	}
	
	
	
	public void loadLocations() {
		String withState = null;
//		int locLength = CSVParser.locations.keySet().size();
		WebService.setUserName("icecrystal");
		
		for (String loc : CSVParser.locations.keySet()) {
			withState = loc + ", " + CSVParser.states.get(loc);
			
			searchCriteria.setQ(withState);
			ToponymSearchResult searchResult;
			try {
				searchResult = WebService.search(searchCriteria);
				Toponym toponym = searchResult.getToponyms().get(0);
				Location coord = new Location(toponym.getLatitude(), toponym.getLongitude());
				
				coords.put(loc, coord);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(coords);
	}

	public void draw() {
		currentMap.draw();
		//currentMap.addMarkers(imgMarker1, imgMarker2, imgMarker3);
	}
	
	public void draw2() {
		loadLocations();
		currentMap.draw();
		for (Location coord : coords.values()) {
			currentMap.addMarker(new ImageMarker(coord, loadImage("ui/marker_gray.png")));
		}
	}
	 
	public void keyPressed() {
	    if (key == '1') {
	        currentMap = map1;
	    } else if (key == '2') {
	        currentMap = map2;
	    } else if (key == '3') {
	        currentMap = map3;
	    }
	}
}