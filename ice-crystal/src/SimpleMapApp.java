import processing.core.PApplet;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
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
 * @author aarkerr
 * @author rblowers
 */
public class SimpleMapApp extends PApplet {

	/** Property change supporter */
	private PropertyChangeSupport changes = new PropertyChangeSupport(this);

	/** Which map is currently being used */
	private UnfoldingMap currentMap;
	/** Google style map */
	private UnfoldingMap mapGoogle;
	/** Microsoft style map */
	private UnfoldingMap mapMs;
	/** Street style map */
	private UnfoldingMap mapStreet;
	/** Number of locations */
	private int numLocations;
	/** Minimum spend */
	private double minSpend;
	/** Maximum spend */
	private double maxSpend;
	/** Amount of spend by which we're filtering */
	private double spendFilter;
	/** Architecture on which we're filtering */
	private String archFilter;
	
	/** List of product IDs on which we're filtering */
	private ArrayList<String> productIdFilter = new ArrayList<String>();
	/** Map of of locations name -> marker */
	private HashMap<String, LocationMarker> locations = new HashMap<String, LocationMarker>();
	/** List of selected markers */
	private ArrayList<LocationMarker> selected = new ArrayList<LocationMarker>();
	/** Old list of selected markers (needed for changeListener) */
	private ArrayList<LocationMarker> oldSelected = new ArrayList<LocationMarker>();
	/** Search criteria for geoNames lookup */
	private ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();

	/**
	 * Setup map
	 */
	public void setup() {
		// Default filter by all
		archFilter = "All";
		// Default spend > 0
		spendFilter = 0;
		// Initial values for min and max
		minSpend = Double.MAX_VALUE;
		maxSpend = Double.MIN_VALUE;

		size(900, 800, OPENGL);

		// These are useless currently, but fun
		mapGoogle = new UnfoldingMap(this, new Google.GoogleMapProvider());
		mapMs = new UnfoldingMap(this, new Microsoft.AerialProvider());
		mapStreet = new UnfoldingMap(this, new OpenStreetMap.OpenStreetMapProvider());
		MapUtils.createDefaultEventDispatcher(this, mapGoogle, mapMs, mapStreet);

		currentMap = mapGoogle;
		
		WebService.setUserName("icecrystal");
	}

