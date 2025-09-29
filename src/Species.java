import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Species extends JPanel {
	
	private Color clr;
	private boolean active;
	private Dimension dim;
	private int indexOfParent;
	private int[] cellsToLive, cellsToReproduce;
	private double mutChance;
	private String name;
	
	public Species(Color clr, int w, int[] cellsToLive, int[] cellsToReproduce, String name, double mutChance, int dexParent) {
		super();
		dim = new Dimension(w, w);
		setPreferredSize(dim);
		active = false;
		setBackground(clr);
		this.cellsToLive = cellsToLive;
		this.cellsToReproduce = cellsToReproduce;
		this.clr = clr;
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.mutChance = mutChance;
		indexOfParent = dexParent;
		this.name = name;
		
	}
	
	public void activate() {
		active = true;
		setBorder(BorderFactory.createLineBorder(Color.yellow, 4));
	}
	
	public void deactivate() {
		active = false;
		setBorder(BorderFactory.createLineBorder(Color.yellow, 0));
	}
	
	public Color getTransparentColor(int alpha) {
		return new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), alpha);
	}
	
	public Color getColor() {
		return clr;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int[] getCellsToLive() {
		return cellsToLive;
	}

	public void setCellsToLive(int[] cellsToLive) {
		this.cellsToLive = cellsToLive;
	}

	public int[] getCellsToReproduce() {
		return cellsToReproduce;
	}

	public void setCellsToReproduce(int[] cellsToReproduce) {
		this.cellsToReproduce = cellsToReproduce;
	}
	
	public String toString() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int getIndexOfParent() {
		return indexOfParent;
	}

	public void setIndexOfParent(int indexOfParent) {
		this.indexOfParent = indexOfParent;
	}

	public Color getClr() {
		return clr;
	}

	public void setClr(Color clr) {
		this.clr = clr;
		setBackground(clr);
	}

	public Dimension getDim() {
		return dim;
	}

	public void setDim(Dimension dim) {
		this.dim = dim;
	}

	public double getMutChance() {
		return mutChance;
	}

	public void setMutChance(double mutChance) {
		this.mutChance = mutChance;
	}	

	
}
