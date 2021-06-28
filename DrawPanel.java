package hufs.cse.grimpan4;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

public class DrawPanel extends JPanel implements MouseInputListener {

	static final int MINPOLYDIST = 6;
	
	private GrimPanModel model = null;

	public DrawPanel(GrimPanModel model){
		this.model = model;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		for (GrimShape grimShape:model.shapeList){
			grimShape.draw(g2);
		}

		if (model.curDrawShape != null){
			GrimShape curGrimShape = new GrimShape(model.curDrawShape, 
					model.getShapeStroke(), model.getStrokeColor(),
					model.getFillColor(), model.isShapeFill());
			curGrimShape.draw(g2);
		}
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	public void mousePressed(MouseEvent ev) {
		Point p1 = ev.getPoint();
		model.setMousePosition(p1);
		//System.out.println("Pressed Mouse Point=("+p1.x+","+p1.y+")");

		if (SwingUtilities.isLeftMouseButton(ev)){
			model.setClickedMousePosition(p1);
			switch (model.getEditState()){
			case GrimPanModel.SHAPE_REGULAR:
				genCenteredPolygon();
				break;
			case GrimPanModel.SHAPE_OVAL:
				genEllipse2D();
				break;
			case GrimPanModel.SHAPE_LINE:
				genLineShape();
				break;
			case GrimPanModel.SHAPE_PENCIL:
				model.setLastMousePosition(model.getMousePosition());				
				model.curDrawShape = new Path2D.Double();
				((Path2D)model.curDrawShape).moveTo((double)p1.x, (double)p1.y);
				break;
			case GrimPanModel.SHAPE_POLYGON:
				break;
			case GrimPanModel.EDIT_MOVE:
				getSelectedShape();
				break;
			case GrimPanModel.EDIT_DELETE:
				getSelectedShape();
				break;
			case GrimPanModel.SHAPE_STAR:
				drawStar();
				break;
			case GrimPanModel.SHAPE_ROUNDPOLYGON:
				drawRPoly();
				break;
			}
		}
		repaint();
	}

	public void mouseReleased(MouseEvent ev) {
		Point p1 = ev.getPoint();
		model.setMousePosition(p1);
		//System.out.println("Released Mouse Point=("+p1.x+","+p1.y+")");

		switch (model.getEditState()){
		case GrimPanModel.SHAPE_REGULAR:
			genCenteredPolygon();
			if (model.curDrawShape != null){
				model.shapeList.add(new GrimShape(model.curDrawShape, 
						model.getShapeStroke(), model.getStrokeColor(),
						model.getFillColor(), model.isShapeFill()));
				model.curDrawShape = null;
			}
			break;
		case GrimPanModel.SHAPE_OVAL:
			genEllipse2D();
			if (model.curDrawShape != null){
				model.shapeList.add(new GrimShape(model.curDrawShape, 
						model.getShapeStroke(), model.getStrokeColor(),
						model.getFillColor(), model.isShapeFill()));
				model.curDrawShape = null;
			}
			break;
		case GrimPanModel.SHAPE_LINE:
			genLineShape();
			if (model.curDrawShape != null){
				model.shapeList.add(new GrimShape(model.curDrawShape, 
						model.getShapeStroke(), model.getStrokeColor(),
						model.getFillColor(), model.isShapeFill()));
				model.curDrawShape = null;
			}
			break;
		case GrimPanModel.SHAPE_PENCIL:
			((Path2D)model.curDrawShape).lineTo((double)p1.x, (double)p1.y);
			if (model.curDrawShape != null){
				model.shapeList.add(new GrimShape(model.curDrawShape, 
						model.getShapeStroke(), model.getStrokeColor(),
						model.getFillColor(), model.isShapeFill()));
				model.curDrawShape = null;
			}
			break;
		case GrimPanModel.SHAPE_POLYGON:
			model.polygonPoints.add(p1);
			model.curDrawShape = buildPath2DFromPoints(model.polygonPoints);
			if (ev.isShiftDown()) {
				((Path2D)(model.curDrawShape)).closePath();
				model.polygonPoints.clear();
				model.shapeList.add(new GrimShape(model.curDrawShape, 
						model.getShapeStroke(), model.getStrokeColor(),
						model.getFillColor(), model.isShapeFill()));
				model.curDrawShape = null;
			}
			break;
		case GrimPanModel.EDIT_MOVE:
			endShapeMove();
			break;
		case GrimPanModel.EDIT_DELETE:
			deleteShapeByMouse();
			break;
		case GrimPanModel.SHAPE_STAR:
			drawStar();
			if (model.curDrawShape != null){
				model.shapeList.add(new GrimShape(model.curDrawShape, 
						model.getShapeStroke(), model.getStrokeColor(),
						model.getFillColor(), model.isShapeFill()));
				model.curDrawShape = null;
			}
			break;
		case GrimPanModel.SHAPE_ROUNDPOLYGON:
			drawRPoly();
			if (model.curDrawShape != null){
				model.shapeList.add(new GrimShape(model.curDrawShape, 
						model.getShapeStroke(), model.getStrokeColor(),
						model.getFillColor(), model.isShapeFill()));
				model.curDrawShape = null;
			}
			break;
		case GrimPanModel.SHAPE_TEXT:
			drawShapeText();
			if (model.curDrawShape != null){
				model.shapeList.add(new GrimShape(model.curDrawShape, 
						model.getShapeStroke(), model.getStrokeColor(),
						model.getFillColor(), model.isShapeFill()));
				model.curDrawShape = null;
			}
			break;
		}

		repaint();

	}

	public void mouseDragged(MouseEvent ev) {
		Point p1 = ev.getPoint();
		model.setLastMousePosition(model.getMousePosition());
		model.setMousePosition(p1);
		//System.out.println("Dragged Mouse Point=("+p1.x+","+p1.y+")");
		
		switch (model.getEditState()){
		case GrimPanModel.SHAPE_REGULAR:
			genCenteredPolygon();
			break;
		case GrimPanModel.SHAPE_OVAL:
			genEllipse2D();
			break;
		case GrimPanModel.SHAPE_LINE:
			genLineShape();
			break;
		case GrimPanModel.SHAPE_PENCIL:
			((Path2D)model.curDrawShape).lineTo((double)p1.x, (double)p1.y);
			break;
		case GrimPanModel.SHAPE_POLYGON:
			break;
		case GrimPanModel.EDIT_MOVE:
			moveShapeByMouse();
			break;
		case GrimPanModel.SHAPE_STAR:
			drawStar();
			break;
		case GrimPanModel.SHAPE_ROUNDPOLYGON:
			drawRPoly();
			break;
			
		}
		repaint();

	}
	private void genLineShape() {
		Point p1 = model.getClickedMousePosition();
		Point p2 = model.getMousePosition();
		model.curDrawShape = new Line2D.Double(p1, p2);

	}
	public static Path2D buildPath2DFromPoints(ArrayList<Point> points){
		Path2D result = new Path2D.Double();
		if (points != null && points.size() > 0) {
			Point p0 = points.get(0);
			result.moveTo((double)(p0.x), (double)(p0.y));
			for (int i=1; i<points.size(); ++i){
				p0 = points.get(i);
				result.lineTo((double)(p0.x), (double)(p0.y));
			}
		}

		return result;
	}
	private void genCenteredPolygon(){
		Point pi = model.getMousePosition();
		Point center = model.getClickedMousePosition();
		if (pi.distance(new Point2D.Double(center.x, center.y)) <= MINPOLYDIST)
			return;

		getPolygonWithCenterNVertex(center, new Point2D.Double(pi.x, pi.y));
	}
	private void getPolygonWithCenterNVertex(Point center, Point2D pi){
		AffineTransform rotNTimes = new AffineTransform();
		rotNTimes.setToRotation(2*Math.PI/model.getNPolygon()); //360/n degree

		Point2D[] polyPoints = new Point2D[model.getNPolygon()];
		polyPoints[0] = new Point2D.Double( (pi.getX()-center.x) , (pi.getY()-center.y) ); 
		for (int i=1; i<polyPoints.length; ++i){
			polyPoints[i] = (Point2D)polyPoints[i-1].clone();
			rotNTimes.transform(polyPoints[i], polyPoints[i]);
		}

		polyPoints[0] = new Point2D.Double((pi.getX()), (pi.getY())); 
		for (int i=1; i<polyPoints.length; ++i){
			polyPoints[i].setLocation(
					(polyPoints[i].getX()+center.x), 
					(polyPoints[i].getY()+center.y));
		}
		Path2D polygonPath = new Path2D.Double();
		polygonPath.moveTo( (polyPoints[0].getX()) , (polyPoints[0].getY()) );
		//System.out.println("x= "+polyPoints[0].getX()+" y= "+polyPoints[0].getY());
		for (int i=1; i<polyPoints.length; ++i){
			polygonPath.lineTo( (polyPoints[i].getX()) , (polyPoints[i].getY()) );
			//System.out.println("x= "+polyPoints[i].getX()+" y= "+polyPoints[i].getY());
		}
		polygonPath.closePath();

		model.curDrawShape = polygonPath;
		
	}
	private void genEllipse2D(){
		Point pi = model.getMousePosition();
		Point topleft = model.getClickedMousePosition();
		if (pi.distance(new Point2D.Double(topleft.x, topleft.y)) <= MINPOLYDIST)
			return;
		Ellipse2D oval = new Ellipse2D.Double(
				topleft.x, topleft.y,
				pi.x-topleft.x, pi.y-topleft.y);
		model.curDrawShape = oval;
	}
	private void getSelectedShape(){
		int selIndex = -1;
		GrimShape shape = null;
		for (int i=model.shapeList.size()-1; i >= 0; --i){
			shape = model.shapeList.get(i);
			if (shape.contains(model.getMousePosition().getX(), model.getMousePosition().getY())){
				selIndex = i;
				break;
			}
		}
		if (selIndex != -1){
			model.setLastMousePosition(model.getClickedMousePosition());
			Color scolor = shape.getGrimStrokeColor();
			Color fcolor = shape.getGrimFillColor();
			if (scolor!=null){
				shape.setGrimStrokeColor(new Color (scolor.getRed(), scolor.getGreen(), scolor.getBlue(), 127));
			}
			if (fcolor!=null){
				shape.setGrimFillColor(new Color (fcolor.getRed(), fcolor.getGreen(), fcolor.getBlue(), 127));
			}
		}
		model.setSelectedShape(selIndex);
	}
	private void moveShapeByMouse(){
		int selIndex = model.getSelectedShape();
		GrimShape shape = null;
		if (selIndex != -1){
			shape = model.shapeList.get(selIndex);
			shape.translate(
					(float)(model.getMousePosition().x-model.getLastMousePosition().x), 
					(float)(model.getMousePosition().y-model.getLastMousePosition().y));
		}
	}
	private void endShapeMove(){
		int selIndex = model.getSelectedShape();
		GrimShape shape = null;
		if (selIndex != -1){
			shape = model.shapeList.get(selIndex);
			Color scolor = shape.getGrimStrokeColor();
			Color fcolor = shape.getGrimFillColor();
			if (scolor!=null){
				shape.setGrimStrokeColor(new Color (scolor.getRed(), scolor.getGreen(), scolor.getBlue()));
			}
			if (fcolor!=null){
				shape.setGrimFillColor(new Color (fcolor.getRed(), fcolor.getGreen(), fcolor.getBlue()));
			}
		}
	}
	
	private void deleteShapeByMouse(){  // 그림판 내의 Shape를 delete하는 함수
		int selIndex = model.getSelectedShape();
		GrimShape shape = null;
		if (selIndex != -1){
			shape = model.shapeList.remove(selIndex);
		}
	}
	
	private void drawStar(){  // 그림판 내에 별을 삽입하기 위한 함수
		Point pi = model.getMousePosition();
		Point center = model.getClickedMousePosition();
		if (pi.distance(new Point2D.Double(center.x, center.y)) <= MINPOLYDIST)
			return;

		Point p1 = model.getClickedMousePosition();
		Point p2 = model.getMousePosition();
		
		Shape shapes = new StarPolygon(p1.x, p1.y, (p2.x-p1.x)+(p2.y-p1.y), 10, model.getNPolygon(), Math.PI / 4);
	
		model.curDrawShape = shapes;
	}
	
	private void drawRPoly(){  // 그림판 내에 둥근 다각형을 삽입하기 위한 함수
		Point p1 = model.getClickedMousePosition();
		Point p2 = model.getMousePosition();
		
        Shape shapes;
        
        shapes = new RouPolygon(new RegPolygon(p1.x, p1.y, (p2.x-p1.x)+(p2.y-p1.y), model.getNPolygon(), Math.PI / 4), 10);
		
        model.curDrawShape = shapes;
	}
	
	public void drawShapeText(){  // 그림판 내에 Text를 삽입하기 위한 함수
		String a = model.getText();
		Shape p = convertText( a.toCharArray() , model.getTextSize() );
		
		model.curDrawShape = p;
	}
	
	public Shape convertText(char[] c, int size) {  // 문자형 배열을 Shape로 바꾸는 함수
		Point p1 = model.getClickedMousePosition();
		
	    Font f = getFont();
	    f = f.deriveFont(Font.BOLD, size);

	    FontRenderContext frc = getFontMetrics(f).getFontRenderContext();
	    GlyphVector v = f.createGlyphVector(frc, c);
	    
	    int k = 0;
	    Point a = new Point();
	    
	    a.setLocation(p1);
	    
	    for(int i=0; i<c.length; i++){  // 문자열 형태로 그림판에 띄우기 위해 사용
	    	a.setLocation(p1.x+k, p1.y);
	    	v.setGlyphPosition(i, a);
	    	if(c[i] == ' ') k = k + size/3;  // 띄어쓰기만 폭을 좁히기 위해 사용
	    	else k = k + size;
	    }
	    
	    return v.getOutline();
	}
}


class RegPolygon extends Polygon {  // 일반 다각형을 생성하는 클래스 - 둥근 다각형을 만들기 위해 필요
    public RegPolygon(int x, int y, int r, int v) {
        this(x, y, r, v, 0);
    }
    