	/**
	 * Load in locations from parsed CSV
	 */
	public void loadLocations() {
		System.out.println("Loading locations");
		String withState = null;

		// For testing
		//String[] locs = { "Raleigh", "Atlanta", "Charlotte North Carolina", "Nashville" };

		// For each location, lookup and add
		for (String loc : /* locs */CSVParser.locations.keySet()) {
			withState = loc + ", " + CSVParser.states.get(loc);
			searchCriteria.setQ(withState);
			ToponymSearchResult searchResult;
			try {
				// Search for location
				//System.out.println("Searching for " + searchCriteria);
				searchResult = WebService.search(searchCriteria);
				//System.out.println("Done searching for " + searchCriteria);
				// Try first result
				Toponym toponym = searchResult.getToponyms().get(0);
				// If first result isn't accurate, keep looking
				if(!toponym.getName().equalsIgnoreCase(loc)) {
					List<Toponym> toponyms = searchResult.getToponyms();
					for (Toponym t : toponyms) {
						// Found
						if (t.getName().equalsIgnoreCase(loc)) {
							toponym = t;
							break;
						}
					}
				}
				// Location not found
				if (!toponym.getName().equalsIgnoreCase(loc)) {
					println(loc + " NOT FOUND!");
				} else {
					// Location found
					println("Found " + loc + " = " + toponym);
					Location coord = new Location(toponym.getLatitude(), toponym.getLongitude());

					// Add location
					if (!locations.containsKey(loc)) {
						double spend = CSVParser.locations.get(loc);
						locations.put(loc,
								new LocationMarker(toponym.getName(), coord, spend, loadImage("ui/marker_gray.png")));
						// Update min/max spend
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
		println("Done loading locations");
	}

	/**
	 * Draw map
	 */
	public void draw() {
		currentMap.draw();
	}

	/**
	 * Draw map,add markers
	 */
	public void drawMap() {
		loadLocations();
		currentMap.draw();
		for (String loc : locations.keySet()) {
			currentMap.addMarker(locations.get(loc));
		}
	}

	/**
	 * Change map style on key press (not fully implemented)
	 */
	public void keyPressed() {
		if (key == '1') {
			currentMap = mapGoogle;
		} else if (key == '2') {
			currentMap = mapMs;
		} else if (key == '3') {
			currentMap = mapStreet;
		}
	}

	/**
	 * Click to select markers
	 */
	public void mouseClicked() {
		if (GUIdriver.fileSelected) {
			// Find nearest marker to click
			LocationMarker hitMarker = (LocationMarker) currentMap.getDefaultMarkerManager().getNearestMarker(mouseX,
					mouseY);
			println("\nClick! Near: " + hitMarker.getName() + hitMarker.getLocation() + hitMarker.getSpend());
			// If not too far away (need a better way to define this)
			if (currentMap.getScreenPosition(hitMarker.getLocation())
					.dist(new ScreenPosition(mouseX, mouseY)) < 14/* magic # */) {
				// Toggle selected
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
	}

	/**
	 * Add property change listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	/**
	 * Remove property change listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

	/**
	 * Return selected markers
	 */
	public ArrayList<LocationMarker> getSelected() {
		return selected;
	}

	/**
	 * Update which markers appear selected on the map
	 */
	public void updateMarkers() {
		// Reset all to appear unselected
		for(String s: locations.keySet()) {
			locations.get(s).setImage(loadImage("ui/marker_gray.png"));
			locations.get(s).setOn(false);
		}
		// For each marker...
		for(LocationMarker m: selected) {
			String name = m.getName().toUpperCase();
			// If marker passes all filters
			if(m.getSpend() >= spendFilter && (archFilter == "All" || CSVParser.archLocations.get(name).containsKey(archFilter))) {
				boolean hasId = false;
				HashMap<String, Integer> prods = CSVParser.prodLocations.get(name);
				if(productIdFilter.isEmpty()) {
					hasId = true;
				}
				// Still testing filters (productID)
				for(String id: productIdFilter) {
					if(prods.containsKey(id)) {
						hasId = true;
						break;
					}
				}
				// Draw marker as selected
				if(hasId) {
					locations.get(name).setImage(loadImage("ui/marker_red.png"));
					locations.get(name).setOn(true);
				}
			}
		}

		// Fire propery change so that GUIDriver class knows to update
		changes.firePropertyChange("selected", oldSelected, selected);
		//Update "old" selected
		oldSelected.clear();
		for (LocationMarker i : selected) {
			oldSelected.add(i);
		}
		// Redraw
		mapGoogle.getDefaultMarkerManager().draw();
	}

	/**
	 * Return the maximum spend
	 * @return the maximum spend
	 */
	public double getMaxSpend() {
		return maxSpend;
	}

	/**
	 * Return the minimum spend
	 * @return the minimum spend
	 */
	public double getMinSpend() {
		return minSpend;
	}

	/**
	 * Remove all markers within a given region/location/state
	 * @param state the region to remove all markers from
	 */
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

	/**
	 * Add all markers within a given region/location/state
	 * @param state the region to add all markers from
	 */
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

	/**
	 * Make all markers selected
	 */
	public void addAll() {
		println("Adding all");
		for (String s : locations.keySet()) {
			if (!selected.contains(locations.get(s))) {
				selected.add(locations.get(s));
			}
		}
		updateMarkers();
	}

	/** 
	 * Make all markers unselected 
	 */
	public void removeAll() {
		println("Removing all");
		for (String s : locations.keySet()) {
			if (selected.contains(locations.get(s))) {
				selected.remove(locations.get(s));
			}
		}
		updateMarkers();
	}
	
	/**
	 * Update the spend filter
	 * @param s the selected spend filter
	 */
	public void setSpendFilter(int s) {
		spendFilter = s;
		println("Filtering by spend > " + s);
		// Update based on filter
		updateMarkers();
	}
	
	/**
	 * Return all markers that have RED pins
	 * @return all "drawn" markers 
	 */
	public ArrayList<LocationMarker> getDrawn() {
		ArrayList<LocationMarker> drawn = new ArrayList<LocationMarker>();
		for(LocationMarker lm: selected) {
			if(lm.getOn()) {
				drawn.add(lm);
			}
		}
		return drawn;
	}

	/**
	 * Set architecture filter
	 * @param a selected architecture filter
	 */
	public void setArchFilter(String a) {
		println("Filter by architecture " + a);
		archFilter = a;
		// Update markers based on filter
		updateMarkers();
	}

	/**
	 * Update product id filter
	 * @param pif product id filter (list of product ids)
	 */
	public void updateProductIdFilter(ArrayList<String> pif) {
		println("Filter by product IDs = " + pif);
		productIdFilter = pif;
		// Update markers based on filter
		updateMarkers();
	}

}