package net.sf.latexdraw.glib.views.Java2D;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.imageio.ImageIO;

import net.sf.latexdraw.bordel.BordelCollector;
import net.sf.latexdraw.filters.PDFFilter;
import net.sf.latexdraw.filters.PNGFilter;
import net.sf.latexdraw.filters.PSFilter;
import net.sf.latexdraw.filters.TeXFilter;
import net.sf.latexdraw.glib.models.interfaces.DrawingTK;
import net.sf.latexdraw.glib.models.interfaces.IPoint;
import net.sf.latexdraw.glib.models.interfaces.IShape;
import net.sf.latexdraw.glib.models.interfaces.IShapeFactory;
import net.sf.latexdraw.glib.models.interfaces.IText;
import net.sf.latexdraw.glib.models.interfaces.IText.TextPosition;
import net.sf.latexdraw.glib.views.latex.DviPsColors;
import net.sf.latexdraw.glib.views.pst.PSTricksConstants;
import net.sf.latexdraw.util.LFileUtils;
import net.sf.latexdraw.util.LNumber;

/**
 * Defines a view of the IText model.<br>
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
 * 05/23/2010<br>
 * @author Arnaud BLOUIN
 * @since 3.0
 */
public class LTextView extends LShapeView<IText> {
	/** The picture. */
	protected Image image;

	/** The log of the compilation. */
	protected String log;

	/** The path of the files: for instance on Unix is can be /tmp/latexdraw180980 (without any extension). */
	private String pathPic;

	/** Used to detect if the last version of the text is different from the view. It helps to update the picture. */
	private String lastText;

	/** Used to detect if the last version of the text is different from the view. It helps to update the picture. */
	private Color lastColour;

	/** Used to detect if the last version of the text is different from the view. It helps to update the picture. */
	private TextPosition lastTextPos;

	public static final Font FONT = new Font("Times New Roman", Font.PLAIN, 18); //$NON-NLS-1$

	public static final FontMetrics FONT_METRICS;

	static {
		BufferedImage bufferImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bufferImage.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(FONT);

		FONT_METRICS = g.getFontMetrics();

		bufferImage.flush();
	}



	/**
	 * Creates and initialises a text view.
	 * @param model The model to view.
	 * @throws IllegalArgumentException If the given model is null.
	 * @since 3.0
	 */
	public LTextView(final IText model) {
		super(model);

		lastText 	= ""; //$NON-NLS-1$
		lastColour 	= null;
		lastTextPos	= null;
		update();
	}


	@Override
	public void update() {
		if(image==null || !lastText.equals(shape.getText()) ||
			lastColour==null || !lastColour.equals(shape.getLineColour()) ||
			lastTextPos==null || lastTextPos!=shape.getTextPosition()) {
			updateImage();
			lastText 	= shape.getText();
			lastColour 	= shape.getLineColour();
			lastTextPos	= shape.getTextPosition();
		}

		super.update();
	}


	@Override
	protected void finalize() throws Throwable {
		flush();
		super.finalize();
	}



	@Override
	public void flush() {
		super.flush();

		if(image!=null)
			image.flush();

		if(pathPic!=null) {
			File file = new File(pathPic);
			if(file.exists() && file.canWrite())
				file.delete();
		}

		pathPic  = null;
		image 	 = null;
		lastText = ""; //$NON-NLS-1$
	}



	/**
	 * Updates the image.
	 * @since 3.0
	 */
	public void updateImage() {
		flush();
		image = createImage();
	}



	/**
	 * @return the image.
	 * @since 3.0
	 */
	public Image getImage() {
		return image;
	}



