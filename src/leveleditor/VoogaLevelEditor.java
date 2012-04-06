/**
 * @author Michael Zhou (Dominator008)
 */
package leveleditor;

import game.SpriteWrapper;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.*;
import com.golden.gamedev.object.Sprite;

@SuppressWarnings("serial")
public class VoogaLevelEditor extends JFrame {
    private JLabel myBackGroundLabel;
    private String myBackGroundImgSrc;
    private Map<JLabel, String> mySpriteLabelSrcMap;
    private Map<JLabel, String> myUniqueSpriteLabelSrcMap;
    protected VoogaLevelEditorModel myModel;
    protected VoogaLevelEditorController myController;
    private GridBagConstraints myConstraints;
    public JPanel myCanvaspane, mySpritepane;
    private JMenuBar myMenuBar = new JMenuBar();
    
    public VoogaLevelEditor(VoogaLevelEditorModel model, 
	    VoogaLevelEditorController controller) {
	myModel = model;
	myController = controller;
	setTitle("Vooga Level Editor (Demo Version)");
	setGlassPane(new JPanel());
	this.getGlassPane().setSize(1024, 768);
	//((JComponent)this.getGlassPane()).setOpaque(false);
	((JComponent)this.getGlassPane()).setLayout(null);
	/*DragSource.getDefaultDragSource().addDragSourceMotionListener(
		new DragSourceMotionListener() {
		    @Override
		    public void dragMouseMoved(DragSourceDragEvent e) {
			Component jl = ((DragSourceContext) e.getSource())
				.getComponent();
			//jl.setLocation(getGlassPane().getMousePosition());
		    }
		});*/
	this.setLayout(new GridBagLayout());
	this.setUpMenu();
	this.setJMenuBar(myMenuBar);
	this.setSize(1024, 768);
	this.setLocation(0, 0);
	this.setLayout(new GridBagLayout());
	this.setUpCanvas();
	this.setUpSpritePane();
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.setVisible(true);
	this.setLocation(0, 0);
	this.getContentPane().setBackground(Color.GRAY);
    }
    private String currentSrc = null;
    
    private class CopySpriteListener implements MouseInputListener {

	public void mouseMoved(MouseEvent e) {}

	public void mouseDragged(MouseEvent e) {
		Component jl = ((DragSourceContext) e.getSource())
			.getComponent();
		jl.setLocation(jl.getParent().getMousePosition());
	}

	public void mouseReleased(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
	    //System.out.println("pressed called");
	    JLabel label = (JLabel) e.getComponent();
	    TransferHandler handler = new ImageSelection();
	    label.setTransferHandler(handler);
	    currentSrc = myUniqueSpriteLabelSrcMap.get(label);
	    //System.out.println("preparing to copy: " + currentSrc);
	    handler.exportAsDrag(label, e, DnDConstants.ACTION_COPY);
	}

    }
    
    private class MoveSpriteListener implements MouseInputListener, MouseMotionListener {
        
	public void mouseMoved(MouseEvent e) {}

	public void mouseDragged(MouseEvent e) {
	    if (!SwingUtilities.isLeftMouseButton(e)) return;
		Component jl = e.getComponent();
		Point p = jl.getParent().getMousePosition();
		if (p == null || p.x < 0 || p.y < 0) return;
		jl.setLocation(jl.getParent().getMousePosition());
	}
	
