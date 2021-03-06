package net.sf.latexdraw.glib.models.impl;

import net.sf.latexdraw.glib.models.ShapeFactory;
import net.sf.latexdraw.glib.models.interfaces.prop.ITextProp;
import net.sf.latexdraw.glib.models.interfaces.shape.IPoint;
import net.sf.latexdraw.glib.models.interfaces.shape.IShape;
import net.sf.latexdraw.glib.models.interfaces.shape.IText;
import net.sf.latexdraw.glib.models.interfaces.shape.TextPosition;

/**
 * Defines a model of a text.<br>
 * <br>
 * This file is part of LaTeXDraw.<br>
 * Copyright (c) 2005-2015 Arnaud BLOUIN<br>
 * <br>
 * LaTeXDraw is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * <br>
 * LaTeXDraw is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br>
 * <br>
 * 07/05/2009<br>
 * @author Arnaud BLOUIN
 * @version 3.0
 * @since 3.0
 */
class LText extends LPositionShape implements IText {
	/** The text */
	protected String text;

	/** The text position of the text. */
	protected TextPosition textPosition;


	/**
	 * The constructor by default.
	 * @since 3.0
	 */
	protected LText() {
		this(ShapeFactory.createPoint(), "text"); //$NON-NLS-1$
	}


	/**
	 * @param pt The position of the text.
	 * @param text The text.
	 * @throws IllegalArgumentException If pt is not valid.
	 * @since 3.0
	 */
	protected LText(final IPoint pt, final String text) {
		super(pt);

		this.text = text==null || text.isEmpty() ? "text" : text; //$NON-NLS-1$
		textPosition = TextPosition.BOT_LEFT;
	}


	@Override
	public String getText() {
		return text;
	}


	@Override
	public void setText(final String text) {
		if(text!=null && !text.isEmpty())
			this.text = text;
	}


	@Override
	public void copy(final IShape s) {
		super.copy(s);

		if(s instanceof ITextProp) {
			final ITextProp textSh= (ITextProp)s;
			text 			= textSh.getText();
			textPosition	= textSh.getTextPosition();
		}
	}


	@Override
	public TextPosition getTextPosition() {
		return textPosition;
	}


	@Override
	public void setTextPosition(final TextPosition textPosition) {
		if(textPosition!=null)
			this.textPosition = textPosition;
	}
}