	/**
	 * @return The LaTeX compiled picture of the text or null.
	 * @since 3.0
	 */
	protected Image createImage() {
		log = ""; //$NON-NLS-1$

		try {
			Image img   	  = null;
			final String code = shape.getText();

			if(code!=null && !code.isEmpty()) {
				File tmpDir 			= LFileUtils.INSTANCE.createTempDir();
				final String sep		= System.getProperty("file.separator"); //$NON-NLS-1$
				final String doc      	= getLaTeXDocument();
				pathPic					= tmpDir.getAbsolutePath() + sep + "latexdrawTmpPic" + System.currentTimeMillis(); //$NON-NLS-1$
				final String pathTex  	= pathPic + TeXFilter.TEX_EXTENSION;
				final FileOutputStream fos = new FileOutputStream(pathTex);
				final OutputStreamWriter osw = new OutputStreamWriter(fos);
				BufferedImage bi;

				try {
					osw.append(doc);
					osw.flush();
					osw.close();
					fos.flush();
					fos.close();

					log  = execute("latex --halt-on-error --interaction=nonstopmode --output-directory=" + tmpDir.getAbsolutePath() + " " + pathTex); //$NON-NLS-1$ //$NON-NLS-2$
					new File(pathTex).delete();
					new File(pathPic + ".aux").delete(); //$NON-NLS-1$
					new File(pathPic + ".log").delete(); //$NON-NLS-1$

					if(log.length()==0) {
						log += execute("dvips " + pathPic + ".dvi -o " + pathPic + PSFilter.PS_EXTENSION); //$NON-NLS-1$ //$NON-NLS-2$
						new File(pathPic + ".dvi").delete(); //$NON-NLS-1$
					}
					if(log.length()==0)
						log += execute("ps2pdf " + pathPic + PSFilter.PS_EXTENSION + " " + pathPic + PDFFilter.PDF_EXTENSION); //$NON-NLS-1$ //$NON-NLS-2$
					if(log.length()==0)
						log += execute("pdfcrop " + pathPic + PDFFilter.PDF_EXTENSION + " " + pathPic + PDFFilter.PDF_EXTENSION); //$NON-NLS-1$ //$NON-NLS-2$
					if(log.length()==0) {
						log += execute("pdftops " + pathPic + PDFFilter.PDF_EXTENSION + " " + pathPic + PSFilter.PS_EXTENSION); //$NON-NLS-1$ //$NON-NLS-2$
						new File(pathPic + PDFFilter.PDF_EXTENSION).delete();
					}
					if(log.length()==0) {
						log += execute("convert -channel RGBA " + pathPic + PSFilter.PS_EXTENSION + " " + pathPic + PNGFilter.PNG_EXTENSION); //$NON-NLS-1$ //$NON-NLS-2$
						new File(pathPic + PSFilter.PS_EXTENSION).delete();
					}

					if(log.length()==0) {
						File picFile = new File(pathPic + PNGFilter.PNG_EXTENSION);
						picFile.deleteOnExit();
						bi = ImageIO.read(picFile);
					}
					else bi = null;
				}catch(final IOException ex) {
					bi = null;
					try { fos.flush(); } catch(final IOException ex2) { BordelCollector.INSTANCE.add(ex2); }
					try { osw.flush(); } catch(final IOException ex2) { BordelCollector.INSTANCE.add(ex2); }
					LFileUtils.INSTANCE.closeStream(fos);
					LFileUtils.INSTANCE.closeStream(osw);
				}

				return bi;
			}

			return img;
		}
		catch(Exception e) {
			new File(pathPic + TeXFilter.TEX_EXTENSION).delete();
			new File(pathPic + PDFFilter.PDF_EXTENSION).delete();
			new File(pathPic + PSFilter.PS_EXTENSION).delete();
			new File(pathPic + ".dvi").delete(); //$NON-NLS-1$
			new File(pathPic + ".aux").delete(); //$NON-NLS-1$
			new File(pathPic + ".log").delete(); //$NON-NLS-1$
			BordelCollector.INSTANCE.add(new FileNotFoundException(log+e.getMessage()));
			return null;
		}
	}



