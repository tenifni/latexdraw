package net.sf.latexdraw.actions;

import org.malai.action.Action;

import javax.swing.*;
import java.io.File;

/**
 * This action loads an SVG document into the app.
 * <br>
 * This file is part of LaTeXDraw<br>
 * Copyright (c) 2005-2015 Arnaud BLOUIN<br>
 * <br>
 *  LaTeXDraw is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  any later version.<br>
 * <br>
 *  LaTeXDraw is distributed without any warranty; without even the
 *  implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 *  PURPOSE. See the GNU General Public License for more details.<br>
 * <br>
 * @author Arnaud Blouin
 * @date 06/09/2011
 * @since 3.0
 */
public class LoadDrawing extends Action implements Modifying { //  extends Load<LFrame, JLabel>
	/** The file chooser that will be used to select the location to save. */
	protected JFileChooser fileChooser;
	File currentFolder;

	@Override
	protected void doActionBody() {
//		if(ui.isModified())
//			switch(SaveDrawing.showAskModificationsDialog(ui)) {
//				case JOptionPane.NO_OPTION: //load
//					load();
//					break;
//				case JOptionPane.YES_OPTION: // save + load
//					final File f = SaveDrawing.showDialog(fileChooser, true, ui, file, currentFolder);
//					if(f!=null) {
//						openSaveManager.save(f.getPath(), ui, progressBar, statusWidget);
//						ui.setModified(false);
//						load();
//					}
//					break;
//				case JOptionPane.CANCEL_OPTION: // nothing
//					break;
//				default:
//					break;
//			}
//		else load();
//		done();
	}


	@Override
	public void flush() {
		super.flush();
		fileChooser = null;
	}

	@Override
	public boolean canDo() {
//		return ui!=null && openSaveManager!=null && fileChooser!=null;
		return false;
	}


	protected void load() {
//		if(file==null) {
//			fileChooser.setCurrentDirectory(currentFolder);
//			file = fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION ? fileChooser.getSelectedFile() : null;
//		}
//		else
//			fileChooser.setSelectedFile(file);
//
//		if(file!=null && file.canRead())
//			super.doActionBody();
//		else
//			ok = false;
	}


	/**
	 * @param fileChooser The file chooser that will be used to select the location to save.
	 * @since 3.0
	 */
	public void setFileChooser(final JFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	public void setCurrentFolder(final File currFolder) {
		currentFolder = currFolder;
	}

	//FIXME to remove
	@Override
	public boolean isRegisterable() {
		return false;
	}
}

