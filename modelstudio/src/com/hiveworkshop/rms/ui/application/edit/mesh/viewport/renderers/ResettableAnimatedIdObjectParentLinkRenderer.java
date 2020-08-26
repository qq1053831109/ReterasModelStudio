package com.hiveworkshop.rms.ui.application.edit.mesh.viewport.renderers;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;

import com.hiveworkshop.rms.editor.model.Attachment;
import com.hiveworkshop.rms.editor.model.Bone;
import com.hiveworkshop.rms.editor.model.Camera;
import com.hiveworkshop.rms.editor.model.CollisionShape;
import com.hiveworkshop.rms.editor.model.EventObject;
import com.hiveworkshop.rms.editor.model.Helper;
import com.hiveworkshop.rms.editor.model.Light;
import com.hiveworkshop.rms.editor.model.ParticleEmitter;
import com.hiveworkshop.rms.editor.model.ParticleEmitter2;
import com.hiveworkshop.rms.editor.model.ParticleEmitterPopcorn;
import com.hiveworkshop.rms.editor.model.RibbonEmitter;
import com.hiveworkshop.rms.editor.model.visitor.IdObjectVisitor;
import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.NodeIconPalette;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.axes.CoordinateSystem;
import com.hiveworkshop.rms.util.Matrix4;
import com.hiveworkshop.rms.util.Vertex3;
import com.hiveworkshop.rms.util.Vertex4;

public final class ResettableAnimatedIdObjectParentLinkRenderer implements IdObjectVisitor {
	private CoordinateSystem coordinateSystem;
	private Graphics2D graphics;
	private final int vertexSize;
	private NodeIconPalette nodeIconPalette;
	private RenderModel renderModel;

	public ResettableAnimatedIdObjectParentLinkRenderer(final int vertexSize) {
		this.vertexSize = vertexSize;
	}

	public ResettableAnimatedIdObjectParentLinkRenderer reset(final CoordinateSystem coordinateSystem,
			final Graphics2D graphics, final NodeIconPalette nodeIconPalette, final RenderModel renderModel) {
		this.coordinateSystem = coordinateSystem;
		this.graphics = graphics;
		this.nodeIconPalette = nodeIconPalette;
		this.renderModel = renderModel;
		return this;
	}

	@Override
	public void bone(final Bone object) {
		if (object.getParent() != null) {
			drawLink(graphics, coordinateSystem, object.getPivotPoint(), object.getParent().getPivotPoint(),
					renderModel.getRenderNode(object).getWorldMatrix(),
					renderModel.getRenderNode(object.getParent()).getWorldMatrix());
		}
	}

	@Override
	public void light(final Light object) {
		if (object.getParent() != null) {
			drawLink(graphics, coordinateSystem, object.getPivotPoint(), object.getParent().getPivotPoint(),
					renderModel.getRenderNode(object).getWorldMatrix(),
					renderModel.getRenderNode(object.getParent()).getWorldMatrix());
		}
	}

	private void drawCrosshair(final Bone object) {
		if (object.getParent() != null) {
			drawLink(graphics, coordinateSystem, object.getPivotPoint(), object.getParent().getPivotPoint(),
					renderModel.getRenderNode(object).getWorldMatrix(),
					renderModel.getRenderNode(object.getParent()).getWorldMatrix());
		}
	}

	@Override
	public void helper(final Helper object) {
		if (object.getParent() != null) {
			drawLink(graphics, coordinateSystem, object.getPivotPoint(), object.getParent().getPivotPoint(),
					renderModel.getRenderNode(object).getWorldMatrix(),
					renderModel.getRenderNode(object.getParent()).getWorldMatrix());
		}
	}

	@Override
	public void attachment(final Attachment object) {
		if (object.getParent() != null) {
			drawLink(graphics, coordinateSystem, object.getPivotPoint(), object.getParent().getPivotPoint(),
					renderModel.getRenderNode(object).getWorldMatrix(),
					renderModel.getRenderNode(object.getParent()).getWorldMatrix());
		}
	}

