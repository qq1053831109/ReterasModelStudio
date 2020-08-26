package com.hiveworkshop.rms.editor.model.util;

import java.util.ArrayList;
import java.util.List;

import com.hiveworkshop.rms.editor.model.Bitmap;
import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.model.GeosetVertex;
import com.hiveworkshop.rms.editor.model.Layer;
import com.hiveworkshop.rms.editor.model.Material;
import com.hiveworkshop.rms.editor.model.Triangle;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.axes.CoordinateSystem;
import com.hiveworkshop.rms.util.Vertex2;
import com.hiveworkshop.rms.util.Vertex3;

public final class ModelUtils {
	public static final class Mesh {
		private final List<GeosetVertex> vertices;
		private final List<Triangle> triangles;

		private Mesh(final List<GeosetVertex> vertices, final List<Triangle> triangles) {
			this.vertices = vertices;
			this.triangles = triangles;
		}

		public List<GeosetVertex> getVertices() {
			return vertices;
		}

		public List<Triangle> getTriangles() {
			return triangles;
		}

	}

	public static String getPortrait(final String filepath) {
		final String portrait = filepath.substring(0, filepath.lastIndexOf('.')) + "_portrait"
				+ filepath.substring(filepath.lastIndexOf('.'));
		return portrait;
	}

	public static Mesh createPlane(final byte planeDimension, final boolean outward, final double planeHeight,
			final double minFirst, final double minSecond, final double maxFirst, final double maxSecond,
			final int numberOfSegments) {
		return createPlane(planeDimension, outward, planeHeight, minFirst, minSecond, maxFirst, maxSecond,
				numberOfSegments, numberOfSegments);
	}

	public static Mesh createPlane(final byte planeDimension, final boolean outward, final double planeHeight,
			final double minFirst, final double minSecond, final double maxFirst, final double maxSecond,
			final int numberOfSegmentsX, final int numberOfSegmentsY) {
		final byte firstDimension;
		final byte secondDimension;
		switch (planeDimension) {
		case 0:
			firstDimension = (byte) 1;
			secondDimension = (byte) 2;
			break;
		case 1:
			firstDimension = (byte) 0;
			secondDimension = (byte) 2;
			break;
		case 2:
			firstDimension = (byte) 0;
			secondDimension = (byte) 1;
			break;
		default:
			throw new IllegalStateException();
		}
		boolean flipFacesForIterationDesignFlaw = false;
		if (planeDimension == 1) {
			flipFacesForIterationDesignFlaw = true;
		}
		final Vertex3 normal = new Vertex3(0, 0, 0);
		normal.setCoord(planeDimension, outward ? 1 : -1);
		return createPlane(firstDimension, secondDimension, normal, planeHeight, minFirst, minSecond, maxFirst,
				maxSecond, numberOfSegmentsX, numberOfSegmentsY);
	}

	public static Mesh createPlane(final byte firstDimension, final byte secondDimension, final Vertex3 facingVector,
			final double planeHeight, final double minFirst, final double minSecond, final double maxFirst,
			final double maxSecond, final int numberOfSegments) {
		return createPlane(firstDimension, secondDimension, facingVector, planeHeight, minFirst, minSecond, maxFirst,
				maxSecond, numberOfSegments, numberOfSegments);
	}

