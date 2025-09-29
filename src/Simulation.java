import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class Simulation extends JLayeredPane {

	private final static double speedMultiplier = 50000;
	private final static double ogSpeed = 100;
	private Grid grid;
	private JPanel maxStats;
	private sidePanel lPanel, rPanel;
	private JLabel play, title;
	private boolean playing, fullScreen;
	private Timer tim;
	private TimerTask run;
	private int gens;
	private double speed;
	private JLabel coordTitle, coordLabel, maxCoordLabel, findPtsLbl, xIn, yIn, zoomTitle, autoZoomLbl, speedTitle,
			incrSpeed, decrSpeed, oneGen, genLbl, maxGenLbl, totalAlive, maxTotal, speciesTitle, selectedAlive, maxSelected, toolsTitle,
			rCellGenTitle, rGenX1Lbl, rGenX2Lbl, rGenY1Lbl, rGenY2Lbl, chanceOfGenLbl, rGenErr, err1;
	private DevButton maximize;
	private DevButton2 reset, searchBut, genBut;
	private JPanel autoZoomCheckBox, controls;
	private JFormattedTextField xInField, yInField, zoomInField, speedInField, ignoreField, rGenX1, rGenX2, rGenY1, rGenY2, chanceOfGen;
	private speciesContainer sCont;
	private toolsContainer tCont;

	public Simulation(Dimension simulationDim, Dimension universeDim) {

		setLayout(new GridBagLayout());
		setBackground(Color.black);
		setPreferredSize(simulationDim);

		GridBagConstraints c = new GridBagConstraints();

		tim = new Timer();

		speed = ogSpeed;
		gens = 0;
		
		playing = false;
		fullScreen = false;

		play = new JLabel("\u25B6");
		play.setPreferredSize(new Dimension(30, 30));
		play.setForeground(Color.white);
		play.setFont(new Font("Serif", Font.BOLD, 25));
		play.setCursor(new Cursor(Cursor.HAND_CURSOR));
		play.addMouseListener(new MouseListen() {
			public void mouseClicked(MouseEvent e) {
				if (playing) {
					playing = false;
					play.setText("\u25B6");
					run.cancel();
				} else {
					playing = true;
					play.setText("\u23F8");
					run = new tT();
					tim.scheduleAtFixedRate(run, 0, (long)(speedMultiplier / speed));
				}
			}
		});

		incrSpeed = new JLabel("\u23E9");
		incrSpeed.setPreferredSize(new Dimension(30, 30));
		incrSpeed.setForeground(Color.white);
		incrSpeed.setFont(new Font("Serif", Font.BOLD, 25));
		incrSpeed.setCursor(new Cursor(Cursor.HAND_CURSOR));
		incrSpeed.addMouseListener(new MouseListen() {
			public void mouseClicked(MouseEvent e) {
				speed += 10;
				speedInField.setText("" + Math.round(speed));
				if (playing) {
					restartTimerTask();
				}
			}
		});

		decrSpeed = new JLabel("\u23EA");
		decrSpeed.setPreferredSize(new Dimension(30, 30));
		decrSpeed.setForeground(Color.white);
		decrSpeed.setFont(new Font("Serif", Font.BOLD, 25));
		decrSpeed.setCursor(new Cursor(Cursor.HAND_CURSOR));
		decrSpeed.addMouseListener(new MouseListen() {
			public void mouseClicked(MouseEvent e) {
				if (speed > 10) {
					speed -= 10;
					speedInField.setText("" + Math.round(speed));
					if (playing) {
						restartTimerTask();
					}
				}
			}
		});

		oneGen = new JLabel("\u23ED");
		oneGen.setPreferredSize(new Dimension(30, 30));
		oneGen.setForeground(Color.white);
		oneGen.setFont(new Font("Serif", Font.BOLD, 25));
		oneGen.setCursor(new Cursor(Cursor.HAND_CURSOR));
		oneGen.addMouseListener(new MouseListen() {
			public void mouseClicked(MouseEvent e) {
				if (!playing) {
					grid.play();
					gens++;
					genLbl.setText("Generations: " + gens);
					genLbl.repaint();
				}
			}
		});

		grid = new Grid(20, 900, 650, (int)universeDim.getWidth(), (int)universeDim.getHeight());
		grid.addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				Point curPoint = grid.coordToCell(new Point(e.getX(), e.getY()));
				if (fullScreen)
					maxCoordLabel.setText("(" + curPoint.x + ", " + curPoint.y + ")");
				else
					coordLabel.setText("Mouse Coordinates: (" + curPoint.x + ", " + curPoint.y + ")");
			}
		});
		
		
		lPanel = new sidePanel(260, 700);
		rPanel = new sidePanel(260, 700);
		

		// LPANEL
		coordTitle = new JLabel("<HTML><U>Coordinates</U></HTML>");
		coordTitle.setForeground(Color.white);
		coordTitle.setFont(new Font("Arial", Font.BOLD, 20));

		findPtsLbl = new JLabel("Find Point: ");
		findPtsLbl.setForeground(Color.white);
		findPtsLbl.setFont(new Font("Arial", Font.BOLD, 15));
		
		// Field
		xInField = new JFormattedTextField();
		xInField.setHorizontalAlignment(SwingConstants.CENTER);
		xInField.setBackground(Color.black);
		xInField.setForeground(Color.white);
		xInField.setCaretColor(Color.white);
		xInField.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(0)));
		xInField.setPreferredSize(new Dimension(50, 20));
		xInField.setText("500");
		
		yInField = new JFormattedTextField();
		yInField.setHorizontalAlignment(SwingConstants.CENTER);
		yInField.setBackground(Color.black);
		yInField.setForeground(Color.white);
		yInField.setCaretColor(Color.white);
		yInField.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(0)));
		yInField.setPreferredSize(new Dimension(50, 20));
		yInField.setText("500");

		xIn = new JLabel("X Coordinate: ");
		xIn.setForeground(Color.white);

		yIn = new JLabel("Y Coordinate: ");
		yIn.setForeground(Color.white);

		searchBut = new DevButton2("Find", new Dimension(15, 10), Color.white, Color.black, true);
		searchBut.setFontSize(18);
		searchBut.addMouseListener(new MouseListen() {
			public void mouseClicked(MouseEvent e) {
				try {
					if (Double.parseDouble(xInField.getText()) >= 0
							&& Double.parseDouble(xInField.getText()) < universeDim.getWidth()
							&& Double.parseDouble(yInField.getText()) >= 0
							&& Double.parseDouble(yInField.getText()) < universeDim.getHeight()) {
						double x = Double.parseDouble(xInField.getText());
						double y = Double.parseDouble(yInField.getText());
						if (x % 1 <= 0.0000001)
							x += 0.5;
						if (y % 1 <= 0.0000001)
							y += 0.5;
						grid.centerOnCoord(new Point.Double(x, y));
					} else {
						err1.setText("Coordinates out of Bounds");
					}
				} catch (Exception a) {
					err1.setText("Invalid Entry");
				}
					
			}
		});

		err1 = new JLabel(" ");
		err1.setFont(new Font("Arial", Font.ITALIC, 10));
		err1.setForeground(Color.red);
		
		coordLabel = new JLabel("Mouse Coordinates: (0,0)");
		coordLabel.setFont(new Font("Arial", Font.BOLD, 14));
		coordLabel.setForeground(Color.white);
		
		maxCoordLabel = new JLabel("(0,0)");
		maxCoordLabel.setFont(new Font("Arial", Font.PLAIN, 25));
		maxCoordLabel.setForeground(Color.white);

		zoomTitle = new JLabel("Zoom: ");
		zoomTitle.setForeground(Color.white);
		zoomTitle.setFont(zoomTitle.getFont().deriveFont((float)(zoomTitle.getFont().getSize()*1.5)));
		
		zoomInField = new JFormattedTextField();
		zoomInField.setHorizontalAlignment(SwingConstants.CENTER);
		zoomInField.setBackground(Color.black);
		zoomInField.setForeground(Color.white);
		zoomInField.setCaretColor(Color.white);
		zoomInField.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(0)));
		zoomInField.setPreferredSize(new Dimension(50, 20));
		zoomInField.setText("100.0");
		zoomInField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					double zoom = Double.parseDouble(zoomInField.getText());
					grid.zoom(zoom);
				} catch(Exception b) {
					zoomInField.setText("" + grid.zoomPercent());
				}
				lPanel.requestFocus();
			}
		});

		autoZoomCheckBox = new JPanel();
		autoZoomCheckBox.setSize(new Dimension(20, 20));
		autoZoomCheckBox.setBackground(Color.red);
		autoZoomCheckBox.setOpaque(true);
		autoZoomCheckBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
		autoZoomCheckBox.addMouseListener(new MouseListen() {
			public void mouseClicked(MouseEvent e) {
				if (grid.isAutoZoom()) {
					autoZoomCheckBox.setBackground(Color.white);
					grid.setAutoZoom(false);
				} else {
					autoZoomCheckBox.setBackground(Color.red);
					grid.setAutoZoom(true);
				}
			}
		});

		autoZoomLbl = new JLabel("Auto Zoom");
		autoZoomLbl.setForeground(Color.white);
		
		/*JLabel ignorePercent = new JLabel("%");
		ignorePercent.setForeground(Color.white);
		ignorePercent.setPreferredSize(new Dimension(10, 10));
		
		ignoreLbl = new JLabel("Percent to Ignore:");
		ignoreLbl.setForeground(Color.white);
		
		ignoreField = new JFormattedTextField();
		ignoreField.setHorizontalAlignment(SwingConstants.CENTER);
		ignoreField.setBackground(Color.black);
		ignoreField.setForeground(Color.white);
		ignoreField.setCaretColor(Color.white);
		ignoreField.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(0)));
		ignoreField.setPreferredSize(new Dimension(50, 20));
		ignoreField.setText("0");*/

		speedTitle = new JLabel("Speed: ");
		speedTitle.setForeground(Color.white);
		speedTitle.setFont(speedTitle.getFont().deriveFont((float)(speedTitle.getFont().getSize()*1.5)));
		
		speedInField = new JFormattedTextField();
		speedInField.setHorizontalAlignment(SwingConstants.CENTER);
		speedInField.setBackground(Color.black);
		speedInField.setForeground(Color.white);
		speedInField.setCaretColor(Color.white);
		speedInField.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(0)));
		speedInField.setPreferredSize(new Dimension(50, 20));
		speedInField.setText("100");
		speedInField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					double temp = Double.parseDouble(speedInField.getText());
					if (temp >= 10.0) {
						speed = temp;
						if (playing)
							restartTimerTask();
					}
					else
						speedInField.setText("" + speed);
				} catch(Exception a) {
					speedInField.setText("" + speed);
				}
				lPanel.requestFocus();
			}
		});

		JLabel zoomPercent = new JLabel("%");
		zoomPercent.setForeground(Color.white);
		
		JLabel speedPercent = new JLabel("%");
		speedPercent.setForeground(Color.white);

		genLbl = new JLabel("Generations: 0");
		genLbl.setForeground(Color.white);
		genLbl.setFont(new Font("Arial", Font.BOLD, 16));
		
		maxGenLbl = new JLabel("Gen 0");
		maxGenLbl.setForeground(Color.white);
		maxGenLbl.setFont(new Font("Arial", Font.BOLD, 25));

		totalAlive = new JLabel("Total Alive: 0");
		totalAlive.setForeground(Color.white);
		totalAlive.setFont(new Font("Arial", Font.BOLD, 16));
		
		maxTotal = new JLabel("Total: 0");
		maxTotal.setForeground(Color.white);
		maxTotal.setFont(new Font("Arial", Font.BOLD, 25));

		selectedAlive = new JLabel("Selected Species Alive: 0");
		selectedAlive.setForeground(Color.white);
		selectedAlive.setFont(new Font("Arial", Font.BOLD, 16));
		
		maxSelected = new JLabel("Selected: 0");
		maxSelected.setForeground(Color.white);
		maxSelected.setFont(new Font("Arial", Font.BOLD, 25));

		reset = new DevButton2("Reset Simulation", new Dimension(20, 15), Color.black, Color.red, true);
		reset.setFontSize(20);
		reset.addMouseListener(new MouseListen() {
			public void mouseClicked(MouseEvent e) {
				grid.clearGrid();
				gens = 0;
				genLbl.setText("Generations: 0");
				totalAlive.setText("Total Alive: 0");
				selectedAlive.setText("Selected Species Alive: 0");
				if (playing) {
					playing = false;
					tim.cancel();
					tim = new Timer();
					play.setText("\u25B6");
				}

				grid.resetViewingFrame();
			}
		});
		
		
		int yCoord = 0;
		
		lPanel.addComponent(coordTitle, 0, yCoord, 3, 1, 1, 0.08);
		lPanel.addComponent(coordLabel, 0, ++yCoord, 3, 1, 1, 0.005);
		lPanel.addComponent(findPtsLbl, 0, ++yCoord, 3, 1, 1, 0.08);
		lPanel.addComponent(xIn, 0, ++yCoord, 1, 1, 0.5, 0.008);
		lPanel.addComponent(xInField, 1, yCoord, 2, 1, 0.5, 0.008);
		lPanel.addComponent(yIn, 0, ++yCoord, 1, 1, 0.5, 0.008);
		lPanel.addComponent(yInField, 1, yCoord, 2, 1, 0.5, 0.008);
		lPanel.addComponent(searchBut, 0, ++yCoord, 3, 1, 1, 0.05);
		lPanel.addComponent(err1, 0, ++yCoord, 3, 1, 0, 0);
		
		lPanel.addComponent(Box.createVerticalStrut(25), 0, ++yCoord, 2, 1, 1, 0.2);
		
		lPanel.addComponent(zoomTitle, 0, ++yCoord, 1, 1, 0.3, 0.1);
		lPanel.addComponent(zoomInField, 1, yCoord, 1, 1, 0.5, 0.1);
		lPanel.addComponent(zoomPercent, 2, yCoord, 1, 1, 0.2, 0.1, GridBagConstraints.LINE_START);

		lPanel.addComponent(autoZoomCheckBox, 0, ++yCoord, 1, 1, 0.1, 0.01);
		lPanel.addComponent(autoZoomLbl, 0, yCoord, 3, 1, 0.5, 0.01);
		//lPanel.addComponent(ignoreLbl, 0, ++yCoord, 1, 1, 0.3, 0.01);
		//lPanel.addComponent(ignoreField,1, yCoord, 1, 1, 0.2, 0.01);
		//lPanel.addComponent(ignorePercent, 2, yCoord, 1, 1, 0.1, 0.01);

		lPanel.addComponent(Box.createVerticalStrut(20), 0, ++yCoord, 2, 1, 1, 0.2);

		lPanel.addComponent(speedTitle, 0, ++yCoord, 1, 1, 1, 0);
		lPanel.addComponent(speedInField, 1, yCoord, 1, 1, 1, 0);
		lPanel.addComponent(speedPercent, 2, yCoord, 1, 1, 1, 0);
	
		lPanel.addComponent(Box.createVerticalStrut(10), 0, ++yCoord, 2, 1, 1, 0.2);

		lPanel.addComponent(genLbl, 0, ++yCoord, 3, 1, 1, 0.08);
		lPanel.addComponent(totalAlive, 0, ++yCoord, 3, 1, 1, 0.08);
		lPanel.addComponent(selectedAlive, 0, ++yCoord, 3, 1, 1, 0.08);

		lPanel.addComponent(Box.createVerticalStrut(10), 0,++yCoord, 2, 1, 1, 0.2);

		lPanel.addComponent(reset, 0, ++yCoord, 3, 1, 1, 0.08);

		lPanel.addComponent(Box.createVerticalStrut(5), 0, ++yCoord, 2, 1, 1, 0.2);

		
		
		// RPANEL
		

		rCellGenTitle = new JLabel("<HTML><U>Random Cell Generation</U></HTML>");
		rCellGenTitle.setForeground(Color.white);
		rCellGenTitle.setFont(new Font("Arial", Font.BOLD, 18));

		chanceOfGenLbl = new JLabel("Chance of Generation (%):");
		chanceOfGenLbl.setForeground(Color.white);
		
		chanceOfGen = new JFormattedTextField();
		chanceOfGen.setHorizontalAlignment(SwingConstants.CENTER);
		chanceOfGen.setBackground(Color.black);
		chanceOfGen.setForeground(Color.white);
		chanceOfGen.setCaretColor(Color.white);
		chanceOfGen.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(0)));
		chanceOfGen.setPreferredSize(new Dimension(50, 20));

		rGenX1Lbl = new JLabel("First X Coordinate: ");
		rGenX1Lbl.setForeground(Color.white);
		
		rGenX1 = new JFormattedTextField();
		rGenX1.setHorizontalAlignment(SwingConstants.CENTER);
		rGenX1.setBackground(Color.black);
		rGenX1.setForeground(Color.white);
		rGenX1.setCaretColor(Color.white);
		rGenX1.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(0)));
		rGenX1.setPreferredSize(new Dimension(50, 20));

		rGenX2Lbl = new JLabel("Second X Coordinate: ");
		rGenX2Lbl.setForeground(Color.white);
		
		rGenX2 = new JFormattedTextField();
		rGenX2.setHorizontalAlignment(SwingConstants.CENTER);
		rGenX2.setBackground(Color.black);
		rGenX2.setForeground(Color.white);
		rGenX2.setCaretColor(Color.white);
		rGenX2.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(0)));
		rGenX2.setPreferredSize(new Dimension(50, 20));

		rGenY1Lbl = new JLabel("First Y Coordinate: ");
		rGenY1Lbl.setForeground(Color.white);
		
		rGenY1 = new JFormattedTextField();
		rGenY1.setHorizontalAlignment(SwingConstants.CENTER);
		rGenY1.setBackground(Color.black);
		rGenY1.setForeground(Color.white);
		rGenY1.setCaretColor(Color.white);
		rGenY1.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(0)));
		rGenY1.setPreferredSize(new Dimension(50, 20));

		rGenY2Lbl = new JLabel("Second Y Coordinate: ");
		rGenY2Lbl.setForeground(Color.white);
		
		rGenY2 = new JFormattedTextField();
		rGenY2.setHorizontalAlignment(SwingConstants.CENTER);
		rGenY2.setBackground(Color.black);
		rGenY2.setForeground(Color.white);
		rGenY2.setCaretColor(Color.white);
		rGenY2.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(0)));
		rGenY2.setPreferredSize(new Dimension(50, 20));

		genBut = new DevButton2("Generate", new Dimension(15, 10), Color.white, Color.black, true);
		genBut.setFontSize(18);
		genBut.addMouseListener(new MouseListen() {
			public void mouseClicked(MouseEvent e) {
				double chance;
				int x1;
				int x2;
				int y1;
				int y2;
				rGenErr.setText(" ");
				try {
					chance = (Double.parseDouble(chanceOfGen.getText())) / 100;
					if (chance > 1 || chance < 0) {
						rGenErr.setText("Chance must be between 0 and 100 (inclusive)");
					} else {

						try {
							x1 = (int) Double.parseDouble(rGenX1.getText());
							if (x1 < 0 || x1 >= universeDim.getWidth()) {
								rGenErr.setText("First X Coordinate out of bounds");
							} else {
								try {
									y1 = (int) Double.parseDouble(rGenY1.getText());
									if (y1 < 0 || y1 >= universeDim.getHeight()) {
										rGenErr.setText("First X Coordinate out of bounds");
									} else {
										try {
											x2 = (int) Double.parseDouble(rGenX2.getText());
											if (x2 < 0 || x2 >= universeDim.getWidth()) {
												rGenErr.setText("First X Coordinate out of bounds");
											} else if (x1 > x2) {
												rGenErr.setText("First X Coordinate larger than Second");
											} else {
												try {
													y2 = (int) Double.parseDouble(rGenY2.getText());
													if (y2 < 0 || y2 >= universeDim.getHeight()) {
														rGenErr.setText("First Y Coordinate out of bounds");
													} else if (y1 > y2) {
														rGenErr.setText("First Y Coordinate larger than Second");
													} else {
														rGenErr.setText(" ");
														grid.setAutoZoom(false);
														grid.generateRandomCells(chance, (int) x1, (int) y1,
																(int) x2, (int) y2);
													}
												} catch (Exception f) {
													rGenErr.setText("Second Y Coordinate is not valid");
												}
											}
										} catch (Exception d) {
											rGenErr.setText("Second X Coordinate is not valid");
										}
									}
								} catch (Exception c) {
									rGenErr.setText("First Y Coordinate is not valid");
								}
							}
						} catch (Exception b) {
							rGenErr.setText("First X Coordinate is not valid");
						}
					}

				} catch (Exception a) {
					rGenErr.setText("Chance is not valid");
				}
			}

		});

		rGenErr = new JLabel(" ");
		rGenErr.setFont(new Font("Arial", Font.ITALIC, 10));
		rGenErr.setForeground(Color.red);

		speciesTitle = new JLabel("<HTML><U>Species</U></HTML>");
		speciesTitle.setForeground(Color.white);
		speciesTitle.setFont(new Font("Arial", Font.BOLD, 18));

		toolsTitle = new JLabel("<HTML><U>Tools</U></HTML>");
		toolsTitle.setForeground(Color.white);
		toolsTitle.setFont(new Font("Arial", Font.BOLD, 18));

		sCont = new speciesContainer(180, 120);
		grid.setSimSpecCont(sCont);
		grid.fillSpecCont();
		tCont = new toolsContainer(160, 100);
		
		yCoord = 0;

		rPanel.addComponent(rCellGenTitle, 0, yCoord, 2, 1, 1, 0.6);
		rPanel.addComponent(chanceOfGenLbl, 0, ++yCoord, 1, 1, 0.6, 0.4);
		rPanel.addComponent(chanceOfGen, 1,yCoord, 1, 1, 0.4, 0.4);
		rPanel.addComponent(rGenX1Lbl, 0, ++yCoord, 1, 1, 0.6, 0.4);
		rPanel.addComponent(rGenX1, 1, yCoord, 1, 1, 0.4, 0.4);
		rPanel.addComponent(rGenY1Lbl, 0,++yCoord, 1, 1, 0.6, 0.4);
		rPanel.addComponent(rGenY1, 1, yCoord, 1, 1, 0.4, 0.4);
		rPanel.addComponent(rGenX2Lbl, 0, ++yCoord, 1, 1, 0.6, 0.4);
		rPanel.addComponent(rGenX2, 1, yCoord, 1, 1, 0.4, 0.4);
		rPanel.addComponent(rGenY2Lbl, 0, ++yCoord, 1, 1, 0.6, 0.4);
		rPanel.addComponent(rGenY2, 1, yCoord, 1, 1, 0.4, 0.4);
		rPanel.addComponent(genBut, 0, ++yCoord, 2, 1, 1, 0.4);
		rPanel.addComponent(rGenErr, 0, ++yCoord, 2, 1, 1, 0.1);

		rPanel.addComponent(toolsTitle, 0, ++yCoord, 2, 1, 1, 0.6, GridBagConstraints.PAGE_END);
		rPanel.addComponent(tCont, 0, ++yCoord, 2, 1, 1, 2.5, GridBagConstraints.PAGE_START);

		rPanel.addComponent(speciesTitle, 0, ++yCoord, 2, 1, 1, 0.6, GridBagConstraints.PAGE_END);
		rPanel.addComponent(sCont, 0, ++yCoord, 2, 1, 1, 2.5, GridBagConstraints.PAGE_START);

		title = new JLabel("Game of Life");
		title.setForeground(Color.white);
		title.setFont(new Font("Arial", Font.BOLD, 30));
		
		maxStats = new JPanel();
		maxStats.setPreferredSize(new Dimension(200, 150));
		maxStats.setOpaque(true);
		maxStats.setBackground(new Color(90, 90, 90, 200));
		maxStats.setLayout(new GridBagLayout());
		
		GridBagConstraints b = new GridBagConstraints();
		b.gridy = 0;
		maxStats.add(maxCoordLabel, b);
		b.anchor = GridBagConstraints.LINE_START;
		b.gridy++;
		maxStats.add(maxGenLbl, b);
		b.gridy++;
		maxStats.add(maxTotal, b);
		b.gridy++;
		maxStats.add(maxSelected, b);
		
		controls = new JPanel(new GridBagLayout());
		controls.setPreferredSize(new Dimension(200, 40));
		controls.setBackground(new Color(90, 90, 90, 150));
		c.gridx = 0;
		c.insets = new Insets(0, 8, 0, 8);
		controls.add(decrSpeed, c);
		c.gridx = 1;
		controls.add(play, c);
		c.gridx = 2;
		controls.add(oneGen, c);
		c.gridx = 3;
		controls.add(incrSpeed, c);
		
		maximize = new DevButton(30, 30, Color.black);
		maximize.setText("\u26F6");
		maximize.setFG(new Color(100, 100, 100));
		maximize.setAllBG(new Color(90, 90, 90, 200));
		maximize.setOpaque(false);
		maximize.sethoverFG(Color.white);
		maximize.setFont(new Font("Serif", Font.BOLD, 35));
		maximize.addMouseListener(new MouseListen() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (!fullScreen) {
					removeAll();
					grid.gridResize(1500, 820);
					GridBagConstraints c = new GridBagConstraints();
					
					maxGenLbl.setText("Gen " + gens);
					maxTotal.setText("Total: " + grid.totalAlive());
					if (grid.getActiveIndex() == -1)
						maxSelected.setText("Selected: N/A");
					else
						maxSelected.setText("Selected: " + grid.countActiveSpecies());

					c.insets = new Insets(15, 25, 0, 0);
					c.anchor = GridBagConstraints.FIRST_LINE_START;
					c.gridx = 0;
					c.gridy = 0;
					c.weightx = 0.3;
					add(maxStats, c);
					
					c.weightx = 0;
					c.insets = new Insets(0,18,0,0);
					c.gridheight = 2;
					c.gridwidth = 3;
					add(grid, c);
					
					c.anchor = GridBagConstraints.PAGE_START;
					c.weightx = 0.4;
					c.gridx = 1;
					c.gridy = 0;
					c.gridwidth = 1;
					c.gridheight = 1;
					c.insets = new Insets(10, 0, 0, 0);
					add(title, c);
					
					c.insets = new Insets(0,0,10,0);
					c.gridy = 1;
					c.anchor = GridBagConstraints.PAGE_END;
					
					add(controls, c);
					
					c.insets = new Insets(10,0,0,25);
					c.weightx = 0.3;
					c.anchor = GridBagConstraints.FIRST_LINE_END;
					c.gridx = 2;
					c.gridy = 0;
					maximize.setText("\u25FA");
					maximize.setFG(Color.white);
					maximize.setOpaque(true);
					add(maximize, c);
					
					Component b = Box.createHorizontalStrut(200);
					c.anchor = GridBagConstraints.CENTER;
					c.insets = new Insets(0,0,0,25);
					c.gridy = 1;
					add(b, c);
					
					fullScreen = true;
				}
				else {
					removeAll();
					GridBagConstraints c = new GridBagConstraints();
					grid.gridResize(900, 650);
					if (grid.getCellLength() > grid.getDim().getHeight() - 1)
						grid.maxZoom();
					
					genLbl.setText("Generations: " + gens);
					totalAlive.setText("Total Alive: " + grid.totalAlive());
					if (grid.getActiveIndex() == -1)
						selectedAlive.setText("Selected Species Alive: N/A");
					else
						selectedAlive.setText("Selected Species Alive: " + grid.countActiveSpecies());
					
					c.weighty = 0.2;
					c.insets = new Insets(0,0,0,0);
					c.anchor = GridBagConstraints.PAGE_START;
					c.gridx = 1;
					c.gridy = 2;
					add(controls, c);

					c.anchor = GridBagConstraints.CENTER;
					c.weighty = 0;
					c.gridx = 1;
					c.gridy = 1;
					add(grid, c);

					c.insets = new Insets(0,0,0,30);
					c.gridx = 0;
					c.gridy = 1;
					add(lPanel, c);

					c.insets = new Insets(0,30,0,0);
					c.gridx = 2;
					c.gridy = 1;
					add(rPanel, c);
					
					c.insets = new Insets(0,0,0,0);
					c.gridx = 1;
					c.gridy = 0;
					add(title, c);
					
					c.weighty = 0.2;
					c.gridx = 2;
					c.gridy = 0;
					c.insets = new Insets(10,10,0,0);
					c.anchor = GridBagConstraints.FIRST_LINE_END;
					maximize.setFG(new Color(100, 100, 100));
					maximize.setText("\u26F6");
					maximize.setOpaque(false);
					add(maximize, c);
					
					fullScreen = false;
				}
				
				revalidate();
			}
		});
		
		
		//SETTING LAYERS
		
		setLayer(maxStats, 2);
		setLayer(grid, 0);
		setLayer(title, 2);
		setLayer(controls, 2);
		setLayer(maximize, 2);
		
		
		c.weighty = 0.2;
		c.insets = new Insets(0,0,0,0);
		c.anchor = GridBagConstraints.PAGE_START;
		c.gridx = 1;
		c.gridy = 2;
		add(controls, c);

		c.anchor = GridBagConstraints.CENTER;
		c.weighty = 0;
		c.gridx = 1;
		c.gridy = 1;
		add(grid, c);

		c.insets = new Insets(0,0,0,30);
		c.gridx = 0;
		c.gridy = 1;
		add(lPanel, c);

		c.insets = new Insets(0,30,0,0);
		c.gridx = 2;
		c.gridy = 1;
		add(rPanel, c);
		
		c.insets = new Insets(0,0,0,0);
		c.gridx = 1;
		c.gridy = 0;
		add(title, c);
		
		c.weighty = 0.2;
		c.gridx = 2;
		c.gridy = 0;
		c.insets = new Insets(10,10,0,0);
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		add(maximize, c);
		
		
		setFocusable(true);
		infoUpdate iU = new infoUpdate();
		iU.start();

	}

	public boolean onclick(int x, int y, int w, int h, int x1, int y1) {
		return x1 >= x && x1 <= x + w && y1 >= y && y1 <= y + h;
	}

	

	public class tT extends TimerTask {
		public void run() {
			grid.play();
			gens++;
			if (fullScreen)
				maxGenLbl.setText("Gen " + gens);
			else
				genLbl.setText("Generations: " + gens);

		}
	}
	
	private void restartTimerTask() {
		run.cancel();
		run = new tT();
		tim.scheduleAtFixedRate(run, 0, (long)(speedMultiplier / speed));
	}
	

	public class infoUpdate extends Thread {
		private double curZoom;
		private boolean autoZoomClone;
		private int currentAlive, selectedAliveCount;

		public infoUpdate() {
			curZoom = grid.zoomPercent();
			autoZoomClone = grid.isAutoZoom();
			currentAlive = grid.totalAlive();
			selectedAliveCount = grid.countActiveSpecies();
		}

		public void run() {
			while (true) {
				if (currentAlive != grid.totalAlive()) {
					currentAlive = grid.totalAlive();
					if (fullScreen)
						maxTotal.setText("Total: " + currentAlive);
					else
						totalAlive.setText("Total Alive: " + currentAlive);
				}
				if (grid.getActiveIndex() == -1) {
					if (fullScreen)
						maxSelected.setText("Selected: N/A");
					else
						selectedAlive.setText("Selected Species Alive: N/A");
				}
				else if (selectedAliveCount != grid.countActiveSpecies()) {
					selectedAliveCount = grid.countActiveSpecies();
					if (fullScreen)
						maxSelected.setText("Selected: " + selectedAliveCount);
					else
						selectedAlive.setText("Selected Species Alive: " + selectedAliveCount);
				}
				if (Math.abs(grid.zoomPercent() - curZoom) > 0.001) {
					curZoom = grid.zoomPercent();
					zoomInField.setText("" + Math.round(curZoom*10)/10.0);
					
				}
				if (grid.isAutoZoom() != autoZoomClone) {
					autoZoomClone = grid.isAutoZoom();
					if (autoZoomClone)
						autoZoomCheckBox.setBackground(Color.red);
					else
						autoZoomCheckBox.setBackground(Color.white);
				}
				
				repaint();
			}
		}
	}

	public class sidePanel extends JPanel {

		public sidePanel(int w, int h) {
			setLayout(new GridBagLayout());
			setPreferredSize(new Dimension(w, h));
			setBackground(new Color(100, 100, 100, 100));
			setFocusable(true);
		}

		public void addComponent(Component c, int x, int y, int spanX, int spanY, double weightX, double weightY) {
			addComponent(c, x, y, spanX, spanY, weightX, weightY, GridBagConstraints.CENTER);
		}

		public void addComponent(Component c, int x, int y, int spanX, int spanY, double weightX, double weightY,
				int anchor) {
			addComponent(c, x, y, spanX, spanY, weightX, weightY, anchor, new Insets(0, 0, 0, 0));
		}

		public void addComponent(Component c, int x, int y, int spanX, int spanY, double weightX, double weightY,
				int anchor, Insets insets) {
			GridBagConstraints a = new GridBagConstraints();
			a.gridheight = spanY;
			a.gridwidth = spanX;
			a.gridx = x;
			a.gridy = y;
			a.weightx = weightX;
			a.weighty = weightY;
			a.anchor = anchor;
			a.insets = insets;
			add(c, a);
		}

		

	}

	public class speciesContainer extends JPanel {
		private Dimension dim;
		private DevButton addSpecies;

		public speciesContainer(int w, int h) {
			dim = new Dimension(w, h);
			setPreferredSize(dim);
			setLayout(new GridBagLayout());
			setBackground(Color.black);

			specContMouse mouse = new specContMouse();
			addMouseListener(mouse);
			setFocusable(true);
		}

		public class specContMouse extends MouseAdapter {
			public void mouseClicked(MouseEvent e) {
				if (getComponentAt(e.getPoint()) instanceof Species) {
					if (SwingUtilities.isLeftMouseButton(e)) {
						grid.setActiveIndex(grid.getSpecies().indexOf(((Species) getComponentAt(e.getPoint()))));
					}
					else if (SwingUtilities.isRightMouseButton(e)) {
						grid.speciesEditor(grid.getSpecies().indexOf(((Species) getComponentAt(e.getPoint()))), false);
					}
				}
			}
		}
	}

	public class toolsContainer extends JPanel {
		private Dimension dim;
		private DevButton pen, eraser, clearAll, templates;
		private final static LineBorder activeBorder = new LineBorder(Color.blue, 2);

		public toolsContainer(int w, int h) {
			dim = new Dimension(w, h);
			setPreferredSize(dim);
			setLayout(new GridBagLayout());
			setBackground(Color.black);
			pen = new DevButton(30, 30, new Color(100, 100, 100), new Color(50, 50, 50), Color.white, new Color(50, 50, 50), "\u270E", new JPanel().getFont().deriveFont((float)(DevButton.defaultFont.getSize()*1.5)), BorderFactory.createLineBorder(new Color(90, 90, 90)), 0);
			pen.sethoverFG(Color.white);
			pen.setFG(Color.white);
			pen.addMouseListener(new MouseListen() {
				public void mouseClicked(MouseEvent e) {
					deactivateAllTools();
					grid.setEditMode((byte)0);	
					pen.setFG(Color.white);
				}
			});
			
			eraser = new DevButton(30, 30, new Color(100, 100, 100), new Color(50, 50, 50), Color.white, new Color(50, 50, 50), "\u232B", new JPanel().getFont().deriveFont((float)(DevButton.defaultFont.getSize()*1.2)), BorderFactory.createLineBorder(new Color(90, 90, 90)), 0);
			eraser.sethoverFG(Color.white);
			eraser.addMouseListener(new MouseListen() {
				public void mouseClicked(MouseEvent e) {
					deactivateAllTools();
					grid.setEditMode((byte)1);	
					eraser.setFG(Color.white);
				}
			});
			
			templates = new DevButton(30, 30, new Color(100, 100, 100), new Color(50, 50, 50), Color.white, new Color(50, 50, 50), "T", new JPanel().getFont().deriveFont((float)(DevButton.defaultFont.getSize()*1.5)), BorderFactory.createLineBorder(new Color(90, 90, 90)), 0);
			templates.sethoverFG(Color.white);
			templates.addMouseListener(new MouseListen() {
				public void mouseClicked(MouseEvent e) {
					deactivateAllTools();
					grid.openTemplates();
					grid.setEditMode((byte)2);
					templates.setFG(Color.white);
				}
			});
			

			clearAll = new DevButton("Clear All Cells", Color.black, Color.white, Color.white, Color.red, 8, DevButton.defaultFont, DevButton.defaultTransitionDuration);
			clearAll.addMouseListener(new MouseListen() {
				public void mouseClicked(MouseEvent e) {
					grid.clearGrid();
				}
			});
			
			/*try {
			eraser = new DevButton(ImageIO.read(new File("nonHoverEraser.png")), BorderFactory.createEmptyBorder(), new Color(255, 255, 0, 100));
			eraser.addMouseListener(new MouseListen() {
				public void mouseClicked(MouseEvent e) {
					deactivateAllTools();
					grid.setPen(false);	
					eraser.setBorders(activeBorder, activeBorder, activeBorder);
				}
			});
			} catch (IOException e) {}*/
			
			
			GridBagConstraints c = new GridBagConstraints();

			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			add(pen, c);

			c.gridx = 1;
			add(eraser, c);
			
			c.gridx = 2;
			add(templates, c);

			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 3;
			add(clearAll, c);

			setFocusable(true);
		}

		public void deactivateAllTools() {
			pen.setFG(new Color(100, 100, 100));
			//eraser.setBorders(BorderFactory.createEmptyBorder(), BorderFactory.createEmptyBorder(), BorderFactory.createEmptyBorder());
			eraser.setFG(new Color(100, 100, 100));
			templates.setFG(new Color(100, 100, 100));
		}

	}
	
	

}