    public RegPolygon(int x, int y, int r, int v, double sAngle) {
        super(getXCoors(x, y, r, v, sAngle)
              ,getYCoors(x, y, r, v, sAngle)
              ,v);
    }

    protected static int[] getXCoors(int x, int y, int r, int v, double sAngle) {
        int res[] = new int[v];
        double addAngle = 2*Math.PI/v;
        double angle = sAngle;
        
        for (int i=0; i<v; i++) {
            res[i] = (int)Math.round(r*Math.cos(angle))+x;
            angle += addAngle;
        }
        
        return res;
    }

    protected static int[] getYCoors(int x, int y, int r, int v, double sAngle) {
        int res[] = new int[v];
        double addAngle = 2*Math.PI/v;
        double angle = sAngle;
        
        for (int i=0; i<v; i++) {
            res[i] = (int)Math.round(r*Math.sin(angle))+y;
            angle += addAngle;
        }
        
        return res;
    }
}


class RouPolygon implements Shape {  // 둥근 다각형을 생성하는 클래스
    GeneralPath path;
    
    public RouPolygon(Polygon p, int arcW) {
        path = new GeneralPath();
        transform(p, arcW, path);
    }
    
    public Rectangle getBounds() {
        return path.getBounds();
    }

    public Rectangle2D getBounds2D() {
        return path.getBounds2D();
    }

