import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.border.LineBorder;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

public class DevButton extends JPanel {
	private Color FG, BG, hoverFG, hoverBG, clickFG, clickBG, highlightColor;
	public final static Color defaultFG = Color.white;
	public final static Color defaultBack = Color.black;
	public final static Color defaultHighlight = new Color(255, 255, 255, 150);
	private String text;
	private Font font;
	public final static Font defaultFont = new Font("Dialog", Font.BOLD, 15);
	public final static int defaultPixelsAroundText = 10;
	public final static double defaultTransitionDuration = 100;
	private Border border, hoverBorder, clickBorder;
	private Dimension dim;
	private boolean hover, sizedFromText;
	private colorTrans cT;
	private int pixelsAroundText;
	private double transitionTime;
	private byte buttonType;
	private BufferedImage image;
	/*
	 * BUTTON TYPES: 
	 * 0 - Color Change 
	 * 1 - Regular Highlight 
	 * 2 - Image with Background 
	 * 3 - Image with Background with Highlight 
	 * 4 - Image 
	 * 5 - Image with Highlight
	 */

	// COLOR CHANGE (0)
	// ----------------------------------------------------------------------------------------------------------

	public DevButton(int w, int h, Color foreground, Color background, Color hoverFG, Color hoverBack, Color clickFG,
			Color clickBG, String text, Font font, Border border, Border hoverBorder, Border clickBorder,
			double transitionTime) {
		super();
		dim = new Dimension(w, h);
		setPreferredSize(dim);
		FG = foreground;
		BG = background;
		this.hoverFG = hoverFG;
		hoverBG = hoverBack;
		this.clickFG = clickFG;
		this.clickBG = clickBG;
		this.text = text;
		this.font = font;
		this.border = border;
		this.hoverBorder = hoverBorder;
		this.clickBorder = clickBorder;
		this.transitionTime = transitionTime;
		
		

		hover = false;
		sizedFromText = false;

		setBorder(border);
		setBackground(BG);
		setForeground(FG);
		setCursor(new Cursor(Cursor.HAND_CURSOR));

		cT = new colorTrans(false);

		buttonType = 0;
		
		addMouseListener(new Mouser());
	}

	public DevButton(int w, int h, Color Foreground, Color background, Color hoverFG, Color hoverBack, String text,
			Font font, Border b, double transitionTime) {
		this(w, h, Foreground, background, hoverFG, hoverBack, null, null, text, font, b, b, b, transitionTime);
	}

	public DevButton(int w, int h, Color FG, Color back, String text, Font font, boolean invertColors,
			double transitionTime) {
		this(w, h, FG, back, FG, back, text, font, BorderFactory.createEmptyBorder(), transitionTime);
		if (invertColors) {
			hoverBG = FG;
			hoverFG = back;
		}
	}

	public DevButton(int w, int h, Color FG, Color back, String text, boolean invertColors) {
		this(w, h, FG, back, text, defaultFont, invertColors, 100);
	}
	
	public DevButton(String text, Color FG, Color BG, Color hoverFG, Color hoverBG, int pixelsAroundText, Font font, double transitionTime) {
		this(new JPanel().getFontMetrics(font).stringWidth(text) + pixelsAroundText,
				new JPanel().getFontMetrics(font).getHeight() + pixelsAroundText, FG, BG, hoverFG, hoverBG, text, font, BorderFactory.createEmptyBorder(), transitionTime);
	}

	public DevButton(String text, Color FG, Color back, int pixelsAroundText, Font font, boolean invertColors) {
		this(new JPanel().getFontMetrics(font).stringWidth(text) + pixelsAroundText,
				new JPanel().getFontMetrics(font).getHeight() + pixelsAroundText, FG, back, text, font, invertColors,
				defaultTransitionDuration);
		sizedFromText = true;
		this.pixelsAroundText = pixelsAroundText;
	}

	public DevButton(int w, int h, Color back) {
		this(w, h, defaultFG, back, "", false);
		transitionTime = 0;
	}

	public DevButton(String text, int pixelsAroundText) {
		this(text, defaultFG, defaultBack, pixelsAroundText, defaultFont, false);

	}

	public DevButton(String text) {
		this(text, defaultPixelsAroundText);
	}

	// REGULAR HIGHLIGHT (1)
	// --------------------------------------------------------------------------------------------------------

	public DevButton(int w, int h, Color foreground, Color background, String text, Font font, Color highlightColor,
			Border border, Border hBorder, Border cBorder) {
		super();
		dim = new Dimension(w, h);
		setPreferredSize(dim);
		FG = foreground;
		BG = background;
		this.text = text;
		this.highlightColor = highlightColor;
		this.border = border;
		hoverBorder = hBorder;
		clickBorder = cBorder;
		this.font = font;

		hover = false;
		sizedFromText = false;

		setBorder(border);
		setBackground(BG);
		setForeground(FG);
		setCursor(new Cursor(Cursor.HAND_CURSOR));

		cT = new colorTrans(false);

		buttonType = 1;

	}

