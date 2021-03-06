/*
 * This file is part of LaTeXDraw.<br>
 * Copyright (c) 2005-2015 Arnaud BLOUIN<br>
 * <br>
 * LaTeXDraw is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version. <br>
 * LaTeXDraw is distributed without any warranty; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */
package net.sf.latexdraw.glib.models.impl;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import net.sf.latexdraw.glib.models.GLibUtilities;
import net.sf.latexdraw.glib.models.ShapeFactory;
import net.sf.latexdraw.glib.models.interfaces.shape.IPoint;
import net.sf.latexdraw.util.LNumber;
import org.eclipse.jdt.annotation.NonNull;

import java.awt.geom.Point2D;

import static java.lang.Math.PI;
import static java.lang.Math.atan;

/**
 * Defines a model of a point. This model must be used only to define other models. It is not a
 * shape. See the LDot class for the shape.<br>
 * 02/13/2008<br>
 * @author Arnaud BLOUIN
 * @version 3.0
 * @since 3.0
 */
class LPoint implements IPoint {
	protected final @NonNull DoubleProperty x;

	protected final @NonNull DoubleProperty y;

	/**
	 * Creates a Point2D with coordinates (0, 0).
	 */
	protected LPoint() {
		this(0, 0);
	}

	/**
	 * Creates a point from a IPoint
	 * @param pt The IPoint, if null the default value (0,0) will be used.
	 */
	protected LPoint(final IPoint pt) {
		this(pt == null?0d:pt.getX(), pt == null?0d:pt.getY());
	}

	/**
	 * Creates a Point2D with the specified coordinates.
	 * @param xCoord The X-coordinate to set.
	 * @param yCoord The Y-coordinate to set.
	 */
	protected LPoint(final double xCoord, final double yCoord) {
		super();
		x = new SimpleDoubleProperty(xCoord);
		y = new SimpleDoubleProperty(yCoord);
	}

	@Override
	public double computeAngle(final IPoint pt) {
		if(!GLibUtilities.isValidPoint(pt))
			return java.lang.Double.NaN;

		double angle;
		final double x2 = pt.getX() - getX();
		final double y2 = pt.getY() - getY();

		if(LNumber.equalsDouble(x2, 0.)) {
			angle = Math.PI / 2.;

			if(y2 < 0.)
				angle = Math.PI * 2. - angle;
		}else
			angle = x2 < 0.?Math.PI - atan(-y2 / x2):atan(y2 / x2);

		return angle;
	}

	@Override
	public IPoint zoom(final double zoomLevel) {
		return ShapeFactory.createPoint(getX() * zoomLevel, getX() * zoomLevel);
	}

	@Override
	public double computeRotationAngle(final IPoint pt1, final IPoint pt2) {
		if(!GLibUtilities.isValidPoint(pt1) || !GLibUtilities.isValidPoint(pt2))
			return java.lang.Double.NaN;

		final double thetaOld = computeAngle(pt1);
		final double thetaNew = computeAngle(pt2);

		return thetaNew - thetaOld;
	}

	@Override
	public IPoint centralSymmetry(final IPoint centre) {
		return rotatePoint(centre, Math.PI);
	}

	@Override
	public IPoint rotatePoint(final IPoint gravityC, final double theta) {
		if(!GLibUtilities.isValidPoint(gravityC) || !GLibUtilities.isValidCoordinate(theta))
			return null;

		final IPoint pt = ShapeFactory.createPoint();
		final double cosTheta;
		final double sinTheta;
		double angle = theta;
		final double gx = gravityC.getX();
		final double gy = gravityC.getY();

		if(angle < 0.)
			angle = 2. * PI + angle;

		angle %= 2. * PI;

		if(LNumber.equalsDouble(angle, 0.))
			return new LPoint(this);

		if(LNumber.equalsDouble(angle - PI / 2., 0.)) {
			cosTheta = 0.;
			sinTheta = 1.;
		}else if(LNumber.equalsDouble(angle - PI, 0.)) {
			cosTheta = -1.;
			sinTheta = 0.;
		}else if(LNumber.equalsDouble(angle - 3. * PI / 2., 0.)) {
			cosTheta = 0.;
			sinTheta = -1.;
		}else {
			cosTheta = Math.cos(angle);
			sinTheta = Math.sin(angle);
		}

		pt.setX(cosTheta * (getX() - gx) - sinTheta * (getY() - gy) + gx);
		pt.setY(sinTheta * (getX() - gx) + cosTheta * (getY() - gy) + gy);

		return pt;
	}