	public static Mesh createPlane(final byte firstDimension, final byte secondDimension, final Vertex3 facingVector,
			final double planeHeight, final double minFirst, final double minSecond, final double maxFirst,
			final double maxSecond, final int numberOfSegmentsX, final int numberOfSegmentsY) {
		final byte planeDimension = CoordinateSystem.Util.getUnusedXYZ(firstDimension, secondDimension);
		final List<GeosetVertex> vertices = new ArrayList<>();
		final List<Triangle> triangles = new ArrayList<>();
		final double firstDimensionSegmentWidth = (maxFirst - minFirst) / numberOfSegmentsX;
		final double secondDimensionSegmentWidth = (maxSecond - minSecond) / numberOfSegmentsY;
		final double segmentWidthUV1 = 1. / numberOfSegmentsX;
		final double segmentWidthUV2 = 1. / numberOfSegmentsY;
		GeosetVertex[] previousRow = null;
		for (int y = 0; y < (numberOfSegmentsY + 1); y++) {
			final GeosetVertex[] currentRow = new GeosetVertex[numberOfSegmentsX + 1];
			for (int x = 0; x < (numberOfSegmentsX + 1); x++) {
				final Vertex3 normal = new Vertex3(facingVector.x, facingVector.y, facingVector.z);
				final GeosetVertex vertex = new GeosetVertex(0, 0, 0, normal);
				currentRow[x] = vertex;
				vertex.setCoord(planeDimension, planeHeight);
				vertex.setCoord(firstDimension, minFirst + (x * firstDimensionSegmentWidth));
				vertex.setCoord(secondDimension, minSecond + (y * secondDimensionSegmentWidth));
				vertex.addTVertex(new Vertex2(x * segmentWidthUV1, y * segmentWidthUV2));
				vertices.add(vertex);
				if (y > 0) {
					if (x > 0) {
						final GeosetVertex lowerLeft = previousRow[x - 1];
						final GeosetVertex lowerRight = previousRow[x];
						final GeosetVertex upperLeft = currentRow[x - 1];
						final Triangle firstFace = new Triangle(vertex, upperLeft, lowerLeft);
						triangles.add(firstFace);
						final Triangle secondFace = new Triangle(vertex, lowerLeft, lowerRight);
						triangles.add(secondFace);
						final boolean flip = firstFace.getFacingVector().dotProduct(facingVector) < 0;
						if (flip) {
							firstFace.flip(false);
							secondFace.flip(false);
						}
					}
				}
			}
			previousRow = currentRow;
		}
		return new Mesh(vertices, triangles);
	}

	/**
	 * @param model
	 * @param max
	 * @param min
	 */
	public static void createBox(final EditableModel model, final Vertex3 max, final Vertex3 min, final int segments) {
		final Geoset geoset = new Geoset();
		geoset.setMaterial(new Material(new Layer("None", new Bitmap("textures\\white.blp"))));

		for (byte side = (byte) 0; side < 2; side++) {
			for (byte dimension = (byte) 0; dimension < 3; dimension++) {
				final Vertex3 sideMaxima;
				switch (side) {
				case 0:
					sideMaxima = min;
					break;
				case 1:
					sideMaxima = max;
					break;
				default:
					throw new IllegalStateException();
				}
				final double coordinateAtSide = sideMaxima.getCoord(dimension);

				final byte firstDimension;
				final byte secondDimension;
				switch (dimension) {
				case 0:
					firstDimension = (byte) 1;
					secondDimension = (byte) 2;
					break;
				case 1:
					firstDimension = (byte) 0;
					secondDimension = (byte) 2;
					break;
				case 2:
					firstDimension = (byte) 0;
					secondDimension = (byte) 1;
					break;
				default:
					throw new IllegalStateException();
				}
				final double minFirst = min.getCoord(firstDimension);
				final double minSecond = min.getCoord(secondDimension);
				final double maxFirst = max.getCoord(firstDimension);
				final double maxSecond = max.getCoord(secondDimension);

				final Mesh sidedPlane = createPlane(dimension, side == 1, coordinateAtSide, minFirst, minSecond,
						maxFirst, maxSecond, segments);
				for (final GeosetVertex vertex : sidedPlane.vertices) {
					geoset.add(vertex);
				}
				for (final Triangle triangle : sidedPlane.triangles) {
					geoset.add(triangle);
				}
			}
		}
		for (final GeosetVertex vertex : geoset.getVertices()) {
			vertex.addTVertex(new Vertex2(0, 0));
			vertex.setGeoset(geoset);
		}
		for (final Triangle triangle : geoset.getTriangles()) {
			triangle.setGeoset(geoset);
			for (final GeosetVertex vertex : triangle.getVerts()) {
				vertex.getTriangles().add(triangle);
			}
		}
		model.add(geoset);
	}