	public DevButton(Color foreground, Color background, String text, Font font, Color highlightColor, Border border,
			int pixelsAroundText) {
		this(new JPanel().getFontMetrics(font).stringWidth(text) + pixelsAroundText,
				new JPanel().getFontMetrics(font).getHeight() + pixelsAroundText, foreground, background, text, font,
				highlightColor, border, border, border);
		sizedFromText = true;
		this.pixelsAroundText = pixelsAroundText;
	}

	public DevButton(Color foreground, Color background, String text) {
		this(foreground, background, text, defaultFont, defaultHighlight, BorderFactory.createEmptyBorder(),
				defaultPixelsAroundText);
	}

	// IMAGE WITH BACKGROUND (2)
	// ------------------------------------------------------------------------------------------------------

	public DevButton(int w, int h, Color background, Color hoverBack, Color clickBG, BufferedImage image, Border border,
			Border hoverBorder, Border clickBorder, double transitionTime) {
		super();
		dim = new Dimension(w, h);
		setPreferredSize(dim);
		BG = background;
		hoverBG = hoverBack;
		this.clickBG = clickBG;
		this.border = border;
		this.hoverBorder = hoverBorder;
		this.clickBorder = clickBorder;
		this.transitionTime = transitionTime;
		this.image = image;

		hover = false;

		setBorder(border);
		setBackground(BG);
		setForeground(FG);
		setCursor(new Cursor(Cursor.HAND_CURSOR));

		cT = new colorTrans(false);

		buttonType = 2;
	}

	public DevButton(int w, int h, Color background, Color hoverBack, Color clickBack, BufferedImage image,
			Border border, double transitionTime) {
		this(w, h, background, hoverBack, clickBack, image, border, border, border, transitionTime);
	}

	public DevButton(Color background, Color hoverBack, Color clickBack, BufferedImage image, Border border,
			int pixelsAroundImage, double transitionTime) {
		this(image.getWidth() + pixelsAroundImage, image.getHeight() + pixelsAroundImage, background, hoverBack,
				clickBack, image, border, transitionTime);
	}

	// IMAGE WITH BACKGROUND WITH HIGHLIGHT (3)

	public DevButton(int w, int h, Color background, BufferedImage image, Border border, Border hoverBorder,
			Border clickBorder, Color highlightColor, double transitionTime) {
		this(w, h, background, background, background, image, border, hoverBorder, clickBorder, transitionTime);

		this.highlightColor = highlightColor;
		buttonType = 3;
	}

	// IMAGE (4)

	public DevButton(BufferedImage image, Border border, Border hoverBord, Border clickBord) {
		super();
		dim = new Dimension(image.getWidth(), image.getHeight());
		setPreferredSize(dim);
		this.border = border;
		this.hoverBorder = hoverBord;
		this.clickBorder = clickBord;
		this.image = image;

		hover = false;

		setBorder(border);
		setCursor(new Cursor(Cursor.HAND_CURSOR));

		cT = new colorTrans(false);

		buttonType = 4;
		
		addMouseListener(new Mouser());
	}

	public DevButton(BufferedImage image) {
		this(image, BorderFactory.createEmptyBorder(), BorderFactory.createEmptyBorder(),
				BorderFactory.createEmptyBorder());
	}

	// IMAGE WITH HIGHLIGHT (5)

	public DevButton(BufferedImage image, Border border, Color highlightColor) {
		this(image, border, border, border);
		this.highlightColor = highlightColor;
		buttonType = 5;
	}
	
	

	public void press() {
		setBorder(clickBorder);
	}

	public void release() {
		setBorder(hoverBorder);
	}

	public void hoverOn() {
		if (!hover) {
			hover = true;
			setBorder(hoverBorder);
			switch (buttonType) {
			case 0, 2:
				cT.end();
				cT = new colorTrans(true);
				cT.start();
				break;
			case 1, 3, 5:
				repaint();
			}

		}
	}

