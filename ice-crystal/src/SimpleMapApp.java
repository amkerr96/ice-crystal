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
import java.util.List;

import org.geonames.*;

/**
 * Demonstrates how to use ImageMarkers with different icons. Note, the used
 * icons contain translucent (the shadows) and transparent (the inner holes)
 * areas.
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
	private double spendFilter;

	HashMap<String, LocationMarker> locations = new HashMap<String, LocationMarker>();
	ArrayList<LocationMarker> selected = new ArrayList<LocationMarker>();
	ArrayList<LocationMarker> oldSelected = new ArrayList<LocationMarker>();
	ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();

	public void setup() {
		spendFilter = 0;
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

		String[] locs = { "Raleigh", "Atlanta", "Charlotte North Carolina", "Nashville" };

		for (String loc : /* locs */CSVParser.locations.keySet()) {
			withState = loc + ", " + CSVParser.states.get(loc);
			searchCriteria.setQ(withState);
			ToponymSearchResult searchResult;
			try {
				searchResult = WebService.search(searchCriteria);
				Toponym toponym = searchResult.getToponyms().get(0);
				if(!toponym.getName().equalsIgnoreCase(loc)) {
					List<Toponym> toponyms = searchResult.getToponyms();
					for (Toponym t : toponyms) {
						if (t.getName().equalsIgnoreCase(loc)) {
							toponym = t;
							break;
						}
					}
				}
				if (!toponym.getName().equalsIgnoreCase(loc)) {
					println(loc + " NOT FOUND!");
				} else {
					println("FOUND " + loc + " = " + toponym);
					Location coord = new Location(toponym.getLatitude(), toponym.getLongitude());

					if (!locations.containsKey(loc)) {
						// Put other data in here
						double spend = CSVParser.locations.get(loc);
						locations.put(loc,
								new LocationMarker(toponym.getName(), coord, spend, loadImage("ui/marker_gray.png")));
						if (spend > maxSpend) {
							maxSpend = spend;
						}
						if (spend < minSpend) {
							minSpend = spend;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(locations);
	}

	public void draw() {
		currentMap.draw();
		// currentMap.addMarkers(imgMarker1, imgMarker2, imgMarker3);
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
		LocationMarker hitMarker = (LocationMarker) currentMap.getDefaultMarkerManager().getNearestMarker(mouseX,
				mouseY);
		println("\nClick! Hit marker: " + hitMarker.getName() + hitMarker.getLocation() + hitMarker.getSpend());
		if (currentMap.getScreenPosition(hitMarker.getLocation())
				.dist(new ScreenPosition(mouseX, mouseY)) < 14/* magic # */) {
			if (selected.contains(hitMarker)) {
				selected.remove(hitMarker);
			} else {
				selected.add(hitMarker);
			}
			updateMarkers();
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

	public void updateMarkers() {
		//print("Selected: ");
		for(LocationMarker lm: selected) {
			print(lm.getName() + ", ");
		}
		println();
		for(String s: locations.keySet()) {
			locations.get(s).setImage(loadImage("ui/marker_gray.png"));
			locations.get(s).setOn(false);
		}
		for(LocationMarker m: selected) {
			if(m.getSpend() >= spendFilter) {
				locations.get(m.getName().toUpperCase()).setImage(loadImage("ui/marker_red.png"));
				locations.get(m.getName().toUpperCase()).setOn(true);
			}
		}

		changes.firePropertyChange("selected", oldSelected, selected);
		oldSelected.clear();
		for (LocationMarker i : selected) {
			oldSelected.add(i);
		}
		map1.getDefaultMarkerManager().draw();
	}

	public double getMaxSpend() {
		return maxSpend;
	}

	public double getMinSpend() {
		return minSpend;
	}

	public void removeByLocation(String state) {
		println("Removing all from " + state);
		for (String s : CSVParser.states.keySet()) {
			String st = CSVParser.states.get(s);
			if (st.equals(state)) {
				if (selected.contains(locations.get(s))) {
					selected.remove(locations.get(s));
				}
			}
		}
		updateMarkers();
	}

	public void addByLocation(String state) {
		println("Adding all from " + state);
		for (String s : CSVParser.states.keySet()) {
			String st = CSVParser.states.get(s);
			if (st.equals(state)) {
				if (!selected.contains(locations.get(s))) {
					selected.add(locations.get(s));
				}
			}
		}
		updateMarkers();
	}

	public void addAll() {
		println("Adding all");
		for (String s : locations.keySet()) {
			if (!selected.contains(locations.get(s))) {
				selected.add(locations.get(s));
			}
		}
		updateMarkers();
	}

	public void removeAll() {
		println("Removing all");
		for (String s : locations.keySet()) {
			if (selected.contains(locations.get(s))) {
				selected.remove(locations.get(s));
			}
		}
		updateMarkers();
	}
	
	public void setSpendFilter(int s) {
		spendFilter = s;
		//println("UPDATED SPEND FILTER");
		updateMarkers();
	}
	
	public ArrayList<LocationMarker> getDrawn() {
		ArrayList<LocationMarker> drawn = new ArrayList<LocationMarker>();
		for(LocationMarker lm: selected) {
			if(lm.getOn()) {
				drawn.add(lm);
			}
		}
		return drawn;
	}

}