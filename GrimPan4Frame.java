package hufs.cse.grimpan4;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Area;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GrimPan4Frame extends JFrame {

	private final String defaultDir = "/home/cskim/temp/";
	private final FileNameExtensionFilter grimFileModelFilter = 
			new FileNameExtensionFilter("Grim Files", "grm");

	private JFileChooser jFileChooser1 = null;
	private JFileChooser jFileChooser2 = null;

	private GrimPanModel model = null;

	private JPanel contentPane;
	private JMenuBar menuBar;
	private JMenu menuFile;
	private JMenuItem jmiNew;
	private JMenuItem jmiOpen;
	private JMenuItem jmiExit;

	GrimPan4Frame thisClass =  this;
	private DrawPanel drawPanel;
	private JMenu menuShape;
	private JMenu menuSetting;
	JRadioButtonMenuItem rdbtnmntmLine;
	JRadioButtonMenuItem rdbtnmntmPen;	
	JRadioButtonMenuItem rdbtnmntmPoly;
	JRadioButtonMenuItem rdbtnmntmStar;
	JRadioButtonMenuItem rdbtnmntmRPoly;

	private JMenuItem jmiStrokeWidth;
	private JMenuItem jmiStrokeColor;
	private JMenuItem jmiFillColor;
	private JCheckBoxMenuItem jcmiFill;

	private ButtonGroup btnGroup = new ButtonGroup();
	private JRadioButtonMenuItem rdbtnmntmReg;
	private JRadioButtonMenuItem rdbtnmntmEllipse;
	private JMenuItem jmiSave;
	private JMenuItem jmiSaveAs;
	private JMenu menuEdit;
	private JMenuItem jmiMove;
	private JMenuItem jmiDelete;
	private JMenuItem jmiSaveAsSVG;
	private JMenu menu;
	private JMenuItem menuItem;
	
	private JRadioButtonMenuItem rdbtnmntmText;

	/**
	 * Create the frame.
	 */
	public GrimPan4Frame() {
		model = new GrimPanModel();
		initialize();
	}

	void initialize(){

		setTitle("그림판");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 50, 800, 600);

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		menuFile = new JMenu("File  ");
		menuBar.add(menuFile);

		jmiNew = new JMenuItem("New  ");
		jmiNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearDrawPanel();
			}
		});
		menuFile.add(jmiNew);

		jmiOpen = new JMenuItem("Open ");
		jmiOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openAction();
			}
		});
		menuFile.add(jmiOpen);

		jmiSave = new JMenuItem("Save ");
		jmiSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAction();
			}
		});
		menuFile.add(jmiSave);

		jmiSaveAs = new JMenuItem("Save As ...");
		jmiSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAsAction();
			}
		});
		menuFile.add(jmiSaveAs);

		jmiSaveAsSVG = new JMenuItem("Save As SVG");
		jmiSaveAsSVG.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAsSVGAction();
			}
		});
		menuFile.add(jmiSaveAsSVG);

		menuFile.addSeparator();

		jmiExit = new JMenuItem("Exit  ");
		jmiExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menuFile.add(jmiExit);

		menuShape = new JMenu("Shape  ");
		menuBar.add(menuShape);

		rdbtnmntmLine = new JRadioButtonMenuItem("선분");
		rdbtnmntmLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setEditState(GrimPanModel.SHAPE_LINE);
			}
		});
		rdbtnmntmLine.setSelected(true);
		menuShape.add(rdbtnmntmLine);

		rdbtnmntmPen = new JRadioButtonMenuItem("연필");
		rdbtnmntmPen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setEditState(GrimPanModel.SHAPE_PENCIL);
			}
		});
		menuShape.add(rdbtnmntmPen);

		rdbtnmntmPoly = new JRadioButtonMenuItem("다각형");
		rdbtnmntmPoly.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setEditState(GrimPanModel.SHAPE_POLYGON);
			}
		});
		menuShape.add(rdbtnmntmPoly);

		rdbtnmntmReg = new JRadioButtonMenuItem("정다각형");
		rdbtnmntmReg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setEditState(GrimPanModel.SHAPE_REGULAR);
				Object[] possibleValues = { 
						"3", "4", "5", "6", "7",
						"8", "9", "10", "11", "12"
				};
				Object selectedValue = JOptionPane.showInputDialog(thisClass,
						"Choose one", "Input",
						JOptionPane.INFORMATION_MESSAGE, null,
						possibleValues, possibleValues[0]);
				model.setNPolygon(Integer.parseInt((String)selectedValue));
				drawPanel.repaint();
			}
		});
		menuShape.add(rdbtnmntmReg);

		rdbtnmntmEllipse = new JRadioButtonMenuItem("타원형");
		rdbtnmntmEllipse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setEditState(GrimPanModel.SHAPE_OVAL);
			}
		});
		menuShape.add(rdbtnmntmEllipse);

		menuEdit = new JMenu("Edit  ");
		menuBar.add(menuEdit);

		jmiMove = new JMenuItem("Move ");
		jmiMove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveEditAction();
			}
		});
		menuEdit.add(jmiMove);

		jmiDelete = new JMenuItem("Delete");
		jmiDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				deleteEditAction();
			}
		});
		menuEdit.add(jmiDelete);

		menuSetting = new JMenu("Setting ");
		menuBar.add(menuSetting);

		jmiStrokeWidth = new JMenuItem("선두께");
		jmiStrokeWidth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String inputVal = JOptionPane.showInputDialog(thisClass, "선두께", "1");
				if (inputVal!=null){
					model.setShapeStroke(new BasicStroke(Integer.parseInt(inputVal)));
					drawPanel.repaint();
				}
			}
		});

		menuSetting.add(jmiStrokeWidth);

		jmiStrokeColor = new JMenuItem("선색깔");
		jmiStrokeColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color color = 
						JColorChooser.showDialog(thisClass, 
								"Choose a color",
								Color.black);					
				model.setStrokeColor(color);
				drawPanel.repaint();
			}
		});
		menuSetting.add(jmiStrokeColor);

		jcmiFill = new JCheckBoxMenuItem("채우기");
		jcmiFill.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				boolean fillState = jcmiFill.getState();
				model.setShapeFill(fillState);
				drawPanel.repaint();
			}
		});
		menuSetting.add(jcmiFill);

		jmiFillColor = new JMenuItem("채움색깔");
		jmiFillColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color color = 
						JColorChooser.showDialog(thisClass, 
								"Choose a color",
								Color.black);					
				model.setFillColor(color);
				drawPanel.repaint();
			}
		});
		menuSetting.add(jmiFillColor);
		
		rdbtnmntmStar = new JRadioButtonMenuItem("별");  // 별
		rdbtnmntmStar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				model.setEditState(GrimPanModel.SHAPE_STAR);
				Object[] possibleValues = { 
						"3", "4", "5", "6", "7",
						"8", "9", "10", "11", "12"
				};
				Object selectedValue = JOptionPane.showInputDialog(thisClass,
						"Choose one", "Input",
						JOptionPane.INFORMATION_MESSAGE, null,
						possibleValues, possibleValues[0]);
				model.setNPolygon(Integer.parseInt((String)selectedValue));
				drawPanel.repaint();
			}
		});
		menuShape.add(rdbtnmntmStar);
		
		rdbtnmntmRPoly = new JRadioButtonMenuItem("\uB465\uADFC\uB2E4\uAC01\uD615");  // 둥근 다각형
		rdbtnmntmRPoly.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				model.setEditState(GrimPanModel.SHAPE_ROUNDPOLYGON);
				Object[] possibleValues = { 
						"3", "4", "5", "6", "7", "8"
				};
				Object selectedValue = JOptionPane.showInputDialog(thisClass,
						"Choose one", "Input",
						JOptionPane.INFORMATION_MESSAGE, null,
						possibleValues, possibleValues[0]);
				model.setNPolygon(Integer.parseInt((String)selectedValue));
				drawPanel.repaint();
			}
		});
		menuShape.add(rdbtnmntmRPoly);

		btnGroup.add(rdbtnmntmLine);
		btnGroup.add(rdbtnmntmPen);
		btnGroup.add(rdbtnmntmPoly);
		btnGroup.add(rdbtnmntmReg);
		btnGroup.add(rdbtnmntmEllipse);
		btnGroup.add(rdbtnmntmStar);
		btnGroup.add(rdbtnmntmRPoly);
		
		rdbtnmntmText = new JRadioButtonMenuItem("\uD14D\uC2A4\uD2B8");  // 텍스트
		rdbtnmntmText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				model.setEditState(GrimPanModel.SHAPE_TEXT);
				setString();
			}
		});
		menuShape.add(rdbtnmntmText);
		btnGroup.add(rdbtnmntmText);
		
		menu = new JMenu("Help  ");
		menuBar.add(menu);
		
		menuItem = new JMenuItem("About");
		menu.add(menuItem);


		contentPane = new JPanel();
		contentPane.setBorder(null);
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		drawPanel = new DrawPanel(model);
		contentPane.add(drawPanel, BorderLayout.CENTER);

		jFileChooser1 = new JFileChooser(defaultDir);
		jFileChooser1.setDialogTitle("Open Saved GrimPan");
		jFileChooser1.setFileFilter(grimFileModelFilter);


		jFileChooser2 = new JFileChooser(defaultDir);
		jFileChooser2.setDialogType(JFileChooser.SAVE_DIALOG);
		jFileChooser2.setDialogTitle("Save As ...");
		jFileChooser2.setFileFilter(grimFileModelFilter);

	}

	void clearDrawPanel(){
		model.shapeList = new ArrayList<GrimShape>();
		model.polygonPoints = new ArrayList<Point>();

		drawPanel.repaint();
	}
	void openAction(){
		if (jFileChooser1.showOpenDialog(this) ==
				JFileChooser.APPROVE_OPTION) {
			File selFile = jFileChooser1.getSelectedFile();
			readShapeFromSaveFile(selFile);
			model.setSaveFile(selFile);
			drawPanel.repaint();
		}
	}
	void saveAction(){
		if (model.getSaveFile()==null){
			model.setSaveFile(new File(defaultDir+"noname.grm"));
		}
		File selFile = model.getSaveFile();
		saveGrimPanData(selFile);	
	}
	void saveAsAction(){
		if (jFileChooser2.showSaveDialog(this) ==
				JFileChooser.APPROVE_OPTION) {
			File selFile = jFileChooser2.getSelectedFile();
			if (selFile!=null){
				model.setSaveFile(selFile);
				saveGrimPanData(selFile);
			}
		}
	}
	void readShapeFromSaveFile(File saveFile) {
		model.setSaveFile(saveFile);
		try {
			ObjectInputStream input =
					new ObjectInputStream(new FileInputStream(model.getSaveFile()));
			model.shapeList = (ArrayList<GrimShape>) input.readObject();
			input.close();

		} catch (ClassNotFoundException e) {
			System.err.println("Class not Found");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void saveGrimPanData(File saveFile){
		ObjectOutputStream output;
		try {
			output = new ObjectOutputStream(new FileOutputStream(saveFile));
			output.writeObject(model.shapeList);
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void moveEditAction(){
		model.setEditState(GrimPanModel.EDIT_MOVE);
		if (model.curDrawShape != null){
			model.shapeList
			.add(new GrimShape(model.curDrawShape, 
					model.getShapeStroke(),	model.getStrokeColor(), 
					model.getFillColor(), model.isShapeFill()));
			model.curDrawShape = null;
		}
		drawPanel.repaint();
	}
	void saveAsSVGAction(){

		File svgFile = new File(defaultDir+"noname.svg");

		if (model.getSaveFile()!=null){
			String saveFileName = model.getSaveFile().getName();
			svgFile = new File(defaultDir+saveFileName.replace(".grm", ".svg"));
		}

		PrintWriter svgout = null;
		try {
			svgout = new PrintWriter(svgFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		svgout.println("<?xml version='1.0' encoding='utf-8' standalone='no'?>");
		//svgout.println("<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.0//EN' 'http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd'>");
		svgout.print("<svg xmlns:svg='http://www.w3.org/2000/svg' ");
		svgout.print("     xmlns='http://www.w3.org/2000/svg' ");
		svgout.print("width='"+this.getWidth()+"' ");
		svgout.print("height='"+this.getHeight()+"' ");
		svgout.println("overflow='visible' xml:space='preserve'>");
		for (GrimShape gs:model.shapeList){
			svgout.println("    "+GrimShape2SVGTranslator.translateShape2SVG(gs));
		}
		svgout.println("</svg>");
		svgout.close();
	}
	
	void deleteEditAction(){
		model.setEditState(GrimPanModel.EDIT_DELETE);
		if (model.curDrawShape != null){
			model.shapeList
			.add(new GrimShape(model.curDrawShape, 
					model.getShapeStroke(),	model.getStrokeColor(), 
					model.getFillColor(), model.isShapeFill()));
			model.curDrawShape = null;
		}
		drawPanel.repaint();
	}
    
    public void setString(){  // 문자열과 크기를 입력받는 함수
    	String inputStr = JOptionPane.showInputDialog(thisClass, "텍스트");
    	String inputSize = JOptionPane.showInputDialog(thisClass, "텍스트 크기", "10");
		if (inputStr!=null && inputSize!=null){
			model.setText(inputStr);
			model.setTextSize( Integer.parseInt(inputSize) );
			drawPanel.repaint();
		}
    }

}