	public void hoverOff() {
		if (hover) {
			hover = false;
			setBorder(border);
			switch (buttonType) {
			case 0, 2:
				cT.end();
				cT = new colorTrans(false);
				cT.start();
				break;
			case 1, 3, 5:
				repaint();
			}
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
				if (buttonType == 0) {
					rIncrFG = (double) (hoverFG.getRed() - getForeground().getRed()) / 100;
					gIncrFG = (double) (hoverFG.getGreen() - getForeground().getGreen()) / 100;
					bIncrFG = (double) (hoverFG.getBlue() - getForeground().getBlue()) / 100;
				}
				if (buttonType == 0 || buttonType == 2) {
					rIncrBack = (double) (hoverBG.getRed() - getBackground().getRed()) / 100;
					gIncrBack = (double) (hoverBG.getGreen() - getBackground().getGreen()) / 100;
					bIncrBack = (double) (hoverBG.getBlue() - getBackground().getBlue()) / 100;
				}
			} else {
				if (buttonType == 0) {
					rIncrFG = (double) (FG.getRed() - getForeground().getRed()) / 100;
					gIncrFG = (double) (FG.getGreen() - getForeground().getGreen()) / 100;
					bIncrFG = (double) (FG.getBlue() - getForeground().getBlue()) / 100;
				}
				if (buttonType == 0 || buttonType == 2) {
					rIncrBack = (double) (BG.getRed() - getBackground().getRed()) / 100;
					gIncrBack = (double) (BG.getGreen() - getBackground().getGreen()) / 100;
					bIncrBack = (double) (BG.getBlue() - getBackground().getBlue()) / 100;
				}
			}
			for (short i = 0; i < 100 && !stop; i++) {
				if (buttonType == 0 || buttonType == 2)
					setBackground(new Color((getBackground().getRed() + intToAdd(rIncrBack, i)),
							(getBackground().getGreen() + intToAdd(gIncrBack, i)),
							(getBackground().getBlue() + intToAdd(bIncrBack, i))));

				if (buttonType == 0)
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

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (buttonType <= 1) {
			g.setFont(font);
			g.setColor(getForeground());

			FontMetrics fm = g.getFontMetrics();
			g.drawString(text, (getWidth() / 2) - (fm.stringWidth(text) / 2),
					(getHeight() / 2) - (int) (fm.getStringBounds(text, g).getHeight() / 2) + fm.getAscent());
		}
		if (buttonType >= 2) {
			g.drawImage(image, (getWidth()/2) - (image.getWidth()/2), (getHeight()/2) - (image.getHeight()/2), null);
		}
		if ((buttonType == 1 || buttonType == 3 || buttonType == 5) && hover) {
			g.setColor(highlightColor);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	public boolean isHover() {
		return hover;
	}

	public void setHover(boolean hover) {
		this.hover = hover;
	}

	public void setFG(Color FG) {
		super.setForeground(FG);
		this.FG = FG;
	}
	
	public void setAllFG(Color fore) {
		super.setForeground(fore);
		this.FG = fore;
		this.hoverFG = fore;
		this.clickFG = fore;
	}

	public void setBG(Color back) {
		super.setBackground(back);
		this.BG = back;
	}
	
	public void setAllBG(Color back) {
		super.setBackground(back);
		this.BG = back;
		this.hoverBG = back;
		this.clickBG = back;
	}

	public Color gethoverFG() {
		return hoverFG;
	}

	public void sethoverFG(Color hoverFG) {
		this.hoverFG = hoverFG;
	}

	public Color gethoverBG() {
		return hoverBG;
	}

	public void sethoverBG(Color hoverBG) {
		this.hoverBG = hoverBG;
	}

	public Color getclickFG() {
		return clickFG;
	}

	public void setclickFG(Color clickFG) {
		this.clickFG = clickFG;
	}

	public Color getclickBG() {
		return clickBG;
	}

	public void setclickBG(Color clickBG) {
		this.clickBG = clickBG;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}
	
	public Border getBorder() {
		return super.getBorder();
	}

	public void setOffBorder(Border border) {
		this.border = border;
	}

	public Border getHoverBorder() {
		return hoverBorder;
	}

	public void setHoverBorder(Border hoverBorder) {
		this.hoverBorder = hoverBorder;
	}

	public Border getClickBorder() {
		return clickBorder;
	}

	public void setClickBorder(Border clickBorder) {
		this.clickBorder = clickBorder;
	}

	public void setBorders(Border border, Border hoverBorder, Border clickBorder) {
		this.border = border;
		setBorder(border);
		this.hoverBorder = hoverBorder;
		this.clickBorder = clickBorder;
	}
	
	public void setBorders(Border border) {
		setBorders(border, border, border);
	}

	public void setFontSize(float size) {
		font = font.deriveFont(size);
		if (sizedFromText) {
			dim = new Dimension(new JPanel().getFontMetrics(font).stringWidth(text) + pixelsAroundText,
					new JPanel().getFontMetrics(font).getHeight() + pixelsAroundText);
			setPreferredSize(dim);
		}

	}
	
	public class Mouser extends MouseListen {
		
		public void mouseEntered(MouseEvent e) {
			hoverOn();
		}
		public void mouseExited(MouseEvent e) {
			hoverOff();
		}
		public void mousePressed(MouseEvent e) {
			press();
		}
		public void mouseReleased(MouseEvent e) {
			release();
		}
	}

}
