import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractMarker;

/**
 * This marker displays an image at its location, and stores some relevant data
 *  * @author aarkerr
 */
public class LocationMarker extends AbstractMarker implements Comparable {

	/** Image for this marker */
	private PImage img;
	/** Name associated with this location */
	private String name;
	/** Spend associated with this location */
	private double spend;
	/** Whether this marker is red */
	private boolean on;

	/**
	 * Initialize marker with given field
	 * @param name name
	 * @param location location
	 * @param spend spend
	 * @param img image
	 */
	public LocationMarker(String name, Location location, double spend, PImage img) {
		super(location);
		this.img = img;
		this.name = name;
		this.spend = spend;
		// Default to off
		on = false;
	}

	/**
	 * Draw marker with given image and location
	 */
	@Override
	public void draw(PGraphics pg, float x, float y) {
		pg.pushStyle();
		pg.imageMode(PConstants.CORNER);
		
		// The image is drawn in object coordinates, i.e. the marker's origin (0,0) is at its geo-location.
		pg.image(img, x - 11, y - 37);
		pg.popStyle();
	}	

	/**
	 * Return the location name
	 * @return the name associated with this marker
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Set this markers image
	 * @param img new marker image
	 */
	public void setImage(PImage img) {
		this.img = img;
	}

	/**
	 * Enable sorting of markers
	 */
	@Override
	public int compareTo(Object o) {
		LocationMarker im = (LocationMarker) o;
		// by name
		return this.getName().compareTo(im.getName());
	}
	
	/**
	 * Return the spend associated with this location
	 * @return the spend at this marker
	 */
	public double getSpend() {
		return spend;
	}
	
	/**
	 * Whether this marker is selected
	 * @return whether this marker is selected
	 */
	public boolean getOn() {
		return on;
	}
	
	/**
	 * Turn this marker on or off
	 * @param o on or off
	 */
	public void setOn(boolean o) {
		on = o;
	}

	/**
	 * Not really sure what this is for
	 */
	@Override
	protected boolean isInside(float checkX, float checkY, float x, float y) {
		return checkX > x && checkX < x + img.width && checkY > y && checkY < y + img.height;
	}
	
}