	/**
	 * Creates a box ready to add to the dataGeoset, but does not actually modify
	 * the geoset itself
	 *
	 * @param max
	 * @param min
	 * @param segments
	 * @param dataGeoset
	 * @return
	 */
	public static Mesh createBox(final Vertex3 max, final Vertex3 min, final int lengthSegs, final int widthSegs,
			final int heightSegs, final Geoset dataGeoset) {
		final Mesh box = new Mesh(new ArrayList<>(), new ArrayList<>());
		for (byte side = (byte) 0; side < 2; side++) {
			for (byte dimension = (byte) 0; dimension < 3; dimension++) {
				final Vertex3 sideMaxima;
				switch (side) {
				case 0:
					sideMaxima = min;
					break;
				case 1:
					sideMaxima = max;
					break;
				default:
					throw new IllegalStateException();
				}
				final double coordinateAtSide = sideMaxima.getCoord(dimension);

				final int segsX;
				final int segsY;
				final byte firstDimension;
				final byte secondDimension;
				switch (dimension) {
				case 0:
					firstDimension = (byte) 1;
					secondDimension = (byte) 2;
					segsX = widthSegs;
					segsY = heightSegs;
					break;
				case 1:
					firstDimension = (byte) 0;
					secondDimension = (byte) 2;
					segsX = lengthSegs;
					segsY = heightSegs;
					break;
				case 2:
					firstDimension = (byte) 0;
					secondDimension = (byte) 1;
					segsX = lengthSegs;
					segsY = widthSegs;
					break;
				default:
					throw new IllegalStateException();
				}
				final double minFirst = min.getCoord(firstDimension);
				final double minSecond = min.getCoord(secondDimension);
				final double maxFirst = max.getCoord(firstDimension);
				final double maxSecond = max.getCoord(secondDimension);

				final Mesh sidedPlane = createPlane(dimension, side != 1, coordinateAtSide, minFirst, minSecond,
						maxFirst, maxSecond, segsX, segsY);
				for (final GeosetVertex vertex : sidedPlane.vertices) {
					box.vertices.add(vertex);
				}
				for (final Triangle triangle : sidedPlane.triangles) {
					box.triangles.add(triangle);
				}
			}
		}
		for (final GeosetVertex vertex : box.getVertices()) {
			vertex.addTVertex(new Vertex2(0, 0));
			vertex.setGeoset(dataGeoset);
		}
		for (final Triangle triangle : box.getTriangles()) {
			triangle.setGeoset(dataGeoset);
			for (final GeosetVertex vertex : triangle.getVerts()) {
				vertex.getTriangles().add(triangle);
			}
		}
		return box;
	}

	/**
	 * @param model
	 * @param max
	 * @param min
	 */
	public static void createGroundPlane(final EditableModel model, final Vertex3 max, final Vertex3 min, final int segments) {
		final Geoset geoset = new Geoset();
		geoset.setMaterial(new Material(new Layer("None", new Bitmap("textures\\white.blp"))));

		final Mesh sidedPlane = createPlane((byte) 2, true, 0, min.x, min.y, max.x, max.y, segments);
		for (final GeosetVertex vertex : sidedPlane.vertices) {
			geoset.add(vertex);
		}
		for (final Triangle triangle : sidedPlane.triangles) {
			geoset.add(triangle);
		}
		for (final GeosetVertex vertex : geoset.getVertices()) {
			vertex.addTVertex(new Vertex2(0, 0));
			vertex.setGeoset(geoset);
		}
		for (final Triangle triangle : geoset.getTriangles()) {
			triangle.setGeoset(geoset);
			for (final GeosetVertex vertex : triangle.getVerts()) {
				vertex.getTriangles().add(triangle);
			}
		}
		model.add(geoset);
	}

	public static float[] flipRGBtoBGR(final float[] rgb) {
		final float[] bgr = new float[3];
		for (int i = 0; i < 3; i++) {
			bgr[i] = rgb[2 - i];
		}
		return bgr;
	}

	public static boolean isLevelOfDetailSupported(final int formatVersion) {
		return (formatVersion == 900) || (formatVersion == 1000);
	}

	public static boolean isShaderStringSupported(final int formatVersion) {
		return (formatVersion == 900) || (formatVersion == 1000);
	}

	public static boolean isTangentAndSkinSupported(final int formatVersion) {
		return (formatVersion == 900) || (formatVersion == 1000);
	}

	public static boolean isBindPoseSupported(final int formatVersion) {
		return (formatVersion == 900) || (formatVersion == 1000);
	}

	public static boolean isEmissiveLayerSupported(final int formatVersion) {
		return (formatVersion == 900) || (formatVersion == 1000);
	}

	public static boolean isFresnelColorLayerSupported(final int formatVersion) {
		return formatVersion == 1000;
	}

	public static boolean isCornSupported(final int formatVersion) {
		return (formatVersion == 900) || (formatVersion == 1000);
	}

	private ModelUtils() {
	}
}