	@Override
	public boolean equals(final IPoint p, final double gap) {
		return !(!GLibUtilities.isValidCoordinate(gap) || !GLibUtilities.isValidPoint(p)) && LNumber.equalsDouble(getX(), p.getX(), gap) && LNumber.equalsDouble(getY(), p.getY(), gap);
	}

	@Override
	public IPoint getMiddlePoint(final IPoint p) {
		return p == null?null:ShapeFactory.createPoint((getX() + p.getX()) / 2., (getY() + p.getY()) / 2.);
	}

	@Override
	public void translate(final double tx, final double ty) {
		if(GLibUtilities.isValidPoint(tx, ty))
			setPoint(getX() + tx, getY() + ty);
	}

	@Override
	public IPoint horizontalSymmetry(final IPoint origin) {
		if(!GLibUtilities.isValidPoint(origin))
			return null;

		return ShapeFactory.createPoint(2. * origin.getX() - getX(), getY());
	}

	@Override
	public IPoint verticalSymmetry(final IPoint origin) {
		if(!GLibUtilities.isValidPoint(origin))
			return null;

		return ShapeFactory.createPoint(getX(), 2. * origin.getY() - getY());
	}

	@Override
	public void setPoint(final double newX, final double newY) {
		setX(newX);
		setY(newY);
	}

	@Override
	public void setX(final double newX) {
		if(GLibUtilities.isValidCoordinate(newX))
			x.set(newX);
	}

	@Override
	public void setY(final double newY) {
		if(GLibUtilities.isValidCoordinate(newY))
			y.set(newY);
	}

	@Override
	public void setPoint(final IPoint pt) {
		if(pt != null)
			setPoint(pt.getX(), pt.getY());
	}

	@Override
	public double distance(final IPoint pt) {
		return pt == null?java.lang.Double.NaN:distance(pt.getX(), pt.getY());
	}

	@Override
	public Point2D.Double toPoint2D() {
		return new Point2D.Double(x.getValue(), y.getValue());
	}

	@Override
	public void setPoint2D(final Point2D pt) {
		if(pt != null)
			setPoint(pt.getX(), pt.getY());
	}

	@Override
	public IPoint substract(final IPoint pt) {
		if(pt == null)
			return null;
		return ShapeFactory.createPoint(getX() - pt.getX(), getY() - pt.getY());
	}

	@Override
	public IPoint normalise() {
		final double magnitude = magnitude();
		return ShapeFactory.createPoint(getX() / magnitude, getY() / magnitude);
	}

	@Override
	public double magnitude() {
		return Math.sqrt(getX() * getX() + getY() * getY());
	}

	@Override
	public IPoint add(final IPoint pt) {
		final IPoint added = ShapeFactory.createPoint(this);
		if(pt != null)
			added.translate(pt.getX(), pt.getY());
		return added;
	}

	@Override
	public @NonNull DoubleProperty xProperty() {
		return x;
	}

	@Override
	public @NonNull DoubleProperty yProperty() {
		return y;
	}

	@Override
	public double getY() {
		return y.get();
	}

	@Override
	public double getX() {
		return x.get();
	}

	@Override
	public double distance(double xCoord, double yCoord) {
		return Math.sqrt(Math.pow(xCoord - x.get(), 2) + Math.pow(yCoord - y.get(), 2));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp = Double.doubleToLongBits(x.doubleValue());
		result = prime * result + (int)(temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y.doubleValue());
		result = prime * result + (int)(temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if(this == obj)
			return true;
		if(!(obj instanceof IPoint))
			return false;
		final IPoint other = (IPoint)obj;
		if(Double.doubleToLongBits(x.doubleValue()) != Double.doubleToLongBits(other.getX()))
			return false;
		if(Double.doubleToLongBits(y.doubleValue()) != Double.doubleToLongBits(other.getY()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LPoint [x=" + x.doubleValue() + ", y=" + y.doubleValue() + "]";
	}
}
