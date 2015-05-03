import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

/**
 * 
 * @author Sani Patel, CSE23229
 * 
 * 
 *         FOR TA:
 * 
 *         In my code, we use PaintPanel class as a replacement for Canvas class
 *         to draw our lines/ shapes. I realize that this class has many
 *         limitations. I have tried to implement it correctly but the following
 *         issues still exist:
 * 
	 *         1) Initially, I wanted to load an image, and draw each pixel onto
	 *         PaintPanel's inkPanel; however, this was impossible with my limited
	 *         knowledge. Hence, whenever I open an image, it loads and is displayed
	 *         on screen. But we cannot edit/ save the image as it's just
	 *         BufferedImage and not belong to a canvas or PaintPanel. 
	 *         - To counter this, I added additional feature, which is "Rotate 180*"
	 *         which rotates any image by 180*. 
	 *         - My initial plan was to allow a feature that would rotate an image 90* 
	 *         after every click. However, I was unable to implement this as the rotation 
	 *         kept cropping up the image. The partial implementation for "Rotate 90*" is
	 *         commented out in the ActionPerformed(evt) method of "Rotate 180*".
	 *         
	 *         2) if I draw a line, and then draw a brush, the new INK_STROKE is set
	 *         to brush, which means resizing the window will set everything drawn
	 *         on the panel to same size. Moreover, if I have drawn a line in color
	 *         blue, then draw in color yellow, resizing the window sets INK_COLOR
	 *         to yellow and "redraws" all the components in paint panel with color
	 *         yellow. In hindsight, I realize Line2D.Double may not be the best
	 *         tool to use for window resizing.
 * 
 *         I tried to compensate for lack in the program's ability edit images
 *         by adding "circle", "rectangle", "clear", "clear image", "stroke++"
 *         and "background color" features that adds a nice touch to the whole
 *         application.
 * 
 *         Stroke++ is a feature that increases the stroke size for brush,
 *         pencil, eraser, and line. This is my custom look-and-feel widget (as
 *         required for the assignment).
 * 
 */

