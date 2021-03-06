package com.hiveworkshop.wc3.gui.modeledit;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;

/**
 * Cool gradient colored JButton
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class ModeButton extends JButton {
	GradientPaint gPaint;

	public ModeButton(final String s) {
		super(s);
		addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(final ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentResized(final ComponentEvent e) {
				if (gPaint != null) {
					gPaint = new GradientPaint(new Point(0, 10), gPaint.getColor1(), new Point(0, getHeight()),
							gPaint.getColor2(), true);
				}
			}

			@Override
			public void componentMoved(final ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentHidden(final ComponentEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void paintComponent(final Graphics g) {
		if (gPaint != null) {
			final Graphics2D g2 = (Graphics2D) g.create();
			g2.setPaint(gPaint);
			final int amt = 4;
			final int indent = 1;
			g2.fillRect(indent, indent, getWidth() - indent * 3, getHeight() - indent * 3);
			g2.setColor(Color.black);
			g2.drawRoundRect(indent, indent, getWidth() - indent * 3, getHeight() - indent * 3, amt, amt);
			g2.dispose();
		}
		super.paintComponent(g);
	}

	public void setColors(final Color a, final Color b) {
		// setBackground(a);
		// setOpaque(false);
		setContentAreaFilled(false);
		gPaint = new GradientPaint(new Point(0, 10), a, new Point(0, getHeight()), b, true);
	}

	public void resetColors() {
		// this.setBackground(null);
		// setOpaque(true);
		gPaint = null;
		setContentAreaFilled(true);
	}

	public boolean isColorModeActive() {
		return gPaint != null;
	}
}
