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
	private double minSpend;
	private double maxSpend;
	
	HashMap<String, LocationMarker> locations = new HashMap<String, LocationMarker>();
	ArrayList<LocationMarker> selected = new ArrayList<LocationMarker>();
	ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();

	public void setup() {
		minSpend = Double.MAX_VALUE;
		maxSpend = Double.MIN_VALUE;
		
		size(900, 800, OPENGL);

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
					double spend = CSVParser.locations.get(loc);
					locations.put(loc, new LocationMarker(toponym.getName(), coord, spend, loadImage("ui/marker_gray.png")));
					if(spend > maxSpend) {
						maxSpend = spend;
					} 
					if(spend < minSpend) {
						minSpend = spend;
					}				}
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
		ArrayList<LocationMarker> newSelected = new ArrayList<LocationMarker>();
		ArrayList<LocationMarker> oldSelected = new ArrayList<LocationMarker>();
		for(LocationMarker i : selected) {
			newSelected.add(i);
			oldSelected.add(i);
		}
	    LocationMarker hitMarker = (LocationMarker) currentMap.getDefaultMarkerManager().getNearestMarker(mouseX, mouseY);
	    println("\nClick! Hit marker: " + hitMarker.getName() + hitMarker.getLocation() + hitMarker.getSpend());
	    if(currentMap.getScreenPosition(hitMarker.getLocation()).dist(new ScreenPosition(mouseX, mouseY)) < 14/*magic #*/) {
	    	if(selected.contains(hitMarker)) {
	    		hitMarker.setImage(loadImage("ui/marker_gray.png"));
	    		newSelected.remove(hitMarker);
	    	} else {
	    		hitMarker.setImage(loadImage("ui/marker_red.png"));
	    		newSelected.add(hitMarker);
	    	}
    		selected = newSelected;
    	    changes.firePropertyChange("selected", oldSelected, newSelected);
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
	
	public ArrayList<LocationMarker> getSelected() {
		return selected;
	}
	
	public void selectGroup(String state, boolean add) {
		ArrayList<LocationMarker> newSelected = new ArrayList<LocationMarker>();
		ArrayList<LocationMarker> oldSelected = new ArrayList<LocationMarker>();
		for(LocationMarker i : selected) {
			newSelected.add(i);
			oldSelected.add(i);
		}
		
		if(state.equals("all")) {
			for(String s: locations.keySet()) {
				if(add) {
					if(!selected.contains(locations.get(s))) {
						newSelected.add(locations.get(s));
			    		locations.get(s).setImage(loadImage("ui/marker_red.png"));
					}
				} else {
					if(selected.contains(locations.get(s))) {
						newSelected.remove(locations.get(s));
			    		locations.get(s).setImage(loadImage("ui/marker_gray.png"));
					}
				}
			}
		} else {
			println((add?"Adding ":"Removing ") + "all from " + state);
	
			for(String s: CSVParser.states.keySet()) {
				String st = CSVParser.states.get(s);
				if(st.equals(state)) {
					if(add) {
						if(!selected.contains(locations.get(s))) {
							newSelected.add(locations.get(s));
				    		locations.get(s).setImage(loadImage("ui/marker_red.png"));
						}
					} else {
						if(selected.contains(locations.get(s))) {
							newSelected.remove(locations.get(s));
				    		locations.get(s).setImage(loadImage("ui/marker_gray.png"));
						}
					}
				}
			}
		} 
		
		selected = newSelected;
	    changes.firePropertyChange("selected", oldSelected, newSelected);
		map1.getDefaultMarkerManager().draw();
	}
	
	public double getMaxSpend() {
		return maxSpend;
	}
	
	public double getMinSpend() {
		return minSpend;
	}			
}