public class a2 extends javax.swing.JFrame implements MouseListener,
		MouseMotionListener {

	private static final long serialVersionUID = 1L;
	private Point[] stroke;
	private int sampleCount = 0;

	   class CustomButtonUI extends ButtonUI
	   {
	      final Insets I = new Insets(8, 15, 8, 15);

	      public void installUI(JComponent c)
	      {
	         AbstractButton b = (AbstractButton)c;
	         BasicButtonListener listener = new BasicButtonListener(b);
	         b.addMouseListener(listener);
				b.setCursor(new Cursor(Cursor.HAND_CURSOR));
	      }

	      public void uninstallUI(JComponent c)
	      {
	         AbstractButton b = (AbstractButton)c;
	         BasicButtonListener listener = new BasicButtonListener(b);
	         b.removeMouseListener(listener);
	      }

	      public Insets getDefaultMargin(AbstractButton ab)
	      {
	         return I;
	      }

	      public Insets getInsets(JComponent c)
	      {
	         return I;
	      }

	      public Dimension getMaximumSize(JComponent c)
	      {
	         return this.getPreferredSize(c);
	      }

	      public Dimension getMinimumSize(JComponent c)
	      {
	         return this.getPreferredSize(c);
	      }

	      public Dimension getPreferredSize(JComponent c)
	      {
	         Graphics g = c.getGraphics();
	         FontMetrics fm = g.getFontMetrics();

	         Dimension d = new Dimension();
	         d.width = fm.stringWidth(((JButton)c).getText()) +  I.left + I.right;
	         d.height = fm.getHeight() + I.top + I.bottom;
	         return d;
	      }

	      public void paint(Graphics g, JComponent c)
	      {
	         Graphics2D g2D = (Graphics2D)g;

	         AbstractButton btn = (AbstractButton)c;
	         ButtonModel bm = btn.getModel();
 
	         FontMetrics fm = g2D.getFontMetrics();

	         Color backgroundColor;
	         Color bevelColor;
	         Color lineColor;

	         if (bm.isArmed())
	         {
	        	 backgroundColor = new Color(127,255,0);
		         bevelColor = new Color(127,255,0);
		         lineColor = bevelColor;
	         }
	         else if (bm.isPressed())
	         {
	            backgroundColor = new Color(0,0,0);
	            bevelColor = new Color(238,130,238);
	            lineColor = new Color(238,130,238);
	          }
	         else
	         {
	        	 backgroundColor = new Color(142, 56, 142);
		         bevelColor = new Color(142, 56, 142);
		         lineColor = bevelColor;
		      }

	         // define some polygons for drawing

	         Dimension d = c.getPreferredSize();
	         int x = d.width - 1;    // x-coordinate of right edge
	         int y = d.height - 1;   // y-coordinate of bottom edge
	         final int bevelWidth = 3;       // bevel width

	         int[] outerX = { 0, 0, x, x };
	         int[] outerY = { 0, y, y, 0 };
	         //int[] outerY = { 0, y, 0, 0 };

	         int[] innerXComp = { bevelWidth, bevelWidth, x - bevelWidth, x - bevelWidth };
	         int[] innerYComp = { bevelWidth, y - bevelWidth, y - bevelWidth, bevelWidth };

	         int[] topBevelX = { 0, bevelWidth, x - bevelWidth, x };
	         int[] topBevelY = { 0, bevelWidth, bevelWidth, 0 };

	         int[] leftBevelX = { 0, 0, bevelWidth, bevelWidth };
	         int[] leftBevelY = { 0, y, y - bevelWidth, bevelWidth };

	         int[] bottomBevelX = { 0, x, x - bevelWidth, bevelWidth };
	         int[] bottomBevelY = { y, y, y - bevelWidth, y - bevelWidth };

	         int[] rightBevelX = { x, x - bevelWidth, x - bevelWidth, x };
	         int[] rightBevelY = { 0, bevelWidth, y - bevelWidth, y };

	         Polygon outer = new Polygon(outerX, outerY, outerX.length);
	         Polygon inner = new Polygon(innerXComp, innerYComp, innerXComp.length);
	         Polygon topBevel = new Polygon(topBevelX, topBevelY, topBevelX.length);
	         Polygon leftBevel = new Polygon(leftBevelX, leftBevelY, leftBevelX.length);
	         Polygon bottomBevel = new Polygon(bottomBevelX, bottomBevelY, bottomBevelX.length);
	         Polygon rightBevel = new Polygon(rightBevelX, rightBevelY, rightBevelX.length);

	         g2D.setColor(backgroundColor);
	         g2D.fillPolygon(outer);

	         g2D.setColor(bevelColor);
	         g2D.fillPolygon(topBevel);
	         g2D.fillPolygon(rightBevel);

	         g2D.setColor(lineColor);
	         g2D.drawPolygon(outer);
	         g2D.drawPolygon(inner);
	         g2D.drawPolygon(topBevel);
	         g2D.drawPolygon(leftBevel);
	         g2D.drawPolygon(bottomBevel);
	         g2D.drawPolygon(rightBevel);
	         g2D.drawPolygon(outer);

	         g2D.setColor(new Color(255, 255, 255));
	         String s = ((JButton)c).getText();
	         x = I.left;
	         y = I.top + fm.getAscent();
	         Font f = new Font("Cambria", Font.PLAIN, 15);
	         g2D.setFont(f);
	         g2D.drawString(s, x, y);
	      }
	   }
	
	/**
	 * Creates new form a2
	 */
	public a2() {
		initComponents();
		groupButton();
	}

	private void initComponents() {

		setTitle("Paint");
		sampleCount = 0;
		stroke = new Point[MAX_SAMPLES];

		jMenuBar1 = new javax.swing.JMenuBar();
		jMenu1 = new javax.swing.JMenu();
		jMenu2 = new javax.swing.JMenu();
		jMenuItem1 = new javax.swing.JMenuItem();
		jPanel1 = new javax.swing.JPanel();
		Eraser1 = new javax.swing.JRadioButton();
		Brush = new javax.swing.JRadioButton();
		Line1 = new javax.swing.JRadioButton();
		Pencil = new javax.swing.JRadioButton();
		Text = new javax.swing.JButton();
		ColorBtn = new javax.swing.JButton();
		jMenuBar2 = new javax.swing.JMenuBar();
		jMenu3 = new javax.swing.JMenu();
		Open = new javax.swing.JMenuItem();
		Save = new javax.swing.JMenuItem();
		Quit = new javax.swing.JMenuItem();
		jMenu4 = new javax.swing.JMenu();
		Color = new javax.swing.JMenuItem();
		Rectangle = new javax.swing.JRadioButton();
		Circle = new javax.swing.JRadioButton();
		clearButton = new javax.swing.JButton();
		clearImage = new javax.swing.JButton();
		rotateImage = new javax.swing.JButton();
		background = new javax.swing.JButton();
		count = 0;

		jMenu1.setText("File");
		jMenuBar1.add(jMenu1);
		jMenu1.setToolTipText("Open An Image");

		jMenu2.setText("Edit");
		jMenuBar1.add(jMenu2);
		jMenu2.setToolTipText("Edit An Image");

		jMenuItem1.setText("jMenuItem1");

		inkPanel = new PaintPanel();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		final JPanel contentPane = new JPanel(new BorderLayout());

		inkPanel.addMouseListener(this);
		inkPanel.addMouseMotionListener(this);
		jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(
				0, 0, 0), 1, true));

		Eraser1.setText("Eraser");
		Eraser1.setToolTipText("Erase");
		Eraser1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				// Eraser1ActionPerformed(evt);
			}
		});

		Rectangle.setText("Rectangle");
		Rectangle.setToolTipText("Draw A Rectangle");

		Circle.setText("Circle");
		Circle.setToolTipText("Draw A Circle");

		final JPanel p1 = new JPanel(new BorderLayout());
		p1.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		p1.setBackground(new Color(142, 56, 142));
		p1.add(inkPanel, "Center");

		Brush.setText("Brush");
		Brush.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				// BrushActionPerformed(evt);
			}
		});
		Brush.setToolTipText("Brush");

		Line1.setText("Line");
		Line1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				// Line1ActionPerformed(evt);
			}
		});
		Line1.setToolTipText("Draw A Line");

		Pencil.setText("Pencil");
		Pencil.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				// PencilActionPerformed(evt);
			}
		});
		Pencil.setToolTipText("Pencil");

		Text.setText("Stroke++");
		Text.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				i++;
				INK_STROKE = new BasicStroke((float) 5.0 + i,
						BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
				;
			}
		});
		Text.setToolTipText("Increase Stroke Size");
		Text.setUI(new CustomButtonUI());

		clearButton = new JButton("Clear");
		clearButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					sampleCount = 0;
					inkPanel.clear();

				} catch (Exception e) {
					e.toString();
				}
			}
		});
		clearButton.setToolTipText("Clear Panel");

		/**
		 * Can be used to clear images as well. Very useful feature.
		 */
		clearImage = new JButton("Clear Image");
		clearImage.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				if (pp != null) {
					inkPanel.clear();
					contentPane.remove(pp);
					p1.setPreferredSize(new Dimension(650, 520));
					p1.setMaximumSize(new Dimension(2000, 1600));
					p1.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
					contentPane.add(p1);
					sampleCount = 0;
					repaint();
					pp = null;
					pack();
				}
			}
		});
		clearImage.setToolTipText("Clear Image");

		background = new JButton("Background Color");
		background.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				c = JColorChooser.showDialog(null, "Choose Background Color",
						new Color(0, 0, 0));
				inkPanel.setBackground(c);
			}
		});
		background.setToolTipText("Change Background Color");
		background.setBackground(new Color(124, 252, 0));

		rotateImage = new JButton(" Rotate 180° ");
		rotateImage.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				if (pp != null) {
					int width = img.getWidth();
					int height = img.getHeight();

					/**
					 * As I described earlier, my initial plan was to allow 90*
					 * rotation to the image, which I was unsuccessful in
					 * implementing due to cropping. Here is the partial code
					 * for it:
					 * 
					 * BufferedImage newImg = new BufferedImage(width, height, img.getType()); 
					 * AffineTransform tx = new AffineTransform(); tx.translate(height/2, width/2);
					 * tx.rotate(Math.PI / 2); 
					 * tx.translate(-width / 2,-height / 2);
					 * 
					 * Graphics2D g2 = newImage.createGraphics();
					 * g2D.drawImage(originalImage, tx, null); img = newImg;
					 * this.repaint(); g2D.dispose();
					 * 
					 * ContentPane.remove(pp); pp = new PaintPanel2(img);...
					 */

					BufferedImage bi = null;

					if (count == 0) {
						try {
							bi = new BufferedImage(img.getWidth(), img
									.getHeight(), img.getType());

							for (int x = 0; x < width; x++) {
								for (int y = 0; y < height; y++) {
									bi.setRGB(width - x - 1, height - y - 1,
											img.getRGB(x, y));
								}
							}
							count++;
							contentPane.remove(pp);
							pp = new PaintPanel2(bi);

						} catch (Exception e) {
							e.toString();
						}
					} else if (count == 1) {
						contentPane.remove(pp);
						pp = new PaintPanel2(img);
						count--;
					}

					pp.setPreferredSize(new Dimension(width, height));
					// contentPane.remove(p1);
					contentPane.add(pp);
					pack();
				}
			}
		});
		rotateImage.setToolTipText("Rotate Image 180*");

		ColorBtn.setText("Color");
		ColorBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				ColorBtnActionPerformed(evt);
			}
		});
		Random rand = new Random();
		ColorBtn.setToolTipText("Color");
		ColorBtn.setBackground(new Color(124, 252, 0));

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(Brush).addComponent(Line1).addComponent(Pencil)
				.addComponent(Eraser1).addComponent(Text).addComponent(Circle)
				.addComponent(Rectangle).addComponent(ColorBtn)
				.addComponent(clearImage).addComponent(clearButton)
				.addComponent(rotateImage).addComponent(background));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(Line1)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(Rectangle)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(Brush)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(Circle)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(Pencil)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(Eraser1)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGap(20, 20, 20)
										.addComponent(ColorBtn)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(clearButton)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(Text)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(clearImage)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(rotateImage)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(background)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)));

		jMenu3.setMnemonic('F');
		jMenu3.setText("File");

		Quit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_Q,
				java.awt.event.InputEvent.CTRL_MASK));
		Quit.setMnemonic('Q');
		Quit.setText("Quit");
		Quit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				System.exit(0);
			}
		});
		Quit.setToolTipText("Quit Application");

		Open.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_O,
				java.awt.event.InputEvent.CTRL_MASK));
		Open.setMnemonic('O');
		Open.setText("Open");
		Open.setToolTipText("Open An Image");
		Open.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(a2.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					FileNameExtensionFilter filter = new FileNameExtensionFilter(
							"png", "jpg", "gif");
					// This is where a real application would open the file.
					fc.setFileFilter(filter);

					try {
						img = ImageIO.read(file);
						pp = new PaintPanel2(img);
						contentPane.remove(p1);
						pp.setPreferredSize(new Dimension(img.getWidth(), img
								.getHeight()));
						contentPane.add(pp);
						pack();

					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, "Invalid File Format");
					} catch (NullPointerException e) {
						JOptionPane pane = new JOptionPane("Invalid File Format");
						final JDialog d = pane.createDialog((JFrame)null, "Invalid File Format");
						d.setLocation(300, 250);   
						d.setVisible(true);
					}

				} else {
					;
				}
			}
		});
		jMenu3.add(Open);

		Save.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_S,
				java.awt.event.InputEvent.CTRL_MASK));
		Save.setMnemonic('S');
		Save.setText("Save");
		Save.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				SaveActionPerformed(evt);
			}
		});
		Save.setToolTipText("Save An Image");

		jMenu3.add(Save);
		jMenu3.add(Quit);

		jMenuBar2.add(jMenu3);

		jMenu4.setText("Edit");
		jMenu4.setMnemonic('E');

		Color.setText("Choose Color");
		Color.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				INK_COLOR = JColorChooser.showDialog(null, "Choose a Color",
						new Color(0, 0, 0));
			}
		});
		jMenu4.add(Color);

		jMenuBar2.add(jMenu4);

		setJMenuBar(jMenuBar2);

		p1.setPreferredSize(new Dimension(650, 520));
		p1.setMaximumSize(new Dimension(2000, 1600));

		contentPane.add(p1, BorderLayout.CENTER);
		contentPane.add(jPanel1, BorderLayout.WEST);

		this.setContentPane(contentPane);
		pack();
	}

	private void SaveActionPerformed(java.awt.event.ActionEvent evt) {
		;
	}

	private void ColorBtnActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			Color c = JColorChooser.showDialog(null, "Choose a Color",
					new Color(0, 0, 0));
			INK_COLOR = c;
		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(null, "Choose Wisely.");
		}
	}

	private void groupButton() {
		ButtonGroup bg1 = new ButtonGroup();
		bg1.add(Brush);
		bg1.add(Pencil);
		bg1.add(Eraser1);
		bg1.add(Line1);
		bg1.add(Circle);
		bg1.add(Rectangle);
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
					.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(a2.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(a2.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(a2.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(a2.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		}

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new a2().setVisible(true);
			}
		});
	}

	private javax.swing.JRadioButton Brush;
	private javax.swing.JMenuItem Color;
	private javax.swing.JButton ColorBtn;
	private javax.swing.JRadioButton Eraser1;
	private javax.swing.JRadioButton Line1;
	private javax.swing.JMenuItem Open;
	private javax.swing.JMenuItem Quit;
	private javax.swing.JRadioButton Pencil;
	private javax.swing.JRadioButton Rectangle;
	private javax.swing.JRadioButton Circle;
	private javax.swing.JMenuItem Save;
	private PaintPanel inkPanel;
	private javax.swing.JButton Text;
	private javax.swing.JMenu jMenu1;
	private javax.swing.JMenu jMenu2;
	private javax.swing.JMenu jMenu3;
	private javax.swing.JMenu jMenu4;
	private javax.swing.JMenuBar jMenuBar1;
	private javax.swing.JMenuBar jMenuBar2;
	private javax.swing.JMenuItem jMenuItem1;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JButton clearButton;
	private javax.swing.JButton clearImage;
	private javax.swing.JButton rotateImage;
	private javax.swing.JButton background;
	final int MAX_SAMPLES = 500000;
	PaintPanel2 pp = null;
	private Color INK_COLOR = null;
	private Stroke INK_STROKE = null;
	private float i = 0.0f;
	BufferedImage img = null;
	Color c;
	int count;

	// End of variables declaration
	@Override
	public void mouseDragged(MouseEvent me) {
		if (Pencil.isSelected()) {
			int x = me.getX();
			int y = me.getY();

			stroke[sampleCount] = new Point(x, y);

			if (SwingUtilities.isLeftMouseButton(me)) {
				stroke[sampleCount] = new Point(x, y);
				int x1 = (int) stroke[sampleCount - 1].getX();
				int y1 = (int) stroke[sampleCount - 1].getY();
				int x2 = (int) stroke[sampleCount].getX();
				int y2 = (int) stroke[sampleCount].getY();
				sampleCount++;

				inkPanel.drawInk(x1, y1, x2, y2);
			}

		} else if (Brush.isSelected()) { 
			int x = me.getX();
			int y = me.getY();

			stroke[sampleCount] = new Point(x, y);

			if (SwingUtilities.isLeftMouseButton(me)) {
				stroke[sampleCount] = new Point(x, y);
				int x1 = (int) stroke[sampleCount - 1].getX();
				int y1 = (int) stroke[sampleCount - 1].getY();
				int x2 = (int) stroke[sampleCount].getX();
				int y2 = (int) stroke[sampleCount].getY();
				sampleCount++;

				// draw ink trail from previous point to current point
				inkPanel.drawInk2(x1, y1, x2, y2);
			}
		} else if (Line1.isSelected()) {

		} else if (Eraser1.isSelected()) {
			int x = me.getX();
			int y = me.getY();

			stroke[sampleCount] = new Point(x, y);

			if (SwingUtilities.isLeftMouseButton(me)) {
				stroke[sampleCount] = new Point(x, y);
				int x1 = (int) stroke[sampleCount - 1].getX();
				int y1 = (int) stroke[sampleCount - 1].getY();
				int x2 = (int) stroke[sampleCount].getX();
				int y2 = (int) stroke[sampleCount].getY();
				sampleCount++;

				// draw ink trail from previous point to current point
				inkPanel.drawInk3(x1, y1, x2, y2);
			}
		} else if (Text.isSelected()) {

		} else {
			;
		}
	}

	@Override
	public void mouseMoved(MouseEvent me) {
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		int x = me.getX();
		int y = me.getY();

		if (Circle.isSelected())
			inkPanel.drawCircle(x, y);
		if (Rectangle.isSelected())
			inkPanel.drawRectangle(x, y);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent me) {
		if (Pencil.isSelected()) {
			int x = me.getX();
			int y = me.getY();

			stroke[sampleCount] = new Point(x, y);
			sampleCount++;

		} else if (Brush.isSelected()) {
			int x = me.getX();
			int y = me.getY();

			stroke[sampleCount] = new Point(x, y);
			sampleCount++;

		} else if (Line1.isSelected()) {
			int x = me.getX();
			int y = me.getY();

			stroke[sampleCount] = new Point(x, y);
			sampleCount++;

			inkPanel.drawInk(x, y);

		} else if (Eraser1.isSelected()) {
			int x = me.getX();
			int y = me.getY();

			stroke[sampleCount] = new Point(x, y);
			sampleCount++;
		} else if (Text.isSelected()) {

		} else {
			;
		}
	}

	@Override
	public void mouseReleased(MouseEvent me) {

		if (Line1.isSelected()) {

			int x1 = (int) stroke[sampleCount - 1].getX();
			int y1 = (int) stroke[sampleCount - 1].getY();
			int x2 = (int) me.getX();
			int y2 = (int) me.getY();
			// System.out.printf("%d,  %d, %d, %d", x1, y1, x2, y2);

			// draw ink trail from previous point to current point
			inkPanel.drawInk(x1, y1, x2, y2);
		}

		if (SwingUtilities.isLeftMouseButton(me)) {
			sampleCount = 0;
		}
	}

	class PaintPanel extends JPanel {
		// the following avoids a "warning" with Java 1.5.0 complier (?)
		static final long serialVersionUID = 42L;

		private Vector<Line2D.Double> v;

		PaintPanel() {
			INK_COLOR = new Color(0, 0, 0);
			INK_STROKE = new BasicStroke(5.0f, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND);
			c = new Color(255, 255, 255);
			v = new Vector<Line2D.Double>();
			this.setBackground(c);
		}

		PaintPanel(Color c) {
			INK_COLOR = new Color(0, 0, 0);
			INK_STROKE = new BasicStroke(5.0f, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND);
			v = new Vector<Line2D.Double>();
			this.setBackground(c);
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			paintInkStrokes(g);
		}

		/**
		 * Paint all the line segments stored in the vector
		 */
		private void paintInkStrokes(Graphics g) {
			Graphics2D graph2D = (Graphics2D) g;

			graph2D.setColor(INK_COLOR);

			Stroke s = graph2D.getStroke(); // save current stroke

			if (Brush.isSelected()) {
				Stroke INK_STROKE3 = new BasicStroke((float) 15.0 + i,
						BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
				graph2D.setStroke(INK_STROKE3);
			} else {
				graph2D.setStroke(INK_STROKE);
			}

			for (int i = 0; i < v.size(); ++i)
				graph2D.draw((Line2D.Double) v.elementAt(i));

			graph2D.setStroke(s); // restore stroke
		}

		/**
		 * Draw one line segment, then add it to the vector.
		 * <p>
		 */
		public void drawInk(int x1, int y1, int x2, int y2) {
			// get graphics context
			Graphics2D graph2D = (Graphics2D) this.getGraphics();

			Line2D.Double inkSegment = new Line2D.Double(x1, y1, x2, y2);

			graph2D.setColor(INK_COLOR);
			Stroke s = graph2D.getStroke();
			Stroke INK_STROKE2 = new BasicStroke(5f, BasicStroke.CAP_BUTT,
					BasicStroke.CAP_BUTT);
			graph2D.setStroke(INK_STROKE);
			graph2D.draw(inkSegment); // draw it!
			graph2D.setStroke(s);
			v.add(inkSegment); // add to vector
		}

		public void drawInk(int x1, int y1) {
			Graphics2D graph2D = (Graphics2D) this.getGraphics();

			Line2D.Double inkSegment = new Line2D.Double(x1, y1, x1, y1);

			graph2D.setColor(INK_COLOR);
			Stroke s = graph2D.getStroke();
			Stroke INK_STROKE2 = new BasicStroke(5f, BasicStroke.CAP_SQUARE,
					BasicStroke.CAP_SQUARE);
			graph2D.setStroke(INK_STROKE2);
			graph2D.draw(inkSegment); // draw it!
			graph2D.setStroke(s);
			v.add(inkSegment); // add to vector
		}

		public void drawInk2(int x1, int y1, int x2, int y2) {
			Graphics2D graph2D = (Graphics2D) this.getGraphics();

			// create the line
			Line2D.Double inkSegment = new Line2D.Double(x1, y1, x2, y2);

			graph2D.setColor(INK_COLOR);
			Stroke s = graph2D.getStroke();
			Stroke INK_STROKE2 = new BasicStroke(15f, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND);
			graph2D.setStroke(INK_STROKE2);
			graph2D.draw(inkSegment);
			graph2D.setStroke(s);
			v.add(inkSegment);
		}

		public void drawCircle(int x, int y) {
			Graphics2D graph2D = (Graphics2D) this.getGraphics();
			graph2D.setColor(INK_COLOR);
			Ellipse2D.Double e = new Ellipse2D.Double(x, y, 25, 25);
			graph2D.draw(e);
		}

		public void drawRectangle(int x, int y) {
			Graphics2D graph2D = (Graphics2D) this.getGraphics();
			graph2D.setColor(INK_COLOR);
			Rectangle2D.Double e = new Rectangle2D.Double(x, y, 40, 25);
			graph2D.draw(e);
		}

		public void drawInk3(int x1, int y1, int x2, int y2) {
			// get graphics context
			Graphics2D graph2D = (Graphics2D) this.getGraphics();

			Line2D.Double inkSegment = new Line2D.Double(x1, y1, x2, y2);

			graph2D.setColor(inkPanel.getBackground());
			Stroke s = graph2D.getStroke(); // save current stroke
			Stroke INK_STROKE2 = new BasicStroke(15f, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND);
			graph2D.setStroke(INK_STROKE2); // set desired stroke
			graph2D.draw(inkSegment);
			graph2D.setStroke(s);
			v.add(inkSegment);
		}

		public void clear() {
			v.clear();
			this.setBackground(new Color(255, 255, 255));
			this.repaint();
		}
	}

	class PaintPanel2 extends JPanel {
		// the following avoids a "warning" with Java 1.5.0 complier (?)
		static final long serialVersionUID = 42L;

		BufferedImage image;

		public PaintPanel2(BufferedImage imageArg) {
			image = imageArg;
			Color c = new Color(0, 0, 0);
			setBackground(c);
		}

		public void paintComponent(Graphics g) {
			try {
				super.paintComponent(g);
				BufferedImage newImg = (BufferedImage) createImage(
						image.getWidth(), image.getHeight());
				Graphics2D graph2D = (Graphics2D) newImg.createGraphics();
				graph2D.drawImage(newImg, 0, 0, this);
				repaint();
				graph2D.dispose();

			} catch (NullPointerException e) {
				// System.out.println("Null Pointer Exception. File must be an Image.");
			}

			if (image != null) {

				int width = (int) ((image.getWidth() * getHeight() / image
						.getHeight()));
				if (width < getWidth()) {
					int leftMove = getWidth() / 2 - width / 2;
					int rightMove = getWidth() / 2 + width / 2;
					g.drawImage(image, leftMove, 0, rightMove, getHeight(), 0,
							0, image.getWidth(), image.getHeight(), null);

				} else {
					int height = (image.getHeight() * getWidth())
							/ image.getWidth();
					int topMove = getHeight() / 2 - height / 2;
					int bottomMove = getHeight() / 2 + height / 2;
					g.drawImage(image, 0, topMove, getWidth(), bottomMove, 0,
							0, image.getWidth(), image.getHeight(), null);
				}
			}
		}
	}
}