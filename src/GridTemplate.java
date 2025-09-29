import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class GridTemplate extends JPanel {
	
	private boolean[][] template;
	private int fullGridWidth;
	private Point2D.Double mouseLoc;
	private double cellLength;
	private boolean shiftHoriz, shiftVert;
	private Color cellColor;
	private JLabel label;
	private InnerGrid ig;

	public GridTemplate(String name, int renderWidth, boolean[][] template) {
		super();
		this.template = template;
		
		if (template.length > template[0].length) {
			fullGridWidth = template.length + 2;
			if ((template.length - template[0].length) % 2 == 1)
				shiftHoriz = true;
		}
		else {
			fullGridWidth = template[0].length + 2;
			if ((template[0].length - template.length) % 2 == 1)
				shiftVert = true;
		}
		
		setPreferredSize(new Dimension(renderWidth + 10, renderWidth + 40));
		
		this.mouseLoc = new Point2D.Double(template[0].length/2.0, template.length/2.0);
		
		cellLength = (double)renderWidth/fullGridWidth;
		
		cellColor = Color.white;
		
		setBackground(Color.black);
		setOpaque(true);
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		setLayout(new GridBagLayout());
		
		ig = new InnerGrid(renderWidth);
		
		label = new JLabel(name);
		label.setBackground(Color.black);
		label.setForeground(Color.white);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(label.getFont().deriveFont((float)(label.getFont().getSize()*1.5)));
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.1;
		add(ig, c);
		
		c.gridy++;
		c.weighty = 1;
		c.anchor = GridBagConstraints.CENTER;
		add(label, c);
		
		deactivate();
		
	}
	
	public void activate() {
		setBorder(BorderFactory.createLineBorder(Color.yellow, 4));
	}
	
	public void deactivate() {
		setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
	}
	
	public void setNewColor(Color c) {
		cellColor = c;
		ig.repaint();
	}
	
	
	
	public boolean[][] getTemplate() {
		return template;
	}

	public void setTemplate(boolean[][] template) {
		this.template = template;
	}

	public Point2D.Double getMouseLoc() {
		return mouseLoc;
	}

	public void setMouseLoc(Point2D.Double mouseLoc) {
		this.mouseLoc = mouseLoc;
	}





	private class InnerGrid extends JPanel {
		
		private InnerGrid(int renderWidth) {
			setBackground(Color.black);
			setOpaque(true);
			setPreferredSize(new Dimension(renderWidth + 1, renderWidth + 1));
		}
		
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			super.paintComponent(g2);
			int y = 0;
			int x = 0;
			int incrX;
			int incrY;
			int hStart = (int)Math.floor((fullGridWidth - template[0].length)/2.0);
			int hEnd = hStart + template[0].length;
			int vStart = (int)Math.floor((fullGridWidth - template.length)/2.0);
			int vEnd = vStart + template.length;

			for (int i = shiftVert?-1:0; i < fullGridWidth; i++) {
				incrY = intToAdd(cellLength, i);
				
				if (i == -1)
					y = (int)(-0.5 * cellLength);
				
				for (int j = shiftHoriz?-1:0; j < fullGridWidth; j++) {
					incrX = intToAdd(cellLength, j);
					
					if (j == -1)
						x = (int)(-0.5 * cellLength);

					g2.setColor(Color.white);
					g2.drawRect(x, y, incrX, incrY);
					
					if (j >= hStart && j < hEnd && i >= vStart && i < vEnd && template[i - vStart][j - hStart]) {
						g2.setColor(cellColor);
						g2.fillRect(x, y, incrX, incrY);
					}

					x += incrX;
				}
				y += incrY;
				x = 0;
			}

		}
		
	}
	
	private int intToAdd(double incr, int i) {
		double decimal = Math.abs(incr % 1.0);
		if ((int) (decimal * (i + 1)) > (int) (decimal * i))
			return (int) incr + 1;
		return (int) incr;
	}
	
}