	/**
	 * Executes a given command and returns the log.
	 * @param cmd The command to execute.
	 * @return The log resulting of the command. If not empty the command failed.
	 * @since 3.0
	 */
	private static String execute(final String cmd) {
		StringBuilder log 		= new StringBuilder();
		InputStreamReader isr 	= null;
		BufferedReader br 		= null;

		try {
			Runtime runtime = Runtime.getRuntime();
			Process process;
			process = runtime.exec(cmd);

			String eol		= System.getProperty("line.separator"); //$NON-NLS-1$
			boolean ok 		= true;
			int cpt    		= 1;
			int exit   		= 0;

			synchronized(runtime) {
				while(ok && cpt<10)
					try {
						exit = process.exitValue();
						ok = false;
					}
					catch(IllegalThreadStateException e) {
						runtime.wait(10);
						cpt++;
					}
			}

			isr = new InputStreamReader(process.getInputStream());
			br     = new BufferedReader(isr);

			String line = br.readLine();

			while(line!=null) {
				log.append(line).append(eol);
				line = br.readLine();
			}

			if(exit==0)
				log.delete(0, log.length());

		}catch(final IOException ex) {
			log.append(ex.getMessage());
		}catch(final InterruptedException ex) {
			log.append(ex.getMessage());
		}

		try{ if(br!=null) br.close(); }   catch(final IOException ex){ log.append(ex.getMessage()); }
		try{ if(isr!=null) isr.close(); } catch(final IOException ex){ log.append(ex.getMessage()); }

		return log.toString();
	}



	/**
	 * @return The LaTeX document that will be compiled in order to get the picture of the text.
	 * @since 3.0
	 */
	public String getLaTeXDocument() {
		final String code		= shape.getText();
		final StringBuffer doc 	= new StringBuffer();
		final String packages	= ""; //$NON-NLS-1$ //TODO latex includes
		final Color textColour	= shape.getLineColour();
		final boolean coloured;

		// We must scale the text to fit its latex size: latexdrawDPI/latexDPI is the ratio to scale the
		// created png picture.
		final double scale = IShape.PPC*PSTricksConstants.INCH_VAL_CM/PSTricksConstants.INCH_VAL_PT;

		doc.append("\\documentclass[10pt]{article}\\usepackage[usenames,dvipsnames]{pstricks}"); //$NON-NLS-1$
		doc.append(packages);
		doc.append("\\pagestyle{empty}\\begin{document}\\psscalebox{"); //$NON-NLS-1$
		doc.append((float)LNumber.INSTANCE.getCutNumber(scale)).append(' ');
		doc.append((float)LNumber.INSTANCE.getCutNumber(scale)).append('}').append('{');

		if(!textColour.equals(PSTricksConstants.DEFAULT_LINE_COLOR)) {
			String name = DviPsColors.INSTANCE.getColourName(textColour);
			coloured = true;

			if(name==null)
				name = DviPsColors.INSTANCE.addUserColour(textColour);

			doc.append(DviPsColors.INSTANCE.getUsercolourCode(name)).append("\\textcolor{").append(name).append('}').append('{'); //$NON-NLS-1$
		}
		else coloured = false;

		doc.append(code);

		if(coloured)
			doc.append('}');

		doc.append("}\\end{document}"); //$NON-NLS-1$

		return doc.toString();
	}


	@Override
	public boolean intersects(final Rectangle2D rec) {
		if(rec==null)
			return false;

		final Shape sh = getRotatedShape2D(shape.getRotationAngle(), border,
						DrawingTK.getFactory().createPoint(border.getMinX(), border.getMinY()),
						DrawingTK.getFactory().createPoint(border.getMaxX(), border.getMaxY()));
		return sh.contains(rec) || sh.intersects(rec);
	}


	@Override
	public boolean contains(final double x, final double y) {
		return border.contains(x, y);
	}


	private IPoint getTextPositionImage() {
		switch(shape.getTextPosition()) {
			case BOT : return DrawingTK.getFactory().createPoint(shape.getX()-image.getWidth(null)/2., shape.getY()-image.getHeight(null));
			case TOP : return DrawingTK.getFactory().createPoint(shape.getX()-image.getWidth(null)/2., shape.getY());
			case BOT_LEFT : return DrawingTK.getFactory().createPoint(shape.getX(), shape.getY()-image.getHeight(null));
			case TOP_LEFT : return DrawingTK.getFactory().createPoint(shape.getX(), shape.getY());
			case BOT_RIGHT : return DrawingTK.getFactory().createPoint(shape.getX()-image.getWidth(null), shape.getY()-image.getHeight(null));
			case TOP_RIGHT : return DrawingTK.getFactory().createPoint(shape.getX()-image.getWidth(null), shape.getY());
		}

		return null;
	}