	public void mouseReleased(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(final MouseEvent e) {
	    if (SwingUtilities.isRightMouseButton(e)) {
		JPopupMenu deleteMenu = new JPopupMenu();
		JMenuItem myDeleteItem = new JMenuItem("Delete");
		deleteMenu.add(myDeleteItem);
		deleteMenu.show(e.getComponent(), e.getX(), e.getY());
		myDeleteItem.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent event) {
			deleteSpriteLabelFromCanvas((JLabel) e.getComponent());
		    }
		});
	    }
	}

    }

    private class ImageDropTargetListener extends DropTargetAdapter {
	@Override
	public void dragEnter(DropTargetDragEvent event) {}

	public void drop(DropTargetDropEvent event) {
	    //System.out.println("drop called");
	    getGlassPane().setVisible(false);
	    event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
	    Transferable transferable = event.getTransferable();
	    //DataFlavor[] flavors = transferable.getTransferDataFlavors();
	    if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
		//System.out.println("is flavor");
		TransferHandler handler = new ImageSelection();
		handler.importData(myCanvaspane, transferable);
	    }
	    //System.out.println(mySpriteLabelSrcMap.keySet().size());
	    event.dropComplete(true);
	}
    }

    public class ImageSelection extends TransferHandler implements Transferable {
	private final DataFlavor flavors[] = { DataFlavor.imageFlavor };
	//private JLabel source;
	private Image image;

	public int getSourceActions(JComponent c) {
	    return TransferHandler.COPY;
	}

	public boolean canImport(JComponent comp, DataFlavor flavor[]) {
	    //System.out.println("canImport called");
	   //System.out.println(myCanvaspane.getLocation());
	    if (!(comp instanceof JLabel)) {
		return false;
	    }
	    for (int i = 0, n = flavor.length; i < n; i++) {
		for (int j = 0, m = flavors.length; j < m; j++) {
		    if (flavor[i].equals(flavors[j])) {
			return true;
		    }
		}
	    }
	    return false;
	}

	public Transferable createTransferable(JComponent comp) {
	    //source = null;
	    image = null;
	    if (comp instanceof JLabel) {
		JLabel label = (JLabel) comp;
		Icon icon = label.getIcon();
		if (icon instanceof ImageIcon) {
		    image = ((ImageIcon) icon).getImage();
		    //source = label;
		    return this;
		}
	    }
	    return null;
	}
	
	public boolean importData(JComponent comp, Transferable t) {
	    //System.out.println("import called");
	   //System.out.println(comp);
	    if (comp instanceof JPanel) {
		//System.out.println("check passed");
		JLabel label = new JLabel();
		//System.out.println(t);
		//System.out.println("t printed");
		if (t.isDataFlavorSupported(flavors[0])) {
		    try {
			image = (Image) t.getTransferData(flavors[0]);
			ImageIcon icon = new ImageIcon(image);
			label.setIcon(icon);
			label.setTransferHandler(new ImageSelection());
			label.addMouseListener(new MoveSpriteListener());
			label.addMouseMotionListener(new MoveSpriteListener());
			myCanvaspane.add(label, 0);
			label.setBounds(myCanvaspane.getMousePosition().x,
				myCanvaspane.getMousePosition().y,
				image.getWidth(null), image.getHeight(null));
			//System.out.println("importing!!! " + currentSrc);
			mySpriteLabelSrcMap.put(label, currentSrc);
			return true;
		    } catch (UnsupportedFlavorException ignored) {
			ignored.printStackTrace();
		    } catch (IOException ignored) {
			ignored.printStackTrace();
		    }
		}
	    }
	    return false;
	}

	public Object getTransferData(DataFlavor flavor) {
	    if (isDataFlavorSupported(flavor)) {
		return image;
	    }
	    return null;
	}

	public DataFlavor[] getTransferDataFlavors() {
	    return flavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
	    return flavor.equals(DataFlavor.imageFlavor);
	}
    }
    
    private void setUpMenu() {
	JMenu[] myMenu = { new JMenu("File"), new JMenu("Edit")};
	for (JMenu temp : myMenu) myMenuBar.add(temp);
	JMenuItem myLoad = new JMenuItem("Load");
	myLoad.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent event) {
		File f = loadFile("Load Level File...");
		if (f == null) return;
		myController.initialize(f);
	    }
	});
	myMenu[0].add(myLoad);
	JMenuItem myExport = new JMenuItem("Export");
	myExport.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent event) {
		File f = loadFile("Save As...");
		if (f == null) return;
		myController.export(f);
	    }
	});
	myMenu[0].add(myExport);
	JMenuItem myExit = new JMenuItem("Exit");
	myExit.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent event) {
		System.exit(0);
	    }
	});
	myMenu[0].add(myExit);
	JMenuItem myChangeBackground = new JMenuItem("Change Background");
	myChangeBackground.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent event) {
		File f = loadFile("Select Background Image...");
		if (f == null) return;
		try {
		    setUpBackground(f.getCanonicalPath());
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	});
	myMenu[1].add(myChangeBackground);
    }
    
    private File loadFile(String title) {
        JFileChooser fc = new JFileChooser(".");
        fc.setDialogTitle(title);
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION)
            return fc.getSelectedFile();
        else
            return null;
    }
    
    private void setUpCanvas() {
	JScrollPane canvas = new JScrollPane();
	canvas.setPreferredSize(new Dimension(800, 600));
	int gridx = 0;
	int gridy = 0;
	int gridwidth = 1;
	int gridheight = 1;
	int weightx = 10;
	int weighty = 1;
	int anchor = GridBagConstraints.WEST;
	int fill = GridBagConstraints.HORIZONTAL;
	Insets inset = new Insets(0, 0, 0, 0);
	int ipadx = 0;
	int ipady = 0;
	myConstraints = new GridBagConstraints(gridx, gridy,
		gridwidth, gridheight, weightx, weighty, anchor, fill, inset,
		ipadx, ipady);
	this.add(canvas, myConstraints, 0);
	myCanvaspane = new JPanel();
	myCanvaspane.setLayout(null);
	myCanvaspane.setPreferredSize(canvas.getPreferredSize());
	myCanvaspane.setBackground(Color.BLACK);
   	Border border = BorderFactory.createTitledBorder
   		(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Canvas");
   	canvas.setBorder(border);
	canvas.getViewport().add(myCanvaspane);
    }

    private void setUpSpritePane() {
	JScrollPane wrapper = new JScrollPane();
	wrapper.setLocation(0, 0);
	wrapper.setPreferredSize(new Dimension(200, 600));
	myConstraints.gridx = 1;
	myConstraints.gridy = 0;
	this.add(wrapper, myConstraints);
	mySpritepane = new JPanel();
	mySpritepane.setLayout(new GridLayout());
	mySpritepane.setPreferredSize(wrapper.getPreferredSize());
	mySpritepane.setBackground(Color.BLACK);
   	Border border = BorderFactory.createTitledBorder
   		(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Sprite Pane");
   	wrapper.setBorder(border);
	wrapper.getViewport().add(mySpritepane);
    }
    
    protected void setUpBackground(String imagesrc) {
	if (myBackGroundLabel != null) {
	    myBackGroundLabel.setVisible(false);
	    myCanvaspane.remove(myBackGroundLabel);
	    myCanvaspane.revalidate();
	}
	else myBackGroundLabel = new JLabel();
	ImageIcon icon = new ImageIcon(imagesrc);
	myBackGroundLabel = new JLabel(icon);
	myCanvaspane.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
	new DropTarget(myCanvaspane, DnDConstants.ACTION_COPY_OR_MOVE,
		new ImageDropTargetListener());
	myCanvaspane.setLocation(0, 0);
	myCanvaspane.add(myBackGroundLabel);
	myBackGroundLabel.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
	myBackGroundImgSrc = imagesrc;
    }
    
    protected void loadSprites(Map<Point, SpriteWrapper> spritemap) {
	if (mySpriteLabelSrcMap != null) {
	    for (JLabel l: mySpriteLabelSrcMap.keySet()) {
		l.setVisible(false);
		//System.out.println("clearing out l!!!!!!!!");
		myCanvaspane.remove(l);
	    }
	    mySpriteLabelSrcMap.clear();
	    mySpritepane.removeAll();
	    mySpritepane.revalidate();
	    myCanvaspane.revalidate();
	}
	else mySpriteLabelSrcMap = new HashMap<JLabel, String>();
	if (myUniqueSpriteLabelSrcMap != null) myUniqueSpriteLabelSrcMap.clear();
	else myUniqueSpriteLabelSrcMap = new HashMap<JLabel, String>();
	Set<String> myUniqueSrcSet = new TreeSet<String>();
	for (Point p: spritemap.keySet()) {
	    Sprite sp = spritemap.get(p).getSprite();
	    BufferedImage currentImage = sp.getImage();
	    JLabel label = new JLabel();
	    ImageIcon icon = new ImageIcon(currentImage);
	    label.setIcon(icon);
	    label.setTransferHandler(new ImageSelection());
	    label.addMouseListener(new MoveSpriteListener());
	    label.addMouseMotionListener(new MoveSpriteListener());
	    myCanvaspane.add(label, 0);
	    label.setBounds(p.x, p.y,
		   currentImage.getWidth(), currentImage.getHeight());
	    mySpriteLabelSrcMap.put(label, spritemap.get(p).getImageSrc());
	    myUniqueSrcSet.add(spritemap.get(p).getImageSrc());
	}
	mySpritepane.setLayout(new GridLayout(myUniqueSrcSet.size(), 1));
	for (String src: myUniqueSrcSet) {
	    CopySpriteListener listener = new CopySpriteListener();
	    JLabel label = new JLabel(new ImageIcon(src));
	    mySpritepane.add(label);
	    label.addMouseListener(listener);
	    myUniqueSpriteLabelSrcMap.put(label, src);
	}
	mySpritepane.revalidate();
    }
    
    protected void prepareForSave(String path) {
	if (myBackGroundImgSrc == null) return;
	HashMap<Point, SpriteWrapper> spritemap = new HashMap<Point, SpriteWrapper>();
	for (JLabel l: mySpriteLabelSrcMap.keySet()) {
	    //System.out.println(mySpriteLabelSrcMap.get(l));
	    BufferedImage bimg =
		    VoogaUtilities.getImageFromString(mySpriteLabelSrcMap.get(l));
	    Sprite created = new Sprite(bimg);
	    spritemap.put(l.getLocation(), 
		    new SpriteWrapper(created, mySpriteLabelSrcMap.get(l)));
	}
	myModel.saveLevel(path, myBackGroundImgSrc, spritemap);
    }
    
    private void deleteSpriteLabelFromCanvas(JLabel l) {
	l.setVisible(false);
	myCanvaspane.remove(l);
	myCanvaspane.revalidate();
	mySpriteLabelSrcMap.remove(l);
    }
    
}