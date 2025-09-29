import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;


public class DevButton2 extends JLabel {
	
	private Color FG, BG, hoverFG, hoverBG;
	public final static Color defaultFG = Color.white;
	public final static Color defaultBack = Color.black;
	public final static Color defaultHighlight = new Color(255, 255, 255, 150);
	public final static double defaultTransitionDuration = 100;
	private Dimension pixelsAround;
	private double transitionTime;
	private Border border, hoverBorder;
	private Dimension dim;
	private boolean hover, textSized;
	private colorTrans cT;
	
	public DevButton2(int w, int h, Color foreground, Color background, Color hoverFG, Color hoverBack, String text, Font font, Border border, Border hoverBorder,
			double transitionTime) {
		super();
		dim = new Dimension(w, h);
		setPreferredSize(dim);
		FG = foreground;
		BG = background;
		this.hoverFG = hoverFG;
		hoverBG = hoverBack;
		this.border = border;
		this.hoverBorder = hoverBorder;
		this.transitionTime = transitionTime;
		
		setBorder(border);
		setBackground(BG);
		setForeground(FG);
		setOpaque(true);
		
		textSized = false;

		setHorizontalAlignment(SwingConstants.CENTER);
		
		setFont(font);
		setText(text);

		hover = false;
		setCursor(new Cursor(Cursor.HAND_CURSOR));

		cT = new colorTrans(false);
		
		addMouseListener(new Mouser());
	}

	public DevButton2(int w, int h, Color Foreground, Color background, Color hoverFG, Color hoverBack, String text,
			Font font, Border b, double transitionTime) {
		this(w, h, Foreground, background, hoverFG, hoverBack, text, font, b, b, transitionTime);
	}

	public DevButton2(int w, int h, Color FG, Color back, String text, Font font, boolean invertColors,
			double transitionTime) {
		this(w, h, FG, back, FG, back, text, font, BorderFactory.createEmptyBorder(), transitionTime);
		if (invertColors) {
			hoverBG = FG;
			hoverFG = back;
		}
	}

	public DevButton2(int w, int h, Color FG, Color back, String text, boolean invertColors) {
		this(w, h, FG, back, text, new JLabel().getFont(), invertColors, defaultTransitionDuration);
	}
	
	public DevButton2(String text, Dimension pixelsAroundText, Color FG, Color BG, boolean invertColors) {
		this(0, 0, FG, BG, text, invertColors);
		this.pixelsAround = pixelsAroundText;
		resize();
		textSized = true;
	}
	
	public DevButton2(int w, int h, String text, Color FG, Color BG, Color hoverFG, Color hoverBG, Font font, double transitionTime) {
		this(w, h, FG, BG, hoverFG, hoverBG, text, font, BorderFactory.createEmptyBorder(), transitionTime);
	}

	public DevButton2(int w, int h, Color back) {
		this(w, h, defaultFG, back, "", false);
		transitionTime = 0;
	}

	public DevButton2(String text) {
		setText(text);
	}
	
	public void hoverOn() {
		if (!hover) {
			hover = true;
			setBorder(hoverBorder);
			cT.end();
			cT = new colorTrans(true);
			cT.start();

		}
	}

	public void hoverOff() {
		if (hover) {
			hover = false;
			setBorder(border);
			cT.end();
			cT = new colorTrans(false);
			cT.start();
		}
	}

	public class colorTrans extends Thread {
		private boolean hover, stop;

		public colorTrans(boolean hover) {
			this.hover = hover;
			stop = false;
		}

