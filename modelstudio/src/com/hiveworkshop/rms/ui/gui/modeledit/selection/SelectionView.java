package com.hiveworkshop.rms.ui.gui.modeledit.selection;

import java.util.Collection;

import com.hiveworkshop.rms.editor.model.Triangle;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.edit.mesh.ModelElementRenderer;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.axes.CoordinateSystem;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.uv.TVertexModelElementRenderer;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.util.Vertex2;
import com.hiveworkshop.rms.util.Vertex3;

public interface SelectionView {
	Vertex3 getCenter();

	Collection<Triangle> getSelectedFaces();

	Collection<? extends Vertex3> getSelectedVertices();

	// needs to be coord system, not coord axes, so that
	// vertex selection view knows the zoom level,
	// so that the width and height of a vertex in pixels
	// is zoom independent
	// boolean canSelectAt(Point point, CoordinateSystem axes);

	double getCircumscribedSphereRadius(Vertex3 center);

	void renderSelection(ModelElementRenderer renderer, final CoordinateSystem coordinateSystem, ModelView modelView,
                         ProgramPreferences programPreferences);

	Vertex2 getUVCenter(int tvertexLayerId);

	Collection<? extends Vertex2> getSelectedTVertices(int tvertexLayerId);

	double getCircumscribedSphereRadius(Vertex2 center, int tvertexLayerId);

	void renderUVSelection(TVertexModelElementRenderer renderer, ModelView modelView,
                           ProgramPreferences programPreferences, int tvertexLayerId);

	boolean isEmpty();
}