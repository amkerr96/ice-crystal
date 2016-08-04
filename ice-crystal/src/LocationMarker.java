import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractMarker;

/**
 * This marker displays an image at its location.
 */
public class LocationMarker extends AbstractMarker implements Comparable {

	private PImage img;
	private String name;
	private double spend;
	private boolean on;
	//private Something otherDataGoesHere;

	public LocationMarker(String name, Location location, double spend, PImage img) {
		super(location);
		this.img = img;
		this.name = name;
		this.spend = spend;
		on = false;
	}

	@Override
	public void draw(PGraphics pg, float x, float y) {
		pg.pushStyle();
		pg.imageMode(PConstants.CORNER);
		
		// The image is drawn in object coordinates, i.e. the marker's origin (0,0) is at its geo-location.
		pg.image(img, x - 11, y - 37);
		pg.popStyle();
	}

	@Override
	protected boolean isInside(float checkX, float checkY, float x, float y) {
		return checkX > x && checkX < x + img.width && checkY > y && checkY < y + img.height;
	}
	
	/*public Location getLocation() {
		return this.location;
	}*/	

	public String getName() {
		return this.name;
	}
	
	public void setImage(PImage img) {
		this.img = img;
	}

	@Override
	public int compareTo(Object o) {
		LocationMarker im = (LocationMarker) o;
		return this.getName().compareTo(im.getName());
	}
	
	public double getSpend() {
		return spend;
	}
	
	public boolean getOn() {
		return on;
	}
	
	public void setOn(boolean o) {
		on = o;
	}
	
}