		public void run() {
			double rIncrFG = 0;
			double gIncrFG = 0;
			double bIncrFG = 0;
			double rIncrBack = 0;
			double gIncrBack = 0;
			double bIncrBack = 0;

			if (hover) {

				rIncrFG = (double) (hoverFG.getRed() - getForeground().getRed()) / 100;
				gIncrFG = (double) (hoverFG.getGreen() - getForeground().getGreen()) / 100;
				bIncrFG = (double) (hoverFG.getBlue() - getForeground().getBlue()) / 100;


				rIncrBack = (double) (hoverBG.getRed() - getBackground().getRed()) / 100;
				gIncrBack = (double) (hoverBG.getGreen() - getBackground().getGreen()) / 100;
				bIncrBack = (double) (hoverBG.getBlue() - getBackground().getBlue()) / 100;

			} else {
				rIncrFG = (double) (FG.getRed() - getForeground().getRed()) / 100;
				gIncrFG = (double) (FG.getGreen() - getForeground().getGreen()) / 100;
				bIncrFG = (double) (FG.getBlue() - getForeground().getBlue()) / 100;

				rIncrBack = (double) (BG.getRed() - getBackground().getRed()) / 100;
				gIncrBack = (double) (BG.getGreen() - getBackground().getGreen()) / 100;
				bIncrBack = (double) (BG.getBlue() - getBackground().getBlue()) / 100;

			}
			for (short i = 0; i < 100 && !stop; i++) {
				setBackground(new Color((getBackground().getRed() + intToAdd(rIncrBack, i)),
						(getBackground().getGreen() + intToAdd(gIncrBack, i)),
						(getBackground().getBlue() + intToAdd(bIncrBack, i))));

				setForeground(new Color((getForeground().getRed() + intToAdd(rIncrFG, i)),
						(getForeground().getGreen() + intToAdd(gIncrFG, i)),
						(int) (getForeground().getBlue() + intToAdd(bIncrFG, i))));

				try {
					Thread.sleep((int) (transitionTime/100), (int) (10 * (transitionTime % 1.0)));
				} catch (InterruptedException e) {
				}
			}
		}

		public void end() {
			stop = true;
		}
	}

	private int intToAdd(double incr, short i) {
		double decimal = Math.abs(incr % 1.0);
		int sign = (int) (incr / Math.abs(incr));
		if ((int) (decimal * (i + 1)) > (int) (decimal * i))
			return (int) incr + sign;
		return (int) incr;
	}
	
	private void resize() {
		JPanel temp = new JPanel();
		dim = new Dimension((int)(temp.getFontMetrics(getFont()).stringWidth(getText()) + pixelsAround.getWidth()), (int) (temp.getFontMetrics(getFont()).getHeight() + pixelsAround.getHeight()));
		setPreferredSize(dim);
	}
	
	public void setText(String text) {
		super.setText(text);
		if (textSized)
			resize();
	}
	
	public Color getFG() {
		return FG;
	}

	public void setFG(Color fG) {
		FG = fG;
	}

	public Color getBG() {
		return BG;
	}

	public void setBG(Color bG) {
		BG = bG;
	}

	public Color getHoverFG() {
		return hoverFG;
	}

	public void setHoverFG(Color hoverFG) {
		this.hoverFG = hoverFG;
	}

	public Color getHoverBG() {
		return hoverBG;
	}

	public void setHoverBG(Color hoverBG) {
		this.hoverBG = hoverBG;
	}

	public double getTransitionTime() {
		return transitionTime;
	}

	public void setTransitionTime(double transitionTime) {
		this.transitionTime = transitionTime;
	}
	
	public void setFontSize(float size) {
		setFont(this.getFont().deriveFont(size));
	}
	
	public void setFont(Font f) {
		super.setFont(f);
		if (textSized)
			resize();
	}
	
	public void setBorders(Border border, Border hoverBorder, Border clickBorder) {
		this.border = border;
		setBorder(border);
		this.hoverBorder = hoverBorder;
	}
	
	public void setBorders(Border border) {
		setBorders(border, border, border);
	}
	
	public void setWidth(int w) {
		dim = new Dimension(w, (int) dim.getHeight());
		setPreferredSize(dim);
	}
	
	public void setHeight(int h) {
		dim = new Dimension((int) dim.getWidth(), h);
		setPreferredSize(dim);
	}

	public class Mouser extends MouseListen {
		
		public void mouseEntered(MouseEvent e) {
			hoverOn();
		}
		public void mouseExited(MouseEvent e) {
			hoverOff();
		}
		public void mousePressed(MouseEvent e) {
			
		}
		public void mouseReleased(MouseEvent e) {
			
		}
	}
	
}