    public boolean contains(double x, double y) {
        return path.contains(x,y);
    }

    public boolean contains(Point2D p) {
        return path.contains(p);
    }

    public boolean intersects(double x, double y, double w, double h) {
        return path.intersects(x,y,w,h);
    }

    public boolean intersects(Rectangle2D r) {
        return path.intersects(r);
    }

    public boolean contains(double x, double y, double w, double h) {
        return path.contains(x,y,w,h);
    }

    public boolean contains(Rectangle2D r) {
        return path.contains(r) ;
    }

    public PathIterator getPathIterator(AffineTransform at) {
        return path.getPathIterator(at);
    }

    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return path.getPathIterator(at, flatness);
    }

    protected static void transform(Polygon s, int arcW, GeneralPath path) {
            PathIterator pIter = s.getPathIterator(new AffineTransform());

            Point2D.Float pFirst = new Point2D.Float(0,0);
            Point2D.Float pSecond = null;

            Point2D.Float pLast = new Point2D.Float(0,0);
            Point2D.Float pCorner = null;
            Point2D.Float pNext = null;

            float [] coor = new float[6];
            
            while (!pIter.isDone()) {
                int type = pIter.currentSegment(coor);
                float x1 = coor[0];
                float y1 = coor[1];
                float x2 = coor[2];
                float y2 = coor[3];
                float x3 = coor[4];
                float y3 = coor[5];

                switch (type) {
                    case PathIterator.SEG_CLOSE:
                        break;
                    case PathIterator.SEG_CUBICTO:
                        path.curveTo(x1, y1, x2, y2, x3, y3);
                        break;
                    case PathIterator.SEG_LINETO:
                        if (pCorner==null) {
                            pCorner=new Point2D.Float(x1,y1);
                            if (pNext==null) {
                                //first move
                                pSecond=new Point2D.Float(x1,y1);
                                Point2D.Float arcStartPoint=getArcPoint(pSecond, pFirst, arcW);

                                path.moveTo(arcStartPoint.x,arcStartPoint.y);
                            }
                        }
                        else {
                            pNext=new Point2D.Float(x1,y1);
                            add(path, pLast, pCorner, pNext, arcW);
                            pLast=pCorner;
                            pCorner=pNext;
                        }
                        break;
                    case PathIterator.SEG_MOVETO:
                        pLast.x=x1;
                        pLast.y=y1;
                        pFirst.x=x1;
                        pFirst.y=y1;
                        break;
                    case PathIterator.SEG_QUADTO:
                        path.quadTo(x1, y1, x2, y2);
                        break;
                }
                pIter.next();
            }

            add(path, pLast, pCorner, pFirst, arcW);
            add(path, pCorner, pFirst, pSecond, arcW);
            path.closePath();

    }

