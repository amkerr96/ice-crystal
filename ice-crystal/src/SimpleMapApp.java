import processing.core.PApplet;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.mapdisplay.MapDisplayFactory;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.providers.StamenMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;


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

	public void setup() {
		size(800, 600, OPENGL);

		map1 = new UnfoldingMap(this, new Google.GoogleMapProvider());
	    map2 = new UnfoldingMap(this, new Microsoft.AerialProvider());
	    map3 = new UnfoldingMap(this, new OpenStreetMap.OpenStreetMapProvider());
	    MapUtils.createDefaultEventDispatcher(this, map1, map2, map3);
	    
	    currentMap = map1;
	
		imgMarker1 = new ImageMarker(veniceLocation, loadImage("ui/marker_red.png"));
		imgMarker2 = new ImageMarker(berlinLocation, loadImage("ui/marker_gray.png"));
		imgMarker3 = new ImageMarker(lisbonLocation, loadImage("ui/marker_gray.png"));
		
	}

	public void draw() {
		currentMap.draw();
		currentMap.addMarkers(imgMarker1, imgMarker2, imgMarker3);
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