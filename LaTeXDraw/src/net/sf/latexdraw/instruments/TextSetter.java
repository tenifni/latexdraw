package net.sf.latexdraw.instruments;

import java.awt.event.KeyEvent;

import javax.swing.JLayeredPane;

import net.sf.latexdraw.actions.AddShape;
import net.sf.latexdraw.bordel.BordelCollector;
import net.sf.latexdraw.glib.models.interfaces.DrawingTK;
import net.sf.latexdraw.glib.models.interfaces.IPoint;
import net.sf.latexdraw.glib.models.interfaces.IShape;
import net.sf.latexdraw.glib.models.interfaces.IText;
import net.sf.latexdraw.ui.TextAreaAutoSize;
import fr.eseo.malai.action.library.ActivateInactivateInstruments;
import fr.eseo.malai.instrument.Instrument;
import fr.eseo.malai.instrument.Link;
import fr.eseo.malai.interaction.library.KeyTyped;
import fr.eseo.malai.widget.MLayeredPane;

/**
 * This instrument allows to add and modify texts to the drawing.<br>
 * <br>
 * This file is part of LaTeXDraw.<br>
 * Copyright (c) 2005-2011 Arnaud BLOUIN<br>
 * <br>
 * LaTeXDraw is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * <br>
 * LaTeXDraw is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br>
 * <br>
 * 20/12/2010<br>
 * @author Arnaud BLOUIN
 * @since 3.0
 */
public class TextSetter extends Instrument {
	/** The text field. */
	protected TextAreaAutoSize textField;

	/** The pane where the text field must be added. */
	protected MLayeredPane layeredPanel;

	/** The pencil used to create shapes. */
	protected Pencil pencil;

	/**
	 * The point where texts are added. It may not corresponds with the location
	 * of the text field since the text field position is absolute (does not consider
	 * the zoom level).
	 */
	protected IPoint relativePoint;


	/**
	 * Creates the instrument.
	 * @param overlayedPanel The pane where the text field must be added.
	 * @throws IllegalArgumentException If the given panel is null.
	 * @since 3.0
	 */
	public TextSetter(final MLayeredPane overlayedPanel) {
		super();

		if(overlayedPanel==null)
			throw new IllegalArgumentException();

		layeredPanel 	= overlayedPanel;
		textField 		= new TextAreaAutoSize();
		layeredPanel.add(textField, JLayeredPane.PALETTE_LAYER);
		textField.setVisible(false);

		initialiseLinks();

		addEventable(textField);
	}


	/**
	 * @param pencil The pencil to set to the text setter.
	 * @since 3.0
	 */
	public void setPencil(final Pencil pencil) {
		this.pencil = pencil;
	}


	@Override
	protected void initialiseLinks() {
		try{
			links.add(new Enter2AddText(this));
			links.add(new KeyPress2Desactivate(this));
		}catch(InstantiationException e){
			BordelCollector.INSTANCE.add(e);
		}catch(IllegalAccessException e){
			BordelCollector.INSTANCE.add(e);
		}
	}


	@Override
	public void setActivated(final boolean activated) {
		super.setActivated(activated);

		if(textField.isVisible()!=activated) {
			if(activated) {
				textField.setVisible(true);
				textField.setLocation(100, 100);
			} else
				textField.setVisible(false);

			layeredPanel.repaint();
			textField.setVisible(activated);
		}

		if(activated) {
			textField.setText(""); //$NON-NLS-1$
			textField.requestFocusInWindow();
		}
	}


	/**
	 * @return The text field used to set texts.
	 * @since 3.0
	 */
	public TextAreaAutoSize getTextField() {
		return textField;
	}


	/**
	 * @param relativePoint The point where texts are added. It may not corresponds with the location
	 * of the text field since the text field position is absolute (does not consider
	 * the zoom level).
	 * @since 3.0
	 */
	public void setRelativePoint(final IPoint relativePoint) {
		this.relativePoint = relativePoint;
	}
}


/**
 * This links maps a key press interaction to an action that desactivates the instrument.
 */
class KeyPress2Desactivate extends Link<ActivateInactivateInstruments, KeyTyped, TextSetter> {
	/**
	 * Creates the link.
	 */
	public KeyPress2Desactivate(final TextSetter ins) throws InstantiationException, IllegalAccessException {
		super(ins, false, ActivateInactivateInstruments.class, KeyTyped.class);
	}

	@Override
	public void initAction() {
		action.addInstrumentToInactivate(instrument);
	}

	@Override
	public boolean isConditionRespected() {
		int key = interaction.getKey();
		// It is useless to check if another key is pressed because if it is the case, the interaction
		// is in state keyPressed.
		return (key==KeyEvent.VK_ENTER && instrument.textField.getText().length()>0) || key==KeyEvent.VK_ESCAPE;
	}
}


/**
 * This links maps a key press interaction to an action that adds a text to the drawing.
 */
class Enter2AddText extends Link<AddShape, KeyTyped, TextSetter> {
	/**
	 * Creates the link.
	 */
	public Enter2AddText(final TextSetter ins) throws InstantiationException, IllegalAccessException {
		super(ins, false, AddShape.class, KeyTyped.class);
	}

	@Override
	public void initAction() {
		final IPoint textPosition = instrument.relativePoint==null ? DrawingTK.getFactory().createPoint(instrument.textField.getX(),
									instrument.textField.getY()+instrument.textField.getHeight()) : instrument.relativePoint;
		final IShape sh = instrument.pencil==null ? null : instrument.pencil.createShapeInstance();

		if(sh instanceof IText) {
			final IText text = (IText)sh;
			text.setPosition(textPosition.getX(), textPosition.getY());
			text.setText(instrument.textField.getText());
			action.setShape(text);
			action.setDrawing(instrument.pencil.drawing);
		}
	}

	@Override
	public boolean isConditionRespected() {
		return instrument.textField.getText().length()>0 && interaction.getKey()==KeyEvent.VK_ENTER;
	}
}