    protected static void add(GeneralPath path, Point2D.Float l, Point2D.Float c, Point2D.Float n, float w) {
        Point2D.Float arcSPoint=getArcPoint(l, c, w);
        Point2D.Float arcEPoint=getArcPoint(n, c, w);

        path.lineTo(arcSPoint.x, arcSPoint.y);
        path.quadTo(c.x, c.y, arcEPoint.x, arcEPoint.y);
    }

    protected static Point2D.Float getArcPoint(Point2D.Float p1, Point2D.Float p2, float w) {
        Point2D.Float res = new Point2D.Float();
        float d = Math.round(Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y)));

        if (p1.x<p2.x) {
            res.x = p2.x - w * Math.abs(p1.x - p2.x) / d;
        }
        else {
            res.x = p2.x + w * Math.abs(p1.x - p2.x) / d;
        }

        if (p1.y<p2.y) {
            res.y = p2.y - w * Math.abs(p1.y - p2.y) / d;
        }
        else {
            res.y = p2.y + w * Math.abs(p1.y - p2.y) / d;
        }

        return res;
    }
}


class StarPolygon extends Polygon {  // 별을 생성하는 클래스
    public StarPolygon(int x, int y, int r, int iR, int v) {
        this(x, y, r, iR, v, 0);
    }
    public StarPolygon(int x, int y, int r, int iR, int v, double sAngle) {
        super(getXCoors(x, y, r, iR,  v, sAngle)
              ,getYCoors(x, y, r, iR, v, sAngle)
              ,v*2);
    }

    protected static int[] getXCoors(int x, int y, int r, int iR, int v, double sAngle) {
        int res[] = new int[v*2];
        double addAngle = 2*Math.PI/v;
        double angle = sAngle;
        double iAngle = sAngle+Math.PI/v;
        
        for (int i=0; i<v; i++) {
            res[i*2] = (int)Math.round(r*Math.cos(angle))+x;
            angle += addAngle;
            res[i*2+1] = (int)Math.round(iR*Math.cos(iAngle))+x;
            iAngle += addAngle;
        }
        
        return res;
    }

    protected static int[] getYCoors(int x, int y, int r, int iR, int v, double sAngle) {
        int res[] = new int[v*2];
        double addAngle = 2*Math.PI/v;
        double angle = sAngle;
        double iAngle = sAngle+Math.PI/v;
        
        for (int i=0; i<v; i++) {
            res[i*2] = (int)Math.round(r*Math.sin(angle))+y;
            angle += addAngle;
            res[i*2+1] = (int)Math.round(iR*Math.sin(iAngle))+y;
            iAngle += addAngle;
        }
        
        return res;
    }
}
