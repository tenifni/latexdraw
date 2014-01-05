package test.glib.models;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.sf.latexdraw.glib.models.ShapeFactory;
import net.sf.latexdraw.glib.models.interfaces.ICircle;
import net.sf.latexdraw.glib.models.interfaces.IEllipse;
import net.sf.latexdraw.glib.models.interfaces.IPositionShape;
import net.sf.latexdraw.glib.models.interfaces.IRectangle;
import net.sf.latexdraw.glib.models.interfaces.IRectangularShape;
import net.sf.latexdraw.glib.models.interfaces.IShape;
import net.sf.latexdraw.glib.models.interfaces.ISquare;

import org.junit.Before;
import org.junit.Test;

import test.HelperTest;
import test.glib.models.interfaces.TestICircle;

public class TestLCircle<T extends ICircle> extends TestICircle<T> {
	@Before
	public void setUp() {
		shape  = (T) ShapeFactory.factory().createCircle(false);
		shape2 = (T) ShapeFactory.factory().createCircle(false);
	}


	@Override
	@Test
	public void testIsTypeOf() {
		assertFalse(shape.isTypeOf(null));
		assertFalse(shape.isTypeOf(IRectangle.class));
		assertFalse(shape.isTypeOf(ISquare.class));
		assertTrue(shape.isTypeOf(IShape.class));
		assertTrue(shape.isTypeOf(IPositionShape.class));
		assertTrue(shape.isTypeOf(IRectangularShape.class));
		assertTrue(shape.isTypeOf(IEllipse.class));
		assertTrue(shape.isTypeOf(ICircle.class));
		assertTrue(shape.isTypeOf(shape.getClass()));
	}


	@Test
	public void testConstructors() {
		ICircle circle = ShapeFactory.factory().createCircle(false);

		assertEquals(4, circle.getNbPoints());
		HelperTest.assertEqualsDouble(circle.getRx(), circle.getRy());
		HelperTest.assertEqualsDouble(circle.getWidth(), circle.getHeight());
		assertTrue(circle.getHeight()>0);
	}


	@Test
	public void testConstructors2() {
		ICircle circle = ShapeFactory.factory().createCircle(false);

		HelperTest.assertEqualsDouble(4, circle.getNbPoints());
		HelperTest.assertEqualsDouble(circle.getRx(), circle.getRy());
		HelperTest.assertEqualsDouble(circle.getWidth(), circle.getHeight());
		assertTrue(circle.getHeight()>0);
	}


	@Test
	public void testConstructors3() {
		ICircle circle;

		try {
			circle = ShapeFactory.factory().createCircle(null, 10., true);
			fail();
		}catch(NullPointerException e) {/* */}

		try {
			circle = ShapeFactory.factory().createCircle(ShapeFactory.factory().createPoint(Double.NaN, 1), 10., true);
			fail();
		}catch(IllegalArgumentException e) {/* */}

		try {
			circle = ShapeFactory.factory().createCircle(ShapeFactory.factory().createPoint(1, Double.NEGATIVE_INFINITY), 10., true);
			fail();
		}catch(IllegalArgumentException e) {/* */}

		try {
			circle = ShapeFactory.factory().createCircle(ShapeFactory.factory().createPoint(1, 1), -10., true);
			fail();
		}catch(IllegalArgumentException e) {/* */}

		try {
			circle = ShapeFactory.factory().createCircle(ShapeFactory.factory().createPoint(1, 1), 0., true);
			fail();
		}catch(IllegalArgumentException e) {/* */}

		circle = ShapeFactory.factory().createCircle(ShapeFactory.factory().createPoint(1, 2), 10., true);

		HelperTest.assertEqualsDouble(1., circle.getGravityCentre().getX());
		HelperTest.assertEqualsDouble(2., circle.getGravityCentre().getY());
		HelperTest.assertEqualsDouble(20., circle.getWidth());
		assertEquals(4, circle.getNbPoints());
		HelperTest.assertEqualsDouble(circle.getRx(), circle.getRy());
		HelperTest.assertEqualsDouble(circle.getWidth(), circle.getHeight());
	}
}
