import processing.core.PApplet;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;

import org.geonames.*;

/**
 * Demonstrates how to use ImageMarkers with different icons. Note, the used icons contain translucent (the shadows) and
 * transparent (the inner holes) areas.
 */
public class SimpleMapApp extends PApplet {
	
	private PropertyChangeSupport changes = new PropertyChangeSupport(this);

	UnfoldingMap currentMap;
	UnfoldingMap map1;
	UnfoldingMap map2;
	UnfoldingMap map3;
	int numLocations;
	
	HashMap<String, ImageMarker> locations = new HashMap<String, ImageMarker>();
	ArrayList<ImageMarker> selected = new ArrayList<ImageMarker>();
	ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();

	public void setup() {
		size(800, 600, OPENGL);

		map1 = new UnfoldingMap(this, new Google.GoogleMapProvider());
	    map2 = new UnfoldingMap(this, new Microsoft.AerialProvider());
	    map3 = new UnfoldingMap(this, new OpenStreetMap.OpenStreetMapProvider());
	    MapUtils.createDefaultEventDispatcher(this, map1, map2, map3);
	    
	    currentMap = map1;
		
		WebService.setUserName("icecrystal");
	}
	
	
	
	public void loadLocations() {
		String withState = null;
		WebService.setUserName("icecrystal");
		
		String[] locs = {"Raleigh", "Atlanta", "Charlotte North Carolina", "Nashville"};
				
		for (String loc : /*locs*/CSVParser.locations.keySet()) {
			withState = loc + ", " + CSVParser.states.get(loc);			
			searchCriteria.setQ(withState);
			ToponymSearchResult searchResult;
			try {
				searchResult = WebService.search(searchCriteria);
				Toponym toponym = searchResult.getToponyms().get(0);
				println(toponym);
				Location coord = new Location(toponym.getLatitude(), toponym.getLongitude());
				
				if(!locations.containsKey(loc)) {
					// Put other data in here
					locations.put(loc, new ImageMarker(toponym.getName(), coord, loadImage("ui/marker_gray.png")));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(locations);
	}

	public void draw() {
		currentMap.draw();
		//currentMap.addMarkers(imgMarker1, imgMarker2, imgMarker3);
	}
	
	public void draw2() {
		loadLocations();
		currentMap.draw();
		for (String loc : locations.keySet()) {
			currentMap.addMarker(locations.get(loc));
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
	
	public void mouseClicked() {
		ArrayList<ImageMarker> newSelected = new ArrayList<ImageMarker>();
		ArrayList<ImageMarker> oldSelected = new ArrayList<ImageMarker>();
		for(ImageMarker i : selected) {
			newSelected.add(i);
			oldSelected.add(i);
		}
	    ImageMarker hitMarker = (ImageMarker) currentMap.getDefaultMarkerManager().getNearestMarker(mouseX, mouseY);
	    println("\nClick! Hit marker: " + hitMarker.getName() + hitMarker.getLocation());
	    if(currentMap.getScreenPosition(hitMarker.getLocation()).dist(new ScreenPosition(mouseX, mouseY)) < 14/*magic #*/) {
	    	if(selected.contains(hitMarker)) {
	    		hitMarker.setImage(loadImage("ui/marker_gray.png"));
	    		newSelected.remove(hitMarker);
	    		selected = newSelected;
	    	    changes.firePropertyChange("selected", oldSelected, newSelected);
	    	} else {
	    		hitMarker.setImage(loadImage("ui/marker_red.png"));
	    		newSelected.add(hitMarker);
	    		selected = newSelected;
	    	    changes.firePropertyChange("selected", oldSelected, newSelected);
	    	}
    		map1.getDefaultMarkerManager().draw();
	    } else {
	    	println("Too far away!");
	    }

	}
	
	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}
	
	public ArrayList<ImageMarker> getSelected() {
		return selected;
	}
}