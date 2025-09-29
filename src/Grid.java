import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Grid extends JPanel {

	private byte[][] universe;
	private double cellLength;
	private final double ogCellLength;
	private Dimension dim;
	private double[] coords;
	private double cellsH, cellsW;
	private boolean[][] hoverUniv;
	private Point pos;
	private boolean findingCoord, autoZoom;
	private byte editMode, activeTemplate;
	private ArrayList<Species> species;
	private ArrayList<ArrayList<Species>> children;
	private ArrayList<GridTemplate> templates;
	private JFrame addSpeciesFrame, templateFrame;
	private JPanel simSpecCont;
	private DevButton addSpeciesBut;
	private final static int gridTemplateWidth = 150;

	public Grid(double cellLength, double width, double height, int uW, int uH) {
		super();

		pos = new Point();

		findingCoord = false;
		editMode = 0;

		autoZoom = true;

		this.cellLength = cellLength;
		ogCellLength = cellLength;
		
		addSpeciesBut = new DevButton(25, 25, new Color(100, 100, 100), new Color(50, 50, 50), Color.white, new Color(50, 50, 50), "+", new Font("Arial", Font.BOLD, 20), BorderFactory.createLineBorder(new Color(90, 90, 90)), 0);
		addSpeciesBut.setBorders(BorderFactory.createLineBorder(new Color(90, 90, 90)), BorderFactory.createLineBorder(Color.white), BorderFactory.createLineBorder(Color.white));			
		addSpeciesBut.addMouseListener(new MouseListen() {
			public void mouseClicked(MouseEvent e) {
				speciesEditor(-1, true);
			}
		});

		dim = new Dimension((int) (width + 1), (int) (height + 1));

		setPreferredSize(dim);

		universe = new byte[uH][uW];
		for (int i = 0; i < universe.length; i++)
			for (int j = 0; j < universe[i].length; j++)
				universe[i][j] = -1;
		
		hoverUniv = new boolean[uH][uW];

		species = new ArrayList<Species>();
		children = new ArrayList<ArrayList<Species>>();
		species.add(new Species(Color.white, 25, (new int[] { 2, 3 }), (new int[] { 3 }), "Origin", 0, -1));
		children.add(new ArrayList<Species>());
		species.get(0).activate();

		cellsW = width / cellLength;
		cellsH = (height / width) * cellsW;

		coords = new double[4];
		coords[0] = universe.length / 2 - (cellsH / 2); // top of viewing frame
		coords[1] = coords[0] + cellsH; // bottom of viewing frame
		coords[2] = universe[0].length / 2 - (cellsW / 2); // left of viewing frame
		coords[3] = coords[2] + cellsW; // right of viewing frame

		setCursor(new Cursor(Cursor.HAND_CURSOR));
		Click click = new Click();
		addMouseListener(click);
		addMouseMotionListener(click);
		addMouseWheelListener(click);
		setFocusable(true);
		
		//GRID TEMPLATES
		GridTemplate glider = gtCreator("Glider", new byte[][] {{0,1,0},{0,0,1},{1,1,1}});
		GridTemplate smSpaceship = gtCreator("Small Spaceship", new byte[][] {{1,0,0,1,0},{0,0,0,0,1},{1,0,0,0,1},{0,1,1,1,1}});
		GridTemplate medSpaceship = gtCreator("Med Spaceship", new byte[][] {{0,0,1,0,0,0},{1,0,0,0,1,0},{0,0,0,0,0,1},{1,0,0,0,0,1},{0,1,1,1,1,1}});
		GridTemplate smBlock = gtCreator("Small Block", new byte[][] {{1,1},{1,1}});
		GridTemplate bgBlock = gtCreator("Big Block", new byte[][] {{1,1,1},{1,1,1},{1,1,1}});
		GridTemplate blinker =  gtCreator("Blinker", new byte[][] {{1},{1},{1}});
		GridTemplate beacon = gtCreator("Beacon", new byte[][] {{1,1,0,0},{1,0,0,0},{0,0,0,1},{0,0,1,1}});
		GridTemplate pulsar = gtCreator("Pulsar", new byte[][] {{0,0,1,1,1,0,0,0,1,1,1,0,0},{0,0,0,0,0,0,0,0,0,0,0,0,0},{1,0,0,0,0,1,0,1,0,0,0,0,1},{1,0,0,0,0,1,0,1,0,0,0,0,1},{1,0,0,0,0,1,0,1,0,0,0,0,1},{0,0,1,1,1,0,0,0,1,1,1,0,0},{0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,1,1,1,0,0,0,1,1,1,0,0},{1,0,0,0,0,1,0,1,0,0,0,0,1},{1,0,0,0,0,1,0,1,0,0,0,0,1},{1,0,0,0,0,1,0,1,0,0,0,0,1},{0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,1,1,1,0,0,0,1,1,1,0,0}});
		
		templates = new ArrayList<GridTemplate>();
		
		templates.add(glider);
		templates.add(smSpaceship);
		templates.add(medSpaceship);
		templates.add(smBlock);
		templates.add(bgBlock);
		templates.add(blinker);
		templates.add(beacon);
		templates.add(pulsar);
		
		activeTemplate = 0;
		templates.get(activeTemplate).activate();
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		super.paintComponent(g2);
		setBackground(Color.black);
		int y = 0;
		int x = 0;
		int incrX;
		int incrY;

		for (int i = (int) coords[0]; i < coords[1]; i++) {
			incrY = intToAdd(cellLength, i);
			
			for (int j = (int) coords[2]; j < coords[3]; j++) {
				incrX = intToAdd(cellLength, j);
				
				if (coords[0] - i > 0.000001 && coords[0] - i < 1) {
					y = -1 * (int) ((coords[0] - i) * cellLength);
				}
				if (coords[2] - j > 0.000001 && coords[2] - j < 1) {
					x = -1 * (int) ((coords[2] - j) * cellLength);
				}

				g2.setColor(Color.white);
				g2.drawRect(x, y, incrX, incrY);

				if (universe[i][j] >= 0) {
					g2.setColor(species.get(universe[i][j]).getColor());
					g2.fillRect(x, y, incrX, incrY);
				}
				if (editMode == 2 && hoverUniv[i][j]) {
					g2.setColor(getActiveSpecies().getTransparentColor(200)); //diminish
					g2.fillRect(x, y, incrX, incrY);
				}

				x += incrX;
			}
			y += incrY;
			x = 0;
		}

	}

	public void play() {
		byte[][] u2 = clone(universe);
		for (int i = 0; i < u2.length; i++) {
			for (int j = 0; j < u2[i].length; j++) {
				u2[i][j] = outcome(i, j);
			}
		}

		universe = u2;

		autoZoom();
	}

	public void removeAndShiftSpecies(int index) {
		for (int i = 0; i < universe.length; i++) {
			for (int j = 0; j < universe[i].length; j++) {
				if (universe[i][j] == index)
					universe[i][j] = -1;
				if (universe[i][j] > index)
					universe[i][j]--;
			}
		}
		
	}

	public byte outcome(int r, int c) {
		int[] ct = countNeighbors(r, c);
		if (universe[r][c] >= 0) {
			if (contains(species.get(universe[r][c]).getCellsToLive(), ct[universe[r][c]])) {
				return universe[r][c];
			}
			return -1;
		}
		
		ArrayList<Byte> reproduces = speciesToReproduce(ct);
		if (reproduces.size() != 1)
			return -1;
		if (children.get(reproduces.get(0)).size() > 0) {
			double temp = 0;
			double val = Math.random();

			for (int i = 0; i < children.get(reproduces.get(0)).size(); i++) {
				if (val > temp && val < temp + children.get(reproduces.get(0)).get(i).getMutChance())
					return (byte) species.indexOf(children.get(reproduces.get(0)).get(i));
				temp += children.get(reproduces.get(0)).get(i).getMutChance();
			}
		}

		return reproduces.get(0);
	}

	public ArrayList<Byte> speciesToReproduce(int[] cts) {
		ArrayList<Byte> reproduces = new ArrayList<Byte>(cts.length);
		for (byte i = 0; i < cts.length; i++) {
			if (contains(species.get(i).getCellsToReproduce(), cts[i]))
				reproduces.add(i);
		}
		return reproduces;
	}

	public boolean contains(int[] arr, int num) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == num)
				return true;
		}
		return false;
	}

	public void generateRandomCells(double proportion, int startX, int startY, int endX, int endY) {
		for (int i = startY; i <= endY; i++) {
			for (int j = startX; j <= endX; j++) {
				if (Math.random() < proportion) {
					universe[i][j] = getActiveIndex();
				}

			}
		}
		repaint();
	}

	public int[] countNeighbors(int r, int c) {
		int[] arr = new int[species.size()];
		if (c > 0 && universe[r][c - 1] >= 0)
			arr[universe[r][c - 1]]++;
		if (c < universe[0].length - 1 && universe[r][c + 1] >= 0)
			arr[universe[r][c + 1]]++;
		if (r > 0 && universe[r - 1][c] >= 0)
			arr[universe[r - 1][c]]++;
		if (r < universe.length - 1 && universe[r + 1][c] >= 0)
			arr[universe[r + 1][c]]++;
		if (c > 0 && r > 0 && universe[r - 1][c - 1] >= 0)
			arr[universe[r - 1][c - 1]]++;
		if (c > 0 && r < universe.length - 1 && universe[r + 1][c - 1] >= 0)
			arr[universe[r + 1][c - 1]]++;
		if (r > 0 && c < universe[0].length - 1 && universe[r - 1][c + 1] >= 0)
			arr[universe[r - 1][c + 1]]++;
		if (c < universe[0].length - 1 && r < universe.length - 1 && universe[r + 1][c + 1] >= 0)
			arr[universe[r + 1][c + 1]]++;
		return arr;
	}

	public int getCellsWide() {
		return universe[0].length;
	}

	public int getCellsHigh() {
		return universe.length;
	}

	public int totalAlive() {
		int count = 0;
		for (int i = 0; i < universe.length; i++) {
			for (int j = 0; j < universe.length; j++) {
				if (universe[i][j] >= 0)
					count++;
			}
		}
		return count;
	}

	public void deactivateAllSpecies() {
		for (Species s : species)
			s.deactivate();
	}

	public void clearGrid() {
		for (int i = 0; i < universe.length; i++)
			for (int j = 0; j < universe[i].length; j++)
				universe[i][j] = -1;
		repaint();
	}

	public void centerOnCoord(Point.Double p) {
		if (!findingCoord) {
			findingCoord = true;
			pan a = new pan(p);
			a.start();
		}
	}

	public void autoZoom() {
		if (!autoZoom)
			return;
		int side = cellsOnEdge();
		if (side > 0) {
			switch (side) {
			case 1:
				cellLengthChange(false);
				coordAdjust();
				break;
			case 2:
				cellLengthChange(true);
				coordAdjust();
			}
			repaint();
		}
	}

	public void cellLengthChange(boolean side) {
		if (side) {
			while (((dim.getHeight() - 1) / cellLength) - cellsH < 1 && cellLength > 2)
				cellLength--;
			cellsH = (dim.getHeight() - 1) / cellLength;
			cellsW = ((dim.getWidth() - 1) / (dim.getHeight() - 1)) * cellsH;
			return;
		}
		while (((dim.getWidth() - 1) / cellLength) - cellsW < 1 && cellLength > 2)
			cellLength--;
		cellsH = (dim.getHeight() - 1) / cellLength;
		cellsW = ((dim.getWidth() - 1) / (dim.getHeight() - 1)) * cellsH;
	}

	public int cellsOnEdge() {
		for (int i = (int) coords[0]; i < coords[1]; i++) { // sides
			if (universe[i][(int) coords[2]] >= 0 || universe[i][(int) coords[3]] >= 0)
				return 1;
		}
		for (int i = (int) coords[2]; i < coords[3]; i++) { // top&bottom
			if (universe[(int) coords[0]][i] >= 0 || universe[(int) coords[1]][i] >= 0)
				return 2;
		}
		return 0;
	}

	public class pan extends Thread {
		private int times;
		private double incrY, incrX;

		public pan(Point.Double p) {
			double centY = (coords[0] + coords[1]) / 2;
			double centX = (coords[2] + coords[3]) / 2;
			double goalX = p.getX() - (centX - coords[2]);
			if (goalX < 0)
				goalX = 0;
			if (goalX + cellsW > universe[0].length)
				goalX = universe[0].length - cellsW - 0.0001;
			double goalY = p.getY() - (centY - coords[0]);
			if (goalY < 0)
				goalY = 0;
			if (goalY + cellsH > universe.length)
				goalY = universe.length - cellsH - 0.0001;
			

			this.times = times(new Point.Double(goalX, goalY).distance(new Point.Double(coords[2], coords[0])));

			incrY = (goalY - coords[0]) / times;
			incrX = (goalX - coords[2])/times;

		}

		public void run() {
			for (int i = 0; i < times; i++) {
				coords[0] += incrY;
				coords[1] += incrY;
				coords[2] += incrX;
				coords[3] += incrX;
				
				repaint();
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			findingCoord = false;

		}

		private int times(double dist) {
			return (int) ((11/9)*dist) + 277;
		}

	}

	public double zoomPercent() {
		return (cellLength / ogCellLength) * 100;
	}

	public void zoom(double percentZoom) {
		double tempCL = ((percentZoom / 100) * ogCellLength);

		if (tempCL < 3) // zooming out when cells are small
			cellLength = 3;
		else if (cellLength < tempCL && tempCL < 10)  //zooming in when cells are small
				cellLength++;
		else if (tempCL >= dim.getHeight()) // zooming in when cells are large
			cellLength = dim.getHeight() - 1;
		else
			cellLength = (int) tempCL;

		cellsW = (dim.getWidth() - 1) / cellLength;
		cellsH = ((dim.getHeight() - 1) / (dim.getWidth() - 1)) * cellsW;

		coordAdjust();

		repaint();
	}
	
	public void maxZoom() {
		cellLength = dim.getHeight() - 1;
		
		cellsW = (dim.getWidth() - 1) / cellLength;
		cellsH = ((dim.getHeight() - 1) / (dim.getWidth() - 1)) * cellsW;

		coordAdjust();

		repaint();
	}
	
	public void resetViewingFrame() {
		cellLength = 20;

		cellsW = (dim.getWidth() - 1) / cellLength;
		cellsH = ((dim.getHeight() - 1) / (dim.getWidth() - 1)) * cellsW;

		coords[0] = universe.length / 2 - (cellsH / 2); // top of viewing frame
		coords[1] = coords[0] + cellsH; // bottom of viewing frame
		coords[2] = universe[0].length / 2 - (cellsW / 2); // left of viewing frame
		coords[3] = coords[2] + cellsW; // right of viewing frame

		repaint();
	}

	public int countSpecies(Species s) {
		int count = 0;
		for (int i = 0; i < universe.length; i++) {
			for (int j = 0; j < universe.length; j++) {
				if (universe[i][j] == species.indexOf(s))
					count++;
			}
		}
		return count;
	}
	
	public int countActiveSpecies() {
		return countSpecies(getActiveSpecies());
	}

	public void setCell(int x, int y, byte num) {
		universe[y][x] = num;
	}
	
	public Point coordToCell(Point p) {
		return new Point((int) ((p.getX() / cellLength) + coords[2]), (int) ((p.getY() / cellLength) + coords[0]));
	}
	
	public Point2D.Double coordToCellDouble(Point p) {
		return new Point2D.Double(((p.getX() / cellLength) + coords[2]), ((p.getY() / cellLength) + coords[0]));
	}

	public byte[][] clone(byte[][] arr) {
		byte[][] arr1 = new byte[arr.length][arr[0].length];
		for (int i = 0; i < arr1.length; i++)
			arr1[i] = arr[i].clone();
		return arr1;
	}
	
	public void openTemplates() {
		templateFrame = new JFrame("Templates");
		templateFrame.setSize(650, 650);
		templateFrame.setLocationRelativeTo(null);
		templateFrame.setResizable(false);
		templateFrame.getContentPane().setBackground(Color.black);
		templateFrame.setAlwaysOnTop(true);
		
		templateFrame.setVisible(true);
		
		GridTemplateContainer gtc = new GridTemplateContainer();
		
		templateFrame.add(gtc);
		
	}
	
	public void speciesEditor(int speciesIndex, boolean newSpecies) {
		addSpeciesFrame = new JFrame();
		addSpeciesFrame.setSize(550, 650);
		addSpeciesFrame.setLocationRelativeTo(null);
		addSpeciesFrame.setResizable(false);
		addSpeciesFrame.getContentPane().setBackground(Color.black);
		addSpeciesFrame.setAlwaysOnTop(true);
		
		if (newSpecies) {
			addSpeciesFrame.setTitle("Add Species");
			
			addSpeciesInterface asi = new addSpeciesInterface();
			addSpeciesFrame.add(asi);
		}
		
		else {
			addSpeciesFrame.setTitle("Species Settings");
			
			addSpeciesInterface asi = new addSpeciesInterface(species.get(speciesIndex), newSpecies);
			addSpeciesFrame.add(asi);
		}
		
		addSpeciesFrame.setVisible(true);
	}
	
	public void fillSpecCont() {
		GridBagConstraints c = new GridBagConstraints();

		c.gridy = 0;
		c.weightx = 0.2;
		c.weighty = 0.3;

		for (int i = 0; i < 15; i++) {
			if (i % 5 == 0)
				c.gridy++;
			c.gridx = i % 5;
			if (i < species.size())
				simSpecCont.add(species.get(i), c);
			else if (i == species.size())
				simSpecCont.add(addSpeciesBut, c);
			else
				simSpecCont.add(Box.createRigidArea(new Dimension(25, 25)), c);
		}
	}
	
	public void gridResize(double newWidth, double newHeight) {
		dim.setSize(newWidth + 1, newHeight + 1);
		setPreferredSize(dim);
		cellsW = newWidth / cellLength;
		cellsH = (newHeight / newWidth) * cellsW;
		coords[0] = (coords[0] + coords[1]) / 2 - (cellsH / 2);
		coords[1] = coords[0] + cellsH;
		coords[2] = (coords[2] + coords[3]) / 2 - (cellsW / 2);
		coords[3] = coords[2] + cellsW;
		repaint();
	}

	public class Click extends MouseAdapter {

		private Point z;

		public Click() {
			z = new Point();
		}

		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				Point p = coordToCell(new Point(e.getX(), e.getY()));

				switch (editMode) {
					case 0: 
						universe[p.y][p.x] = getActiveIndex();
						break;
					case 1: 
						universe[p.y][p.x] = -1;
						break;
					case 2:
						Point2D.Double pd = coordToCellDouble(new Point(e.getX(), e.getY()));
						GridTemplate plate = templates.get(activeTemplate);
						int xStart = (int)Math.round(pd.getX() - plate.getMouseLoc().getX());
						int yStart = (int)Math.round(pd.getY() - plate.getMouseLoc().getY());
						if (xStart >= 0 && xStart < universe[0].length && yStart >=0 && yStart < universe.length) {
							boolean[][] temp = plate.getTemplate();
							for (int i = 0; i < temp.length; i++) {
								for (int j = 0; j < temp[i].length; j++) {
									if (temp[i][j])
										universe[i + yStart][j + xStart] = getActiveIndex();
								}
							}
						}
						break;
				}
				
				repaint();
			}
		}

		public void mousePressed(MouseEvent e) {

			if (SwingUtilities.isRightMouseButton(e)) {
				z.setLocation(e.getX(), e.getY());
				setCursor(new Cursor(Cursor.MOVE_CURSOR));
			}
		}

		public void mouseDragged(MouseEvent e) {
			pos = coordToCell(new Point(e.getX(), e.getY()));
			if (SwingUtilities.isRightMouseButton(e)) {
				if (coords[0] + (z.getY() - e.getY()) / cellLength >= 0
						&& coords[1] + (z.getY() - e.getY()) / cellLength < universe.length) {
					coords[0] += (z.getY() - e.getY()) / cellLength;
					coords[1] += (z.getY() - e.getY()) / cellLength;
					z.setLocation(z.getX(), e.getY());

				}
				if (coords[2] + (z.getX() - e.getX()) / cellLength >= 0
						&& coords[3] + (z.getX() - e.getX()) / cellLength < universe[0].length) {
					coords[2] += (z.getX() - e.getX()) / cellLength;
					coords[3] += (z.getX() - e.getX()) / cellLength;
					z.setLocation(e.getX(), z.getY());
				}

				repaint();
			}

			else if (SwingUtilities.isLeftMouseButton(e)) {
				Point p = coordToCell(new Point(e.getX(), e.getY()));

				switch (editMode) {
					case 0: 
						universe[p.y][p.x] = getActiveIndex();
						break;
					case 1: 
						universe[p.y][p.x] = -1;
						break;
					case 2:
						Point2D.Double pd = coordToCellDouble(new Point(e.getX(), e.getY()));
						GridTemplate plate = templates.get(activeTemplate);
						int xStart = (int)Math.round(pd.getX() - plate.getMouseLoc().getX());
						int yStart = (int)Math.round(pd.getY() - plate.getMouseLoc().getY());
						if (xStart >= 0 && xStart < universe[0].length && yStart >=0 && yStart < universe.length) {
							boolean[][] temp = plate.getTemplate();
							for (int i = 0; i < temp.length; i++) {
								for (int j = 0; j < temp[i].length; j++) {
									if (temp[i][j])
										universe[i + yStart][j + xStart] = getActiveIndex();
								}
							}
						}
						break;
				}

				repaint();
			}

		}

		public void mouseWheelMoved(MouseWheelEvent e) {
			if (autoZoom)
				autoZoom = false;
			if (e.getWheelRotation() > 0) { // zoom out
				zoom(100*(cellLength/1.1)/ogCellLength);
			} else { // zoom in
				zoom(100*(cellLength*1.1)/ogCellLength);
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
		}
		
		public void mouseMoved(MouseEvent e) {
			if (editMode == 2) {
				
				hoverUniv = new boolean[universe.length][universe[0].length];
				
				Point2D.Double pd = coordToCellDouble(new Point(e.getX(), e.getY()));
				GridTemplate plate = templates.get(activeTemplate);
				int xStart = (int)Math.round(pd.getX() - plate.getMouseLoc().getX());
				int yStart = (int)Math.round(pd.getY() - plate.getMouseLoc().getY());
				if (xStart >= 0 && xStart < universe[0].length && yStart >=0 && yStart < universe.length) {
					boolean[][] temp = plate.getTemplate();
					for (int i = 0; i < temp.length; i++) {
						for (int j = 0; j < temp[i].length; j++) {
							if (temp[i][j])
								hoverUniv[i + yStart][j + xStart] = true;
						}
					}
				}
				repaint();
			}
		}
		
		public void mouseExited(MouseEvent e) {
			if (editMode == 2) {
				hoverUniv = new boolean[universe.length][universe[0].length];
			}
		}
	}
	

	public Point getPos() {
		return pos;
	}

	public byte[][] getUniverse() {
		return universe;
	}

	public double[] getCoords() {
		return coords;
	}

	public ArrayList<Species> getSpecies() {
		return species;
	}

	public void addSpecies(Species s) {
		species.add(s);
	}

	public void setCoords(double[] coords) {
		this.coords = coords;
	}

	public boolean isAutoZoom() {
		return autoZoom;
	}

	public void setAutoZoom(boolean autoZoom) {
		this.autoZoom = autoZoom;
	}


	public void setEditMode(byte edit) {
		this.editMode = edit;
	}

	public byte getActiveIndex() {
		for (byte i = 0; i < species.size(); i++)
			if (species.get(i).isActive())
				return i;
		return -1;
	}
	
	public Species getActiveSpecies() {
		for (Species s: species) 
			if (s.isActive())
				return s;
		return null;
	}

	public void setActiveIndex(int index) {
		for (int i = 0; i < species.size(); i++)
			species.get(i).deactivate();
		if (index != -1)
			species.get(index).activate();
	}

	public ArrayList<ArrayList<Species>> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<ArrayList<Species>> children) {
		this.children = children;
	}

	public double getCellLength() {
		return cellLength;
	}

	public void setCellLength(double cellLength) {
		this.cellLength = cellLength;
	}

	public Dimension getDim() {
		return dim;
	}

	public void setDim(Dimension dim) {
		this.dim = dim;
	}
	
	public void setSimSpecCont(JPanel cont) {
		simSpecCont = cont;
	}

	public double getCellsH() {
		return cellsH;
	}

	public void setCellsH(double cellsH) {
		this.cellsH = cellsH;
	}

	public double getCellsW() {
		return cellsW;
	}

	public void setCellsW(double cellsW) {
		this.cellsW = cellsW;
	}

	private void coordAdjust() {
		coords[0] = (coords[0] + coords[1]) / 2 - (cellsH / 2);
		coords[1] = coords[0] + cellsH;
		coords[2] = (coords[2] + coords[3]) / 2 - (cellsW / 2);
		coords[3] = coords[2] + cellsW;

		if (coords[0] < 0) {
			coords[0] = 0;
			coords[1] = coords[0] + cellsH;
		}
		if (coords[1] > universe.length) {
			coords[1] = universe.length;
			coords[0] = coords[1] - cellsH;
		}
		if (coords[2] < 0) {
			coords[2] = 0;
			coords[3] = coords[2] + cellsW;
		}
		if (coords[3] > universe[0].length) {
			coords[3] = universe[0].length;
			coords[2] = coords[3] - cellsW;
		}
	}
	
	private GridTemplate gtCreator(String name, byte[][] arr) {
		boolean[][] arrOut = new boolean[arr.length][arr[0].length];
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++) {
				if (arr[i][j] == 1)
					arrOut[i][j] = true;
			}
		}
		return new GridTemplate(name, gridTemplateWidth, arrOut);
	}
	
	private int intToAdd(double incr, int i) {
		double decimal = Math.abs(incr % 1.0);
		if ((int) (decimal * (i + 1)) > (int) (decimal * i))
			return (int) incr + 1;
		return (int) incr;
	}

	public class addSpeciesInterface extends JPanel {

		private JColorChooser jcc;
		private Color clr;
		private JLabel clrPick, parentPick, mutChance, cellsToLiveLbl, cellsToReproLbl, MCerr, clrErr, nameErr, title, nameLbl;
		private DevButton2 saveSpecies, removeSpecies, saveColor;
		private JPanel clrPickBox;
		private int indexOfParent;
		private double chanceOfMut;
		private String name;
		private JComboBox jcb1;
		private JFormattedTextField mutchce, nameIn;
		private int[] cellsToLive, cellsToRepro;
		private JFrame colorChooser;
		private boolean newSpecies;
		private Species oldSpecies;
		private cellChooser selectLive, selectRepro;

		public addSpeciesInterface(Species oldSpecies, boolean newSpecies) {
			setBackground(Color.black);
			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			
			this.oldSpecies = oldSpecies;
			
			name = oldSpecies.toString();
			
			title = new JLabel(name);
			title.setForeground(Color.white);
			title.setFont(new Font("Arial", Font.BOLD, 40));

			this.cellsToLive = oldSpecies.getCellsToLive();
			this.cellsToRepro = oldSpecies.getCellsToReproduce();

			this.indexOfParent = oldSpecies.getIndexOfParent();
			
			this.newSpecies = newSpecies;

			clr = oldSpecies.getClr();
			clrPick = new JLabel("Color: ");
			clrPick.setForeground(Color.white);
			clrPick.setFont(clrPick.getFont().deriveFont((float) (clrPick.getFont().getSize() * 2)));

			clrPickBox = new JPanel();
			clrPickBox.setPreferredSize(new Dimension(30, 30));
			clrPickBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
			clrPickBox.setBackground(clr);
			clrPickBox.addMouseListener(new MouseListen() {
				public void mouseClicked(MouseEvent e) {
					openColorChooser();
				}
			});

			jcb1 = new JComboBox<String>();

			if (indexOfParent == -1) {
				jcb1.addItem("None");

				for (Species s : species) {
					if (newSpecies)
						jcb1.addItem(s.toString());
					else if (!s.equals(oldSpecies))
						jcb1.addItem(s.toString());
				}
						
			}
			else {
				jcb1.addItem(species.get(indexOfParent).toString());
				jcb1.addItem("None");
				for (Species s : species)
					if (!s.equals(species.get(indexOfParent)) && !s.equals(oldSpecies))
						jcb1.addItem(s.toString());
			}

			

			parentPick = new JLabel("Mutates from: ");
			parentPick.setForeground(Color.white);
			parentPick.setFont(parentPick.getFont().deriveFont((float) (parentPick.getFont().getSize() * 2)));

			mutChance = new JLabel("<html>Mutation Chance<br>from Parent (%)<html>");
			mutChance.setForeground(Color.white);
			mutChance.setFont(mutChance.getFont().deriveFont((float) (mutChance.getFont().getSize() * 2)));

			mutchce = new JFormattedTextField();
			mutchce.setHorizontalAlignment(SwingConstants.CENTER);
			mutchce.setBackground(Color.black);
			mutchce.setForeground(Color.white);
			mutchce.setCaretColor(Color.white);
			mutchce.setBorder(BorderFactory.createLineBorder(Color.white));
			mutchce.setPreferredSize(new Dimension(50, 30));
			mutchce.setFont(mutchce.getFont().deriveFont((float) (mutchce.getFont().getSize() * 2)));
			if (!newSpecies)
				mutchce.setText("" + chanceOfMut * 100);
			
			nameLbl = new JLabel("Name: ");
			nameLbl.setForeground(Color.white);
			nameLbl.setFont(nameLbl.getFont().deriveFont((float) (nameLbl.getFont().getSize() * 2)));
			
			nameIn = new JFormattedTextField();
			nameIn.setHorizontalAlignment(SwingConstants.CENTER);
			nameIn.setBackground(Color.black);
			nameIn.setForeground(Color.white);
			nameIn.setCaretColor(Color.white);
			nameIn.setBorder(BorderFactory.createLineBorder(Color.white));
			nameIn.setPreferredSize(new Dimension(100, 30));
			nameIn.setFont(nameIn.getFont().deriveFont((float) (nameIn.getFont().getSize() * 1.8)));
			if (!newSpecies)
				nameIn.setText(name);
			

			this.cellsToLiveLbl = new JLabel("Surrounding Cells to Live");
			this.cellsToLiveLbl.setForeground(Color.white);
			this.cellsToLiveLbl
					.setFont(cellsToLiveLbl.getFont().deriveFont((float) (cellsToLiveLbl.getFont().getSize() * 2)));
			
			
			selectLive = new cellChooser(oldSpecies.getCellsToLive());
			

			this.cellsToReproLbl = new JLabel("Surrounding Cells to Reproduce");
			this.cellsToReproLbl.setForeground(Color.white);
			this.cellsToReproLbl
					.setFont(cellsToReproLbl.getFont().deriveFont((float) (cellsToReproLbl.getFont().getSize() * 2)));
			
			selectRepro = new cellChooser(oldSpecies.getCellsToReproduce());

			saveSpecies = new DevButton2("Save", new Dimension(25, 18), Color.black, Color.green, true);
			saveSpecies.setBorders(BorderFactory.createLineBorder(Color.green, 2));
			saveSpecies.setFontSize(22);
			saveSpecies.addMouseListener(new MouseListen() {
				public void mouseClicked(MouseEvent e) {
					String parent = (String) jcb1.getSelectedItem();
					indexOfParent = findIndexOfSpecies(parent);
					clrErr.setText(" ");
					MCerr.setText(" ");
					
					
					cellsToLive = selectLive.exportSelections();
					cellsToRepro = selectRepro.exportSelections();
					
					if (findIndexOfSpecies(nameIn.getText()) != -1 && (newSpecies || (!newSpecies && !nameIn.getText().equals(oldSpecies.toString())))) {
							nameErr.setText("Name already picked");
					}
					else {
						nameErr.setText(" ");
						name = nameIn.getText();
						
						try {
							if (mutchce.getText().equals("") && parent.equals("None"))
								chanceOfMut = 0;
							else
								chanceOfMut = Double.parseDouble(mutchce.getText());
							if (chanceOfMut < 0 || chanceOfMut > 100) {
								MCerr.setText("Enter a number between 0 and 100");
							} else if (indexOfParent != -1 && !validMutChance(indexOfParent, chanceOfMut / 100)) {
								MCerr.setText("Mutation chance too high");
							} else {
								MCerr.setText(" ");
								if (!validColor(clr)) {
									clrErr.setText("Color already picked");
								}
								else {
									clrErr.setText(" ");
									if (newSpecies) {
										Species s = new Species(clr, 25, cellsToLive, cellsToRepro, name, (chanceOfMut / 100), indexOfParent);

										species.add(s);
										children.add(new ArrayList<Species>());
										if (indexOfParent != -1)
											children.get(indexOfParent).add(s);
										
										if (species.size() == 1)
											species.get(0).activate();
	
									} else {
										oldSpecies.setName(name);
										oldSpecies.setClr(clr);
										oldSpecies.setMutChance(chanceOfMut/100);
										oldSpecies.setCellsToLive(cellsToLive);
										oldSpecies.setCellsToReproduce(cellsToRepro);
	
										if (indexOfParent != -1 && indexOfParent != oldSpecies.getIndexOfParent()) {
											if (oldSpecies.getIndexOfParent() != -1)
												children.get(oldSpecies.getIndexOfParent()).remove(oldSpecies);
											children.get(indexOfParent).add(oldSpecies);
										}
										oldSpecies.setIndexOfParent(indexOfParent);
									}
									
									simSpecCont.removeAll();
									fillSpecCont();
									
									addSpeciesFrame.dispatchEvent(new WindowEvent(addSpeciesFrame, WindowEvent.WINDOW_CLOSING));
								
								}
							}
						} catch (Exception a) {
							MCerr.setText("Invalid Entry");
						}
					}
				}
			});

			removeSpecies = new DevButton2("Remove Species", new Dimension(25, 18), Color.black, Color.red, true);
			removeSpecies.setBorders(BorderFactory.createLineBorder(Color.red, 2));
			removeSpecies.setFontSize(22);
			if (newSpecies)
				removeSpecies.setText("Close");
			removeSpecies.addMouseListener(new MouseListen() {
				public void mouseClicked(MouseEvent e) {
					if (!newSpecies) {
						if (oldSpecies.isActive()) {
							if (species.size() > 1) {
								if (species.indexOf(oldSpecies) > 0)
									setActiveIndex(species.indexOf(oldSpecies) - 1);
								else
									setActiveIndex(1);
							}
							else
								setActiveIndex(-1);
						}
						removeAndShiftSpecies(species.indexOf(oldSpecies));
						for (Species s : children.get(species.indexOf(oldSpecies))) // all children of deleted parent now have parent index of -1
							s.setIndexOfParent(-1);
						
						children.remove(species.indexOf(oldSpecies));
						species.remove(oldSpecies);
						updateIds();
					}
					
					simSpecCont.removeAll();
					fillSpecCont();
					
					addSpeciesFrame.dispatchEvent(new WindowEvent(addSpeciesFrame, WindowEvent.WINDOW_CLOSING));
				}
			});

			MCerr = new JLabel(" ");
			MCerr.setFont(new Font("Arial", Font.ITALIC, 10));
			MCerr.setForeground(Color.red);

			clrErr = new JLabel(" ");
			clrErr.setFont(new Font("Arial", Font.ITALIC, 10));
			clrErr.setForeground(Color.red);
			
			nameErr = new JLabel(" ");
			nameErr.setFont(new Font("Arial", Font.ITALIC, 10));
			nameErr.setForeground(Color.red);

			gbc.insets = new Insets(0, 0, 0, 0);
			gbc.weighty = 1;
			
			gbc.gridwidth = 2;
			gbc.gridx = 0;
			gbc.gridy = 0;
			add(title, gbc);
			
			gbc.weighty = 0;
			gbc.insets = new Insets(0, 0, 0, 5);
			gbc.gridwidth = 1;
			gbc.anchor = GridBagConstraints.LINE_END;
			gbc.gridx = 0;
			gbc.gridy++;
			add(nameLbl, gbc);
			
			gbc.insets = new Insets(0, 0, 0, 0);
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.gridx = 1;
			add(nameIn, gbc);
			
			gbc.insets = new Insets(0, 0, 5, 0);
			gbc.gridy++;
			add(nameErr, gbc);
			
			gbc.weighty = 0;
			gbc.insets = new Insets(0, 0, 0, 5);
			gbc.anchor = GridBagConstraints.LINE_END;
			gbc.gridx = 0;
			gbc.gridy++;
			add(clrPick, gbc);

			gbc.insets = new Insets(0, 0, 0, 0);
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.gridx = 1;
			add(clrPickBox, gbc);

			gbc.gridy++;
			add(clrErr, gbc);

			gbc.insets = new Insets(0, 0, 0, 5);
			gbc.anchor = GridBagConstraints.LINE_END;
			gbc.weighty = 1;
			gbc.gridx = 0;
			gbc.gridy++;
			add(parentPick, gbc);

			gbc.insets = new Insets(0, 0, 0, 0);
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.gridx = 1;
			add(jcb1, gbc);

			gbc.insets = new Insets(0, 0, 0, 10);
			gbc.anchor = GridBagConstraints.LINE_END;
			gbc.weighty = 0;
			gbc.gridx = 0;
			gbc.gridy++;
			add(mutChance, gbc);

			gbc.insets = new Insets(0, 0, 0, 0);
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.gridx = 1;
			add(mutchce, gbc);

			gbc.insets = new Insets(0, 0, 5, 0);
			gbc.gridy++;
			add(MCerr, gbc);

			gbc.insets = new Insets(0, 0, 0, 0);
			gbc.weighty = 0.5;
			gbc.anchor = GridBagConstraints.CENTER;
			gbc.gridx = 0;
			gbc.gridy++;
			gbc.gridwidth = 2;
			add(cellsToLiveLbl, gbc);
			
			gbc.weighty = 0;
			gbc.insets = new Insets(0, 0, 10, 0);
			gbc.gridy++;
			add(selectLive, gbc);

			gbc.insets = new Insets(0, 0, 0, 0);
			gbc.weighty = 0.5;
			gbc.gridx = 0;
			gbc.gridy++;
			add(cellsToReproLbl, gbc);
			
			gbc.insets = new Insets(0, 0, 10, 0);
			gbc.weighty = 0;
			gbc.gridy++;
			add(selectRepro, gbc);

			gbc.weighty = 1;
			gbc.insets = new Insets(10, 0, 5, 0);
			gbc.gridx = 0;
			gbc.gridy++;
			gbc.gridwidth = 1;
			add(saveSpecies, gbc);

			gbc.gridx = 1;
			add(removeSpecies, gbc);
			
			Click click = new Click();
			addMouseListener(click);
			setFocusable(true);
		}

		public addSpeciesInterface() {
			this(new Species(Color.white, 20, new int[] {}, new int[] {}, "New Species", 0, -1), true);

		}

		public int indexOf(int[] arr, int a) {
			for (int i = 0; i < arr.length; i++) {
				if (arr[i] == a)
					return i;
			}
			return -1;
		}

		public void openColorChooser() {
			jcc = new JColorChooser();
			colorChooser = new JFrame("Choose a Color");
			colorChooser.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			colorChooser.setLocationRelativeTo(null);
			colorChooser.setResizable(false);
			colorChooser.getContentPane().setBackground(Color.black);
			colorChooser.setAlwaysOnTop(true);
			addSpeciesFrame.setAlwaysOnTop(false);
			colorChooser.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					addSpeciesFrame.setAlwaysOnTop(true);
				}
			});

			c.gridx = 0;
			c.gridy = 0;
			colorChooser.add(jcc, c);

			saveColor = new DevButton2("Save", new Dimension(15, 12), Color.black, Color.green, true);
			saveColor.setBorders(BorderFactory.createLineBorder(Color.green, 2));
			saveColor.setFontSize(20);

			saveColor.addMouseListener(new MouseListen() {
				public void mouseClicked(MouseEvent e) {
					clr = jcc.getColor();
					colorChooser.dispatchEvent(new WindowEvent(colorChooser, WindowEvent.WINDOW_CLOSING));
					clrPickBox.setBackground(clr);
				}
			});

			c.gridy = 1;
			c.insets = new Insets(10, 0, 10, 0);
			colorChooser.add(saveColor, c);

			colorChooser.pack();
			colorChooser.setVisible(true);
		}

		public int findIndexOfSpecies(String name) {
			for (int i = 0; i < species.size(); i++)
				if (species.get(i).toString().equals(name))
					return i;
			return -1;
		}

		public boolean validMutChance(int dex, double newMutChance) {
			double sum = 0;
			for (int i = 0; i < children.get(dex).size(); i++) {
				sum += children.get(dex).get(i).getMutChance();
			}
			if (newSpecies) {
				return sum + newMutChance <= 1;
			} else {
				return (sum - oldSpecies.getMutChance()) + newMutChance <= 1;
			}
		}

		public boolean validColor(Color c) {
			for (Species s : species) {
				if (s.getColor().equals(c) && (newSpecies || (!newSpecies && !s.equals(oldSpecies)))) {
					return false;
				}
			}

			return true;
		}

		public void updateIds() {
			for (int i = 0; i < children.size(); i++) {
				for (Species s: children.get(i)) {
					s.setIndexOfParent(i);
				}
			}
		}

		
		
		private class cellChooser extends JPanel {
			
			private JLabel[] nums;
			private boolean[] numStates;
			private final static Color regBG = new Color(90, 90, 90);
			private final static Color activeBG = new Color(39, 184, 133);
			private final static Color hoverRegBG = new Color(120, 120, 120);
			private final static Color hoverActiveBG = new Color(69, 214, 163);
			
			public cellChooser(int[] states) {
				setLayout(new GridBagLayout());
				setBackground(new Color(50, 50, 50));
				setOpaque(true);
				
				nums = new JLabel[8];
				importSelections(states);
				
				GridBagConstraints abc = new GridBagConstraints();
				
				for (byte i = 0; i < nums.length; i++) {
					nums[i] = new JLabel("" + (i + 1), SwingConstants.CENTER);
					nums[i].setPreferredSize(new Dimension(30, 30));
					nums[i].setOpaque(true);
					nums[i].setForeground(Color.white);
					nums[i].setFont(new Font("Dialog", Font.BOLD, 20));
					
					if (numStates[i])
						nums[i].setBackground(activeBG);
					else
						nums[i].setBackground(regBG);
					
					nums[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
					
					abc.gridy = 0;
					abc.gridx = i;
					abc.insets = new Insets(5, 5, 5, 5);
					add(nums[i], abc);
					
					nums[i].addMouseListener(new LabelMouser(i));
					
				}
				
				
			}
			
			public int[] exportSelections() {
				byte count = 0;
				for (boolean b : numStates)
					if (b)
						count++;
				int[] out = new int[count];
				count = 0;
				for (byte i = 0; i < numStates.length; i++) {
					if (numStates[i]) {
						out[count] = i + 1;
						count++;
					}
				}
				
				return out;
			}
			
			private void importSelections(int[] selects) {
				numStates = new boolean[8];
				for (byte i = 0; i < selects.length; i++)
					numStates[selects[i] - 1] = true;
			}
			
			public class LabelMouser extends MouseListen {
				
				private byte index;
				
				public LabelMouser(byte index) {
					this.index = index;
				}
				
				public void mouseEntered(MouseEvent e) {
					if (numStates[index])
						nums[index].setBackground(hoverActiveBG);
					else
						nums[index].setBackground(hoverRegBG);
				}
				public void mouseExited(MouseEvent e) {
					if (numStates[index])
						nums[index].setBackground(activeBG);
					else
						nums[index].setBackground(regBG);
				}
				public void mouseClicked(MouseEvent e) {
					if (numStates[index]) {
						nums[index].setBackground(regBG);
						numStates[index] = false;
					}
					else {
						nums[index].setBackground(activeBG);
						numStates[index] = true;
					}
				}
				
			}
		}
	}
	
	public class GridTemplateContainer extends JPanel {
		
		private JLabel title;
		private JLabel shipsLbl;
		private JLabel oscLbl;
		private JPanel tempCont;
		private JScrollPane jsp;
		
		public GridTemplateContainer() {
			super();
			setLayout(new GridBagLayout());
			setBackground(Color.black);
			setOpaque(true);
			setPreferredSize(new Dimension(650, 650));
			
			title = new JLabel("Templates");
			title.setForeground(Color.white);
			title.setFont(new Font("Arial", Font.BOLD, 40));
			
			tempCont = new JPanel();
			tempCont.setPreferredSize(new Dimension(550, 800));
			tempCont.setLayout(new GridBagLayout());
			tempCont.setBackground(Color.black);
			tempCont.setOpaque(true);
			
			jsp = new JScrollPane(tempCont);
			jsp.setPreferredSize(new Dimension(600, 500));
			
			
			GridBagConstraints c = new GridBagConstraints();
			
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 3;
			c.weightx = 1;
			c.weighty = 1;
			add(title, c);
			
			c.gridy++;
			add(jsp, c);
			
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 1;
			
			for (int i = 0; i < templates.size(); i++) {
				if (i%3 == 0) {
					c.gridx = 0;
					c.gridy++;
				}
				templates.get(i).setNewColor(getActiveSpecies().getClr());
				tempCont.add(templates.get(i), c);
				c.gridx++;
			}
			
			
			
			tempCont.addMouseListener(new MouseListen() {
				public void mouseClicked(MouseEvent e) {
					System.out.println(tempCont.getComponentAt(e.getPoint()));
					if (tempCont.getComponentAt(e.getPoint()) instanceof GridTemplate) {
						System.out.println(2);
						deactivateAllTemplates();
						activeTemplate = (byte)templates.indexOf((GridTemplate)tempCont.getComponentAt(e.getPoint()));
						templates.get(activeTemplate).activate();
					}
				}
			});
			
		}
		
		private void deactivateAllTemplates() {
			for (GridTemplate gt: templates)
				gt.deactivate();
		}
		
	}

}