	@Override
	public void particleEmitter(final ParticleEmitter object) {
		if (object.getParent() != null) {
			drawLink(graphics, coordinateSystem, object.getPivotPoint(), object.getParent().getPivotPoint(),
					renderModel.getRenderNode(object).getWorldMatrix(),
					renderModel.getRenderNode(object.getParent()).getWorldMatrix());
		}
	}

	@Override
	public void popcornFxEmitter(final ParticleEmitterPopcorn object) {
		if (object.getParent() != null) {
			drawLink(graphics, coordinateSystem, object.getPivotPoint(), object.getParent().getPivotPoint(),
					renderModel.getRenderNode(object).getWorldMatrix(),
					renderModel.getRenderNode(object.getParent()).getWorldMatrix());
		}
	}

	@Override
	public void particleEmitter2(final ParticleEmitter2 object) {
		if (object.getParent() != null) {
			drawLink(graphics, coordinateSystem, object.getPivotPoint(), object.getParent().getPivotPoint(),
					renderModel.getRenderNode(object).getWorldMatrix(),
					renderModel.getRenderNode(object.getParent()).getWorldMatrix());
		}
	}

	@Override
	public void ribbonEmitter(final RibbonEmitter object) {
		if (object.getParent() != null) {
			drawLink(graphics, coordinateSystem, object.getPivotPoint(), object.getParent().getPivotPoint(),
					renderModel.getRenderNode(object).getWorldMatrix(),
					renderModel.getRenderNode(object.getParent()).getWorldMatrix());
		}
	}

	@Override
	public void eventObject(final EventObject object) {
		if (object.getParent() != null) {
			drawLink(graphics, coordinateSystem, object.getPivotPoint(), object.getParent().getPivotPoint(),
					renderModel.getRenderNode(object).getWorldMatrix(),
					renderModel.getRenderNode(object.getParent()).getWorldMatrix());
		}
	}

	@Override
	public void collisionShape(final CollisionShape object) {
		if (object.getParent() != null) {
			drawLink(graphics, coordinateSystem, object.getPivotPoint(), object.getParent().getPivotPoint(),
					renderModel.getRenderNode(object).getWorldMatrix(),
					renderModel.getRenderNode(object.getParent()).getWorldMatrix());
		}
	}

	@Override
	public void camera(final Camera camera) {
		// TODO ANIMATE CAMERAS
		System.err.println("TODO ANIMATE CAMERAS");
	}

	private static final Vertex4 vertexHeap = new Vertex4();
	private static final Vertex4 vertexHeap2 = new Vertex4();

	public static void drawLink(final Graphics2D graphics, final CoordinateSystem coordinateSystem,
			final Vertex3 pivotPoint, final Vertex3 target, final Matrix4 worldMatrix,
			final Matrix4 targetWorldMatrix) {
		loadPivotInVertexHeap(pivotPoint, worldMatrix, vertexHeap);
		loadPivotInVertexHeap(target, targetWorldMatrix, vertexHeap2);

		final int xCoord = (int) coordinateSystem
				.convertX(Vertex3.getCoord(vertexHeap, coordinateSystem.getPortFirstXYZ()));
		final int yCoord = (int) coordinateSystem
				.convertY(Vertex3.getCoord(vertexHeap, coordinateSystem.getPortSecondXYZ()));
		final int xCoord2 = (int) coordinateSystem
				.convertX(Vertex3.getCoord(vertexHeap2, coordinateSystem.getPortFirstXYZ()));
		final int yCoord2 = (int) coordinateSystem
				.convertY(Vertex3.getCoord(vertexHeap2, coordinateSystem.getPortSecondXYZ()));
		// TODO resettable
		graphics.setPaint(
				new GradientPaint(new Point(xCoord, yCoord), Color.WHITE, new Point(xCoord2, yCoord2), Color.BLACK));

		graphics.drawLine(xCoord, yCoord, xCoord2, yCoord2);
	}

	public static void loadPivotInVertexHeap(final Vertex3 pivotPoint, final Matrix4 worldMatrix,
			final Vertex4 vertexHeap) {
		vertexHeap.x = (float) pivotPoint.x;
		vertexHeap.y = (float) pivotPoint.y;
		vertexHeap.z = (float) pivotPoint.z;
		vertexHeap.w = 1;
		Matrix4.transform(worldMatrix, vertexHeap, vertexHeap);
	}

}