	private IPoint getTextPositionText() {
		TextLayout tl = new TextLayout(shape.getText(), FONT, FONT_METRICS.getFontRenderContext());
		Rectangle2D bounds = tl.getBounds();

		switch(shape.getTextPosition()) {
			case BOT : return DrawingTK.getFactory().createPoint(shape.getX()-bounds.getWidth()/2., shape.getY());
			case TOP : return DrawingTK.getFactory().createPoint(shape.getX()-bounds.getWidth()/2., shape.getY()+bounds.getHeight());
			case BOT_LEFT : return DrawingTK.getFactory().createPoint(shape.getX(), shape.getY());
			case TOP_LEFT : return DrawingTK.getFactory().createPoint(shape.getX(), shape.getY()+bounds.getHeight());
			case BOT_RIGHT : return DrawingTK.getFactory().createPoint(shape.getX()-bounds.getWidth(), shape.getY());
			case TOP_RIGHT : return DrawingTK.getFactory().createPoint(shape.getX()-bounds.getWidth(), shape.getY()+bounds.getHeight());
		}

		return null;
	}



	@Override
	public void paint(final Graphics2D g) {
		if(g==null)
			return ;

		final IPoint p = beginRotation(g);
		final IPoint position = image==null ? getTextPositionText() : getTextPositionImage();

		if(image==null) {
			g.setColor(shape.getLineColour());
			g.setFont(FONT);
			g.drawString(shape.getText(), (int)position.getX(), (int)position.getY());
		}
		else
			g.drawImage(image, (int)position.getX(), (int)position.getY(), null);

		if(p!=null)
			endRotation(g, p);
	}



	@Override
	public void updateBorder() {
		final IPoint position = image==null ? getTextPositionText() : getTextPositionImage();

		if(image==null) {
			TextLayout tl = new TextLayout(shape.getText(), FONT, FONT_METRICS.getFontRenderContext());
			Rectangle2D bounds = tl.getBounds();
			border.setFrame(position.getX(), position.getY(), bounds.getWidth(), bounds.getHeight());
		}
		else {
			final double height = image.getHeight(null);
			border.setFrame(position.getX(), position.getY(), image.getWidth(null), height);
		}
	}



	@Override
	protected IPoint beginRotation(final Graphics2D g) {//TODO To change to remove duplicate code.
		final double rotationAngle 	= shape.getRotationAngle();
		IPoint p 			 		= null;

		if(Math.abs(rotationAngle%(Math.PI*2.))>0.00001  && g!=null) {
			IShapeFactory factory = DrawingTK.getFactory();
			IPoint tl = factory.createPoint(border.getMinX(), border.getMinY());
			IPoint br = factory.createPoint(border.getMaxX(), border.getMaxY());
			double cx = (tl.getX() + br.getX()) / 2., cy = (tl.getY() + br.getY()) / 2.;
			double c2x = Math.cos(rotationAngle) * cx - Math.sin(rotationAngle)* cy;
			double c2y = Math.sin(rotationAngle) * cx + Math.cos(rotationAngle)* cy;
			double c3x = Math.cos(-rotationAngle) * (cx - c2x)- Math.sin(-rotationAngle) * (cy - c2y);
			double c3y = Math.sin(-rotationAngle) * (cx - c2x)+ Math.cos(-rotationAngle) * (cy - c2y);

			g.rotate(rotationAngle);
			g.translate(c3x, c3y);
			p = factory.createPoint(c3x, c3y);
		}

		return p;
	}


	@Override
	protected void updateDblePathInside() {
		// Nothing to do.
	}

	@Override
	protected void updateDblePathMiddle() {
		// Nothing to do.
	}

	@Override
	protected void updateDblePathOutside() {
		// Nothing to do.
	}

	@Override
	protected void updateGeneralPathInside() {
		// Nothing to do.
	}

	@Override
	protected void updateGeneralPathMiddle() {
		// Nothing to do.
	}

	@Override
	protected void updateGeneralPathOutside() {
		// Nothing to do.
	}
}